package com.Football.football.Controllers;

import com.Football.football.Repositories.PogrupowaneRepository;
import com.Football.football.Repositories.SredniaDruzynyRepository;
import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Tables.PogrupowaneStatystykiZawodnikow;
import com.Football.football.Tables.SredniaDruzyny;
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

    @GetMapping("/get-avg")
    public String getAvg(Model model) {
        Iterable<StatystykiZawodnika> players = statystykiZawodnikaRepository.findAll();

        for (StatystykiZawodnika player: players) {

            Optional<PogrupowaneStatystykiZawodnikow> optionalPlayer = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByIdAndSeason(player.getId(), player.getSeason());

            PogrupowaneStatystykiZawodnikow zawodnik = new PogrupowaneStatystykiZawodnikow();
            zawodnik.setImie(player.getImie() + " " + player.getNazwisko());
            zawodnik.setId(player.getId());
            zawodnik.setTeamId(player.getTeamId());
            zawodnik.setSeason(player.getSeason());

            zawodnik.setPozycja(player.getPozycja());

            double minutes = player.getMinuty();

            double weightPasses = 1.0;
            double weightDribblingGoals = 1.5;
            double weightAggression = 0.8;
            double weightDefenseAndControl = 1.2;

            double accuracyPerMinute = ((player.getPodania() * (player.getDokladnoscPodan() / 100)) / minutes) * weightPasses;
            double keysPerMinute = (player.getPodaniaKluczowe() / minutes) * weightPasses;
            double assistsPerMinute = (player.getAsysty() / minutes) * weightPasses;
            double summaryPasses = accuracyPerMinute + keysPerMinute + assistsPerMinute;
            zawodnik.setPodaniaKreatywnosc(summaryPasses);

            double wonDribblingsPerMinute = (player.getDryblingiWygrane() / minutes) * weightDribblingGoals;
            double shotsOnGoalPerMinute = (player.getStrzalyCelne() / minutes) * weightDribblingGoals;
            double goalsPerMinute = (player.getGole() / minutes) * weightDribblingGoals;
            double summaryDribblingGoals = wonDribblingsPerMinute + shotsOnGoalPerMinute + goalsPerMinute;
            zawodnik.setDryblingSkutecznosc(summaryDribblingGoals);

            double foulsCommitedPerMinute = (player.getFaulePopelnione() / minutes) * weightAggression;
            double redCards = (player.getKartkiCzerwone() / minutes) * weightAggression;
            double yellowCards = (player.getKartkiZolte() / minutes) * weightAggression;
            double duelsLostPerMinute = ((player.getPojedynki() - player.getPojedynkiWygrane()) / minutes) * weightAggression;
            double summaryAggression = foulsCommitedPerMinute + redCards + yellowCards + duelsLostPerMinute;
            zawodnik.setFizycznoscInterakcje(summaryAggression);

            double interceptionsWonPerMinute = (player.getPrzechwytyUdane() / minutes) * weightDefenseAndControl;
            double foulsDrawnPerMinute = (player.getFauleNaZawodniku() / minutes) * weightDefenseAndControl;
            double duelsWonPerMinute = (player.getPojedynkiWygrane() / minutes) * weightDefenseAndControl;
            double summaryDefenseAndControll = interceptionsWonPerMinute + foulsDrawnPerMinute + duelsWonPerMinute;
            zawodnik.setObronaKotrolaPrzeciwnika(summaryDefenseAndControll);

            if (optionalPlayer.isPresent()) {
                PogrupowaneStatystykiZawodnikow updatePlayer = optionalPlayer.get();
                pogrupowaneRepository.delete(updatePlayer);
            }
            pogrupowaneRepository.save(zawodnik);
        }
        return "index";
    }

    @GetMapping("/get-summary")
    public String getSum(Model model) {

        double wagaFizycznosc = 0.1;
        double wagaDrybling = 0.5;
        double wagaObrona = 0.2;
        double wagaPodania = 0.3;

        List<Object[]> combinationsTeamsAndSeasons = statystykiZawodnikaRepository.getDistinctBySeasonAndTeamId();
        for (Object[] singleCombination: combinationsTeamsAndSeasons) {
            Long season = (Long) singleCombination[0];
            Long teamId = (Long) singleCombination[1];

            List<PogrupowaneStatystykiZawodnikow> players = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByTeamIdAndSeason(teamId, season);
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

            avgFizycznoscInterakcje *= wagaFizycznosc;
            avgDryblingSkutecznosc *= wagaDrybling;
            avgObronaKotrolaPrzeciwnika *= wagaObrona;
            avgPodaniaKreatywanosc *= wagaPodania;

            SredniaDruzyny team = new SredniaDruzyny();
            team.setTeamId(teamId);
            team.setSeason(season);
            team.setDryblingSkutecznosc(avgDryblingSkutecznosc);
            team.setFizycznoscInterakcje(avgFizycznoscInterakcje);
            team.setPodaniaKreatywnosc(avgPodaniaKreatywanosc);
            team.setObronaKotrolaPrzeciwnika(avgObronaKotrolaPrzeciwnika);

            sredniaDruzynyRepository.save(team);
        }
        return "index";
    }
}
