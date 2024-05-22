package com.Football.football.Repositories;

import com.Football.football.Tables.TeamGroupAvg;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamGroupAvgRepo extends CrudRepository<TeamGroupAvg, Long> {
    Optional<TeamGroupAvg> getSredniaDruzynyByTeamStatsAndSeason(TeamStats teamStats, Long year);
}
