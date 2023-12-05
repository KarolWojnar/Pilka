package com.Football.football.Controllers;

import com.Football.football.Repositories.FixturesRepository;
import com.Football.football.Repositories.PogrupowaneRepository;
import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Repositories.TeamStatsRepository;
import com.Football.football.Tables.PogrupowaneStatystykiZawodnikow;
import com.Football.football.Tables.StatystykiZawodnika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class ViewController {
    @Autowired
    private StatystykiZawodnikaRepository statystykiZawodnikaRepository;
    @Autowired
    private TeamStatsRepository teamStatsRepository;
    @Autowired
    private PogrupowaneRepository pogrupowaneRepository;
    @Autowired
    FixturesRepository fixturesRepository;

    @GetMapping("/player/{id}")
    public String getProfilPlayer(@PathVariable Long id, Model model) {
        Optional<PogrupowaneStatystykiZawodnikow> optionalPlayer = pogrupowaneRepository.findById(id);
        Optional<PogrupowaneStatystykiZawodnikow> avgPlayers = pogrupowaneRepository.findById(0L);
        if (optionalPlayer.isPresent()) {
            PogrupowaneStatystykiZawodnikow player = optionalPlayer.get();
            PogrupowaneStatystykiZawodnikow player0 = avgPlayers.get();
            model.addAttribute("player0", player0);
            model.addAttribute("player", player);
        }
        else model.addAttribute("noPlayer", "Nie ma takiego zawodnika");
        return "playerView2";
    }

    @GetMapping("/playerByName/{name}")
    public String getProfilPlayer(@PathVariable String name, Model model) {
        List<StatystykiZawodnika> optionalPlayers = statystykiZawodnikaRepository.getStatystykiZawodnikaByImieContaining(name);
        if (!optionalPlayers.isEmpty()){
            for (StatystykiZawodnika player : optionalPlayers) model.addAttribute("player", player);
        } else model.addAttribute("noPlayer", "Nie ma takiego zawodnika");
        return "playerView";
    }

}
