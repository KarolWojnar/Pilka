package com.Football.football.Services;

import com.Football.football.Repositories.*;
import com.Football.football.Tables.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerStatsService {

    private final PlayersStatsRepo statystykiZawodnikaRepository;
    private final TeamStatsRepo teamStatsRepository;
    private final PlayerStatsGroupRepo pogrupowaneRepository;
    private final TeamGroupAvgRepo sredniaDruzynyRepository;
    private final PlayerStatsGroupWPosRepo pogrupowanePozycjamiRepository;
    private final TeamGroupAvgWPosRepo srDruzynyPozycjeRepository;


    public String updatePlayerStats(int teamId, int season, int leagueId) throws IOException, InterruptedException, JSONException {
        StringBuilder all = new StringBuilder();
        Optional<TeamStats> opTeam = teamStatsRepository.findFirstByTeamId((long) teamId);

        if (!opTeam.isPresent()) {
            throw new IllegalArgumentException("Team with ID " + teamId + " not found");
        }

        TeamStats teamStats = opTeam.get();

        for (int page = 1; page <= 3; page++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/players?season=" + season + "&league=" + leagueId +"&team=" + teamId + "&page=" + page))
                    .header("X-RapidAPI-Key", "d33e623437msha2a56a1ea6f5bfbp18d606jsndd5dc6ff099b")
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            all.append(responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            if (jsonResponse.has("response")) {
                JSONArray statsArray = jsonResponse.getJSONArray("response");
                for (int i = 0; i < statsArray.length(); i++) {
                    JSONObject playerStats = statsArray.getJSONObject(i);
                    int isActive = playerStats.getJSONArray("statistics").getJSONObject(0).getJSONObject("games").optInt("minutes", 0);
                    if (isActive == 0) continue;

                    Long playerId = playerStats.getJSONObject("player").getLong("id");
                    Optional<PlayerStats> optional = statystykiZawodnikaRepository.getPlayerStatsByPlayerIdAndTeamStatsAndSeason(playerId, teamStats, (long) season);

                    if (optional.isPresent()) {
                        PlayerStats toUpdate = optional.get();
                        statystykiZawodnikaRepository.delete(toUpdate);
                    }

                    Optional<PlayerStats> moreMinuteOptionalPlayer = statystykiZawodnikaRepository.getStatystykiZawodnikaByPlayerIdAndSeason(playerId, (long) season);
                    if (moreMinuteOptionalPlayer.isPresent()) {
                        PlayerStats playerX = moreMinuteOptionalPlayer.get();
                        if (playerX.getMinuty() < (float) isActive) {
                            statystykiZawodnikaRepository.delete(playerX);
                        } else {
                            continue;
                        }
                    }

                    PlayerStats player = new PlayerStats();
                    player.setTeamStats(teamStats);
                    player.setSeason((long) season);
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
                            continue;
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
            }
        }
        return all.toString();
    }

    public void getAvgOfAllPlayers(Iterable<PlayerStats> players, boolean isPositions, String whereIsPlaying) {
        Iterable<TeamStats> teams = teamStatsRepository.findAll();

        double fixturesCount = 0.0;
        for (TeamStats team : teams) {
            fixturesCount += team.getSumaSpotkan();
        }

        double sumPasses = 0, sumKeyPasses = 0, sumDribbleWon = 0, sumShootsOnGoal = 0, sumFoulsCommited = 0,
                sumRedCards = 0, sumYellowCards = 0, sumDuelsLoss = 0, sumFoulsDrawn = 0, sumInterpWon = 0, sumDuelsWon = 0;
        for (PlayerStats player : players) {
            sumPasses += player.getPodania();
            sumKeyPasses += player.getPodaniaKluczowe();
            sumDribbleWon += player.getDryblingiWygrane();
            sumShootsOnGoal += player.getStrzalyCelne();
            sumFoulsCommited += player.getFaulePopelnione();
            sumRedCards += player.getKartkiCzerwone();
            sumYellowCards += player.getKartkiZolte();
            sumDuelsLoss += (player.getPojedynki() - player.getPojedynkiWygrane());
            sumInterpWon += player.getPrzechwytyUdane();
            sumFoulsDrawn += player.getFauleNaZawodniku();
            sumDuelsWon += player.getPojedynkiWygrane();
        }
        double goleAsystySuma = 0.0;
        for (TeamStats team : teams) {
            goleAsystySuma += team.getGoleStrzeloneNaWyjezdzie();
            goleAsystySuma += team.getGoleStrzeloneWDomu();
        }

        sumPasses /= fixturesCount;
        sumKeyPasses /= fixturesCount;
        sumDribbleWon /= fixturesCount;
        sumShootsOnGoal /= fixturesCount;
        sumFoulsCommited /= fixturesCount;
        sumRedCards /= fixturesCount;
        sumYellowCards /= fixturesCount;
        sumDuelsLoss /= fixturesCount;
        sumInterpWon /= fixturesCount;
        sumFoulsDrawn /= fixturesCount;
        sumDuelsWon /= fixturesCount;
        goleAsystySuma /= fixturesCount;

        double[] weights = {90 / sumPasses, 90 / sumKeyPasses, 90 / sumDribbleWon, 90 / sumShootsOnGoal, 90 / sumFoulsCommited,
                90 / sumRedCards, 90 / sumYellowCards, 90 / sumDuelsLoss, 90 / sumInterpWon, 90 / sumFoulsDrawn, 90 / sumDuelsWon, 90 / goleAsystySuma};

        if (!whereIsPlaying.equals("GK")) calculateWeightAndSave(players, weights, isPositions);
        else getAvgForGKeappers(statystykiZawodnikaRepository.getStatystykiZawodnikasByPozycja("GOALKEEPER"), weights);
    }

    private void calculateWeightAndSave(Iterable<PlayerStats> players, double[] weights, boolean isPos) {
        for (PlayerStats player : players) {
            if (!isPos) {
                Optional<PlayersStatsGroup> optionalPlayer = pogrupowaneRepository
                        .getPogrupowaneStatystykiZawodnikowByPlayerStatsAndSeason(player, player.getSeason());
                PlayersStatsGroup zawodnik = new PlayersStatsGroup();
                zawodnik.setImie(player.getImie() + " " + player.getNazwisko());
                zawodnik.setPlayerStats(player);
                zawodnik.setTeamStats(player.getTeamStats());
                zawodnik.setSeason(player.getSeason());
                zawodnik.setPozycja(player.getPozycja());

                double minutes = player.getMinuty();

                double accuracyPerMinute = (player.getPodania() / minutes) * weights[0];
                double keysPerMinute = (player.getPodaniaKluczowe() / minutes) * weights[1];
                double assistsPerMinute = (player.getAsysty() / minutes) * weights[11];
                zawodnik.setPodaniaKreatywnosc(accuracyPerMinute + keysPerMinute + assistsPerMinute);

                double wonDribblingsPerMinute = (player.getDryblingiWygrane() / minutes) * weights[2];
                double shotsOnGoalPerMinute = (player.getStrzalyCelne() / minutes) * weights[3];
                double goalsPerMinute = (player.getGole() / minutes) * weights[11];
                zawodnik.setDryblingSkutecznosc(wonDribblingsPerMinute + shotsOnGoalPerMinute + goalsPerMinute);

                double foulsCommitedPerMinute = (player.getFaulePopelnione() / minutes) * weights[4];
                double redCards = (player.getKartkiCzerwone() / minutes) * weights[5];
                double yellowCards = (player.getKartkiZolte() / minutes) * weights[6];
                double duelsLost = player.getPojedynki() - player.getPojedynkiWygrane();
                double duelsLostPerMinute = (duelsLost / minutes) * weights[7];
                double duelsToReturn = foulsCommitedPerMinute + redCards + yellowCards + duelsLostPerMinute;
                zawodnik.setFizycznoscInterakcje((duelsToReturn / 4) * 3);

                double interceptionsWonPerMinute = (player.getPrzechwytyUdane() / minutes) * weights[8];
                double foulsDrawnPerMinute = (player.getFauleNaZawodniku() / minutes) * weights[9];
                double duelsWonPerMinute = (player.getPojedynkiWygrane() / minutes) * weights[10];
                zawodnik.setObronaKotrolaPrzeciwnika(interceptionsWonPerMinute + foulsDrawnPerMinute + duelsWonPerMinute);

                if (optionalPlayer.isPresent()) {
                    PlayersStatsGroup updatePlayer = optionalPlayer.get();
                    pogrupowaneRepository.delete(updatePlayer);
                }
                pogrupowaneRepository.save(zawodnik);
            } else {
                setStatsAndSave(weights, player);
            }
        }
    }

    public void getAvgForGKeappers(Iterable<PlayerStats> goalkeepers, double[] weights) {
        for (PlayerStats goalkeeper : goalkeepers) {
            setStatsAndSave(weights, goalkeeper);
        }
    }

    private void setStatsAndSave(double[] weights, PlayerStats goalkeeper) {
        Optional<PlayersStatsGroupWPos> optionalPlayer = pogrupowanePozycjamiRepository
                .getPogrypowaneStatsZawodPozycjeUwzglednioneByPlayerStatsAndSeason(goalkeeper, goalkeeper.getSeason());
        PlayersStatsGroupWPos zawodnik = new PlayersStatsGroupWPos();

        zawodnik.setImie(goalkeeper.getImie() + " " + goalkeeper.getNazwisko());
        zawodnik.setPlayerStats(goalkeeper);
        zawodnik.setTeamStats(goalkeeper.getTeamStats());
        zawodnik.setSeason(goalkeeper.getSeason());
        zawodnik.setPozycja(goalkeeper.getPozycja());

        double minutes = goalkeeper.getMinuty();

        double accuracyPerMinute = ((goalkeeper.getPodania() * (goalkeeper.getDokladnoscPodan() / 100)) / minutes) * weights[0];
        double keysPerMinute = (goalkeeper.getPodaniaKluczowe() / minutes) * weights[1];
        double assistsPerMinute = (goalkeeper.getAsysty() / minutes) * weights[11];
        zawodnik.setPodaniaKreatywnosc(accuracyPerMinute + keysPerMinute + assistsPerMinute);

        double wonDribblingsPerMinute = (goalkeeper.getDryblingiWygrane() / minutes) * weights[2];
        double shotsOnGoalPerMinute = (goalkeeper.getStrzalyCelne() / minutes) * weights[3];
        double goalsPerMinute = (goalkeeper.getGole() / minutes) * weights[11];
        zawodnik.setDryblingSkutecznosc(wonDribblingsPerMinute + shotsOnGoalPerMinute + goalsPerMinute);

        double foulsCommitedPerMinute = (goalkeeper.getFaulePopelnione() / minutes) * weights[4];
        double redCards = (goalkeeper.getKartkiCzerwone() / minutes) * weights[5];
        double yellowCards = (goalkeeper.getKartkiZolte() / minutes) * weights[6];
        double duelsLostPerMinute = ((goalkeeper.getPojedynki() - goalkeeper.getPojedynkiWygrane()) / minutes) * weights[7];
        double duelsToReturn = foulsCommitedPerMinute + redCards + yellowCards + duelsLostPerMinute;
        zawodnik.setFizycznoscInterakcje((duelsToReturn / 4) * 3);

        double interceptionsWonPerMinute = (goalkeeper.getPrzechwytyUdane() / minutes) * weights[8];
        double foulsDrawnPerMinute = (goalkeeper.getFauleNaZawodniku() / minutes) * weights[9];
        double duelsWonPerMinute = (goalkeeper.getPojedynkiWygrane() / minutes) * weights[10];
        zawodnik.setObronaKotrolaPrzeciwnika(interceptionsWonPerMinute + foulsDrawnPerMinute + duelsWonPerMinute);

        if (optionalPlayer.isPresent()) {
            PlayersStatsGroupWPos updatePlayer = optionalPlayer.get();
            pogrupowanePozycjamiRepository.delete(updatePlayer);
        }
        pogrupowanePozycjamiRepository.save(zawodnik);
    }

    public void getSummary() {
        List<Object[]> combinationsTeamsAndSeasons = statystykiZawodnikaRepository.getDistinctBySeasonAndTeamStats();
        for (Object[] singleCombination : combinationsTeamsAndSeasons) {
            Long season = (Long) singleCombination[0];
            Long teamId = (Long) singleCombination[1];

            Optional<TeamStats> teamStatsOp = teamStatsRepository.findFirstByTeamId(teamId);
            if (teamStatsOp.isPresent()) {
                List<PlayersStatsGroup> players = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByTeamStatsAndSeason(teamStatsOp.get(), season);
                Optional<TeamGroupAvg> optionalSredniaDruzyny = sredniaDruzynyRepository.getSredniaDruzynyByTeamStatsAndSeason(teamStatsOp.get(), season);
                if (optionalSredniaDruzyny.isPresent()) {
                    TeamGroupAvg prevTeam = optionalSredniaDruzyny.get();
                    sredniaDruzynyRepository.delete(prevTeam);
                }
                double sum = 0, avgPodaniaKreatywanosc = 0, avgDryblingSkutecznosc = 0, avgFizycznoscInterakcje = 0, avgObronaKotrolaPrzeciwnika = 0;

                for (PlayersStatsGroup player : players) {
                    sum++;
                    avgFizycznoscInterakcje += player.getFizycznoscInterakcje();
                    avgDryblingSkutecznosc += player.getDryblingSkutecznosc();
                    avgObronaKotrolaPrzeciwnika += player.getObronaKotrolaPrzeciwnika();
                    avgPodaniaKreatywanosc += player.getPodaniaKreatywnosc();
                }

                avgFizycznoscInterakcje /= sum;
                avgDryblingSkutecznosc /= sum;
                avgObronaKotrolaPrzeciwnika /= sum;
                avgPodaniaKreatywanosc /= sum;

                TeamGroupAvg team = new TeamGroupAvg();
                Optional<TeamStats> optionalTeam = teamStatsRepository.findFirstByTeamId(teamId);
                if (optionalTeam.isPresent()) {
                    team.setTeamStats(optionalTeam.get());
                }
                team.setSeason(season);
                Optional<TeamStats> optionalName = teamStatsRepository.findFirstByTeamId(teamId);
                optionalName.ifPresent(statystykiDruzyny -> team.setTeamName(statystykiDruzyny.getTeamName()));
                team.setDryblingSkutecznosc(avgDryblingSkutecznosc);
                team.setFizycznoscInterakcje(avgFizycznoscInterakcje);
                team.setPodaniaKreatywnosc(avgPodaniaKreatywanosc);
                team.setObronaKotrolaPrzeciwnika(avgObronaKotrolaPrzeciwnika);

                sredniaDruzynyRepository.save(team);
            }
            }

    }

    public void getSummaryWithPos() {
        List<Object[]> combinationsTeamsAndSeasons = statystykiZawodnikaRepository.getDistinctBySeasonAndTeamStats();
        for (Object[] singleCombination : combinationsTeamsAndSeasons) {
            Long season = (Long) singleCombination[0];
            Long teamId = (Long) singleCombination[1];

            Optional<TeamStats> teamStatsOp = teamStatsRepository.findFirstByTeamId(teamId);
            if (teamStatsOp.isPresent()) {
                List<PlayersStatsGroupWPos> players = pogrupowanePozycjamiRepository
                        .getPogrypowaneStatsZawodPozycjeUwzglednioneByTeamStatsAndSeason(teamStatsOp.get(), season);
                Optional<TeamGroupAvgWPos> optionalSredniaDruzyny = srDruzynyPozycjeRepository
                        .getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(teamStatsOp.get(), season);
                if (optionalSredniaDruzyny.isPresent()) {
                    TeamGroupAvgWPos prevTeam = optionalSredniaDruzyny.get();
                    srDruzynyPozycjeRepository.delete(prevTeam);
                }
                double sum = 0, avgPodaniaKreatywanosc = 0, avgDryblingSkutecznosc = 0, avgFizycznoscInterakcje = 0, avgObronaKotrolaPrzeciwnika = 0;

                for (PlayersStatsGroupWPos player : players) {
                    sum++;
                    avgFizycznoscInterakcje += player.getFizycznoscInterakcje();
                    avgDryblingSkutecznosc += player.getDryblingSkutecznosc();
                    avgObronaKotrolaPrzeciwnika += player.getObronaKotrolaPrzeciwnika();
                    avgPodaniaKreatywanosc += player.getPodaniaKreatywnosc();
                }

                avgFizycznoscInterakcje /= sum;
                avgDryblingSkutecznosc /= sum;
                avgObronaKotrolaPrzeciwnika /= sum;
                avgPodaniaKreatywanosc /= sum;

                TeamGroupAvgWPos team = new TeamGroupAvgWPos();
                Optional<TeamStats> optionalTeam = teamStatsRepository.findFirstByTeamId(teamId);
                if (optionalTeam.isPresent()) {
                    team.setTeamStats(optionalTeam.get());
                }
                team.setSeason(season);
                Optional<TeamStats> optionalName = teamStatsRepository.findFirstByTeamId(teamId);
                optionalName.ifPresent(statystykiDruzyny -> team.setTeamName(statystykiDruzyny.getTeamName()));
                optionalName.ifPresent(statystykiDruzyny -> team.setLeagueId(statystykiDruzyny.getLeagues().getLeagueId()));
                team.setDryblingSkutecznosc(avgDryblingSkutecznosc);
                team.setFizycznoscInterakcje(avgFizycznoscInterakcje);
                team.setPodaniaKreatywnosc(avgPodaniaKreatywanosc);
                team.setObronaKotrolaPrzeciwnika(avgObronaKotrolaPrzeciwnika);

                srDruzynyPozycjeRepository.save(team);
            }
        }
    }
    public Iterable<PlayerStats> findAllPlayers() {
        return statystykiZawodnikaRepository.findAll();
    }

}
