package com.Football.football.Services;

import com.Football.football.Repositories.FixturesStatsRepo;
import com.Football.football.Tables.FixturesStats;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Optional;

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
