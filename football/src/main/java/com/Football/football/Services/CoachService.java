package com.Football.football.Services;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Tables.CoachTeam;
import com.Football.football.Tables.Role;
import com.Football.football.Tables.TeamStats;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CoachRepository coachRepository;

    public String saveTeam(CoachTeam coach, TeamStats team, Role role) throws Exception {
        coach.setPassword(bCryptPasswordEncoder.encode(coach.getPassword()));
        coach.setTeamStats(List.of(team));
        coach.setRoles(List.of(role));
        coachRepository.save(coach);
        return coach.getLogin();
    }
}
