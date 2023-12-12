package com.Football.football.Controllers;

import com.Football.football.Repositories.*;
import com.Football.football.Services.TeamStatsService;
import com.Football.football.Tables.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Controller
public class ViewController {
    @Autowired
    private SredniaDruzynyRepository sredniaDruzynyRepository;
    @Autowired
    private SrDruzynyPozycjeRepository srDruzynyPozycjeRepository;
    @Autowired
    private PogrupowaneRepository pogrupowaneRepository;
    @Autowired
    private TeamStatsService teamStatsService;
    @Autowired
    private AvgAllRepository avgAllRepository;

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

    @GetMapping("/compare/year/{year}/team/{teamId}")
    public String compareTeams(@PathVariable Long year, @PathVariable Long teamId, Model model) {
        Optional<SredniaDruzyny> optionalTeamA = sredniaDruzynyRepository.getSredniaDruzynyByTeamIdAndSeason(teamId, year);
        if (optionalTeamA.isPresent()) {
            Optional<SredniaDruzynyPozycjeUwzglednione> optionalTeamB = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamIdAndSeason(teamId, year);
            if (optionalTeamB.isPresent()) {
                SredniaDruzyny TeamA = optionalTeamA.get();
                SredniaDruzynyPozycjeUwzglednione TeamB = optionalTeamB.get();
                TeamB.setTeamName(TeamB.getTeamName() + " z pozycją");
                model.addAttribute("TeamA", TeamA);
                model.addAttribute("TeamB", TeamB);
            } else model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
        } else model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
        return "teamView";
    }

    @GetMapping("/compare-raiting/teams/{teamA}&{teamB}")
    public String compareRaiting(Model model, @PathVariable Long teamA, @PathVariable Long teamB) {
        Iterable<SredniaZeWszystkiego> teamsA = avgAllRepository.getSredniaZeWszystkiegoByTeamIdAndCzyUwzglednionePozycjeOrderBySeasonAsc(teamA, false);
        Iterable<SredniaZeWszystkiego> PosTeamsA = avgAllRepository.getSredniaZeWszystkiegoByTeamIdAndCzyUwzglednionePozycjeOrderBySeasonAsc(teamA, true);
        Iterable<SredniaZeWszystkiego> teamsB = avgAllRepository.getSredniaZeWszystkiegoByTeamIdAndCzyUwzglednionePozycjeOrderBySeasonAsc(teamB, false);
        Iterable<SredniaZeWszystkiego> PosTeamsB = avgAllRepository.getSredniaZeWszystkiegoByTeamIdAndCzyUwzglednionePozycjeOrderBySeasonAsc(teamB, true);

        Iterator<SredniaZeWszystkiego> optionalTeamA = teamsA.iterator();
        Iterator<SredniaZeWszystkiego> optionalTeamB = teamsB.iterator();

        Iterator<SredniaZeWszystkiego> PosOptionalTeamA = PosTeamsA.iterator();
        Iterator<SredniaZeWszystkiego> PosOptionalTeamB = PosTeamsB.iterator();

        if (optionalTeamA.hasNext() && optionalTeamB.hasNext() && PosOptionalTeamA.hasNext() && PosOptionalTeamB.hasNext()) {
            List<Double> raitings;

            raitings = teamStatsService.getAllRaitings(teamsA, teamsB, PosTeamsA, PosTeamsB);

            model.addAttribute("minRaiting", Collections.max(raitings));
            model.addAttribute("raitings", raitings);
            model.addAttribute("maxRaiting", Collections.min(raitings));
            model.addAttribute("avgRaiting", (Collections.max(raitings) + Collections.min(raitings)) / 2);
            model.addAttribute("teamsA", teamsA);
            model.addAttribute("teamsB", teamsB);
            model.addAttribute("PosTeamsA", PosTeamsA);
            model.addAttribute("PosTeamsB", PosTeamsB);

        } else model.addAttribute("noCompare", "Brak druzyn do porownania");

        return "teamView2";
    }
}
