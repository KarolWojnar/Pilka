package com.Football.football.Repositories;

import com.Football.football.Tables.StatystykiZawodnika;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StatystykiZawodnikaRepository extends CrudRepository<StatystykiZawodnika, Long> {


    Optional<StatystykiZawodnika> getStatystykiZawodnikaById(Long id);
    Optional<StatystykiZawodnika> getStatystykiZawodnikaByTeamIdAndSeason(Long id, Long season);
    List<StatystykiZawodnika> getStatystykiZawodnikaByImieContaining(String name);

    @Query("SELECT DISTINCT s.season, s.teamId FROM StatystykiZawodnika s")
    List<Object[]> getDistinctBySeasonAndTeamId();
}
