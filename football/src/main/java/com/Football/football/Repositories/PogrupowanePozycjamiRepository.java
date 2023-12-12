package com.Football.football.Repositories;

import com.Football.football.Tables.PogrupowaneStatsZawodPozycjeUwzglednione;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PogrupowanePozycjamiRepository extends CrudRepository<PogrupowaneStatsZawodPozycjeUwzglednione, Long> {
    Optional<PogrupowaneStatsZawodPozycjeUwzglednione> getPogrypowaneStatsZawodPozycjeUwzglednioneByPlayerIdAndSeason(Long id, Long season);
    List<PogrupowaneStatsZawodPozycjeUwzglednione> getPogrypowaneStatsZawodPozycjeUwzglednioneByTeamIdAndSeason(Long id, Long season);
}
