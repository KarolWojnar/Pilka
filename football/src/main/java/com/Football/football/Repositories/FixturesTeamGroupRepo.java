package com.Football.football.Repositories;

import com.Football.football.Tables.FixtureTeamsStats;
import com.Football.football.Tables.FixturesTeamGroup;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface FixturesTeamGroupRepo extends CrudRepository<FixturesTeamGroup, Long> {

}
