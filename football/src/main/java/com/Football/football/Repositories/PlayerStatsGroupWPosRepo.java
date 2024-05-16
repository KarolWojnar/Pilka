package com.Football.football.Repositories;

import com.Football.football.Tables.PlayersStatsGroupWPos;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerStatsGroupWPosRepo extends CrudRepository<PlayersStatsGroupWPos, Long> {
    Optional<PlayersStatsGroupWPos> getPogrypowaneStatsZawodPozycjeUwzglednioneByPlayerIdAndSeason(Long id, Long season);
    List<PlayersStatsGroupWPos> getPogrypowaneStatsZawodPozycjeUwzglednioneByTeamIdAndSeason(Long id, Long season);
}
