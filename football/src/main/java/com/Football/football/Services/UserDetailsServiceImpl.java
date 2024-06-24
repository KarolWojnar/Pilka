package com.Football.football.Services;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Tables.CoachTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CoachRepository coachRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CoachTeam coach = coachRepository.findCoachTeamByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.builder()
                .username(coach.getLogin())
                .password(coach.getPassword())
                .roles("USER")
                .build();
    }
}
