package com.Football.football.Repositories;

import com.Football.football.Tables.FixtureTeamsStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixtureTeamsStatsRepository extends CrudRepository<FixtureTeamsStats, Long> {
}
