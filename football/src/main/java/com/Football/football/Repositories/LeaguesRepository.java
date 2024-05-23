package com.Football.football.Repositories;

import com.Football.football.Tables.Leagues;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LeaguesRepository extends CrudRepository<Leagues, Long> {
    Optional<Leagues> getFirstByLeagueId(Long id);
}
