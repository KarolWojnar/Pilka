package com.Football.football.Repositories;

import com.Football.football.Tables.PlayerStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayersStatsRepo extends CrudRepository<PlayerStats, Long> {


    Optional<PlayerStats> getStatystykiZawodnikaById(Long id);
    Optional<PlayerStats> getStatystykiZawodnikaByPlayerIdAndTeamIdAndSeason(int id, int teamId, int season);
    Optional<PlayerStats> getStatystykiZawodnikaByPlayerIdAndSeason(int id, int season);

    Iterable<PlayerStats> getStatystykiZawodnikasByPozycja(String pos);
    List<PlayerStats> getStatystykiZawodnikaByImieContaining(String name);

    @Query("SELECT DISTINCT s.season, s.teamId FROM PlayerStats s")
    List<Object[]> getDistinctBySeasonAndTeamId();
}
