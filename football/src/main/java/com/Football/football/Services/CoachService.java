package com.Football.football.Services;

import com.Football.football.Repositories.CoachRepository;
import com.Football.football.Repositories.FixtureTeamsStatsRepository;
import com.Football.football.Tables.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CoachRepository coachRepository;
    private final FixtureTeamsStatsRepository fixtureTeamsStatsRepository;
    private final FixturesService fixturesService;

    public String saveTeam(CoachTeam coach, TeamStats team, Role role) {
        coach.setPassword(bCryptPasswordEncoder.encode(coach.getPassword()));
        coach.setTeamStats(List.of(team));
        coach.setRoles(List.of(role));
        coachRepository.save(coach);
        return coach.getLogin();
    }

    public void getRatingsByDateAndTeamId(long teamId, LocalDate startDate, LocalDate endDate, String rounding, Model model) throws JsonProcessingException {
        List<FixtureTeamsStats> tfS = fixtureTeamsStatsRepository.findAllByFixtureDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
        List<FixturesTeamGroup> ftg = fixturesService.groupAllTeams(tfS, false);
        List<FixturesTeamRating> ftr = fixturesService.getRatings(ftg, false);
        List<FixturesTeamRating> myTeam = ftr.stream()
                .filter(team -> team.getTeamStats().getTeamId() == teamId)
                .toList();

        List<LocalDate> periodStartDates = new ArrayList<>();
        LocalDate periodStartDate = startDate;

        if ("week".equals(rounding)) {
            while (!periodStartDate.isAfter(endDate)) {
                periodStartDates.add(periodStartDate);
                periodStartDate = periodStartDate.plusWeeks(1);
            }
        } else if ("month".equals(rounding)) {
            while (!periodStartDate.isAfter(endDate)) {
                periodStartDates.add(periodStartDate);
                periodStartDate = periodStartDate.plusMonths(1);
            }
        }
        List<Double> avgRatings = new ArrayList<>(Collections.nCopies(periodStartDates.size(), 0.0));
        List<Double> myTeamRatings = new ArrayList<>(Collections.nCopies(periodStartDates.size(), 0.0));

        for (int i = 0; i < periodStartDates.size(); i++) {
            periodStartDate = periodStartDates.get(i);
            LocalDate periodEndDate = (i == periodStartDates.size() - 1) ? endDate : periodStartDates.get(i + 1).minusDays(1);

            double periodAverage = fixturesService.calculatePeriodAverage(myTeam, periodStartDate, periodEndDate);
            double periodAverage2 = fixturesService.calculatePeriodAverage(ftr, periodStartDate, periodEndDate);
            avgRatings.set(i, periodAverage2);

            if (periodAverage != 0.0) {
                myTeamRatings.set(i, periodAverage);
            }
        }
        List<String> dates = periodStartDates.stream().map(LocalDate::toString).collect(Collectors.toList());
        dates.removeLast();
        myTeamRatings.removeLast();
        avgRatings.removeLast();

        ObjectMapper objectMapper = new ObjectMapper();
        String datesJson = objectMapper.writeValueAsString(dates);

        model.addAttribute("datesJson", datesJson);
        model.addAttribute("myTeamRatings", myTeamRatings);
        model.addAttribute("averageRatings", avgRatings);
        if (!myTeam.isEmpty()) {
            model.addAttribute("teamName", myTeam.get(0).getTeamStats().getTeamName());
        } else {
            model.addAttribute("teamName", "Unknown Team");
        }
    }

    public void getPlayers(long teamId, LocalDate startDate, LocalDate endDate, Model model) {
        List<FixtureTeamsStats> tfS = fixtureTeamsStatsRepository.findAllByFixtureDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
        List<FixturesTeamGroup> ftg = fixturesService.groupAllTeams(tfS, false);

        List<FixturesTeamGroup> myTeam = ftg.stream()
                .filter(teams -> teams.getTeamStats().getTeamId() == teamId)
                .toList();

        model.addAttribute("TeamB", calculateAvg(ftg, "Avg"));
        model.addAttribute("TeamA", calculateAvg(myTeam, myTeam.getFirst().getTeamName()));
    }
    private FixturesTeamGroup calculateAvg(List<FixturesTeamGroup> avgTeams, String name) {
        double sum = 0, diS = 0, fiI = 0, oiKP = 0, piK = 0;

        for(FixturesTeamGroup fAll : avgTeams) {
            sum++;
            diS += fAll.getDryblingSkutecznosc();
            fiI += fAll.getFizycznoscInterakcje();
            oiKP += fAll.getObronaKotrolaPrzeciwnika();
            piK += fAll.getPodaniaKreatywnosc();
        }
        FixturesTeamGroup avgTeamGroup = new FixturesTeamGroup();
        avgTeamGroup.setDryblingSkutecznosc(diS / sum);
        avgTeamGroup.setFizycznoscInterakcje(fiI / sum);
        avgTeamGroup.setObronaKotrolaPrzeciwnika(oiKP / sum);
        avgTeamGroup.setPodaniaKreatywnosc(piK / sum);
        avgTeamGroup.setTeamName(name);
        return avgTeamGroup;
    }
}
