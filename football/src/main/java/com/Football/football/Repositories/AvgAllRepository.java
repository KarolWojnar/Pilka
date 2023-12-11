package com.Football.football.Repositories;

import com.Football.football.Tables.SredniaZeWszystkiego;
import org.springframework.data.repository.CrudRepository;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface AvgAllRepository extends CrudRepository<SredniaZeWszystkiego, Long> {
    Optional<SredniaZeWszystkiego> findSredniaZeWszystkiegoByTeamIdAndSeason(Long id, Long year);
    Iterable<SredniaZeWszystkiego> getSredniaZeWszystkiegoByTeamIdAndCzyUwzglednionePozycjeOrderBySeasonAsc(Long teamId, boolean isPozycja);
}
