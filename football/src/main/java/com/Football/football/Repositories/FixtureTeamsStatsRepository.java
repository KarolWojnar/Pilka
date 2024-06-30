package com.Football.football.Repositories;

import com.Football.football.Tables.FixtureTeamsStats;

import com.Football.football.Tables.TeamStats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FixtureTeamsStatsRepository extends CrudRepository<FixtureTeamsStats, Long> {
    List<FixtureTeamsStats> findAllByFixtureDateBetween(LocalDateTime fixtureDate, LocalDateTime fixtureDate2);
    @Query(value = "SELECT id_druzyny FROM team_stats WHERE `nazwa drużyny` LIKE CONCAT('%', :name, '%') LIMIT 1", nativeQuery = true)
    Optional<Long> findIdTeam(@Param("name") String name);
    Optional<FixtureTeamsStats> findByTeamStats(TeamStats teamStats);

}
