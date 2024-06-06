package com.Football.football.Controllers;

import com.Football.football.Repositories.PlayersStatsRepo;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ManipulateController {

    private final PlayerStatsService playerStatsService;
    private final TeamStatsService teamStatsService;
    private final PlayersStatsRepo statystykiZawodnikaRepository;

    @GetMapping("/get-avg")
    public String getAvg() {
        playerStatsService.getAvgOfAllPlayers(statystykiZawodnikaRepository.findAll(), false, "All");
        return "index";
    }
    @GetMapping("/get-avg-by-possition")
    public String getAvgByPos() {
        playerStatsService.getAvgOfAllPlayers(statystykiZawodnikaRepository.getPlayerStatsByPozycja("Attacker"), true, "Player");
        playerStatsService.getAvgOfAllPlayers(statystykiZawodnikaRepository.getPlayerStatsByPozycja("Midfielder"), true, "Player");
        playerStatsService.getAvgOfAllPlayers(statystykiZawodnikaRepository.getPlayerStatsByPozycja("Defender"), true, "Player");
        playerStatsService.getAvgOfAllPlayers(statystykiZawodnikaRepository.findAll(), true, "GK");
        return "index";
    }

    @GetMapping("/get-summary")
    public String getSum() {
        playerStatsService.getSummary();
        return "index";
    }
    @GetMapping("/get-summary-by-possition")
    public String getSumWithPos() {
        playerStatsService.getSummaryWithPos();
        return "index";
    }
    @GetMapping("/get-avg-of-summary")
    public String getAvgOfSummary() {
        teamStatsService.getSumSum();
        return "index";
    }
    @GetMapping("/get-avg-of-summary-by-possition")
    public String getAvgOfSummaryWithPos() {
        teamStatsService.getSumSumWPos();
        return "index";
    }
}
