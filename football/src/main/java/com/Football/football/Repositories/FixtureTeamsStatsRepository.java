package com.Football.football.Repositories;

import com.Football.football.Tables.FixtureTeamsStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FixtureTeamsStatsRepository extends CrudRepository<FixtureTeamsStats, Long> {
    List<FixtureTeamsStats> findAllByFixtureDateBetween(LocalDateTime fixtureDate, LocalDateTime fixtureDate2);
}
