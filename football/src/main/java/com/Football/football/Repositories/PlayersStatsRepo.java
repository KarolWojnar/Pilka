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

    @Query("SELECT DISTINCT s.season, s.teamStats FROM PlayerStats s")
    List<Object[]> getDistinctBySeasonAndTeamStats();

    Iterable<PlayerStats> findPlayerStatsByTeamStats(TeamStats team);
    Iterable<PlayerStats> findPlayerStatsBySeason(long season);
}
