package com.Football.football.Services;

import com.Football.football.Repositories.*;
import com.Football.football.Tables.*;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class TeamStatsService {
    private final TeamStatsRepo teamStatsRepository;
    private final TeamGroupAvgRepo sredniaDruzynyRepository;
    private final TeamGroupAvgWPosRepo srDruzynyPozycjeRepository;
    private final TeamAvgRepo avgAllRepository;
    private final LeaguesRepository leaguesRepository;

    @Value("${api.key}")
    private String apiKey;
    public void getAllTeamsByLeague(long leagueId, Long season) throws IOException, InterruptedException, JSONException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-beta.p.rapidapi.com/teams?league=" + leagueId + "&season=" + season))
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
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
        }
    }

    private void getLeagueById(Long leagueId) throws IOException, InterruptedException, JSONException {
        Optional<Leagues> optionalLeague = leaguesRepository.getFirstByLeagueId(leagueId);
        if (optionalLeague.isEmpty()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/leagues?id=" + leagueId))
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject jsonResponse = new JSONObject(responseBody);

            if (jsonResponse.has("response")) {
                JSONObject leagueObject = jsonResponse.getJSONArray("response").getJSONObject(0);
                Leagues league = new Leagues();
                league.setLeagueId(leagueId);
                league.setLeagueName(leagueObject.getJSONObject("league").getString("name"));
                league.setCountry(leagueObject.getJSONObject("country").getString("name"));
                leaguesRepository.save(league);
            }
        }
    }

    public void updateTeamStats(long teamId, Long year, long leagueId) throws IOException, InterruptedException, JSONException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-beta.p.rapidapi.com/teams/statistics?league=" + leagueId + "&team=" + teamId +"&season=" + year))
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response2 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response2.body();
        JSONObject jsonResponse = new JSONObject(responseBody);
        if (jsonResponse.has("response")) {
            JSONObject responseData = jsonResponse.getJSONObject("response");

            Optional<TeamStats> optional = teamStatsRepository.getStatystykiDruzyniesByTeamIdAndSeason(responseData.getJSONObject("team").getLong("id"), (long) year);
            if (optional.isPresent()) {
                TeamStats updateTeam = optional.get();
                teamStatsRepository.delete(updateTeam);
            }
            TeamStats teamStats = new TeamStats();
            teamStats.setTeamName(responseData.getJSONObject("team").getString("name"));
            teamStats.setTeamId(teamId);
            teamStats.setSeason(year);
            Optional<Leagues> league = leaguesRepository.getFirstByLeagueId(leagueId);
            if (league.isPresent()) {
                teamStats.setLeagues(league.get());
            }
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
    }

    public void getSumSum() {
        double[] weights = {1.0, 0.5, 0.3, -0.2};
        Iterable<TeamGroupAvg> allTeams = sredniaDruzynyRepository.findAll();
        for (TeamGroupAvg team : allTeams) {
            Optional<TeamAvg> optional = avgAllRepository
                    .findSredniaZeWszystkiegoByTeamStatsAndSeasonAndCzyUwzglednionePozycje(team.getTeamStats(), team.getSeason(), false);
            if (optional.isPresent()) {
                TeamAvg updateTeam = optional.get();
                avgAllRepository.delete(updateTeam);
            }
            TeamAvg avgTeam = getSredniaZeWszystkiego(team, weights, false);
            avgAllRepository.save(avgTeam);
        }
    }

    public void getSumSumWPos() {
        double[] weights = {1.0, 0.5, 0.3, -0.2};
        Iterable<TeamGroupAvgWPos> allTeams = srDruzynyPozycjeRepository.findAll();
        for (TeamGroupAvgWPos team : allTeams) {
            Optional<TeamAvg> optional = avgAllRepository
                    .findSredniaZeWszystkiegoByTeamStatsAndSeasonAndCzyUwzglednionePozycje(team.getTeamStats(), team.getSeason(), true);
            if (optional.isPresent()) {
                TeamAvg updateTeam = optional.get();
                avgAllRepository.delete(updateTeam);
            }
            TeamAvg avgTeam = getSredniaZeWszystkiegoPos(team, weights, true);

            avgAllRepository.save(avgTeam);
        }
    }
    private TeamAvg getSredniaZeWszystkiegoPos(TeamGroupAvgWPos team, double[] weights, boolean isPos) {
        double summaryWeight = 0.0;

        TeamAvg avgTeam = new TeamAvg();

        summaryWeight += (team.getDryblingSkutecznosc() * weights[0]);
        summaryWeight += (team.getPodaniaKreatywnosc() * weights[1]);
        summaryWeight += (team.getObronaKotrolaPrzeciwnika() * weights[2]);
        summaryWeight += (team.getFizycznoscInterakcje() * weights[3]);

        double sum = 0;
        for (double x: weights) sum += x;

        avgTeam.setRaiting(summaryWeight / sum);
        avgTeam.setTeamStats(team.getTeamStats());
        avgTeam.setSeason(team.getSeason());
        avgTeam.setCzyUwzglednionePozycje(isPos);
        return avgTeam;
    }

    private TeamAvg getSredniaZeWszystkiego(TeamGroupAvg team, double[] weights, boolean isPos) {
        double summaryWeight = 0.0;

        TeamAvg avgTeam = new TeamAvg();


        summaryWeight += (team.getDryblingSkutecznosc() * weights[0]);
        summaryWeight += (team.getPodaniaKreatywnosc() * weights[1]);
        summaryWeight += (team.getObronaKotrolaPrzeciwnika() * weights[2]);
        summaryWeight += (team.getFizycznoscInterakcje() * weights[3]);

        double sum = 0;
        for (double x: weights) sum += x;


        avgTeam.setRaiting(summaryWeight / sum);
        avgTeam.setTeamStats(team.getTeamStats());
        avgTeam.setSeason(team.getSeason());
        avgTeam.setCzyUwzglednionePozycje(isPos);
        return avgTeam;
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
