package com.Football.football.Controllers.Fixtures;

import com.Football.football.Services.FixturesService;
import com.Football.football.Tables.FixturesStats;
import com.Football.football.Tables.PlayerStats;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/fixtures")
@RequiredArgsConstructor
public class FixtureController {
    private final FixturesService fixtureService;

    @GetMapping("/saveAllByLeague/{leagueId}&{year}")
    public String saveAllByLeague(@PathVariable Long leagueId, @PathVariable Long year) throws IOException, InterruptedException, JSONException {
        fixtureService.saveAllFixtures(leagueId, year);
        return "index";
    }

    @PostMapping("/team")
    public String getFixtures(@RequestParam("teamId") long teamId,
                              @RequestParam("season") long season,
                              @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              Model model) {
        try {
            List<PlayerStats> fixtures = fixtureService.getFixturesForTeamAndSeason(teamId, season, startDate.atStartOfDay(), endDate.atStartOfDay());
            model.addAttribute("players", fixtures);
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving fixtures: " + e.getMessage());
        }
        return "fixtures";
    }

    @GetMapping
    public String getFixtureView() {
        return "fixtures";
    }

    @GetMapping("/sumByFixture")
    public String sumByFixture(Model model) {
        fixtureService.sumFixturesByTeam();
        return "index";
    }
}
