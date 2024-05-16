package com.Football.football.Repositories;

import com.Football.football.Tables.TeamAvg;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamAvgRepo extends CrudRepository<TeamAvg, Long> {
    Optional<TeamAvg> findSredniaZeWszystkiegoByTeamIdAndSeasonAndCzyUwzglednionePozycje(Long id, Long year, boolean isPos);
    Iterable<TeamAvg> getSredniaZeWszystkiegoByTeamIdAndCzyUwzglednionePozycjeOrderBySeasonAsc(Long teamId, boolean isPozycja);
    Iterable<TeamAvg> getSredniaZeWszystkiegoByLeagueIdAndSeasonAndCzyUwzglednionePozycjeOrderByRaitingDesc(int leagueId, int season, boolean withPositions);

}
