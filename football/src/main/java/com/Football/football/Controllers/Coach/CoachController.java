package com.Football.football.Controllers.Coach;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Repositories.TeamStatsRepo;
import com.Football.football.Services.CoachService;
import com.Football.football.Tables.CoachTeam;
import com.Football.football.Tables.TeamStats;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CoachController {
    private final TeamStatsRepo teamStatsRepo;
    private final CoachService coachService;
    private final CoachRepository coachRepository;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("teams", teamStatsRepo.getDistinctTeams());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute CoachTeam coach, @RequestParam long team, Model model) throws Exception {
        TeamStats teamStats = teamStatsRepo.findFirstById(team).orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));
        coachService.saveTeam(coach, teamStats, model);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/profile")
    public String goToProfile(Model model) {
        return "coachProfile";
    }

    private static final Logger logger = LoggerFactory.getLogger(CoachController.class);

    @GetMapping("/checkLogin")
    @ResponseBody
    public boolean checkLogin(@RequestParam String login) {
        logger.info("Checking login: " + login);
        return coachRepository.findByLogin(login).isPresent();
    }

    @GetMapping("/checkEmail")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        logger.info("Checking email: " + email);
        return coachRepository.findByEmail(email).isPresent();
    }
}
