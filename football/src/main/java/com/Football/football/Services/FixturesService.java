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
import java.util.Optional;

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
                    for (int i = 0; i < fixtures.length(); i++) {
                        JSONObject fixture = fixtures.getJSONObject(i);
                        saveNewFixture(fixture, year);
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

    private void saveNewFixture(JSONObject fixture, Long year) throws JSONException, IOException, InterruptedException {
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
        Optional<TeamStats> opTeam0 = teamStatsRepo.findTeamStatsByTeamIdAndSeason(team0Id, year);
        Optional<TeamStats> opTeam1 = teamStatsRepo.findTeamStatsByTeamIdAndSeason(team1Id, year);
        if (opTeam0.isPresent() && opTeam1.isPresent()) {
            JSONArray players0 = team0.getJSONArray("players");

            for (int i = 0; i < players0.length(); i++) {
                JSONObject resp = players0.getJSONObject(i);
                long playerId = resp.getJSONObject("player").getLong("id");
                Optional<PlayerStats> optionalPlayer = playersStatsRepo
                        .getPlayerStatsByPlayerIdAndSeason(playerId, year);
                if (optionalPlayer.isPresent()) {
                    JSONObject ps = resp.getJSONArray("statistics").getJSONObject(0);
                    savePlayerFixtureStats(fixtureId, date, opTeam0.get(), optionalPlayer.get(), opTeam1.get(), ps, resp);
                }
            }

            JSONArray players1 = team1.getJSONArray("players");

            for (int i = 0; i < players1.length(); i++) {
                JSONObject resp = players1.getJSONObject(i);
                long playerId = resp.getJSONObject("player").getLong("id");
                Optional<PlayerStats> optionalPlayer = playersStatsRepo
                        .getPlayerStatsByPlayerIdAndSeason(playerId, year);
                if (optionalPlayer.isPresent()) {
                    JSONObject ps = resp.getJSONArray("statistics").getJSONObject(0);
                    savePlayerFixtureStats(fixtureId, date, opTeam1.get(), optionalPlayer.get(), opTeam0.get(), ps, resp);
                }

            }
        }

    }

    private void savePlayerFixtureStats(int fixtureId, LocalDateTime date, TeamStats opTeam0, PlayerStats optionalPlayer, TeamStats opTeam1, JSONObject ps, JSONObject resp) throws JSONException {
        FixturesStats fixturePlayer = new FixturesStats();

        fixturePlayer.setFixtureId(fixtureId);
        fixturePlayer.setFixtureDate(date);
        fixturePlayer.setTeamStats(opTeam0);
        fixturePlayer.setPlayerStats(optionalPlayer);
        fixturePlayer.setEnemyStats(opTeam1);
        String accuracy = ps.getJSONObject("passes").getString("accuracy");
        long accuracyPasses = 0L;
        if (accuracy != null && !accuracy.equals("null")) {
            accuracyPasses = Long.parseLong(accuracy.replace("%", ""));
        }
        fixturePlayer.setAccuracyPasses(accuracyPasses);
        fixturePlayer.setAsists(ps.getJSONObject("goals").optInt("assists"));
        fixturePlayer.setBlocks(ps.getJSONObject("tackles").optLong("blocks"));
        fixturePlayer.setDribbles(ps.getJSONObject("dribbles").optLong("attempts"));
        fixturePlayer.setDribblesWon(ps.getJSONObject("dribbles").optLong("success"));
        fixturePlayer.setDuels(ps.getJSONObject("duels").optLong("total"));
        fixturePlayer.setDuelsWon(ps.getJSONObject("duels").optLong("won"));
        fixturePlayer.setFoulsCommited(ps.getJSONObject("fouls").optLong("committed"));
        fixturePlayer.setFoulsDrawn(ps.getJSONObject("fouls").optLong("drawn"));
        fixturePlayer.setGoals(ps.getJSONObject("goals").optInt("total"));
        fixturePlayer.setGoalsConceded(ps.getJSONObject("goals").optInt("conceded"));
        fixturePlayer.setInterceptions(ps.getJSONObject("tackles").optLong("interceptions"));
        fixturePlayer.setKeyPasses(ps.getJSONObject("passes").optInt("key"));
        fixturePlayer.setMinutes(ps.getJSONObject("games").optInt("minutes"));
        fixturePlayer.setName(resp.getJSONObject("player").optString("name"));
        fixturePlayer.setOffside(ps.optInt("offsides"));
        fixturePlayer.setPasses(ps.getJSONObject("passes").optInt("total"));
        fixturePlayer.setPenaltyCommited(ps.getJSONObject("penalty").optInt("commited"));
        fixturePlayer.setPenaltySaves(ps.getJSONObject("penalty").optInt("saved"));
        fixturePlayer.setPenaltyWon(ps.getJSONObject("penalty").optInt("won"));
        fixturePlayer.setPenaltyMissed(ps.getJSONObject("penalty").optInt("missed"));
        fixturePlayer.setPenaltyScored(ps.getJSONObject("penalty").optInt("scored"));
        fixturePlayer.setPosition(ps.getJSONObject("games").optString("position"));
        Double rating = ps.getJSONObject("games").optDouble("rating");
        if (!Double.isNaN(rating)) {
            fixturePlayer.setRating(rating);
        } else {
            fixturePlayer.setRating(0);
        }
        fixturePlayer.setRedCards(ps.getJSONObject("cards").optInt("red"));
        fixturePlayer.setShots(ps.getJSONObject("shots").optInt("total"));
        fixturePlayer.setShotsOnGoal(ps.getJSONObject("shots").optInt("on"));
        fixturePlayer.setTotalTackles(ps.getJSONObject("tackles").optInt("total"));
        fixturePlayer.setYellowCards(ps.getJSONObject("cards").optInt("yellow"));
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

            if (response.statusCode() == 429) { // 429 Too Many Requests
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

    public void getFixture(int teamId) {
        // TODO: Pobranie jednego spotkania
    }

    public void saveFixture(FixturesStats fixture) {
        // TODO: Dodanie jednego spotkania
    }

    public void fixPlayers() {
        Iterable<PlayerStats> allFrom2022 = playersStatsRepo.findPlayerStatsBySeason(2022);
        for (PlayerStats player : allFrom2022) {
            long teamId = player.getTeamStats().getTeamId();
            Optional<TeamStats> team = teamStatsRepo.findTeamStatsByTeamIdAndSeason(teamId, 2022);
            if (team.isPresent()) {
                player.setTeamStats(team.get());
                playersStatsRepo.save(player);
            }

        }
    }
}
