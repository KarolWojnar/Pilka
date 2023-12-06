package com.Football.football.Controllers;

import com.Football.football.Repositories.FixturesRepository;
import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Repositories.TeamStatsRepository;
import com.Football.football.Tables.StatystykiDruzyny;
import com.Football.football.Tables.StatystykiSpotkan;
import com.Football.football.Tables.StatystykiZawodnika;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Controller
public class FootballController {

    @Autowired
    private StatystykiZawodnikaRepository statystykiZawodnikaRepository;

    @Autowired
    private TeamStatsRepository teamStatsRepository;

    @Autowired
    FixturesRepository fixturesRepository;

    @GetMapping("/getStatsForSeason/{teamId}&{year}")
    public String giveTeam(Model model, @PathVariable Long teamId, @PathVariable Long year) throws IOException, InterruptedException, JSONException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-beta.p.rapidapi.com/teams/statistics?team=" + teamId +"&season=" + year))
                .header("X-RapidAPI-Key", "d33e623437msha2a56a1ea6f5bfbp18d606jsndd5dc6ff099b")
                .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response2 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response2.body();
        model.addAttribute("ess", responseBody);
        JSONObject jsonResponse = new JSONObject(responseBody);
        if (jsonResponse.has("response")) {
            JSONObject responseData = jsonResponse.getJSONObject("response");

            Optional<StatystykiDruzyny> optional = teamStatsRepository.getStatystykiDruzyniesByTeamIdAndSeason(responseData.getJSONObject("team").getLong("id"), year);
            if (optional.isPresent()) {
                StatystykiDruzyny updateTeam = optional.get();
                teamStatsRepository.delete(updateTeam);
            }
            StatystykiDruzyny teamStats = new StatystykiDruzyny();
            teamStats.setTeamId(teamId);
            teamStats.setSeason(year);
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

            teamStats.setFormaca(responseData.getJSONArray("lineups").getJSONObject(0).optString("formation"));
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
        return "index";
    }

    @GetMapping("/getPlayers/{id}&{year}")
    public String give(Model model, @PathVariable int id, @PathVariable int year) throws IOException, InterruptedException, JSONException {
        int teamId = id;
        int season = year;
        for (int page = 1; page <= 3; page++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/players?season=" + season + "&league=140&team=" + teamId + "&page=" + page))
                    .header("X-RapidAPI-Key", "d33e623437msha2a56a1ea6f5bfbp18d606jsndd5dc6ff099b")
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            model.addAttribute("ess", responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            if (jsonResponse.has("response")) {
                JSONArray statsArray = jsonResponse.getJSONArray("response");
                for(int i = 0; i < statsArray.length(); i++) {
                    if (statsArray.length() > 0) {
                        JSONObject playerStats = statsArray.getJSONObject(i);
                        int isActive = playerStats.getJSONArray("statistics").getJSONObject(0).getJSONObject("games").optInt("minutes", 0);
                        if (isActive == 0) continue;
                        StatystykiZawodnika player = new StatystykiZawodnika();
                        player.setTeamId((long) teamId);
                        player.setSeason((long) season);
                        player.setPlayerId(playerStats.getJSONObject("player").getLong("id"));
                        player.setImie(playerStats.getJSONObject("player").getString("firstname"));
                        player.setNazwisko(playerStats.getJSONObject("player").getString("lastname"));
                        player.setWiek(playerStats.getJSONObject("player").optDouble("age"));
                        player.setWzrost(Double.parseDouble(playerStats.getJSONObject("player").optString("height", "10 cm")
                                .replaceAll(" cm", "").replaceAll("\n", "0").replaceAll("null", "0")));
                        player.setWaga(Double.parseDouble(playerStats.getJSONObject("player").optString("weight", "10 kg")
                                .replaceAll(" kg", "").replaceAll("\n", "0").replaceAll("null", "0")));
                        player.setKraj(playerStats.getJSONObject("player").optString("nationality"));
                        player.setCzyKontuzjowany((playerStats.getJSONObject("player").getString("injured")).equals("true"));
                        if (playerStats.has("statistics")) {
                            JSONArray statisticsArray = playerStats.getJSONArray("statistics");
                            if (statisticsArray.length() > 0) {
                                JSONObject statistics = statisticsArray.getJSONObject(0);
                                if (statistics.has("games")) {
                                    JSONObject games = statistics.getJSONObject("games");
                                    player.setWystepy(games.optDouble("appearences", 0));
                                    player.setMinuty(games.optDouble("minutes", 0));
                                    player.setPozycja(games.getString("position"));
                                    player.setRating(games.optDouble("rating", 0));
                                    if ((player.getMinuty() == 0) || (player.getRating() == 0)) continue;
                                }
                                if (statistics.has("shots")) {
                                    JSONObject shots = statistics.getJSONObject("shots");
                                    player.setStrzaly(shots.optDouble("total", 0));
                                    player.setStrzalyCelne(shots.optDouble("on", 0));
                                }
                                if (statistics.has("goals")) {
                                    JSONObject goals = statistics.getJSONObject("goals");
                                    player.setGole(goals.optDouble("total", 0));
                                    player.setAsysty(goals.optDouble("assists", 0));
                                }
                                if (statistics.has("passes")) {
                                    JSONObject passes = statistics.getJSONObject("passes");
                                    player.setPodania(passes.optDouble("total", 0));
                                    player.setDokladnoscPodan(passes.optDouble("accuracy", 0));
                                    player.setPodaniaKluczowe(passes.optDouble("key", 0));
                                }
                                if (statistics.has("duels")) {
                                    JSONObject duels = statistics.getJSONObject("duels");
                                    player.setPojedynki(duels.optDouble("total", 0));
                                    player.setPojedynkiWygrane(duels.optDouble("won", 0));
                                }
                                if (statistics.has("dribbles")) {
                                    JSONObject dribbles = statistics.getJSONObject("dribbles");
                                    player.setDryblingi(dribbles.optDouble("attempts", 0));
                                    player.setDryblingiWygrane(dribbles.optDouble("success", 0));
                                }
                                if (statistics.has("fouls")) {
                                    JSONObject fouls = statistics.getJSONObject("fouls");
                                    player.setFaulePopelnione(fouls.optDouble("committed", 0));
                                    player.setFauleNaZawodniku(fouls.optDouble("drawn", 0));
                                }
                                if (statistics.has("cards")) {
                                    JSONObject cards = statistics.getJSONObject("cards");
                                    player.setKartkiZolte(cards.optDouble("yellow", 0));
                                    player.setKartkiCzerwone(cards.optDouble("red", 0));
                                }
                                if (statistics.has("tackles")) {
                                    JSONObject tackles = statistics.getJSONObject("tackles");
                                    player.setProbyPrzechwytu(tackles.optDouble("total", 0));
                                    player.setPrzechwytyUdane(tackles.optDouble("interceptions", 0));
                                }
                            }
                        }
                        statystykiZawodnikaRepository.save(player);
                    }
                }
            }
        }
        return "index";
    }


    @GetMapping("/fixture")
    public String getFixtures() throws IOException, InterruptedException, JSONException {
        int teamId = 529;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-beta.p.rapidapi.com/fixtures?season=2022&team=" + teamId))
                .header("X-RapidAPI-Key", "d33e623437msha2a56a1ea6f5bfbp18d606jsndd5dc6ff099b")
                .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        JSONObject jsonResponse = new JSONObject(responseBody);

        if (jsonResponse.has("response")) {
            JSONArray jsonArray = jsonResponse.getJSONArray("response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject fixture = jsonArray.getJSONObject(i);
                StatystykiSpotkan statystykiSpotkan = new StatystykiSpotkan();
                if (fixture.has("fixture")) {
                    int idFixture = fixture.getJSONObject("fixture").getInt("id");
                    Optional<StatystykiSpotkan> optionalStatystykiSpotkan = fixturesRepository.findById((long) idFixture);
                    if (optionalStatystykiSpotkan.isPresent()) continue;
                    statystykiSpotkan.setIdSpotkania(idFixture);
                    statystykiSpotkan.setDataSpotkania(fixture.getJSONObject("fixture").getString("date"));

                    String result = fixture.getJSONObject("goals").getString("home") + ":" + fixture.getJSONObject("goals").getString("away");
                    statystykiSpotkan.setWynik(result);

                    String playedVs = fixture.getJSONObject("teams").getJSONObject("home").getString("name")
                            + " vs " + fixture.getJSONObject("teams").getJSONObject("away").getString("name");

                    statystykiSpotkan.setDruzyny(playedVs);
                    HttpRequest requestId = HttpRequest.newBuilder()
                            .uri(URI.create("https://api-football-beta.p.rapidapi.com/fixtures/statistics?fixture=" + idFixture))
                            .header("X-RapidAPI-Key", "d33e623437msha2a56a1ea6f5bfbp18d606jsndd5dc6ff099b")
                            .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                            .build();
                    HttpResponse<String> responseFixtureStats = HttpClient.newHttpClient().send(requestId, HttpResponse.BodyHandlers.ofString());

                    String responseFixtureBody = responseFixtureStats.body();
                    JSONObject jsonFixtureResponse = new JSONObject(responseFixtureBody);
                    if(jsonFixtureResponse.has("response")) {
                        JSONArray fixtureStatsArray = jsonFixtureResponse.getJSONArray("response");
                        for (int j = 0; j < fixtureStatsArray.length(); j++) {
                            JSONObject fixtureStats = fixtureStatsArray.getJSONObject(j);
                            if (fixtureStats.getJSONObject("team").getInt("id") == teamId) {
                                JSONArray statisticsArray = fixtureStats.getJSONArray("statistics");
                                statystykiSpotkan.setId(fixtureStats.getJSONObject("team").getLong("id"));
                                int x = 0;
                                statystykiSpotkan.setStrzalyCelne(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setStrzalyNiecelne(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setStrzaly(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setStrzalyZablokowane(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setStrzalyZPolaKarnego(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setStrzalyZzaPolaKarnego(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setFaule(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setRzutRozne(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setSpalone(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setPosiadaniePilkiWProcentach(Double.parseDouble(statisticsArray.getJSONObject(x++).optString("value", "0")
                                        .replaceAll("%", "")));
                                statystykiSpotkan.setKartkiZolte(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setKartkiCzerwone(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setObronyBramkarza(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setPodania(statisticsArray.getJSONObject(x++).optDouble("value", 0));
                                statystykiSpotkan.setPodaniaCelne(statisticsArray.getJSONObject(x).optDouble("value", 0));
                            }
                        }
                        fixturesRepository.save(statystykiSpotkan);
                    }
                }
            }
        }

        return "index";
    }

}
