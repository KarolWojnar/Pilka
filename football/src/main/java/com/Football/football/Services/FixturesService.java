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
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FixturesService {
    private final FixturesStatsRepo fixtureRepository;
    private final ApiKeyManager apiKeyManager;
    private final TeamStatsRepo teamStatsRepo;
    private final PlayersStatsRepo playersStatsRepo;

    public void saveAllFixtures(Long leagueId, Long year) throws IOException, InterruptedException, JSONException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-beta.p.rapidapi.com/fixtures?season=" + year + "&league=" + leagueId))
                .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String rBody = response.body();
        System.out.println(rBody);
        apiKeyManager.incrementRequestCounter();
        JSONObject jResponse = new JSONObject(rBody);
        if (jResponse.has("response")) {
            JSONArray fixtures = jResponse.getJSONArray("response");
            for (int i = 0; i < fixtures.length(); i++) {
                JSONObject fixture = fixtures.getJSONObject(i);
                saveNewFixture(fixture, year);
            }
        }

    }

    private void saveNewFixture(JSONObject fixture, Long year) throws JSONException, IOException, InterruptedException {
        int fixtureId = fixture.getJSONObject("fixture").getInt("id");
        Optional<FixturesStats> optionalFixture = fixtureRepository.findById((long) fixtureId);
        if (optionalFixture.isPresent()) {
            return;
        }

        LocalDateTime date = LocalDateTime.parse(fixture.getJSONObject("fixture").getString("date"));
        JSONArray fixturePlayers = getFixtureStatsFromAPI(fixtureId);
        JSONObject team0 = fixturePlayers.getJSONObject(0);
        long teamId = team0.getJSONObject("team").getLong("id");
        Optional<TeamStats> opTeam = teamStatsRepo.findTeamStatsByTeamIdAndSeason(teamId, year);
        if (opTeam.isPresent()) {
            JSONArray players = team0.getJSONArray("players");

            for (int i = 0; i < players.length(); i++) {
                JSONObject player = players.getJSONObject(i);
                long playerId = player.getJSONObject("player").getLong("id");
                Optional<PlayerStats> optionalPlayer = playersStatsRepo
                        .getPlayerStatsByPlayerIdAndSeason(playerId, year);
                if (optionalPlayer.isPresent()) {
                    FixturesStats fixturePlayer = new FixturesStats();

                    fixturePlayer.setFixtureId(fixtureId);
                    fixturePlayer.setFixtureDate(date);
                    fixturePlayer.setTeamStats(opTeam.get());
                    fixturePlayer.setPlayerStats(optionalPlayer.get());
                    //TODO: DOKONCZ
                }

            }
        }

    }

    private JSONArray getFixtureStatsFromAPI(long fixtureId) throws IOException, InterruptedException, JSONException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-beta.p.rapidapi.com/fixtures/players?fixture=" + fixtureId))
                .header("X-RapidAPI-Key", apiKeyManager.getApiKey())
                .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        apiKeyManager.incrementRequestCounter();
        JSONObject fixturePlayers = new JSONObject(response.body());
        if (fixturePlayers.has("response")) {
            return fixturePlayers.getJSONArray("response");
        } else {
            throw new IOException("Brak spotkania o takim id");
        }
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
}
