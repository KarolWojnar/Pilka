package com.Football.football.Controllers;

import com.Football.football.Repositories.*;
import com.Football.football.Tables.PogrupowaneStatystykiZawodnikow;
import com.Football.football.Tables.SredniaDruzyny;
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
    private SredniaDruzynyRepository sredniaDruzynyRepository;
    @Autowired
    private PogrupowaneRepository pogrupowaneRepository;
    @Autowired
    FixturesRepository fixturesRepository;

    @GetMapping("/player/{id}&{year}")
    public String getProfilPlayer(@PathVariable Long id, @PathVariable Long year, Model model) {
        Optional<PogrupowaneStatystykiZawodnikow> optionalPlayer = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByPlayerIdAndSeason(id, year);

        if (optionalPlayer.isPresent()) {
            PogrupowaneStatystykiZawodnikow player = optionalPlayer.get();
            Optional<SredniaDruzyny> avgPlayer = sredniaDruzynyRepository.getSredniaDruzynyByTeamIdAndSeason(player.getTeamId(), player.getSeason());
            if (avgPlayer.isPresent()) {
                SredniaDruzyny avgTeam = avgPlayer.get();
                model.addAttribute("player", player);
                model.addAttribute("player0", avgTeam);
            }
            else model.addAttribute("noPlayer", "Nie ma takiego zawodnika");
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

    @GetMapping("/compare/year/{year}/teams/{teamA}&{teamB}")
    public String compareTeams(@PathVariable Long year, @PathVariable Long teamA, @PathVariable Long teamB, Model model) {
        Optional<SredniaDruzyny> optionalTeamA = sredniaDruzynyRepository.getSredniaDruzynyByTeamIdAndSeason(teamA, year);
        if (optionalTeamA.isPresent()) {
            Optional<SredniaDruzyny> optionalTeamB = sredniaDruzynyRepository.getSredniaDruzynyByTeamIdAndSeason(teamB, year);
            if (optionalTeamB.isPresent()) {
                SredniaDruzyny TeamA = optionalTeamA.get();
                SredniaDruzyny TeamB = optionalTeamB.get();
                model.addAttribute("TeamA", TeamA);
                model.addAttribute("TeamB", TeamB);
            } else model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
        } else model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
        return "teamView";
    }
}
