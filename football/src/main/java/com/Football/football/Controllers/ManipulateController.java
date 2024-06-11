package com.Football.football.Controllers;

import com.Football.football.Repositories.FixtureTeamsStatsRepository;
import com.Football.football.Repositories.FixturesTeamGroupRepo;
import com.Football.football.Repositories.PlayersStatsRepo;
import com.Football.football.Services.FixturesService;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import com.Football.football.Tables.FixtureTeamsStats;
import com.Football.football.Tables.FixturesTeamGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ManipulateController {

    private final PlayerStatsService playerStatsService;
    private final TeamStatsService teamStatsService;
    private final PlayersStatsRepo statystykiZawodnikaRepository;
    private final FixturesService fixtureService;
    private final FixtureTeamsStatsRepository fixtureTeamsStatsRepository;
    private final FixturesTeamGroupRepo fixturesTeamGroupRepo;

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

    @GetMapping("/sumByFixture")
    public String sumByFixture(Model model) {
        fixtureService.sumFixturesByTeam();
        return "index";
    }

    @GetMapping("/groupAll")
    public String groupAll() {
        Iterable<FixtureTeamsStats> allFixtures = fixtureTeamsStatsRepository.findAll();
        fixtureService.groupAllTeams(allFixtures, true);
        return "index";
    }

    @GetMapping("/getRatings")
    public String getRatings() {
        fixtureService.getRatings((List<FixturesTeamGroup>) fixturesTeamGroupRepo.findAll(), true);
        return "index";
    }
}
