package com.Football.football.Repositories;

import com.Football.football.Tables.TeamGroupAvgWPos;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamGroupAvgWPosRepo extends CrudRepository<TeamGroupAvgWPos, Long> {
    Optional<TeamGroupAvgWPos> getSredniaDruzynyPozycjeUwzglednioneByTeamIdAndSeason(Long id, Long year);
    Iterable<TeamGroupAvgWPos> getSredniaDruzynyPozycjeUwzglednionesBySeason(Long year);
}
