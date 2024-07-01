package com.Football.football.Repositories;

import com.Football.football.Tables.FixtureTeamsStats;
import com.Football.football.Tables.FixturesTeamGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FixturesTeamGroupRepo extends CrudRepository<FixturesTeamGroup, Long> {
    @Query("SELECT MIN(ft.fixtureDate) FROM FixtureTeamsStats ft")
    Optional<LocalDate> getMinFixturesTeamGroup();
}
