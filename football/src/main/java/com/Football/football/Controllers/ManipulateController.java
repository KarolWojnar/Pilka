package com.Football.football.Controllers;

import com.Football.football.Repositories.PogrupowaneRepository;
import com.Football.football.Repositories.SredniaDruzynyRepository;
import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Repositories.TeamStatsRepository;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import com.Football.football.Tables.PogrupowaneStatystykiZawodnikow;
import com.Football.football.Tables.SredniaDruzyny;
import com.Football.football.Tables.StatystykiDruzyny;
import com.Football.football.Tables.StatystykiZawodnika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class ManipulateController {

    @Autowired
    private PlayerStatsService playerStatsService;
    @Autowired
    private TeamStatsService teamStatsService;

    @GetMapping("/get-avg")
    public String getAvg(Model model) {
        playerStatsService.getAvgOfAllPlayers();
        return "index";
    }

    @GetMapping("/get-summary")
    public String getSum() {
        playerStatsService.getSummary();
        return "index";
    }
    @GetMapping("/get-avg-of-summary")
    public String getAvgOfSummary(Model model) {
        teamStatsService.getSumSum();
        return "index";
    }
}
