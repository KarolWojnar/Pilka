package com.Football.football.Services;

import com.Football.football.ApiKeyManager;
import com.Football.football.Repositories.FixturesStatsRepo;
import com.Football.football.Repositories.PlayersStatsRepo;
import com.Football.football.Repositories.TeamStatsRepo;
import com.Football.football.Tables.FixturesStats;
import com.Football.football.Tables.PlayerStats;
import com.Football.football.Tables.TeamStats;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FixturesService {
    private final FixturesStatsRepo fixtureRepository;
    private final ApiKeyManager apiKeyManager;
    private final TeamStatsRepo teamStatsRepo;
    private final PlayersStatsRepo playersStatsRepo;
    private HttpClient httpClient = HttpClient.newHttpClient();

    public void saveAllFixtures(Long leagueId, Long year) throws IOException, InterruptedException, JSONException {
        int attempts = 0;

        while (attempts < apiKeyManager.getApiKeysLength()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/fixtures?season=" + year + "&league=" + leagueId))
                    .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 429) {
                apiKeyManager.switchToNextApiKey();
                attempts++;
            } else if (response.statusCode() == 200) {
                String rBody = response.body();
                JSONObject jResponse = new JSONObject(rBody);

                if (jResponse.has("response")) {
                    JSONArray fixtures = jResponse.getJSONArray("response");
                    Map<Long, TeamStats> teamStatsMap = fetchTeamStatsMap(year);
                    Map<Long, PlayerStats> playerStatsMap = fetchPlayerStatsMap(year);
                    for (int i = 0; i < fixtures.length(); i++) {
                        JSONObject fixture = fixtures.getJSONObject(i);
                        saveNewFixture(fixture, year, teamStatsMap, playerStatsMap);
                    }
                    return;
                } else {
                    throw new IOException("Brak spotkania o takim id");
                }
            } else {
                throw new IOException("Unexpected response status: " + response.statusCode());
            }
        }

        throw new IOException("Failed to retrieve data from API after trying all API keys.");
    }

    private void saveNewFixture(JSONObject fixture, Long year, Map<Long, TeamStats> teamStatsMap, Map<Long, PlayerStats> playerStatsMap) throws JSONException, IOException, InterruptedException {
        int fixtureId = fixture.getJSONObject("fixture").getInt("id");
        Optional<FixturesStats> optionalFixture = fixtureRepository.findById((long) fixtureId);
        if (optionalFixture.isPresent()) {
            return;
        }

        String dateString = fixture.getJSONObject("fixture").getString("date").substring(0, 19);
        LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        JSONArray fixturePlayers = getFixtureStatsFromAPI(fixtureId);
        JSONObject team0 = fixturePlayers.getJSONObject(0);
        JSONObject team1 = fixturePlayers.getJSONObject(1);
        long team0Id = team0.getJSONObject("team").getLong("id");
        long team1Id = team1.getJSONObject("team").getLong("id");

        TeamStats opTeam0 = teamStatsMap.get(team0Id);
        TeamStats opTeam1 = teamStatsMap.get(team1Id);

        if (opTeam0 != null && opTeam1 != null) {
            JSONArray players0 = team0.getJSONArray("players");

            for (int i = 0; i < players0.length(); i++) {
                JSONObject resp = players0.getJSONObject(i);
                long playerId = resp.getJSONObject("player").getLong("id");
                PlayerStats optionalPlayer = playerStatsMap.get(playerId);
                if (optionalPlayer != null) {
                    JSONObject ps = resp.getJSONArray("statistics").getJSONObject(0);
                    savePlayerFixtureStats(fixtureId, date, opTeam0, optionalPlayer, opTeam1, ps, resp);
                }
            }

            JSONArray players1 = team1.getJSONArray("players");

            for (int i = 0; i < players1.length(); i++) {
                JSONObject resp = players1.getJSONObject(i);
                long playerId = resp.getJSONObject("player").getLong("id");
                PlayerStats optionalPlayer = playerStatsMap.get(playerId);
                if (optionalPlayer != null) {
                    JSONObject ps = resp.getJSONArray("statistics").getJSONObject(0);
                    savePlayerFixtureStats(fixtureId, date, opTeam1, optionalPlayer, opTeam0, ps, resp);
                }
            }
        }
    }

    private Map<Long, TeamStats> fetchTeamStatsMap(Long year) {
        List<TeamStats> teamStatsList = teamStatsRepo.findAllBySeason(year);
        return teamStatsList.stream().collect(Collectors.toMap(TeamStats::getTeamId, teamStats -> teamStats));
    }

    private Map<Long, PlayerStats> fetchPlayerStatsMap(Long year) {
        List<PlayerStats> playerStatsList = playersStatsRepo.findAllBySeason(year);
        return playerStatsList.stream().collect(Collectors.toMap(PlayerStats::getPlayerId, playerStats -> playerStats));
    }

    private void savePlayerFixtureStats(int fixtureId, LocalDateTime date, TeamStats opTeam0, PlayerStats optionalPlayer, TeamStats opTeam1, JSONObject ps, JSONObject resp) throws JSONException {
        FixturesStats fixturePlayer = new FixturesStats();

        fixturePlayer.setFixtureId(fixtureId);
        fixturePlayer.setFixtureDate(date);
        fixturePlayer.setTeamStats(opTeam0);
        fixturePlayer.setPlayerStats(optionalPlayer);
        fixturePlayer.setEnemyStats(opTeam1);

        String accuracy = ps.optJSONObject("passes").optString("accuracy", "0").replace("%", "");
        long accuracyPasses = 0L;
        if (!"null".equals(accuracy)) {
            accuracyPasses = Long.parseLong(accuracy);
        }

        fixturePlayer.setAccuracyPasses(accuracyPasses);
        fixturePlayer.setAsists(ps.optJSONObject("goals").optInt("assists"));
        fixturePlayer.setBlocks(ps.optJSONObject("tackles").optLong("blocks"));
        fixturePlayer.setDribbles(ps.optJSONObject("dribbles").optLong("attempts"));
        fixturePlayer.setDribblesWon(ps.optJSONObject("dribbles").optLong("success"));
        fixturePlayer.setDuels(ps.optJSONObject("duels").optLong("total"));
        fixturePlayer.setDuelsWon(ps.optJSONObject("duels").optLong("won"));
        fixturePlayer.setFoulsCommited(ps.optJSONObject("fouls").optLong("committed"));
        fixturePlayer.setFoulsDrawn(ps.optJSONObject("fouls").optLong("drawn"));
        fixturePlayer.setGoals(ps.optJSONObject("goals").optInt("total"));
        fixturePlayer.setGoalsConceded(ps.optJSONObject("goals").optInt("conceded"));
        fixturePlayer.setInterceptions(ps.optJSONObject("tackles").optLong("interceptions"));
        fixturePlayer.setKeyPasses(ps.optJSONObject("passes").optInt("key"));
        fixturePlayer.setMinutes(ps.optJSONObject("games").optInt("minutes"));
        fixturePlayer.setName(resp.optJSONObject("player").optString("name"));
        fixturePlayer.setOffside(ps.optInt("offsides"));
        fixturePlayer.setPasses(ps.optJSONObject("passes").optInt("total"));
        fixturePlayer.setPenaltyCommited(ps.optJSONObject("penalty").optInt("commited"));
        fixturePlayer.setPenaltySaves(ps.optJSONObject("penalty").optInt("saved"));
        fixturePlayer.setPenaltyWon(ps.optJSONObject("penalty").optInt("won"));
        fixturePlayer.setPenaltyMissed(ps.optJSONObject("penalty").optInt("missed"));
        fixturePlayer.setPenaltyScored(ps.optJSONObject("penalty").optInt("scored"));
        fixturePlayer.setPosition(ps.optJSONObject("games").optString("position"));
        double rating = ps.optJSONObject("games").optDouble("rating", 0.0);
        fixturePlayer.setRating(rating);
        fixturePlayer.setRedCards(ps.optJSONObject("cards").optInt("red"));
        fixturePlayer.setShots(ps.optJSONObject("shots").optInt("total"));
        fixturePlayer.setShotsOnGoal(ps.optJSONObject("shots").optInt("on"));
        fixturePlayer.setTotalTackles(ps.optJSONObject("tackles").optInt("total"));
        fixturePlayer.setYellowCards(ps.optJSONObject("cards").optInt("yellow"));

        fixtureRepository.save(fixturePlayer);
    }

    public JSONArray getFixtureStatsFromAPI(long fixtureId) throws IOException, InterruptedException, JSONException {
        JSONArray responseArray = null;
        int attempts = 0;

        while (attempts < apiKeyManager.getApiKeysLength()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/fixtures/players?fixture=" + fixtureId))
                    .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 429) {
                apiKeyManager.switchToNextApiKey();
                attempts++;
            } else if (response.statusCode() == 200) {
                JSONObject fixturePlayers = new JSONObject(response.body());
                if (fixturePlayers.has("response")) {
                    responseArray = fixturePlayers.getJSONArray("response");
                    break;
                } else {
                    throw new IOException("Brak spotkania o takim id");
                }
            } else {
                throw new IOException("Unexpected response status: " + response.statusCode());
            }
        }
        if (responseArray == null) {
            throw new IOException("Failed to retrieve data from API after trying all API keys.");
        }
        return responseArray;
    }

    public void updateMatch(Long id) {
        // TODO: Implementacja aktualizacji spotkania
    }

    public void createMatch(Long id) {
        // TODO: Implementacja tworzenia spotkania
    }

    public void getFixturesByTeam(long teamId, long season) {
        // TODO: Pobranie jednego spotkania
    }

    public void saveFixture(FixturesStats fixture) {
        // TODO: Dodanie jednego spotkania
    }

    public List<PlayerStats> getFixturesForTeamAndSeason(long teamId, long season, LocalDateTime startDate, LocalDateTime endDate) {
        Optional<TeamStats> optionalTeam = teamStatsRepo.findTeamStatsByTeamIdAndSeason(teamId, season);
        List<PlayerStats> aggregatedPlayersStats = new ArrayList<>();

        if (optionalTeam.isPresent()) {
            List<FixturesStats> fixtures = fixtureRepository.findByTeamStatsAndFixtureDateBetween(optionalTeam.get(), startDate, endDate);

            Map<Long, List<FixturesStats>> groupedByPlayer = fixtures.stream()
                    .collect(Collectors.groupingBy(f -> f.getPlayerStats().getPlayerId()));

            groupedByPlayer.forEach((playerId, stats) -> {
                Optional<PlayerStats> aggregatedPlayerStats = playersStatsRepo.getPlayerStatsByPlayerIdAndSeason(playerId, season);
                if (aggregatedPlayerStats.isPresent()) {
                    PlayerStats aggPS = aggregatedPlayerStats.get();
                    aggPS.setMinuty(stats.stream().mapToDouble(FixturesStats::getMinutes).sum());
                    aggPS.setStrzaly(stats.stream().mapToDouble(FixturesStats::getShots).sum());
                    aggPS.setStrzalyCelne(stats.stream().mapToDouble(FixturesStats::getShotsOnGoal).sum());
                    aggPS.setGole(stats.stream().mapToDouble(FixturesStats::getGoals).sum());
                    aggPS.setPodania(stats.stream().mapToDouble(FixturesStats::getPasses).sum());
                    aggPS.setPodaniaKluczowe(stats.stream().mapToDouble(FixturesStats::getKeyPasses).sum());
                    aggPS.setAsysty(stats.stream().mapToDouble(FixturesStats::getAsists).sum());
                    aggPS.setPojedynki(stats.stream().mapToDouble(FixturesStats::getDuels).sum());
                    aggPS.setPojedynkiWygrane(stats.stream().mapToDouble(FixturesStats::getDuelsWon).sum());
                    aggPS.setProbyPrzechwytu(stats.stream().mapToDouble(FixturesStats::getTotalTackles).sum());
                    aggPS.setPrzechwytyUdane(stats.stream().mapToDouble(FixturesStats::getInterceptions).sum());
                    aggPS.setDryblingi(stats.stream().mapToDouble(FixturesStats::getDribbles).sum());
                    aggPS.setDryblingiWygrane(stats.stream().mapToDouble(FixturesStats::getDribblesWon).sum());
                    aggPS.setFauleNaZawodniku(stats.stream().mapToDouble(FixturesStats::getFoulsDrawn).sum());
                    aggPS.setFaulePopelnione(stats.stream().mapToDouble(FixturesStats::getFoulsCommited).sum());
                    aggPS.setKartkiCzerwone(stats.stream().mapToDouble(FixturesStats::getRedCards).sum());
                    aggPS.setKartkiZolte(stats.stream().mapToDouble(FixturesStats::getYellowCards).sum());
                    aggregatedPlayersStats.add(aggPS);
                }
            });
        }
        return aggregatedPlayersStats;
    }
}
