package com.Football.football.Repositories;

import com.Football.football.Tables.PlayersStatsGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerStatsGroupRepo extends CrudRepository<PlayersStatsGroup, Long> {
    Optional<PlayersStatsGroup> getPogrupowaneStatystykiZawodnikowByPlayerIdAndSeason(Long id, Long season);
    List<PlayersStatsGroup> getPogrupowaneStatystykiZawodnikowByTeamIdAndSeason(Long id, Long season);
}
