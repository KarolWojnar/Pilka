package com.Football.football.Controllers;

import com.Football.football.Services.FixturesService;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Controller
@RequiredArgsConstructor
public class FootballController {

    private final TeamStatsService teamStatsService;

    private final PlayerStatsService playerStatsService;

    private final FixturesService fixturesService;

    @GetMapping("/getStatsForSeason/{teamId}&{year}&{leagueId}")
    public String giveTeam(@PathVariable Long teamId, @PathVariable Long year, @PathVariable Long leagueId) throws IOException, InterruptedException, JSONException {
        teamStatsService.updateTeamStats(teamId, year, leagueId);
        return "index";
    }
    @GetMapping("getLeagueAll/{leagueId}&{season}")
    String getAllTeamsByLeague(@PathVariable Long leagueId, @PathVariable Long season) throws IOException, InterruptedException, JSONException {
        teamStatsService.getAllTeamsByLeague(leagueId, season);
        return "index";
    }


    @GetMapping("/getPlayers/{id}&{year}&{leagueId}")
    public String give(@PathVariable Long id, @PathVariable Long year, @PathVariable Long leagueId, Model model) throws IOException, InterruptedException, JSONException {
        model.addAttribute("team", playerStatsService.updatePlayerStats(id, year, leagueId));
        return "index";
    }


    @GetMapping("/fixture/{id}")
    public String getFixtures(@PathVariable int teamId) {
        fixturesService.getFixtures(teamId);
        return "index";
    }
    @PostMapping("/fixtures")
    public String getFixturesBySeason(@RequestParam("id") int id,
                                      @RequestParam("season") int season, Model model)  {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-football-beta.p.rapidapi.com/fixtures?season=" + season + "&league=" + id))
                    .header("X-RapidAPI-Key", "d33e623437msha2a56a1ea6f5bfbp18d606jsndd5dc6ff099b")
                    .header("X-RapidAPI-Host", "api-football-beta.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            model.addAttribute("error", "Błąd podczas przetawrzania danych: "
            + e.getMessage());
            return "getFixtures";
        }
        return "redirect:/getFixtures";
    }

}
