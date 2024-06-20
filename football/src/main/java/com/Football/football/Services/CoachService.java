package com.Football.football.Services;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Tables.CoachTeam;
import com.Football.football.Tables.TeamStats;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoachService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CoachRepository coachRepository;

    public void saveTeam(CoachTeam coach, TeamStats team, Model model) throws Exception {
        coach.setPassword(bCryptPasswordEncoder.encode(coach.getPassword()));
        coach.setTeamStats(List.of(team));
        coachRepository.save(coach);
    }
}
