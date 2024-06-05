package com.Football.football.Repositories;

import com.Football.football.Tables.PlayerStats;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayersStatsRepo extends CrudRepository<PlayerStats, Long> {

    Optional<PlayerStats> getPlayerStatsByPlayerIdAndTeamStatsAndSeason(Long id, TeamStats teamId, Long season);
    Optional<PlayerStats> getPlayerStatsByPlayerIdAndSeason(Long id, Long season);

    Iterable<PlayerStats> getStatystykiZawodnikasByPozycja(String pos);

    @Query("select ps from PlayerStats ps group by ps.season, ps.teamStats")
    List<PlayerStats> getPlayerStatsGroupedBySeasonAndTeamStats();

    Iterable<PlayerStats> findPlayerStatsByTeamStats(TeamStats team);
    List<PlayerStats> findAllBySeason(long year);
}
