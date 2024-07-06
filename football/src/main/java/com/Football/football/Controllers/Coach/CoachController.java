package com.Football.football.Controllers.Coach;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Repositories.FixturesTeamGroupRepo;
import com.Football.football.Repositories.RoleRepository;
import com.Football.football.Repositories.TeamStatsRepo;
import com.Football.football.Services.CoachService;
import com.Football.football.Tables.CoachTeam;
import com.Football.football.Tables.Role;
import com.Football.football.Tables.TeamStats;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequiredArgsConstructor
public class CoachController {
    private final TeamStatsRepo teamStatsRepo;
    private final CoachService coachService;
    private final CoachRepository coachRepository;
    private final RoleRepository roleRepository;
    private final FixturesTeamGroupRepo fixturesTeamGroupRepo;


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("teams", teamStatsRepo.getDistinctTeams());
        Iterable<Role> goodRoles = StreamSupport
                .stream(roleRepository.findAll().spliterator(), false)
                .filter(role -> !role.getName().equals("ADMIN"))
                .collect(Collectors.toList());
        model.addAttribute("roles", goodRoles);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute CoachTeam coach, @RequestParam long team, @RequestParam long roleId, Model model) throws Exception {
        TeamStats teamStats = teamStatsRepo.findFirstById(team).orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));
        model.addAttribute("login", coachService.saveTeam(coach, teamStats, role));
        return "/login";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/profile")
    public String profileCoach(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<CoachTeam> coach = coachRepository.findUser(auth.getName());
        Iterable<Role> goodRoles = StreamSupport
                .stream(roleRepository.findAll().spliterator(), false)
                .filter(role -> !role.getName().equals("ADMIN"))
                .collect(Collectors.toList());
        model.addAttribute("roles", goodRoles);
        model.addAttribute("teams", teamStatsRepo.getDistinctTeams());
        if (coach.isPresent()) {
            model.addAttribute("coach", coach.get());
        } else {
            model.addAttribute("error", "No profile data.");
        }
        return "coachProfile";
    }
    @GetMapping("/profile/stats")
    public String goToProfile(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "rounding", required = false, defaultValue = "week") String rounding,
            Model model) throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<CoachTeam> coach = coachRepository.findUser(auth.getName());
        if (coach.isPresent()) {
            model.addAttribute("coach", coach.get());
            LocalDate firstDate = fixturesTeamGroupRepo.getMinFixturesTeamGroup().get();
            if (startDate == null) {
                startDate = firstDate;
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            coachService.getRatingsByDateAndTeamId(coach.get().getTeamStats().getFirst().getTeamId(), startDate, endDate, rounding, model);
            coachService.getPlayers(coach.get().getTeamStats().getFirst().getTeamId(), startDate, endDate, model);
        }
        return "coachProfileStats";
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

    @PostMapping("/saveChanges")
    public String saveChanges(@ModelAttribute CoachTeam coachTeam, Model model) {
        return "redirect:/profile";
    }
}
