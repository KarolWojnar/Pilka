package com.Football.football.Controllers.Admin;

import com.Football.football.Services.FixturesService;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final TeamStatsService teamStatsService;
    private final PlayerStatsService playerStatsService;
    private final FixturesService fixturesService;
    @GetMapping
    public String returnAdminPanel() {
        return "adminHandler";
    }

    @GetMapping("/getAll")
    @Transactional
    public String getLeagueById(@RequestParam("leagueId") long leagueId, @RequestParam("season") long season, Model model) throws Exception {
        teamStatsService.getAllTeamsByLeague(leagueId, season);
        playerStatsService.updatePlayersLeague(season, leagueId);
        fixturesService.saveAllFixtures(leagueId, season);
        model.addAttribute("success", "Teams, players and fixtures added successfull");
        return "adminHandler";
    }
}
