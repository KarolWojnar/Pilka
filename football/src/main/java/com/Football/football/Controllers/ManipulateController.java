package com.Football.football.Controllers;

import com.Football.football.Repositories.PogrupowaneRepository;
import com.Football.football.Repositories.SredniaDruzynyRepository;
import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Repositories.TeamStatsRepository;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Tables.PogrupowaneStatystykiZawodnikow;
import com.Football.football.Tables.SredniaDruzyny;
import com.Football.football.Tables.StatystykiDruzyny;
import com.Football.football.Tables.StatystykiZawodnika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class ManipulateController {
    @Autowired
    private PogrupowaneRepository pogrupowaneRepository;
    @Autowired
    private StatystykiZawodnikaRepository statystykiZawodnikaRepository;
    @Autowired
    private SredniaDruzynyRepository sredniaDruzynyRepository;
    @Autowired
    private TeamStatsRepository teamStatsRepository;
    @Autowired
    private PlayerStatsService playerStatsService;

    @GetMapping("/get-avg")
    public String getAvg(Model model) {
        playerStatsService.getAvgOfAllPlayers();
        return "index";
    }

    @GetMapping("/get-summary")
    public String getSum() {

        List<Object[]> combinationsTeamsAndSeasons = statystykiZawodnikaRepository.getDistinctBySeasonAndTeamId();
        for (Object[] singleCombination: combinationsTeamsAndSeasons) {
            Long season = (Long) singleCombination[0];
            Long teamId = (Long) singleCombination[1];

            List<PogrupowaneStatystykiZawodnikow> players = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByTeamIdAndSeason(teamId, season);
            Optional<SredniaDruzyny> optionalSredniaDruzyny = sredniaDruzynyRepository.getSredniaDruzynyByTeamIdAndSeason(teamId, season);
            if (optionalSredniaDruzyny.isPresent()) {
                SredniaDruzyny prevTeam = optionalSredniaDruzyny.get();
                sredniaDruzynyRepository.delete(prevTeam);
            }
            double sum = 0, avgPodaniaKreatywanosc = 0, avgDryblingSkutecznosc = 0, avgFizycznoscInterakcje = 0, avgObronaKotrolaPrzeciwnika = 0;

            for (PogrupowaneStatystykiZawodnikow player : players) {
                sum++;
                avgFizycznoscInterakcje += player.getFizycznoscInterakcje();
                avgDryblingSkutecznosc += player.getDryblingSkutecznosc();
                avgObronaKotrolaPrzeciwnika += player.getObronaKotrolaPrzeciwnika();
                avgPodaniaKreatywanosc += player.getPodaniaKreatywnosc();
            }

            avgFizycznoscInterakcje /= sum;
            avgDryblingSkutecznosc /= sum;
            avgObronaKotrolaPrzeciwnika /= sum;
            avgPodaniaKreatywanosc /= sum;

            SredniaDruzyny team = new SredniaDruzyny();
            team.setTeamId(teamId);
            team.setSeason(season);
            Optional<StatystykiDruzyny> optionalName = teamStatsRepository.findFirstByTeamId(teamId);
            optionalName.ifPresent(statystykiDruzyny -> team.setTeamName(statystykiDruzyny.getTeamName()));
            team.setDryblingSkutecznosc(avgDryblingSkutecznosc);
            team.setFizycznoscInterakcje(avgFizycznoscInterakcje);
            team.setPodaniaKreatywnosc(avgPodaniaKreatywanosc);
            team.setObronaKotrolaPrzeciwnika(avgObronaKotrolaPrzeciwnika);

            sredniaDruzynyRepository.save(team);
        }
        return "index";
    }
}
