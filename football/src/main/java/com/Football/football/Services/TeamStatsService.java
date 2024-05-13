package com.Football.football.Services;

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

@Service
@RequiredArgsConstructor
public class TeamStatsService {
    private final TeamStatsRepository teamStatsRepository;
    private final SredniaDruzynyRepository sredniaDruzynyRepository;
    private final SrDruzynyPozycjeRepository srDruzynyPozycjeRepository;
    private final AvgAllRepository avgAllRepository;
    private final RealnePozycjeRepository realnePozycjeRepository;

    public void getRealStandings(int season, int leagueId) throws IOException, InterruptedException, JSONException {

        if ((realnePozycjeRepository.findFirstByLeagueIdAndYear(leagueId, season)).isPresent()) return;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-beta.p.rapidapi.com/standings?season=" + season + "&league=" + leagueId))
                .header("X-RapidAPI-Key", "d33e623437msha2a56a1ea6f5bfbp18d606jsndd5dc6ff099b")
                .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String responseString = response.body();
        JSONObject jsonResponse = new JSONObject(responseString);

        if (jsonResponse.has("response")) {

            JSONArray standingsArray = jsonResponse.getJSONArray("response")
                    .getJSONObject(0)
                    .getJSONObject("league")
                    .getJSONArray("standings")
                    .getJSONArray(0);

            for (int i = 0; i < standingsArray.length(); i++) {
                RealnePozycjeTabela table = new RealnePozycjeTabela();
                JSONObject teamStandings = standingsArray.getJSONObject(i);
                table.setPozycja(teamStandings.getInt("rank"));
                table.setPoints(teamStandings.getInt("points"));
                table.setLeagueId(leagueId);
                table.setTeamId(teamStandings.getJSONObject("team").getInt("id"));
                table.setTeamName(teamStandings.getJSONObject("team").getString("name"));
                table.setYear(season);
                realnePozycjeRepository.save(table);
            }
        }

    }

    public void updateTeamStats(int teamId, int year, int leagueId) throws IOException, InterruptedException, JSONException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-beta.p.rapidapi.com/teams/statistics?league=" + leagueId + "&team=" + teamId +"&season=" + year))
                .header("X-RapidAPI-Key", "ffd6a2d4f7mshd804fef0d09cb33p131f2bjsnf34096b2c4ec")
                .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response2 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response2.body();
        JSONObject jsonResponse = new JSONObject(responseBody);
        if (jsonResponse.has("response")) {
            JSONObject responseData = jsonResponse.getJSONObject("response");

            Optional<StatystykiDruzyny> optional = teamStatsRepository.getStatystykiDruzyniesByTeamIdAndSeason(responseData.getJSONObject("team").getLong("id"), (long) year);
            if (optional.isPresent()) {
                StatystykiDruzyny updateTeam = optional.get();
                teamStatsRepository.delete(updateTeam);
            }
            StatystykiDruzyny teamStats = new StatystykiDruzyny();
            teamStats.setTeamName(responseData.getJSONObject("team").getString("name"));
            teamStats.setTeamId((long) teamId);
            teamStats.setSeason((long) year);
            teamStats.setLeagueId(leagueId);
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
            JSONObject goalsMinsFor = goalsFor.getJSONObject("minute");

            teamStats.setGoleStrzeloneWDomu(goalsFor.getJSONObject("total").optDouble("home", 0));
            teamStats.setGoleStrzeloneNaWyjezdzie(goalsFor.getJSONObject("total").optDouble("away", 0));

            teamStats.setSredniaGoliStrzelonychNaWyjezdzie(goalsFor.getJSONObject("average").optDouble("away", 0));
            teamStats.setSredniaGoliStrzelonychWDomu(goalsFor.getJSONObject("average").optDouble("home", 0));

            teamStats.setGoleStrzelonePomiedzyMinutami0_15(goalsMinsFor.getJSONObject("0-15").optDouble("total", 0));
            teamStats.setGoleStrzelonePomiedzyMinutami16_30(goalsMinsFor.getJSONObject("16-30").optDouble("total", 0));
            teamStats.setGoleStrzelonePomiedzyMinutami31_45(goalsMinsFor.getJSONObject("31-45").optDouble("total", 0));
            teamStats.setGoleStrzelonePomiedzyMinutami46_60(goalsMinsFor.getJSONObject("46-60").optDouble("total", 0));
            teamStats.setGoleStrzelonePomiedzyMinutami61_75(goalsMinsFor.getJSONObject("61-75").optDouble("total", 0));
            teamStats.setGoleStrzelonePomiedzyMinutami76_90(goalsMinsFor.getJSONObject("76-90").optDouble("total", 0));
            teamStats.setGoleStrzelonePomiedzyMinutami91_105(goalsMinsFor.getJSONObject("91-105").optDouble("total", 0));

            JSONObject goalsTotalAgainst = goals.getJSONObject("against").getJSONObject("total");
            JSONObject goalsAvgAgainst = goals.getJSONObject("against").getJSONObject("average");
            JSONObject goalsMinsAgainst = goals.getJSONObject("against").getJSONObject("minute");

            teamStats.setGoleStraconeWDomu(goalsTotalAgainst.optDouble("home", 0));
            teamStats.setGoleStraconeNaWyjezdzie(goalsTotalAgainst.optDouble("away", 0));

            teamStats.setSredniaGoliStraconychNaWyjezdzie(goalsAvgAgainst.optDouble("away", 0));
            teamStats.setSredniaGoliStraconychWDomu(goalsAvgAgainst.optDouble("home", 0));

            teamStats.setGoleStraconePomiedzyMinutami0_15(goalsMinsAgainst.getJSONObject("0-15").optDouble("total", 0));
            teamStats.setGoleStraconePomiedzyMinutami16_30(goalsMinsAgainst.getJSONObject("16-30").optDouble("total", 0));
            teamStats.setGoleStraconePomiedzyMinutami31_45(goalsMinsAgainst.getJSONObject("31-45").optDouble("total", 0));
            teamStats.setGoleStraconePomiedzyMinutami46_60(goalsMinsAgainst.getJSONObject("46-60").optDouble("total", 0));
            teamStats.setGoleStraconePomiedzyMinutami61_75(goalsMinsAgainst.getJSONObject("61-75").optDouble("total", 0));
            teamStats.setGoleStraconePomiedzyMinutami76_90(goalsMinsAgainst.getJSONObject("76-90").optDouble("total", 0));
            teamStats.setGoleStraconePomiedzyMinutami91_105(goalsMinsAgainst.getJSONObject("91-105").optDouble("total", 0));

            teamStats.setCzysteKontaWDomu(responseData.getJSONObject("clean_sheet").optDouble("home", 0));
            teamStats.setCzysteKontaNaWyjezdzie(responseData.getJSONObject("clean_sheet").optDouble("away", 0));

            teamStats.setMeczeBezGolaWDomu(responseData.getJSONObject("failed_to_score").optDouble("home", 0));
            teamStats.setMeczeBezGolaNaWyjezdzie(responseData.getJSONObject("failed_to_score").optDouble("away", 0));

            teamStats.setKarneStrzelone(responseData.getJSONObject("penalty").getJSONObject("scored").optDouble("total", 0));
            teamStats.setKarneNiestrzelone(responseData.getJSONObject("penalty").getJSONObject("missed").optDouble("total", 0));

            teamStats.setFormacja(responseData.getJSONArray("lineups").getJSONObject(0).optString("formation"));
            teamStats.setIleRazyWtejFormacji(responseData.getJSONArray("lineups").getJSONObject(0).optDouble("played", 0));

            JSONObject cardsYellow = responseData.getJSONObject("cards").getJSONObject("yellow");

            teamStats.setKartkiZolteWMinucie0_15(cardsYellow.getJSONObject("0-15").optDouble("total", 0));
            teamStats.setKartkiZolteWMinucie16_30(cardsYellow.getJSONObject("16-30").optDouble("total", 0));
            teamStats.setKartkiZolteWMinucie31_45(cardsYellow.getJSONObject("31-45").optDouble("total", 0));
            teamStats.setKartkiZolteWMinucie46_60(cardsYellow.getJSONObject("46-60").optDouble("total", 0));
            teamStats.setKartkiZolteWMinucie61_75(cardsYellow.getJSONObject("61-75").optDouble("total", 0));
            teamStats.setKartkiZolteWMinucie76_90(cardsYellow.getJSONObject("76-90").optDouble("total", 0));
            teamStats.setKartkiZolteWMinucie91_105(cardsYellow.getJSONObject("91-105").optDouble("total", 0));

            JSONObject cardsRed = responseData.getJSONObject("cards").getJSONObject("red");

            teamStats.setKartkiCzerwoneWMinucie0_15(cardsRed.getJSONObject("0-15").optDouble("total", 0));
            teamStats.setKartkiCzerwoneWMinucie16_30(cardsRed.getJSONObject("16-30").optDouble("total", 0));
            teamStats.setKartkiCzerwoneWMinucie31_45(cardsRed.getJSONObject("31-45").optDouble("total", 0));
            teamStats.setKartkiCzerwoneWMinucie46_60(cardsRed.getJSONObject("46-60").optDouble("total", 0));
            teamStats.setKartkiCzerwoneWMinucie61_75(cardsRed.getJSONObject("61-75").optDouble("total", 0));
            teamStats.setKartkiCzerwoneWMinucie76_90(cardsRed.getJSONObject("76-90").optDouble("total", 0));
            teamStats.setKartkiCzerwoneWMinucie91_105(cardsRed.getJSONObject("91-105").optDouble("total", 0));
            teamStatsRepository.save(teamStats);
        }
    }

    public void getSumSum() {
        double[] weights = {1.0, 0.5, 0.3, -0.2};
        Iterable<SredniaDruzyny> allTeams = sredniaDruzynyRepository.findAll();
        for (SredniaDruzyny team : allTeams) {
            Optional<SredniaZeWszystkiego> optional = avgAllRepository
                    .findSredniaZeWszystkiegoByTeamIdAndSeasonAndCzyUwzglednionePozycje(team.getTeamId(), team.getSeason(), false);
            if (optional.isPresent()) {
                SredniaZeWszystkiego updateTeam = optional.get();
                avgAllRepository.delete(updateTeam);
            }
            SredniaZeWszystkiego avgTeam = getSredniaZeWszystkiego(team, weights, false);
            avgAllRepository.save(avgTeam);
        }
    }

    public void getSumSumWPos() {
        double[] weights = {1.0, 0.5, 0.3, -0.2};
        Iterable<SredniaDruzynyPozycjeUwzglednione> allTeams = srDruzynyPozycjeRepository.findAll();
        for (SredniaDruzynyPozycjeUwzglednione team : allTeams) {
            Optional<SredniaZeWszystkiego> optional = avgAllRepository
                    .findSredniaZeWszystkiegoByTeamIdAndSeasonAndCzyUwzglednionePozycje(team.getTeamId(), team.getSeason(), true);
            if (optional.isPresent()) {
                SredniaZeWszystkiego updateTeam = optional.get();
                avgAllRepository.delete(updateTeam);
            }
            SredniaZeWszystkiego avgTeam = getSredniaZeWszystkiegoPos(team, weights, true);

            avgAllRepository.save(avgTeam);
        }
    }
    private SredniaZeWszystkiego getSredniaZeWszystkiegoPos(SredniaDruzynyPozycjeUwzglednione team, double[] weights, boolean isPos) {
        double summaryWeight = 0.0;

        SredniaZeWszystkiego avgTeam = new SredniaZeWszystkiego();

        summaryWeight += (team.getDryblingSkutecznosc() * weights[0]);
        summaryWeight += (team.getPodaniaKreatywnosc() * weights[1]);
        summaryWeight += (team.getObronaKotrolaPrzeciwnika() * weights[2]);
        summaryWeight += (team.getFizycznoscInterakcje() * weights[3]);

        double sum = 0;
        for (double x: weights) sum += x;

        avgTeam.setRaiting(summaryWeight / sum);
        avgTeam.setTeamName(team.getTeamName());
        avgTeam.setTeamId(team.getTeamId());
        avgTeam.setSeason(team.getSeason());
        avgTeam.setLeagueId(team.getLeagueId());
        avgTeam.setCzyUwzglednionePozycje(isPos);
        return avgTeam;
    }

    private SredniaZeWszystkiego getSredniaZeWszystkiego(SredniaDruzyny team, double[] weights, boolean isPos) {
        double summaryWeight = 0.0;

        SredniaZeWszystkiego avgTeam = new SredniaZeWszystkiego();


        summaryWeight += (team.getDryblingSkutecznosc() * weights[0]);
        summaryWeight += (team.getPodaniaKreatywnosc() * weights[1]);
        summaryWeight += (team.getObronaKotrolaPrzeciwnika() * weights[2]);
        summaryWeight += (team.getFizycznoscInterakcje() * weights[3]);

        double sum = 0;
        for (double x: weights) sum += x;


        avgTeam.setRaiting(summaryWeight / sum);
        avgTeam.setTeamName(team.getTeamName());
        avgTeam.setTeamId(team.getTeamId());
        avgTeam.setSeason(team.getSeason());
        avgTeam.setCzyUwzglednionePozycje(isPos);
        avgTeam.setLeagueId(team.getLeagueId());
        return avgTeam;
    }

    public List<Double> getAllRaitings(Iterable<SredniaZeWszystkiego> a, Iterable<SredniaZeWszystkiego> b,
                                       Iterable<SredniaZeWszystkiego> c, Iterable<SredniaZeWszystkiego> d) {
        List<Double> raitings = new ArrayList<>();
        for (SredniaZeWszystkiego team : a) raitings.add(team.getRaiting());
        for (SredniaZeWszystkiego team : b) raitings.add(team.getRaiting());
        for (SredniaZeWszystkiego team : c) raitings.add(team.getRaiting());
        for (SredniaZeWszystkiego team : d) raitings.add(team.getRaiting());
        return raitings;
    }
    public Iterable<StatystykiDruzyny> findAllTeams() {
        return teamStatsRepository.findAll();
    }
}
