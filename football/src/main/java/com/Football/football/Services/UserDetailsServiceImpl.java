package com.Football.football.Services;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Tables.CoachTeam;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CoachRepository coachRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CoachTeam coachTeam = coachRepository.findUser(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Hibernate.initialize(coachTeam.getRoles());

        return new org.springframework.security.core.userdetails.User(
                coachTeam.getLogin(),
                coachTeam.getPassword(),
                coachTeam.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }
}
