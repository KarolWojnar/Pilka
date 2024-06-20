package com.Football.football.Controllers.Fixtures;

import com.Football.football.Services.FixturesService;
import com.Football.football.Tables.FixturesStats;
import com.Football.football.Tables.PlayerStats;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public String getFixtures(@RequestParam("teamName") String teamName,
                              @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              @RequestParam("rounding") String rounding,
                              Model model) throws JsonProcessingException {
        fixtureService.getRatingsByDateAndTeamId(teamName, startDate, endDate, rounding, model);
        return "fixtures";
    }

    @GetMapping
    public String getFixtureView() {
        return "fixtures";
    }
}
