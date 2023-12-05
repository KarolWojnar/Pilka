package com.Football.football.Controllers;

import com.Football.football.Repositories.PogrupowaneRepository;
import com.Football.football.Repositories.SredniaDruzynyRepository;
import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Repositories.TeamStatsRepository;
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

    @GetMapping("/get-avg")
    public String getAvg(Model model) {
        Iterable<StatystykiZawodnika> players = statystykiZawodnikaRepository.findAll();
        Iterable<StatystykiDruzyny> teams = teamStatsRepository.findAll();

        double fixturesCount = 0.0;
        double goleAsystySuma = 0.0;
        double celnePodaniaSuma = 0.0;
        double kluczowePodaniaSuma = 0.0;
        double wygraneDryblingiSuma = 0.0;
        double strzalyNaBramkeSuma = 0.0;
        double faulePopelnioneSuma = 0.0;
        double kartkiCzerwoneSuma = 0.0;
        double kartkiZolteSuma = 0.0;
        double przegranePojedynkiSuma = 0.0;
        double przechwytyUdaneSuma = 0.0;
        double fauleNaZawodnikuSuma = 0.0;
        double pojedynkiWygraneSuma = 0.0;

        for (StatystykiZawodnika player : players) {
            celnePodaniaSuma += (player.getPodania() * player.getDokladnoscPodan());
            kluczowePodaniaSuma += player.getPodaniaKluczowe();
            wygraneDryblingiSuma += player.getDryblingiWygrane();
            strzalyNaBramkeSuma += player.getStrzalyCelne();
            faulePopelnioneSuma += player.getFaulePopelnione();
            kartkiCzerwoneSuma += player.getKartkiCzerwone();
            kartkiZolteSuma += player.getKartkiZolte();
            przegranePojedynkiSuma += (player.getPojedynki() - player.getPojedynkiWygrane());
            przechwytyUdaneSuma += player.getPrzechwytyUdane();
            fauleNaZawodnikuSuma += player.getFauleNaZawodniku();
            pojedynkiWygraneSuma += player.getPojedynkiWygrane();
        }

        for (StatystykiDruzyny team : teams) {
            fixturesCount += team.getSumaSpotkan();
            goleAsystySuma += team.getGoleStrzeloneNaWyjezdzie();
            goleAsystySuma += team.getGoleStrzeloneWDomu();
        }

        goleAsystySuma /= fixturesCount;
        celnePodaniaSuma /= fixturesCount;
        kluczowePodaniaSuma /= fixturesCount;
        wygraneDryblingiSuma /= fixturesCount;
        strzalyNaBramkeSuma /= fixturesCount;
        faulePopelnioneSuma /= fixturesCount;
        kartkiCzerwoneSuma /= fixturesCount;
        kartkiZolteSuma /= fixturesCount;
        przegranePojedynkiSuma /= fixturesCount;
        przechwytyUdaneSuma /= fixturesCount;
        fauleNaZawodnikuSuma /= fixturesCount;
        pojedynkiWygraneSuma /= fixturesCount;

        double goleAsystyWaga = 90 / goleAsystySuma;
        double celnePodaniaWaga = 90 / celnePodaniaSuma;
        double kluczowePodaniaWaga = 90 / kluczowePodaniaSuma;
        double wygraneDryblingiWaga = 90 / wygraneDryblingiSuma;
        double strzalyNaBramkeWaga = 90 / strzalyNaBramkeSuma;
        double faulePopelnioneWaga = 90 / faulePopelnioneSuma;
        double kartkiCzerwoneWaga = 90 / kartkiCzerwoneSuma;
        double kartkiZolteWaga = 90 / kartkiZolteSuma;
        double przegranePojedynkiWaga = 90 / przegranePojedynkiSuma;
        double przechwytyUdaneWaga = 90 / przechwytyUdaneSuma;
        double fauleNaZawodnikuWaga = 90 / fauleNaZawodnikuSuma;
        double pojedynkiWygraneWaga = 90 / pojedynkiWygraneSuma;

        for (StatystykiZawodnika player: players) {

            Optional<PogrupowaneStatystykiZawodnikow> optionalPlayer = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByPlayerIdAndSeason(player.getPlayerId(), player.getSeason());

            PogrupowaneStatystykiZawodnikow zawodnik = new PogrupowaneStatystykiZawodnikow();
            zawodnik.setImie(player.getImie() + " " + player.getNazwisko());
            zawodnik.setPlayerId(player.getPlayerId());
            zawodnik.setTeamId(player.getTeamId());
            zawodnik.setSeason(player.getSeason());

            zawodnik.setPozycja(player.getPozycja());

            double minutes = player.getMinuty();

            double accuracyPerMinute = ((player.getPodania() * (player.getDokladnoscPodan() / 100)) / minutes) * celnePodaniaWaga;
            double keysPerMinute = (player.getPodaniaKluczowe() / minutes) * kluczowePodaniaWaga;
            double assistsPerMinute = (player.getAsysty() / minutes) * goleAsystyWaga;
            double summaryPasses = accuracyPerMinute + keysPerMinute + assistsPerMinute;
            zawodnik.setPodaniaKreatywnosc(summaryPasses);

            double wonDribblingsPerMinute = (player.getDryblingiWygrane() / minutes) * wygraneDryblingiWaga;
            double shotsOnGoalPerMinute = (player.getStrzalyCelne() / minutes) * strzalyNaBramkeWaga;
            double goalsPerMinute = (player.getGole() / minutes) * goleAsystyWaga;
            double summaryDribblingGoals = wonDribblingsPerMinute + shotsOnGoalPerMinute + goalsPerMinute;
            zawodnik.setDryblingSkutecznosc(summaryDribblingGoals);

            double foulsCommitedPerMinute = (player.getFaulePopelnione() / minutes) * faulePopelnioneWaga;
            double redCards = (player.getKartkiCzerwone() / minutes) * kartkiCzerwoneWaga;
            double yellowCards = (player.getKartkiZolte() / minutes) * kartkiZolteWaga;
            double duelsLostPerMinute = ((player.getPojedynki() - player.getPojedynkiWygrane()) / minutes) * przegranePojedynkiWaga;
            double summaryAggression = foulsCommitedPerMinute + redCards + yellowCards + duelsLostPerMinute;
            zawodnik.setFizycznoscInterakcje(summaryAggression);

            double interceptionsWonPerMinute = (player.getPrzechwytyUdane() / minutes) * przechwytyUdaneWaga;
            double foulsDrawnPerMinute = (player.getFauleNaZawodniku() / minutes) * fauleNaZawodnikuWaga;
            double duelsWonPerMinute = (player.getPojedynkiWygrane() / minutes) * pojedynkiWygraneWaga;
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

        double wagaFizycznosc = 0.8;
        double wagaDrybling = 1.1;
        double wagaObrona = 1.2;
        double wagaPodania = 1.4;

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
