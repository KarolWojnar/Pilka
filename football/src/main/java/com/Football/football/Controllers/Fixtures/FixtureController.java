package com.Football.football.Controllers.Fixtures;

import com.Football.football.Services.FixturesService;
import com.Football.football.Tables.FixturesStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fixtures")
@RequiredArgsConstructor
public class FixtureController {
    private final FixturesService fixtureService;

    @PostMapping("/save")
    public String saveFixture() {
        fixtureService.saveAllFixtures();
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
}
