package com.Football.football.Controllers;

import com.Football.football.Repositories.PogrupowaneRepository;
import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Tables.PogrupowaneStatystykiZawodnikow;
import com.Football.football.Tables.StatystykiZawodnika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class ManipulateController {
    @Autowired
    private PogrupowaneRepository pogrupowaneRepository;
    @Autowired
    private StatystykiZawodnikaRepository statystykiZawodnikaRepository;

    @GetMapping("/get-avg")
    public String getAvg(Model model) {
        Iterable<StatystykiZawodnika> players = statystykiZawodnikaRepository.findAll();

        int sum = 0;
        double sumSumPasses = 0;
        double sumSumDriblingGoals = 0;
        double sumSumAggression = 0;
        double sumSumDefenseAndControll = 0;

        for (StatystykiZawodnika player: players) {

            Optional<PogrupowaneStatystykiZawodnikow> optionalPlayer = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowById(player.getId());

            PogrupowaneStatystykiZawodnikow zawodnik = new PogrupowaneStatystykiZawodnikow();
            zawodnik.setImie(player.getImie() + " " + player.getNazwisko());
            zawodnik.setId(player.getId());

            zawodnik.setPozycja(player.getPozycja());

            double minutes = player.getMinuty();

// Wagi/mnożniki dla poszczególnych kategorii
            double weightPasses = 1.0;
            double weightDribblingGoals = 1.5; // Przykładowa większa waga dla kategorii związanej z dryblingiem i golami
            double weightAggression = 0.8;
            double weightDefenseAndControl = 1.2;

// Obliczenia z użyciem wag/mnożników
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

            sumSumPasses += summaryPasses;
            sumSumAggression += summaryAggression;
            sumSumDriblingGoals += summaryDribblingGoals;
            sumSumDefenseAndControll += summaryDefenseAndControll;
            sum++;

            if (optionalPlayer.isPresent()) {
                PogrupowaneStatystykiZawodnikow updatePlayer = optionalPlayer.get();
                pogrupowaneRepository.delete(updatePlayer);
            }
            pogrupowaneRepository.save(zawodnik);
        }

        PogrupowaneStatystykiZawodnikow avgFromAllPlayers = new PogrupowaneStatystykiZawodnikow();

        avgFromAllPlayers.setId(0L);
        avgFromAllPlayers.setImie("Średnia z wszystkich piłkarzy");
        avgFromAllPlayers.setPozycja("ALL");
        avgFromAllPlayers.setDryblingSkutecznosc(sumSumDriblingGoals / sum);
        avgFromAllPlayers.setFizycznoscInterakcje(sumSumAggression / sum);
        avgFromAllPlayers.setPodaniaKreatywnosc(sumSumPasses / sum);
        avgFromAllPlayers.setObronaKotrolaPrzeciwnika(sumSumDefenseAndControll / sum);
        pogrupowaneRepository.save(avgFromAllPlayers);
        return "index";
    }

}
