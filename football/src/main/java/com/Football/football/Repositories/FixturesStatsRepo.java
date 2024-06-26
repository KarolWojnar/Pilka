package com.Football.football.Repositories;

import com.Football.football.Tables.FixturesStats;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FixturesStatsRepo extends CrudRepository<FixturesStats, Long> {
    List<FixturesStats> findByTeamStatsAndFixtureDateBetween(TeamStats teamStats, LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT distinct fs.fixtureId FROM FixturesStats fs")
    List<Integer> getAllFixturesIDs();
    List<FixturesStats> findByFixtureId(int id);

}
