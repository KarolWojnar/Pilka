package com.Football.football.Repositories;

import com.Football.football.Tables.SredniaZeWszystkiego;
import org.springframework.data.repository.CrudRepository;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface AvgAllRepository extends CrudRepository<SredniaZeWszystkiego, Long> {
    Optional<SredniaZeWszystkiego> findSredniaZeWszystkiegoByTeamIdAndSeasonAndCzyUwzglednionePozycje(Long id, Long year,boolean isPos);
    Iterable<SredniaZeWszystkiego> getSredniaZeWszystkiegoByTeamIdAndCzyUwzglednionePozycjeOrderBySeasonAsc(Long teamId, boolean isPozycja);
}
