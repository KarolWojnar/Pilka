package com.Football.football.Repositories;

import com.Football.football.Tables.PlayerStats;
import com.Football.football.Tables.PlayersStatsGroup;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerStatsGroupRepo extends CrudRepository<PlayersStatsGroup, Long> {
    Optional<PlayersStatsGroup> getPogrupowaneStatystykiZawodnikowByPlayerStatsAndSeason(PlayerStats playerStats, Long season);
    List<PlayersStatsGroup> getPogrupowaneStatystykiZawodnikowByTeamStatsAndSeason(TeamStats teamStats, Long season);
}
