package com.Football.football.Repositories;

import com.Football.football.Tables.StatystykiDruzyny;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamStatsRepository extends CrudRepository<StatystykiDruzyny, Long> {
    Optional<StatystykiDruzyny> getStatystykiDruzyniesByTeamIdAndSeason(Long id, Long year);
    Optional<StatystykiDruzyny> findFirstByTeamId(Long id);
    @Query("SELECT s FROM StatystykiDruzyny s GROUP BY s.teamName")
    Iterable<StatystykiDruzyny> getDistinctTeams();
}
