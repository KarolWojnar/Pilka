package com.Football.football.Repositories;

import com.Football.football.Tables.TeamGroupAvg;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamGroupAvgRepo extends CrudRepository<TeamGroupAvg, Long> {
    Optional<TeamGroupAvg> getSredniaDruzynyByTeamIdAndSeason(Long id, Long year);
}
