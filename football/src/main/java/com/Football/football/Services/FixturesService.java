package com.Football.football.Services;

import com.Football.football.Repositories.FixturesStatsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FixturesService {

    private final FixturesStatsRepo fixturesRepository;

    public void getFixtures(int teamId) {
    }
}
