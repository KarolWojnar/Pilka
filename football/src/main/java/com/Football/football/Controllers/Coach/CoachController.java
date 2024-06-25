package com.Football.football.Controllers.Coach;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Repositories.RoleRepository;
import com.Football.football.Repositories.TeamStatsRepo;
import com.Football.football.Services.CoachService;
import com.Football.football.Tables.CoachTeam;
import com.Football.football.Tables.Role;
import com.Football.football.Tables.TeamStats;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CoachController {
    private final TeamStatsRepo teamStatsRepo;
    private final CoachService coachService;
    private final CoachRepository coachRepository;
    private final RoleRepository roleRepository;


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("teams", teamStatsRepo.getDistinctTeams());
        model.addAttribute("roles", roleRepository.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute CoachTeam coach, @RequestParam long team, @RequestParam long roleId, Model model) throws Exception {
        TeamStats teamStats = teamStatsRepo.findFirstById(team).orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));
        model.addAttribute("login", coachService.saveTeam(coach, teamStats, role));
        return "login";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/profile")
    public String goToProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<CoachTeam> coach = coachRepository.findUser(auth.getName());
        coach.ifPresent(coachTeam -> model.addAttribute("coach", coachTeam));
        return "coachProfile";
    }

    @GetMapping("/checkLogin")
    @ResponseBody
    public ResponseEntity<Boolean> checkLogin(@RequestParam("login") String login) {
        boolean exists = coachRepository.findCoachTeamByLogin(login).isPresent();
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/checkEmail")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String email) {
        boolean exists = coachRepository.findCoachTeamByEmail(email).isPresent();
        return ResponseEntity.ok(exists);
    }
}
