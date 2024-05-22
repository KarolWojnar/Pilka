package com.Football.football.Repositories;

import com.Football.football.Tables.TeamAvg;
import com.Football.football.Tables.TeamStats;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamAvgRepo extends CrudRepository<TeamAvg, Long> {
    Optional<TeamAvg> findSredniaZeWszystkiegoByTeamStatsAndSeasonAndCzyUwzglednionePozycje(TeamStats teamStats, Long year, boolean isPos);
    Iterable<TeamAvg> getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(TeamStats teamStats, boolean isPozycja);

}
