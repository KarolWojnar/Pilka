package com.Football.football.Repositories;

import com.Football.football.Tables.RealnePozycjeTabela;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.Optional;

public interface RealnePozycjeRepository extends CrudRepository<RealnePozycjeTabela, Long> {
    Optional<RealnePozycjeTabela> findFirstByLeagueIdAndYear(int leagueId, int year);
    Optional<RealnePozycjeTabela> findFirstByTeamId(Long teamId);
    Iterable<RealnePozycjeTabela> getRealnePozycjeTabelaByLeagueIdAndYearOrderByPozycja(int leagueId, int season);
}
