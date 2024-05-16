package com.Football.football.Services;

import com.Football.football.Repositories.FixturesStatsRepo;
import com.Football.football.Tables.FixturesStats;
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
import java.util.Optional;

@Service
public class FixturesService {

    private final FixturesStatsRepo fixturesRepository;

    @Autowired
    public FixturesService (FixturesStatsRepo fixturesRepository) {
        this.fixturesRepository = fixturesRepository;
    }

    public void getFixtures(int teamId) throws IOException, InterruptedException, JSONException {
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
                FixturesStats statystykiSpotkan = new FixturesStats();
                if (fixture.has("fixture")) {
                    int idFixture = fixture.getJSONObject("fixture").getInt("id");
                    Optional<FixturesStats> optionalStatystykiSpotkan = fixturesRepository.findById((long) idFixture);
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
    }
}
