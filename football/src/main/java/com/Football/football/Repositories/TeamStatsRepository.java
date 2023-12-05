package com.Football.football.Repositories;

import com.Football.football.Tables.StatystykiDruzynySezon2022;
import org.springframework.data.repository.CrudRepository;

public interface TeamStatsRepository extends CrudRepository<StatystykiDruzynySezon2022, Long> {
}
