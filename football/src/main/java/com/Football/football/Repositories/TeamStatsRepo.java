package com.Football.football.Repositories;

import com.Football.football.Tables.TeamStats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamStatsRepo extends CrudRepository<TeamStats, Long> {
    Optional<TeamStats> getStatystykiDruzyniesByTeamIdAndSeason(Long id, Long year);
    Optional<TeamStats> findFirstByTeamId(Long id);
    @Query("SELECT s FROM TeamStats s GROUP BY s.teamName")
    Iterable<TeamStats> getDistinctTeams();
}
