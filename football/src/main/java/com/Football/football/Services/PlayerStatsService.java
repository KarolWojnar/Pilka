package com.Football.football.Services;

import com.Football.football.ApiKeyManager;
import com.Football.football.Repositories.*;
import com.Football.football.Tables.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PlayerStatsService {

    private final PlayersStatsRepo statystykiZawodnikaRepository;
    private final TeamStatsRepo teamStatsRepository;
    private final PlayerStatsGroupRepo pogrupowaneRepository;
    private final TeamGroupAvgRepo sredniaDruzynyRepository;
    private final PlayersStatsRepo playersStatsRepo;
    private final PlayerStatsGroupWPosRepo pogrupowanePozycjamiRepository;
    private final TeamGroupAvgWPosRepo srDruzynyPozycjeRepository;
    private final ApiKeyManager apiKeyManager;

    public String updatePlayersLeague(Long year, Long leagueId) throws JSONException, IOException, InterruptedException {
        int attempts = 0;
        while (attempts < apiKeyManager.getApiKeysLength()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/teams?league=" + leagueId + "&season=" + year))
                    .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 429) {
                apiKeyManager.switchToNextApiKey();
                attempts++;
            } else if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jResponse = new JSONObject(responseBody);
                StringBuilder sb = new StringBuilder();
                if (jResponse.has("response")) {
                    JSONArray teams = jResponse.getJSONArray("response");
                    for (int i = 0; i < teams.length(); i++) {
                        JSONObject team = teams.getJSONObject(i);
                        long teamId = team.getJSONObject("team").getLong("id");
                        sb.append(updatePlayerStats(teamId, year, leagueId));
                    }
                }
                return sb.toString();
            } else {
                throw new IOException("Unexpected response status: " + response.statusCode());
            }
        }
        throw new IOException("Failed to retrieve data from API after trying all API keys.");
    }

    public String updatePlayerStats(Long teamId, Long season, Long leagueId) throws IOException, InterruptedException, JSONException {
        StringBuilder all = new StringBuilder();
        Optional<TeamStats> opTeam = teamStatsRepository.findTeamStatsByTeamIdAndSeason(teamId, season);
        if (opTeam.isEmpty()) {
            throw new IllegalArgumentException("Team with ID " + teamId + " not found");
        }
        TeamStats teamStats = opTeam.get();

        int page = 1;
        boolean hasNextPage = true;

        while (hasNextPage) {
            int attempts = 0;
            while (attempts < apiKeyManager.getApiKeysLength()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api-football-beta.p.rapidapi.com/players?season=" + season + "&league=" + leagueId + "&team=" + teamId + "&page=" + page))
                        .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                        .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 429) {
                    apiKeyManager.switchToNextApiKey();
                    attempts++;
                } else if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    all.append(responseBody);
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.has("response")) {
                        JSONArray statsArray = jsonResponse.getJSONArray("response");
                        for (int i = 0; i < statsArray.length(); i++) {
                            JSONObject playerStats = statsArray.getJSONObject(i);
                            savePlayer(season, playerStats, teamStats);
                        }

                        hasNextPage = (jsonResponse.has("paging"))
                                && (jsonResponse.getJSONObject("paging").getInt("current") < jsonResponse.getJSONObject("paging").getInt("total"));
                        if (hasNextPage) page++;
                    }
                    break;
                } else {
                    throw new IOException("Unexpected response status: " + response.statusCode());
                }
            }
            if (attempts == apiKeyManager.getApiKeysLength()) {
                throw new IOException("Failed to retrieve data from API after trying all API keys.");
            }
        }
        return all.toString();
    }

    private void savePlayer(Long season, JSONObject playerStats, TeamStats teamStats) throws JSONException {
        int isActive = playerStats.getJSONArray("statistics").getJSONObject(0).getJSONObject("games").optInt("minutes", 0);
        if (isActive == 0) return;

        Long playerId = playerStats.getJSONObject("player").getLong("id");
        Optional<PlayerStats> optional = statystykiZawodnikaRepository.getPlayerStatsByPlayerIdAndTeamStatsAndSeason(playerId, teamStats, season);

        if (optional.isPresent()) {
            PlayerStats toUpdate = optional.get();
            statystykiZawodnikaRepository.delete(toUpdate);
        }

        Optional<PlayerStats> moreMinuteOptionalPlayer = statystykiZawodnikaRepository.getPlayerStatsByPlayerIdAndSeason(playerId, season);
        if (moreMinuteOptionalPlayer.isPresent()) {
            PlayerStats playerX = moreMinuteOptionalPlayer.get();
            if (playerX.getMinuty() < (float) isActive) {
                statystykiZawodnikaRepository.delete(playerX);
            } else {
                return;
            }
        }

        PlayerStats player = new PlayerStats();
        player.setTeamStats(teamStats);
        player.setSeason(season);
        player.setPlayerId(playerId);
        player.setImie(playerStats.getJSONObject("player").getString("firstname"));
        player.setNazwisko(playerStats.getJSONObject("player").getString("lastname"));
        player.setWiek(playerStats.getJSONObject("player").optDouble("age"));
        player.setWzrost(Double.parseDouble(playerStats.getJSONObject("player").optString("height", "10 cm")
                .replaceAll(" cm", "").replaceAll("\n", "0").replaceAll("null", "0")));
        player.setWaga(Double.parseDouble(playerStats.getJSONObject("player").optString("weight", "10 kg")
                .replaceAll(" kg", "").replaceAll("\n", "0").replaceAll("null", "0")));
        player.setKraj(playerStats.getJSONObject("player").optString("nationality"));
        player.setCzyKontuzjowany((playerStats.getJSONObject("player").getString("injured")).equals("true"));

        JSONArray statisticsArray = playerStats.getJSONArray("statistics");
        if (statisticsArray.length() > 0) {
            JSONObject statistics = statisticsArray.getJSONObject(0);

            JSONObject games = statistics.getJSONObject("games");

            player.setWystepy(games.optDouble("appearences", 0));
            player.setMinuty(games.optDouble("minutes", 0));
            player.setPozycja(games.getString("position"));
            player.setRating(games.optDouble("rating", 0));

            if ((player.getMinuty() == 0) || (player.getRating() == 0)) {
                statystykiZawodnikaRepository.delete(player);
                return;
            }

            JSONObject shots = statistics.getJSONObject("shots");
            player.setStrzaly(shots.optDouble("total", 0));
            player.setStrzalyCelne(shots.optDouble("on", 0));
            JSONObject goals = statistics.getJSONObject("goals");
            player.setGole(goals.optDouble("total", 0));
            player.setAsysty(goals.optDouble("assists", 0));
            JSONObject passes = statistics.getJSONObject("passes");
            player.setPodania(passes.optDouble("total", 0));
            player.setDokladnoscPodan(passes.optDouble("accuracy", 0));
            player.setPodaniaKluczowe(passes.optDouble("key", 0));
            JSONObject duels = statistics.getJSONObject("duels");
            player.setPojedynki(duels.optDouble("total", 0));
            player.setPojedynkiWygrane(duels.optDouble("won", 0));
            JSONObject dribbles = statistics.getJSONObject("dribbles");
            player.setDryblingi(dribbles.optDouble("attempts", 0));
            player.setDryblingiWygrane(dribbles.optDouble("success", 0));
            JSONObject fouls = statistics.getJSONObject("fouls");
            player.setFaulePopelnione(fouls.optDouble("committed", 0));
            player.setFauleNaZawodniku(fouls.optDouble("drawn", 0));
            JSONObject cards = statistics.getJSONObject("cards");
            player.setKartkiZolte(cards.optDouble("yellow", 0));
            player.setKartkiCzerwone(cards.optDouble("red", 0));
            JSONObject tackles = statistics.getJSONObject("tackles");
            player.setProbyPrzechwytu(tackles.optDouble("total", 0));
            player.setPrzechwytyUdane(tackles.optDouble("interceptions", 0));
        }
        statystykiZawodnikaRepository.save(player);
    }

    public void getAvgOfAllPlayers(Iterable<PlayerStats> players, boolean isPositions, String whereIsPlaying) {
        Iterable<TeamStats> teams = teamStatsRepository.findAll();

        double fixturesCount = StreamSupport.stream(teams.spliterator(), false)
                .mapToDouble(TeamStats::getSumaSpotkan)
                .sum();

        double sumPasses = 0, sumKeyPasses = 0, sumDribbleWon = 0, sumShootsOnGoal = 0, sumFoulsCommited = 0 ,
                sumRedCards = 0, sumYellowCards = 0, sumDuelsLoss = 0, sumInterpWon = 0, sumFoulsDrawn = 0,
                sumDuelsWon = 0, sumGoals = 0, sumAsists = 0;

        for (PlayerStats player : players) {
            sumPasses += player.getPodania();
            sumKeyPasses += player.getPodaniaKluczowe();
            sumDribbleWon += player.getDryblingiWygrane();
            sumShootsOnGoal += player.getStrzalyCelne();
            sumFoulsCommited += player.getFaulePopelnione();
            sumRedCards += player.getKartkiCzerwone();
            sumYellowCards += player.getKartkiZolte();
            sumDuelsLoss += player.getPojedynki() - player.getPojedynkiWygrane();
            sumInterpWon += player.getPrzechwytyUdane();
            sumFoulsDrawn += player.getFauleNaZawodniku();
            sumDuelsWon += player.getPojedynkiWygrane();
            sumGoals += player.getGole();
            sumAsists += player.getAsysty();
        }

        double[] normalizedSums = {
                normalize(sumPasses, fixturesCount),
                normalize(sumKeyPasses, fixturesCount),
                normalize(sumDribbleWon, fixturesCount),
                normalize(sumShootsOnGoal, fixturesCount),
                normalize(sumFoulsCommited, fixturesCount),
                normalize(sumRedCards, fixturesCount),
                normalize(sumYellowCards, fixturesCount),
                normalize(sumDuelsLoss, fixturesCount),
                normalize(sumInterpWon, fixturesCount),
                normalize(sumFoulsDrawn, fixturesCount),
                normalize(sumDuelsWon, fixturesCount),
                normalize(sumGoals, fixturesCount),
                normalize(sumAsists, fixturesCount)
        };

        double[] weights = calculateWeights(normalizedSums);

        if (!whereIsPlaying.equals("GK")) {
            calculateWeightAndSave(players, weights, isPositions);
        } else {
            List<String> positions = new ArrayList<>();
            positions.add("Goalkeeper");
            positions.add("Defender");
            Iterable<PlayerStats> goalkeepers = statystykiZawodnikaRepository.findPlayerStatsByPozycjaIn(positions);
            getAvgForGoalkeepers(goalkeepers, weights);
        }
    }

    public double normalize(double value, double max) {
        return value / max;
    }

    public double[] calculateWeights(double[] normalizedSums) {
        double[] weights = new double[normalizedSums.length + 1];
        for (int i = 0; i < normalizedSums.length; i++) {
            weights[i] = 90 / ((normalizedSums[i]));
        }

        return weights;
    }


    private <T extends PlayersStatsGroupBase> void processPlayerStats(Iterable<PlayerStats> players, double[] weights,
                                                                      Function<PlayerStats, Optional<T>> getPlayerStatsGroupFunction,
                                                                      Consumer<T> deletePlayerStatsGroupFunction,
                                                                      Consumer<T> savePlayerStatsGroupFunction,
                                                                      Supplier<T> createPlayerStatsGroupFunction) {
        double maxPasses = 1, maxKeyPasses = 1, maxDribbleWon = 1, maxShootsOnGoal = 1, maxFoulsCommited = 1,
                maxRedCards = 1, maxYellowCards = 1, maxDuelsLoss = 1, maxInterpWon = 1, maxFoulsDrawn = 1,
                maxDuelsWon = 1, maxAssists = 1, maxGoals = 1;

        for (PlayerStats player : players) {
            maxPasses = Math.max(maxPasses, player.getPodania());
            maxKeyPasses = Math.max(maxKeyPasses, player.getPodaniaKluczowe());
            maxDribbleWon = Math.max(maxDribbleWon, player.getDryblingiWygrane());
            maxShootsOnGoal = Math.max(maxShootsOnGoal, player.getStrzalyCelne());
            maxFoulsCommited = Math.max(maxFoulsCommited, player.getFaulePopelnione());
            maxRedCards = Math.max(maxRedCards, player.getKartkiCzerwone());
            maxYellowCards = Math.max(maxYellowCards, player.getKartkiZolte());
            maxDuelsLoss = Math.max(maxDuelsLoss, player.getPojedynki() - player.getPojedynkiWygrane());
            maxInterpWon = Math.max(maxInterpWon, player.getPrzechwytyUdane());
            maxFoulsDrawn = Math.max(maxFoulsDrawn, player.getFauleNaZawodniku());
            maxDuelsWon = Math.max(maxDuelsWon, player.getPojedynkiWygrane());
            maxAssists = Math.max(maxAssists, player.getAsysty());
            maxGoals = Math.max(maxGoals, player.getGole());
        }

        for (PlayerStats player : players) {
            Optional<T> optionalPlayerStatsGroup = getPlayerStatsGroupFunction.apply(player);
            T playerStatsGroup = optionalPlayerStatsGroup.orElseGet(createPlayerStatsGroupFunction);

            playerStatsGroup.setImie(player.getImie() + " " + player.getNazwisko());
            playerStatsGroup.setPlayerStats(player);
            playerStatsGroup.setTeamStats(player.getTeamStats());
            playerStatsGroup.setSeason(player.getSeason());
            playerStatsGroup.setPozycja(player.getPozycja());

            double normalizedPasses = player.getPodania() / maxPasses;
            double normalizedKeyPasses = player.getPodaniaKluczowe() / maxKeyPasses;
            double normalizedAssists = player.getAsysty() / maxAssists;
            double sumPIK = (normalizedPasses * weights[0] + normalizedKeyPasses * weights[1] + normalizedAssists * weights[12]) /
                    (weights[0] + weights[1] + weights[12]);
            playerStatsGroup.setPodaniaKreatywnosc(sumPIK);

            double normalizedDribbleWon = player.getDryblingiWygrane() / maxDribbleWon;
            double normalizedShotsOnGoal = player.getStrzalyCelne() / maxShootsOnGoal;
            double normalizedGoals = player.getGole() / maxGoals;
            double sumDIS = (normalizedDribbleWon * weights[2] + normalizedShotsOnGoal * weights[3] + normalizedGoals * weights[11]) /
                    (weights[2] + weights[3] + weights[11]);
            playerStatsGroup.setDryblingSkutecznosc(sumDIS);

            double normalizedFoulsCommited = player.getFaulePopelnione() / maxFoulsCommited;
            double normalizedRedCards = player.getKartkiCzerwone() / maxRedCards;
            double normalizedYellowCards = player.getKartkiZolte() / maxYellowCards;
            double normalizedDuelsLost = (player.getPojedynki() - player.getPojedynkiWygrane()) / maxDuelsLoss;
            double duelsToReturn = (normalizedFoulsCommited * weights[4] + normalizedRedCards * weights[5] + normalizedYellowCards * weights[6] + normalizedDuelsLost * weights[7]) /
                    (weights[4] + weights[5] + weights[5] + weights[6]);
            playerStatsGroup.setFizycznoscInterakcje((duelsToReturn / 4) * 3);

            double normalizedInterceptionsWon = player.getPrzechwytyUdane() / maxInterpWon;
            double normalizedFoulsDrawn = player.getFauleNaZawodniku() / maxFoulsDrawn;
            double normalizedDuelsWon = player.getPojedynkiWygrane() / maxDuelsWon;
            double sumOKP = (normalizedInterceptionsWon * weights[8] + normalizedFoulsDrawn * weights[9] + normalizedDuelsWon * weights[10]) /
                    (weights[8] + weights[9] + weights[10]);
            playerStatsGroup.setObronaKotrolaPrzeciwnika(sumOKP);

            if (optionalPlayerStatsGroup.isPresent()) {
                deletePlayerStatsGroupFunction.accept(playerStatsGroup);
            }
            savePlayerStatsGroupFunction.accept(playerStatsGroup);
        }
    }

    private void calculateWeightAndSave(Iterable<PlayerStats> players, double[] weights, boolean isPos) {
        if (isPos) {
            processPlayerStats(players, weights,
                    (player) -> pogrupowanePozycjamiRepository.getPlayerStatsGroupWPosByPlayerStatsAndSeason(player, player.getSeason()),
                    (playerStatsGroup) -> pogrupowanePozycjamiRepository.delete(playerStatsGroup),
                    (playerStatsGroup) -> pogrupowanePozycjamiRepository.save(playerStatsGroup),
                    PlayersStatsGroupWPos::new);
        } else {
            processPlayerStats(players, weights,
                    (player) -> pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByPlayerStatsAndSeason(player, player.getSeason()),
                    (playerStatsGroup) -> pogrupowaneRepository.delete(playerStatsGroup),
                    (playerStatsGroup) -> pogrupowaneRepository.save(playerStatsGroup),
                    PlayersStatsGroup::new);
        }
    }

    public void getAvgForGoalkeepers(Iterable<PlayerStats> goalkeepers, double[] weights) {
        processPlayerStats(goalkeepers, weights,
                (player) -> pogrupowanePozycjamiRepository.getPlayerStatsGroupWPosByPlayerStatsAndSeason(player, player.getSeason()),
                (playerStatsGroup) -> pogrupowanePozycjamiRepository.delete(playerStatsGroup),
                (playerStatsGroup) -> pogrupowanePozycjamiRepository.save(playerStatsGroup),
                PlayersStatsGroupWPos::new);
    }

    public void getSummary() {
        processSummary(false);
    }

    public void getSummaryWithPos() {
        processSummary(true);
    }

    @Transactional
    public void processSummary(boolean withPos) {
        List<PlayerStats> combinationsTeamsAndSeasons = statystykiZawodnikaRepository.getPlayerStatsGroupedBySeasonAndTeamStats();

        for (PlayerStats singleCombination : combinationsTeamsAndSeasons) {
            long season = singleCombination.getSeason();
            TeamStats teamStats = singleCombination.getTeamStats();

            if (!Hibernate.isInitialized(teamStats)) {
                Hibernate.initialize(teamStats);
            }

            Optional<TeamStats> teamStatsOp = teamStatsRepository.findTeamStatsByTeamIdAndSeason(teamStats.getTeamId(), season);
            if (teamStatsOp.isPresent()) {
                teamStats = teamStatsOp.get();
                if (withPos) {
                    processTeamStatsWithPos(season, teamStats);
                } else {
                    processTeamStats(season, teamStats);
                }
            }
        }
    }

    private void processTeamStats(long season, TeamStats teamStats) {
        List<PlayersStatsGroup> players = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByTeamStatsAndSeason(teamStats, season);

        double sum = 0, avgPodaniaKreatywanosc = 0, avgDryblingSkutecznosc = 0, avgFizycznoscInterakcje = 0, avgObronaKotrolaPrzeciwnika = 0;

        for (PlayersStatsGroup player : players) {
            sum++;
            avgFizycznoscInterakcje += player.getFizycznoscInterakcje();
            avgDryblingSkutecznosc += player.getDryblingSkutecznosc();
            avgObronaKotrolaPrzeciwnika += player.getObronaKotrolaPrzeciwnika();
            avgPodaniaKreatywanosc += player.getPodaniaKreatywnosc();
        }
        TeamGroupAvg teamGroupAvg = new TeamGroupAvg();
        if (!players.isEmpty()) {
            teamGroupAvg.setFizycznoscInterakcje(avgFizycznoscInterakcje / sum);
            teamGroupAvg.setDryblingSkutecznosc(avgDryblingSkutecznosc / sum);
            teamGroupAvg.setObronaKotrolaPrzeciwnika(avgObronaKotrolaPrzeciwnika / sum);
            teamGroupAvg.setPodaniaKreatywnosc(avgPodaniaKreatywanosc / sum);
        } else {
            teamGroupAvg = null;
        }

        teamGroupAvg.setSeason(season);
        teamGroupAvg.setTeamStats(teamStats);
        teamGroupAvg.setTeamName(teamStats.getTeamName());
        saveTeamGroupAvg(teamGroupAvg, teamStats, season);
    }

    private void processTeamStatsWithPos(long season, TeamStats teamStats) {
        List<PlayersStatsGroupWPos> players = pogrupowanePozycjamiRepository.getPlayerStatsGroupWPosByTeamStatsAndSeason(teamStats, season);

        double sum = 0, avgPodaniaKreatywanosc = 0, avgDryblingSkutecznosc = 0, avgFizycznoscInterakcje = 0, avgObronaKotrolaPrzeciwnika = 0;

        for (PlayersStatsGroupWPos player : players) {
            sum++;
            avgFizycznoscInterakcje += player.getFizycznoscInterakcje();
            avgDryblingSkutecznosc += player.getDryblingSkutecznosc();
            avgObronaKotrolaPrzeciwnika += player.getObronaKotrolaPrzeciwnika();
            avgPodaniaKreatywanosc += player.getPodaniaKreatywnosc();
        }
        TeamGroupAvgWPos teamGroupAvgWPos = new TeamGroupAvgWPos();
        if (!players.isEmpty()) {
            teamGroupAvgWPos.setFizycznoscInterakcje(avgFizycznoscInterakcje / sum);
            teamGroupAvgWPos.setDryblingSkutecznosc(avgDryblingSkutecznosc / sum);
            teamGroupAvgWPos.setObronaKotrolaPrzeciwnika(avgObronaKotrolaPrzeciwnika / sum);
            teamGroupAvgWPos.setPodaniaKreatywnosc(avgPodaniaKreatywanosc / sum);
        } else {
            teamGroupAvgWPos = null;
        }

        teamGroupAvgWPos.setSeason(season);
        teamGroupAvgWPos.setTeamStats(teamStats);
        teamGroupAvgWPos.setTeamName(teamStats.getTeamName());
        saveTeamGroupAvgWPos(teamGroupAvgWPos, teamStats, season);
    }


    private void saveTeamGroupAvg(TeamGroupAvg teamGroupAvg, TeamStats teamStats, long season) {
        Optional<TeamGroupAvg> optionalSredniaDruzyny = sredniaDruzynyRepository.getSredniaDruzynyByTeamStatsAndSeason(teamStats, season);
        optionalSredniaDruzyny.ifPresent(sredniaDruzynyRepository::delete);
        sredniaDruzynyRepository.save(teamGroupAvg);
    }

    private void saveTeamGroupAvgWPos(TeamGroupAvgWPos teamGroupAvgWPos, TeamStats teamStats, long season) {
        Optional<TeamGroupAvgWPos> optionalSredniaDruzyny = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(teamStats, season);
        optionalSredniaDruzyny.ifPresent(srDruzynyPozycjeRepository::delete);
        srDruzynyPozycjeRepository.save(teamGroupAvgWPos);
    }

    public Optional<PlayerStats> getPlayerById(Long id) {
        return playersStatsRepo.findById(id);
    }

    public Iterable<PlayerStats> getPlayersByTeamId(Long teamId) {
        Optional<TeamStats> opTeam = null;
        Iterable<PlayerStats> players = null;
        if (opTeam.isPresent()) {
            players = playersStatsRepo.findPlayerStatsByTeamStats(opTeam.get());
        }
        return players;
    }

    public Iterable<PlayerStats> getAveragePlayerRatings() {
        return List.of();
    }

    public void savePlayer(PlayerStats player) {
        playersStatsRepo.save(player);
    }

    public void updatePlayer(Long id, PlayerStats player) {
        Optional<PlayerStats> existingPlayer = playersStatsRepo.findById(id);
        if (existingPlayer.isPresent()) {
            PlayerStats p = existingPlayer.get();
            p.setImie(player.getImie());
            p.setTeamStats(player.getTeamStats());
            p.setRating(player.getRating());
            playersStatsRepo.save(p);
        }
    }

    public void deletePlayer(Long id) {
        playersStatsRepo.deleteById(id);
    }
}
