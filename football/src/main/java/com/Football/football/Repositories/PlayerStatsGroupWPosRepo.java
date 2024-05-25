package com.Football.football.Repositories;

import com.Football.football.Tables.PlayerStats;
import com.Football.football.Tables.PlayersStatsGroupWPos;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerStatsGroupWPosRepo extends CrudRepository<PlayersStatsGroupWPos, Long> {
    Optional<PlayersStatsGroupWPos> getPlayerStatsGroupWPosByPlayerStatsAndSeason(PlayerStats playerStats, Long season);
    List<PlayersStatsGroupWPos> getPlayerStatsGroupWPosByTeamStatsAndSeason(TeamStats teamStats, Long season);
}
