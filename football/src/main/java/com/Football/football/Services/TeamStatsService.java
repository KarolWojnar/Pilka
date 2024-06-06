package com.Football.football.Services;

import com.Football.football.ApiKeyManager;
import com.Football.football.Repositories.*;
import com.Football.football.Tables.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TeamStatsService {
    private final TeamStatsRepo teamStatsRepository;
    private final TeamGroupAvgRepo sredniaDruzynyRepository;
    private final TeamGroupAvgWPosRepo srDruzynyPozycjeRepository;
    private final TeamAvgRepo avgAllRepository;
    private final LeaguesRepository leaguesRepository;
    private final ApiKeyManager apiKeyManager;

    private HttpClient httpClient = HttpClient.newHttpClient();

    public void getAllTeamsByLeague(long leagueId, Long season) throws IOException, InterruptedException, JSONException {
        int attempts = 0;
        while (attempts < apiKeyManager.getApiKeysLength()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/teams?league=" + leagueId + "&season=" + season))
                    .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 429) {
                apiKeyManager.switchToNextApiKey();
                attempts++;
            } else if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jResponse = new JSONObject(responseBody);

                if (jResponse.has("response")) {
                    getLeagueById(leagueId);
                    JSONArray teams = jResponse.getJSONArray("response");
                    for (int i = 0; i < teams.length(); i++) {
                        JSONObject team = teams.getJSONObject(i);
                        long teamId = team.getJSONObject("team").getLong("id");
                        updateTeamStats(teamId, season, leagueId);
                    }
                    return;
                } else {
                    throw new IOException("Brak drużyn w odpowiedzi API.");
                }
            } else {
                throw new IOException("Unexpected response status: " + response.statusCode());
            }
        }
        throw new IOException("Failed to retrieve data from API after trying all API keys.");
    }

    private void getLeagueById(Long leagueId) throws IOException, InterruptedException, JSONException {
        Optional<Leagues> optionalLeague = leaguesRepository.getFirstByLeagueId(leagueId);
        if (optionalLeague.isEmpty()) {
            int attempts = 0;
            while (attempts < apiKeyManager.getApiKeysLength()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api-football-beta.p.rapidapi.com/leagues?id=" + leagueId))
                        .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                        .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 429) {
                    apiKeyManager.switchToNextApiKey();
                    attempts++;
                } else if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.has("response")) {
                        JSONObject leagueObject = jsonResponse.getJSONArray("response").getJSONObject(0);
                        Leagues league = new Leagues();
                        league.setLeagueId(leagueId);
                        league.setLeagueName(leagueObject.getJSONObject("league").getString("name"));
                        league.setCountry(leagueObject.getJSONObject("country").getString("name"));
                        leaguesRepository.save(league);
                        return;
                    } else {
                        throw new IOException("Brak ligi w odpowiedzi API.");
                    }
                } else {
                    throw new IOException("Unexpected response status: " + response.statusCode());
                }
            }
            throw new IOException("Failed to retrieve data from API after trying all API keys.");
        }
    }

    public void updateTeamStats(long teamId, Long year, long leagueId) throws IOException, InterruptedException, JSONException {
        int attempts = 0;
        while (attempts < apiKeyManager.getApiKeysLength()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/teams/statistics?league=" + leagueId + "&team=" + teamId + "&season=" + year))
                    .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 429) {
                apiKeyManager.switchToNextApiKey();
                attempts++;
            } else if (response.statusCode() == 200) {
                String responseBody = response.body();
                JSONObject jsonResponse = new JSONObject(responseBody);

                if (jsonResponse.has("response")) {
                    JSONObject responseData = jsonResponse.getJSONObject("response");

                    Optional<TeamStats> optional = teamStatsRepository.getStatystykiDruzyniesByTeamIdAndSeason(responseData.getJSONObject("team").getLong("id"), year);
                    if (optional.isPresent()) {
                        TeamStats updateTeam = optional.get();
                        teamStatsRepository.delete(updateTeam);
                    }
                    saveTeamStats(teamId, year, leagueId, responseData);
                    return;
                } else {
                    throw new IOException("Brak statystyk drużyny w odpowiedzi API.");
                }
            } else {
                throw new IOException("Unexpected response status: " + response.statusCode());
            }
        }
        throw new IOException("Failed to retrieve data from API after trying all API keys.");
    }
    private void saveTeamStats(long teamId, Long year, long leagueId, JSONObject responseData) throws JSONException {
        TeamStats teamStats = new TeamStats();
        teamStats.setTeamName(responseData.getJSONObject("team").getString("name"));
        teamStats.setTeamId(teamId);
        teamStats.setSeason(year);
        Optional<Leagues> league = leaguesRepository.getFirstByLeagueId(leagueId);
        league.ifPresent(teamStats::setLeagues);
        JSONObject fixtures = responseData.getJSONObject("fixtures");
        JSONObject played = fixtures.getJSONObject("played");
        teamStats.setSumaSpotkan(played.optDouble("total", 0));
        teamStats.setMeczeDomowe(played.optDouble("home", 0));
        teamStats.setMeczeWyjazdowe(played.optDouble("away", 0));

        JSONObject wins = fixtures.getJSONObject("wins");
        teamStats.setWygraneWDomu(wins.optDouble("home", 0));
        teamStats.setWygraneNaWyjezdzie(wins.optDouble("away", 0));

        teamStats.setRemisyWDomu(fixtures.getJSONObject("draws").optDouble("home", 0));
        teamStats.setRemisyNaWyjezdzie(fixtures.getJSONObject("draws").optDouble("away", 0));

        teamStats.setPrzegraneWDomu(fixtures.getJSONObject("loses").optDouble("home", 0));
        teamStats.setPrzegraneNaWyjezdzie(fixtures.getJSONObject("loses").optDouble("away", 0));

        JSONObject goals = responseData.getJSONObject("goals");
        JSONObject goalsFor = goals.getJSONObject("for");

        teamStats.setGoleStrzeloneWDomu(goalsFor.getJSONObject("total").optDouble("home", 0));
        teamStats.setGoleStrzeloneNaWyjezdzie(goalsFor.getJSONObject("total").optDouble("away", 0));

        teamStats.setSredniaGoliStrzelonychNaWyjezdzie(goalsFor.getJSONObject("average").optDouble("away", 0));
        teamStats.setSredniaGoliStrzelonychWDomu(goalsFor.getJSONObject("average").optDouble("home", 0));

        JSONObject goalsTotalAgainst = goals.getJSONObject("against").getJSONObject("total");
        JSONObject goalsAvgAgainst = goals.getJSONObject("against").getJSONObject("average");

        teamStats.setGoleStraconeWDomu(goalsTotalAgainst.optDouble("home", 0));
        teamStats.setGoleStraconeNaWyjezdzie(goalsTotalAgainst.optDouble("away", 0));

        teamStats.setSredniaGoliStraconychNaWyjezdzie(goalsAvgAgainst.optDouble("away", 0));
        teamStats.setSredniaGoliStraconychWDomu(goalsAvgAgainst.optDouble("home", 0));

        teamStats.setCzysteKontaWDomu(responseData.getJSONObject("clean_sheet").optDouble("home", 0));
        teamStats.setCzysteKontaNaWyjezdzie(responseData.getJSONObject("clean_sheet").optDouble("away", 0));

        teamStats.setMeczeBezGolaWDomu(responseData.getJSONObject("failed_to_score").optDouble("home", 0));
        teamStats.setMeczeBezGolaNaWyjezdzie(responseData.getJSONObject("failed_to_score").optDouble("away", 0));

        teamStats.setKarneStrzelone(responseData.getJSONObject("penalty").getJSONObject("scored").optDouble("total", 0));
        teamStats.setKarneNiestrzelone(responseData.getJSONObject("penalty").getJSONObject("missed").optDouble("total", 0));

        teamStats.setFormacja(responseData.getJSONArray("lineups").getJSONObject(0).optString("formation"));
        teamStats.setIleRazyWtejFormacji(responseData.getJSONArray("lineups").getJSONObject(0).optDouble("played", 0));

        JSONObject cardsYellow = responseData.getJSONObject("cards").getJSONObject("yellow");

        double yellow = cardsYellow.getJSONObject("0-15").optDouble("total", 0) + cardsYellow.getJSONObject("16-30").optDouble("total", 0)
                + cardsYellow.getJSONObject("31-45").optDouble("total", 0) + cardsYellow.getJSONObject("46-60").optDouble("total", 0)
                + cardsYellow.getJSONObject("61-75").optDouble("total", 0) + cardsYellow.getJSONObject("76-90").optDouble("total", 0)
                + cardsYellow.getJSONObject("91-105").optDouble("total", 0);

        teamStats.setYellowCards(yellow);

        JSONObject cardsRed = responseData.getJSONObject("cards").getJSONObject("red");

        double red = cardsRed.getJSONObject("0-15").optDouble("total", 0) + cardsRed.getJSONObject("16-30").optDouble("total", 0)
                + cardsRed.getJSONObject("31-45").optDouble("total", 0) + cardsRed.getJSONObject("46-60").optDouble("total", 0)
                + cardsRed.getJSONObject("61-75").optDouble("total", 0) + cardsRed.getJSONObject("76-90").optDouble("total", 0)
                + cardsRed.getJSONObject("91-105").optDouble("total", 0);

        teamStats.setRedCards(red);

        teamStatsRepository.save(teamStats);
    }

    public void getSumSum() {
        calculateAndSaveTeamAverages(sredniaDruzynyRepository::findAll, false);
    }

    public void getSumSumWPos() {
        calculateAndSaveTeamAverages(srDruzynyPozycjeRepository::findAll, true);
    }

    private <T> void calculateAndSaveTeamAverages(Supplier<Iterable<T>> supplier, boolean isPos) {
        Iterable<T> allTeams = supplier.get();

        double[] sums = StreamSupport.stream(allTeams.spliterator(), false)
                .reduce(new double[5], (acc, team) -> {
                    acc[0] += getDryblingSkutecznosc(team);
                    acc[1] += getPodaniaKreatywnosc(team);
                    acc[2] += getObronaKotrolaPrzeciwnika(team);
                    acc[3] += getFizycznoscInterakcje(team);
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

        // Normalizacja wartości
        double maxStat = Math.max(Math.max(sumDiS, sumPiK), Math.max(sumOiKK, Math.abs(sumFiI)));
        sumDiS /= maxStat;
        sumPiK /= maxStat;
        sumOiKK /= maxStat;
        sumFiI /= maxStat;

        // Zastosowanie wartości bezwzględnych
        sumFiI = Math.abs(sumFiI);

        double[] weights = {sumDiS, sumPiK, sumOiKK, sumFiI};

        allTeams.forEach(team -> {
            Optional<TeamAvg> opt = avgAllRepository.findSredniaZeWszystkiegoByTeamStatsAndSeasonAndCzyUwzglednionePozycje(
                    getTeamStats(team), getSeason(team), isPos
            );
            opt.ifPresent(avgAllRepository::delete);

            TeamAvg teamAvg = getSredniaZeWszystkiego(team, weights, isPos);
            avgAllRepository.save(teamAvg);
        });
    }

    private <T> TeamAvg getSredniaZeWszystkiego(T team, double[] weights, boolean isPos) {
        double summaryWeight = 0.0;
        summaryWeight += (getDryblingSkutecznosc(team) * weights[0]);
        summaryWeight += (getPodaniaKreatywnosc(team) * weights[1]);
        summaryWeight += (getObronaKotrolaPrzeciwnika(team) * weights[2]);
        summaryWeight += (getFizycznoscInterakcje(team) * weights[3]);

        double sumWeights = 0;
        for (double weight : weights) {
            sumWeights += weight;
        }

        TeamAvg avgTeam = new TeamAvg();
        avgTeam.setRaiting(summaryWeight / sumWeights);
        avgTeam.setTeamStats(getTeamStats(team));
        avgTeam.setSeason(getSeason(team));
        avgTeam.setCzyUwzglednionePozycje(isPos);
        return avgTeam;
    }

    private <T> double getDryblingSkutecznosc(T team) {
        if (team instanceof TeamGroupAvg) {
            return ((TeamGroupAvg) team).getDryblingSkutecznosc();
        } else if (team instanceof TeamGroupAvgWPos) {
            return ((TeamGroupAvgWPos) team).getDryblingSkutecznosc();
        }
        throw new IllegalArgumentException("Unsupported team type");
    }

    private <T> double getPodaniaKreatywnosc(T team) {
        if (team instanceof TeamGroupAvg) {
            return ((TeamGroupAvg) team).getPodaniaKreatywnosc();
        } else if (team instanceof TeamGroupAvgWPos) {
            return ((TeamGroupAvgWPos) team).getPodaniaKreatywnosc();
        }
        throw new IllegalArgumentException("Unsupported team type");
    }

    private <T> double getObronaKotrolaPrzeciwnika(T team) {
        if (team instanceof TeamGroupAvg) {
            return ((TeamGroupAvg) team).getObronaKotrolaPrzeciwnika();
        } else if (team instanceof TeamGroupAvgWPos) {
            return ((TeamGroupAvgWPos) team).getObronaKotrolaPrzeciwnika();
        }
        throw new IllegalArgumentException("Unsupported team type");
    }

    private <T> double getFizycznoscInterakcje(T team) {
        if (team instanceof TeamGroupAvg) {
            return ((TeamGroupAvg) team).getFizycznoscInterakcje();
        } else if (team instanceof TeamGroupAvgWPos) {
            return ((TeamGroupAvgWPos) team).getFizycznoscInterakcje();
        }
        throw new IllegalArgumentException("Unsupported team type");
    }

    private <T> TeamStats getTeamStats(T team) {
        if (team instanceof TeamGroupAvg) {
            return ((TeamGroupAvg) team).getTeamStats();
        } else if (team instanceof TeamGroupAvgWPos) {
            return ((TeamGroupAvgWPos) team).getTeamStats();
        }
        throw new IllegalArgumentException("Unsupported team type");
    }

    private <T> Long getSeason(T team) {
        if (team instanceof TeamGroupAvg) {
            return ((TeamGroupAvg) team).getSeason();
        } else if (team instanceof TeamGroupAvgWPos) {
            return ((TeamGroupAvgWPos) team).getSeason();
        }
        throw new IllegalArgumentException("Unsupported team type");
    }

    public List<Double> getAllRaitings(Iterable<TeamAvg> a, Iterable<TeamAvg> b,
                                       Iterable<TeamAvg> c, Iterable<TeamAvg> d) {
        List<Double> raitings = new ArrayList<>();
        for (TeamAvg team : a) raitings.add(team.getRaiting());
        for (TeamAvg team : b) raitings.add(team.getRaiting());
        for (TeamAvg team : c) raitings.add(team.getRaiting());
        for (TeamAvg team : d) raitings.add(team.getRaiting());
        return raitings;
    }
}
