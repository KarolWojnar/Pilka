package com.Football.football.Repositories;

import com.Football.football.Tables.SredniaDruzyny;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.Optional;

public interface SredniaDruzynyRepository extends CrudRepository<SredniaDruzyny, Long> {
    Optional<SredniaDruzyny> getSredniaDruzynyByTeamIdAndSeason(Long id, Long year);
}
