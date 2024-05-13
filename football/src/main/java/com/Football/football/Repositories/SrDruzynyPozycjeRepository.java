package com.Football.football.Repositories;

import com.Football.football.Tables.SredniaDruzynyPozycjeUwzglednione;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SrDruzynyPozycjeRepository extends CrudRepository<SredniaDruzynyPozycjeUwzglednione, Long> {
    Optional<SredniaDruzynyPozycjeUwzglednione> getSredniaDruzynyPozycjeUwzglednioneByTeamIdAndSeason(Long id, Long year);
    Iterable<SredniaDruzynyPozycjeUwzglednione> getSredniaDruzynyPozycjeUwzglednionesBySeason(Long year);
}
