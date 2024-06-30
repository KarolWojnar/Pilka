package com.Football.football.Repositories;

import com.Football.football.Tables.CoachTeam;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CoachRepository extends CrudRepository<CoachTeam, Long> {
    Optional<CoachTeam> findCoachTeamByLogin(String name);
    @Query("SELECT c FROM CoachTeam c LEFT JOIN FETCH c.roles WHERE c.login = :login")
    Optional<CoachTeam> findUser(@Param("login") String login);
    Optional<CoachTeam> findCoachTeamByEmail(String email);
}
