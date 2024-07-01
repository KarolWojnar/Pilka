package com.Football.football.Services;

import com.Football.football.ApiKeyManager;
import com.Football.football.Repositories.*;
import com.Football.football.Tables.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FixturesService {
    private final FixturesStatsRepo fixtureRepository;
    private final ApiKeyManager apiKeyManager;
    private final TeamStatsRepo teamStatsRepo;
    private final TeamStatsService teamStatsService;
    private final PlayersStatsRepo playersStatsRepo;
    private final FixtureTeamsStatsRepository fixtureTeamsStatsRepository;
    private final PlayerStatsService playerStatsService;
    private final FixturesTeamGroupRepo fixturesTeamGroupRepo;
    private final FixtureRatingRepo fixtureRatingRepo;
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
        if (fixturePlayers.length() == 0) {
            return;
        }
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

    public void sumFixturesByTeam() {
        List<Integer> fixturesId = fixtureRepository.getAllFixturesIDs();
        System.out.println(fixturesId.size());
        for (int fID : fixturesId) {
            findFixtureAndSaveByTeam(fID);
        }
    }

    private void findFixtureAndSaveByTeam(int fID) {
        List<FixturesStats> fixture = fixtureRepository.findByFixtureId(fID);
        Long teamAId = null;
        Long teamBId = null;
        for (FixturesStats x : fixture) {
            if (teamAId == null) {
                teamAId = x.getTeamStats().getId();
            } else if (teamBId == null && !Objects.equals(x.getTeamStats().getId(), teamAId)) {
                teamBId = x.getTeamStats().getId();
                break;
            }
        }
        Long finalTeamAId = teamAId;
        List<FixturesStats> teamA = fixture.stream()
                .filter(fs -> Objects.equals(fs.getTeamStats().getId(), finalTeamAId))
                .toList();

        Long finalTeamBId = teamBId;
        List<FixturesStats> teamB = fixture.stream()
                .filter(fs -> Objects.equals(fs.getTeamStats().getId(), finalTeamBId))
                .toList();

        saveFixtures(teamA);
        saveFixtures(teamB);
    }

    public void saveFixtures(List<FixturesStats> fixture) {
        FixtureTeamsStats fts = new FixtureTeamsStats();

        fts.setFixtureDate(fixture.getFirst().getFixtureDate());
        fts.setTeamStats(fixture.getFirst().getTeamStats());
        fts.setEnemyStats(fixture.getFirst().getEnemyStats());
        fts.setFixtureId(fixture.getFirst().getFixtureId());

        double rating = 0, accuracyPasses = 0, interceptions = 0, blocks = 0,
            totalTrackles = 0, duels = 0, duelsWon = 0, dribbles = 0, dribblesWon = 0,
            foulsCommited = 0, foulsDrawn = 0, redCards = 0, yellowCards = 0, penaltyWon = 0, penaltyCommited = 0,
            penaltyScored = 0, penaltyMissed = 0, penaltySaves = 0,
            offisde = 0, shots = 0, shotsOnGoal = 0, goals = 0, goalsConceded = 0,
            asists = 0, passes = 0, keyPasses = 0, summary = 0;

        for (FixturesStats fs : fixture) {
            summary++;
            rating += fs.getRating();
            accuracyPasses += fs.getAccuracyPasses();
            blocks += fs.getBlocks(); totalTrackles += fs.getTotalTackles();
            duels += fs.getDuels(); duelsWon += fs.getDuelsWon();
            dribbles += fs.getDribbles(); dribblesWon += fs.getDribblesWon();
            foulsCommited += fs.getFoulsCommited(); foulsDrawn += fs.getFoulsDrawn();
            redCards += fs.getRedCards(); yellowCards += fs.getYellowCards();
            penaltyScored += fs.getPenaltyScored(); interceptions += fs.getInterceptions();
            penaltySaves += fs.getPenaltySaves(); penaltyWon += fs.getPenaltyWon();
            penaltyCommited += fs.getPenaltyCommited();
            penaltyMissed += fs.getPenaltyMissed(); offisde += fs.getOffside();
            shots += fs.getShots(); shotsOnGoal += fs.getShotsOnGoal();
            goals += fs.getGoals(); goalsConceded += fs.getGoalsConceded();
            asists += fs.getAsists(); passes += fs.getPasses();
            keyPasses += fs.getKeyPasses();
        }

        fts.setRating(rating / summary);
        fts.setAccuracyPasses(accuracyPasses / summary);
        fts.setInterceptions(interceptions / summary);
        fts.setBlocks(blocks / summary);
        fts.setTotalTackles(totalTrackles / summary);
        fts.setDuels(duels / summary);
        fts.setDuelsWon(duelsWon / summary);
        fts.setDribbles(dribbles / summary);
        fts.setDribblesWon(dribblesWon / summary);
        fts.setFoulsCommited(foulsCommited / summary);
        fts.setFoulsDrawn(foulsDrawn / summary);
        fts.setRedCards(redCards / summary);
        fts.setYellowCards(yellowCards / summary);
        fts.setPenaltyWon(penaltyWon / summary);
        fts.setPenaltyCommited(penaltyCommited / summary);
        fts.setPenaltyScored(penaltyScored / summary);
        fts.setPenaltyMissed(penaltyMissed / summary);
        fts.setPenaltySaves(penaltySaves / summary);
        fts.setOffside(offisde / summary);
        fts.setShots(shots / summary);
        fts.setShotsOnGoal(shotsOnGoal / summary);
        fts.setGoals(goals / summary);
        fts.setGoalsConceded(goalsConceded / summary);
        fts.setAsists(asists / summary);
        fts.setPasses(passes / summary);
        fts.setKeyPasses(keyPasses / summary);

        fixtureTeamsStatsRepository.save(fts);
    }

    public List<FixturesTeamGroup> groupAllTeams(Iterable<FixtureTeamsStats> allFixtures, boolean addToDb) {
        double sumPasses = 0, sumKeyPasses = 0, sumAccuratePasses = 0, sumDribbleWon = 0,
                sumDribbles = 0, sumShootsOnGoal = 0, sumOffsides = 0, sumTrackles = 0,
                sumShoots = 0, sumFoulsCommited = 0, sumRedCards = 0, sumYellowCards = 0,
                sumDuelsLoss = 0, sumInterpWon = 0, sumBlocks = 0,
                sumFoulsDrawn = 0, sumgoalsConceded = 0, sumDuelsWon = 0, sumGoals = 0,
                sumAsists = 0, sumRecords = 0;

        for (FixtureTeamsStats fixture : allFixtures) {
            sumRecords++;
            sumPasses += fixture.getPasses();
            sumAccuratePasses += fixture.getAccuracyPasses();
            sumKeyPasses += fixture.getKeyPasses();
            sumDribbleWon += fixture.getDribblesWon();
            sumDribbles += fixture.getDribbles();
            sumShootsOnGoal += fixture.getShotsOnGoal();
            sumShoots += fixture.getShots();
            sumFoulsCommited += fixture.getFoulsCommited();
            sumFoulsDrawn += fixture.getFoulsDrawn();
            sumRedCards += fixture.getRedCards();
            sumYellowCards += fixture.getYellowCards();
            sumDuelsLoss += (fixture.getDuels() - fixture.getDuelsWon());
            sumInterpWon += fixture.getInterceptions();
            sumDuelsWon += fixture.getDuelsWon();
            sumGoals += fixture.getGoals();
            sumAsists += fixture.getAsists();
            sumTrackles += fixture.getTotalTackles();
            sumBlocks += fixture.getBlocks();
            sumOffsides += fixture.getOffside();
            sumgoalsConceded += fixture.getGoalsConceded();
        }

        double[] normalizedSums  ={
            sumPasses /= sumRecords, // 0
            sumAccuratePasses /= sumRecords, // 1
            sumKeyPasses /= sumRecords, // 2
            sumDribbleWon /= sumRecords, // 3
            sumDribbles /= sumRecords, // 4
            sumShootsOnGoal /= sumRecords, // 5
            sumShoots /= sumRecords, // 6
            sumFoulsCommited /= sumRecords, // 7
            sumFoulsDrawn /= sumRecords, // 8
            sumRedCards /= sumRecords, // 9
            sumYellowCards /= sumRecords, // 10
            sumDuelsLoss /= sumRecords, // 11
            sumInterpWon /= sumRecords, // 12
            sumDuelsWon /= sumRecords, // 13
            sumGoals /= sumRecords, // 14
            sumAsists /= sumRecords, // 15
            sumTrackles /= sumRecords, // 16
            sumBlocks /= sumRecords, // 17
            sumOffsides /= sumRecords, // 18
            sumgoalsConceded /= sumRecords // 19
        };

        double[] weights = playerStatsService.calculateWeights(normalizedSums);

        return calculateStatsAndSave(weights, allFixtures, addToDb);

    }

    public List<FixturesTeamGroup> calculateStatsAndSave(double[] weights, Iterable<FixtureTeamsStats> allFixtures, boolean addToDB) {
        double  maxPasses = 0.01, maxKeyPasses = 0.01, maxAccuratePasses = 0.01, maxDribbleWon = 0.01,
                maxDribbles = 0.01, maxShootsOnGoal = 0.01, maxOffsides = 0.01, maxTrackles = 0.01,
                maxShoots = 0.01, maxFoulsCommited = 0.01, maxRedCards = 0.01, maxYellowCards = 0.01,
                maxDuelsLost = 0.01, maxInterpWon = 0.01, maxBlocks = 0.01,
                maxFoulsDrawn = 0.01, maxGoalsConceded = 0.01, maxDuelsWon = 0.01, maxGoals = 0.01,
                maxAsists = 0.01;

        int fixtureCount = 0;

        for (FixtureTeamsStats fixture : allFixtures) {
            fixtureCount++;
            maxAccuratePasses = Math.max(fixture.getAccuracyPasses(), maxAccuratePasses);
            maxKeyPasses = Math.max(fixture.getKeyPasses(), maxKeyPasses);
            maxPasses = Math.max(fixture.getPasses(), maxPasses);
            maxDribbleWon = Math.max(fixture.getDribblesWon(), maxDribbleWon);
            maxDribbles = Math.max(fixture.getDribbles(), maxDribbles);
            maxShootsOnGoal = Math.max(fixture.getShotsOnGoal(), maxShootsOnGoal);
            maxShoots = Math.max(fixture.getShots(), maxShoots);
            maxFoulsCommited = Math.max(fixture.getFoulsCommited(), maxFoulsCommited);
            maxFoulsDrawn = Math.max(fixture.getFoulsDrawn(), maxFoulsDrawn);
            maxRedCards = Math.max(fixture.getRedCards(), maxRedCards);
            maxYellowCards = Math.max(fixture.getYellowCards(), maxYellowCards);
            maxDuelsLost = (Math.max(fixture.getDuels() - fixture.getDuelsWon(), maxDuelsLost));
            maxInterpWon = Math.max(fixture.getInterceptions(), maxInterpWon);
            maxDuelsWon = Math.max(fixture.getDuelsWon(), maxDuelsWon);
            maxGoals = Math.max(fixture.getGoals(), maxGoals);
            maxAsists = Math.max(fixture.getAsists(), maxAsists);
            maxTrackles = Math.max(fixture.getTotalTackles(), maxTrackles);
            maxBlocks = Math.max(fixture.getBlocks(), maxBlocks);
            maxOffsides = Math.max(fixture.getOffside(), maxOffsides);
            maxGoalsConceded = Math.max(fixture.getGoalsConceded(), maxGoalsConceded);
        }

        List<FixturesTeamGroup> teamsToReturn = new ArrayList<>();

        for (FixtureTeamsStats fixture : allFixtures) {
            FixturesTeamGroup x = new FixturesTeamGroup();

            x.setFixtureDate(fixture.getFixtureDate());
            x.setFixtureTeamStats(fixture);

            x.setTeamStats(fixture.getTeamStats());
            x.setTeamName(fixture.getTeamStats().getTeamName());
            x.setSeason(fixture.getTeamStats().getSeason());

            double normPasses = fixture.getPasses() / maxPasses;
            double normAccPasses = fixture.getAccuracyPasses() / maxAccuratePasses;
            double normKeyPasses = fixture.getKeyPasses() / maxKeyPasses;
            double normAsists = fixture.getAsists() / maxAsists;
            double sumPIK = ((normAccPasses * weights[1]) + (normPasses * weights[0]) +
                    (normKeyPasses * weights[2]) + (normAsists * weights[15]))
                     / (weights[1] + weights[2] + weights[15] + weights[0]);
            x.setPodaniaKreatywnosc(sumPIK);

            double normDribblesWon = fixture.getDribblesWon() / maxDribbleWon;
            double normShootsOnGoal = fixture.getShotsOnGoal() / maxShootsOnGoal;
            double normGoals = fixture.getGoals() / maxGoals;
            double normOffsides = fixture.getOffside() / maxOffsides;
            double sumDIS = ((normDribblesWon * weights[3]) + (normShootsOnGoal * weights[5]) +
                    (normGoals * weights[14]) - (normOffsides * weights[18]))
                    / (weights[3] + weights[5] + weights[14] - weights[18]);
            x.setDryblingSkutecznosc(sumDIS);

            double normFoulsCommited = fixture.getFoulsCommited() / maxFoulsCommited;
            double normRedCards = fixture.getRedCards() / maxRedCards;
            double normYellowCards = fixture.getYellowCards() / maxYellowCards;
            double normDuelsLost = (fixture.getDuels() - fixture.getDuelsWon()) / maxDuelsLost;
            double normFailTrackles = (fixture.getTotalTackles() - fixture.getInterceptions())
                    / (maxTrackles - maxInterpWon);
            double normGoalsConceded = fixture.getGoalsConceded() / maxGoalsConceded;
            double sumFII = ((normFoulsCommited * weights[7]) + (normRedCards * weights[9]) +
                    (normYellowCards * weights[10]) + (normDuelsLost * weights[11])
                    + (normFailTrackles * (weights[16] - weights[12])) + (normGoalsConceded * weights[19]))
                    / (weights[3] + weights[5] + weights[14] + weights[18]
                    + (weights[16] - weights[12]) + weights[19]);
            x.setFizycznoscInterakcje(sumFII);

            double normDuelsWon = fixture.getDuelsWon() / maxDuelsWon;
            double normInterpWon = fixture.getInterceptions() / maxInterpWon;
            double normFoulsDrawn = fixture.getFoulsDrawn() / maxFoulsDrawn;
            double normBlocks = fixture.getBlocks() / maxBlocks;
            double sumOIK = ((normDuelsWon * weights[13]) + (normInterpWon * weights[12]) +
                    (normFoulsDrawn * weights[8]) + (normBlocks * weights[17]))
                    / (weights[13] + weights[12] + weights[8] + weights[17]);
            x.setObronaKotrolaPrzeciwnika(sumOIK);
            if (addToDB) {
                fixturesTeamGroupRepo.save(x);
            } else {
                teamsToReturn.add(x);
            }
        }
        return teamsToReturn;
    }

    public List<FixturesTeamRating> getRatings(List<FixturesTeamGroup> all, boolean saveToDB) {
        return calculateStatsAndSaveRating(all, saveToDB);
    }

    private List<FixturesTeamRating> calculateStatsAndSaveRating(Iterable<FixturesTeamGroup> allTeams, boolean saveToDB) {

        double[] sums = StreamSupport.stream(allTeams.spliterator(), false)
                .reduce(new double[5], (acc, team) -> {
                    acc[0] += team.getDryblingSkutecznosc();
                    acc[1] += team.getPodaniaKreatywnosc();
                    acc[2] += team.getObronaKotrolaPrzeciwnika();
                    acc[3] += team.getFizycznoscInterakcje();
                    acc[4]++;
                    return acc;
                }, (a, b) -> {
                    for (int i = 0; i < a.length; i++) {
                        a[i] += b[i];
                    }
                    return a;
                });

        double sumDiS = sums[0] / sums[4];
        double sumPiK = sums[1] / sums[4];
        double sumOiKK = sums[2] / sums[4];
        double sumFiI = sums[3] / sums[4];

        double maxStat = Math.max(Math.max(sumDiS, sumPiK), Math.max(sumOiKK, Math.abs(sumFiI)));
        sumDiS /= maxStat;
        sumPiK /= maxStat;
        sumOiKK /= maxStat;
        sumFiI /= maxStat;

        sumFiI = Math.abs(sumFiI);
        double[] weights = {sumDiS, sumPiK, sumOiKK, sumFiI};

        List<FixturesTeamRating> teamsToReturn = new ArrayList<>();

        allTeams.forEach(team -> {
            FixturesTeamRating fTeam = getAvgofFixture(team, weights);
            if (saveToDB) {
                fixtureRatingRepo.save(fTeam);
            } else {
                teamsToReturn.add(fTeam);
            }
        });
        return teamsToReturn;
    }

    private FixturesTeamRating getAvgofFixture(FixturesTeamGroup team, double[] weights) {
        double summaryWeight = 0.0;
        summaryWeight += (team.getDryblingSkutecznosc() * weights[0]);
        summaryWeight += (team.getPodaniaKreatywnosc() * weights[1]);
        summaryWeight += (team.getObronaKotrolaPrzeciwnika() * weights[2]);
        summaryWeight += (team.getFizycznoscInterakcje() * weights[3]);

        double sumWeights = 0;
        for (double weight : weights) {
            sumWeights += weight;
        }

        FixturesTeamRating fTeam = new FixturesTeamRating();
        fTeam.setTeamStats(team.getTeamStats());
        fTeam.setRaiting(summaryWeight / sumWeights);
        fTeam.setCzyUwzglednionePozycje(false);
        fTeam.setFixtureDate(team.getFixtureDate());
        fTeam.setFixtureTeamStats(team.getFixtureTeamStats());

        return fTeam;
    }

    public void getRatingsByDateAndTeamId(String teamName, LocalDate startDate, LocalDate endDate, String rounding, Model model) throws JsonProcessingException {
        List<FixtureTeamsStats> tfS = fixtureTeamsStatsRepository.findAllByFixtureDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
        Optional<Long> teamOp = fixtureTeamsStatsRepository.findIdTeam(teamName);
        if (teamOp.isPresent()) {
            long teamId = teamOp.get();
            List<FixturesTeamGroup> ftg = groupAllTeams(tfS, false);
            List<FixturesTeamRating> ftr = getRatings(ftg, false);
            List<FixturesTeamRating> myTeam = ftr.stream()
                    .filter(team -> team.getTeamStats().getTeamId() == teamId)
                    .toList();

            List<LocalDate> periodStartDates = new ArrayList<>();
            LocalDate periodStartDate = startDate;

            if ("week".equals(rounding)) {
                while (!periodStartDate.isAfter(endDate)) {
                    periodStartDates.add(periodStartDate);
                    periodStartDate = periodStartDate.plusWeeks(1);
                }
            } else if ("month".equals(rounding)) {
                while (!periodStartDate.isAfter(endDate)) {
                    periodStartDates.add(periodStartDate);
                    periodStartDate = periodStartDate.plusMonths(1);
                }
            }
            List<Double> avgRatings = new ArrayList<>(Collections.nCopies(periodStartDates.size(), 0.0));
            List<Double> myTeamRatings = new ArrayList<>(Collections.nCopies(periodStartDates.size(), 0.0));

            for (int i = 0; i < periodStartDates.size(); i++) {
                periodStartDate = periodStartDates.get(i);
                LocalDate periodEndDate = (i == periodStartDates.size() - 1) ? endDate : periodStartDates.get(i + 1).minusDays(1);

                double periodAverage = calculatePeriodAverage(myTeam, periodStartDate, periodEndDate);
                double periodAverage2 = calculatePeriodAverage(ftr, periodStartDate, periodEndDate);
                avgRatings.set(i, periodAverage2);

                if (periodAverage != 0.0) {
                    myTeamRatings.set(i, periodAverage);
                }
            }
            List<String> dates = periodStartDates.stream().map(LocalDate::toString).collect(Collectors.toList());
            dates.removeLast();
            myTeamRatings.removeLast();
            avgRatings.removeLast();

            ObjectMapper objectMapper = new ObjectMapper();
            String datesJson = objectMapper.writeValueAsString(dates);

            model.addAttribute("datesJson", datesJson);
            model.addAttribute("myTeamRatings", myTeamRatings);
            model.addAttribute("averageRatings", avgRatings);
            if (!myTeam.isEmpty()) {
                model.addAttribute("teamName", myTeam.get(0).getTeamStats().getTeamName());
            } else {
                model.addAttribute("teamName", "Unknown Team");
            }
        } else {
            model.addAttribute("error", "Nie ma takiej druzyny");
        }
    }

    protected double calculatePeriodAverage(List<FixturesTeamRating> teamRatings, LocalDate periodStartDate, LocalDate periodEndDate) {
        List<FixturesTeamRating> matchesInPeriod = teamRatings.stream()
                .filter(team -> !team.getFixtureDate().toLocalDate().isBefore(periodStartDate) &&
                        !team.getFixtureDate().toLocalDate().isAfter(periodEndDate))
                .toList();
        if (matchesInPeriod.isEmpty()) {
            return 0.0;
        } else {
            return matchesInPeriod.stream().mapToDouble(FixturesTeamRating::getRaiting).average().orElse(0.0);
        }
    }
}
