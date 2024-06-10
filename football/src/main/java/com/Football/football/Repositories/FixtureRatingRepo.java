package com.Football.football.Repositories;

import com.Football.football.Tables.FixturesTeamRating;
import org.springframework.data.repository.CrudRepository;

public interface FixtureRatingRepo extends CrudRepository<FixturesTeamRating, Long> {
}
