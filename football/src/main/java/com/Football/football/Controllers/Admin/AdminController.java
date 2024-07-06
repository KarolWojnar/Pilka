package com.Football.football.Controllers.Admin;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Repositories.RoleRepository;
import com.Football.football.Repositories.TeamStatsRepo;
import com.Football.football.Services.CoachService;
import com.Football.football.Services.FixturesService;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final TeamStatsService teamStatsService;
    private final PlayerStatsService playerStatsService;
    private final FixturesService fixturesService;
    private final TeamStatsRepo teamStatsRepo;
    private final CoachRepository coachRepository;
    private final RoleRepository roleRepository;
    private final CoachService coachService;
    @GetMapping("/leagues")
    public String returnAdminPanel() {
        return "adminHandler";
    }
    @GetMapping("/users")
    public String returnUsersAdmin(Model model) {
        model.addAttribute("coaches", coachRepository.findAll());
        model.addAttribute("teams", teamStatsRepo.getDistinctTeams());
        model.addAttribute("roles", roleRepository.findAll());
        return "adminCoachHandler";
    }

    @PostMapping("/updateCoach")
    @ResponseBody
    public ResponseEntity<?> updateCoach(@RequestParam Map<String, String> params) {
        boolean success = coachService.updateCoach(params);
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/deleteCoach")
    @ResponseBody
    public ResponseEntity<?> deleteCoach(@RequestParam Long id) {
        boolean success = coachService.deleteCoach(id);
        return ResponseEntity.ok(Map.of("success", success));
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
