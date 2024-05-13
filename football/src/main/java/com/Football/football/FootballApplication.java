package com.Football.football;

import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class FootballApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(FootballApplication.class, args);
	}

}
