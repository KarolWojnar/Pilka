package com.Football.football.Controllers;

import com.Football.football.Repositories.FixturesRepository;
import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Repositories.TeamStatsRepository;
import com.Football.football.Services.FixturesService;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
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
    FixturesRepository fixturesRepository;

    @Autowired
    private TeamStatsService teamStatsService;

    @Autowired
    private PlayerStatsService playerStatsService;

    @Autowired
    private FixturesService fixturesService;

    @GetMapping("/getStatsForSeason/{teamId}&{year}&{leagueId}")
    public String giveTeam(@PathVariable int teamId, @PathVariable int year, @PathVariable int leagueId) throws IOException, InterruptedException, JSONException {
        teamStatsService.updateTeamStats(teamId, year, leagueId);
        return "index";
    }

    @GetMapping("/getPlayers/{id}&{year}&{leagueId}")
    public String give(@PathVariable int id, @PathVariable int year, @PathVariable int leagueId, Model model) throws IOException, InterruptedException, JSONException {
        model.addAttribute("team", playerStatsService.updatePlayerStats(id, year, leagueId));
        return "index";
    }


    @GetMapping("/fixture/{id}")
    public String getFixtures(@PathVariable int teamId) throws IOException, InterruptedException, JSONException {
        fixturesService.getFixtures(teamId);
        return "index";
    }

}
