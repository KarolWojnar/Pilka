package com.Football.football.Controllers.Fixtures;

import com.Football.football.Services.FixturesService;
import com.Football.football.Tables.FixturesStats;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/fixtures")
@RequiredArgsConstructor
public class FixtureController {
    private final FixturesService fixtureService;

    @PostMapping("/save")
    public String saveFixture(@RequestBody FixturesStats fixture) {
        fixtureService.saveFixture(fixture);
        return "index";
    }

    @GetMapping("/update/{id}")
    public String updateFixture(@PathVariable Long id) {
        fixtureService.updateMatch(id);
        return "index";
    }

    @GetMapping("/create/{id}")
    public String createFixture(@PathVariable Long id) {
        fixtureService.createMatch(id);
        return "index";
    }

    @GetMapping("/saveAllByLeague/{leagueId}&{year}")
    public String saveAllByLeague(@PathVariable Long leagueId, @PathVariable Long year) throws IOException, InterruptedException, JSONException {
        fixtureService.saveAllFixtures(leagueId, year);
        return "index";
    }
}
