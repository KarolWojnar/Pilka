package com.Football.football.Repositories;

import com.Football.football.Tables.CoachTeam;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CoachRepository extends CrudRepository<CoachTeam, Long> {
    Optional<CoachTeam> findByLogin(String name);
    Optional<CoachTeam> findByEmail(String email);
}
