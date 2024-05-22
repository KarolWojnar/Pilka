package com.Football.football.Repositories;

import com.Football.football.Tables.TeamGroupAvgWPos;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamGroupAvgWPosRepo extends CrudRepository<TeamGroupAvgWPos, Long> {
    Optional<TeamGroupAvgWPos> getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(TeamStats teamStats, Long year);
    Iterable<TeamGroupAvgWPos> getSredniaDruzynyPozycjeUwzglednionesBySeason(Long year);
}
