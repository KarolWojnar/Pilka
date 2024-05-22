package com.Football.football.Services;

import com.Football.football.Repositories.FixturesStatsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FixturesService {

    private final FixturesStatsRepo fixturesRepository;

    @Autowired
    public FixturesService (FixturesStatsRepo fixturesRepository) {
        this.fixturesRepository = fixturesRepository;
    }

    public void getFixtures(int teamId) {
    }
}
