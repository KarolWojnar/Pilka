package com.Football.football.Services;

import com.Football.football.Repositories.FixturesStatsRepo;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FixturesService {
    private final FixturesStatsRepo fixtureRepository;

    public void saveAllFixtures() {
        // TODO: Implementacja zapisu wszystkich spotkań
    }

    public void updateMatch(Long id) {
        // TODO: Implementacja aktualizacji spotkania
    }

    public void createMatch(Long id) {
        // TODO: Implementacja tworzenia spotkania
    }

    public void getFixture(int teamId) {
        // TODO: Pobranie jednego spotkania
    }
}
