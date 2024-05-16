package com.Football.football.Controllers;

import com.Football.football.Services.FixturesService;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class FootballController {

    private final TeamStatsService teamStatsService;

    private final PlayerStatsService playerStatsService;

    private final FixturesService fixturesService;

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
