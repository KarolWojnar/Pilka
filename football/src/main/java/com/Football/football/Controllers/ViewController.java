package com.Football.football.Controllers;

import com.Football.football.Repositories.*;
import com.Football.football.Services.TeamStatsService;
import com.Football.football.Tables.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class ViewController {
    private final SredniaDruzynyRepository sredniaDruzynyRepository;
    private final SrDruzynyPozycjeRepository srDruzynyPozycjeRepository;
    private final PogrupowaneRepository pogrupowaneRepository;
    private final TeamStatsRepository teamStatsRepository;
    private final TeamStatsService teamStatsService;
    private final AvgAllRepository avgAllRepository;
    private final LeaguesRepository leaguesRepository;
    private final RealnePozycjeRepository realnePozycjeRepository;

    @GetMapping("/player/{id}&{year}")
    public String getProfilPlayer(@PathVariable Long id, @PathVariable Long year, Model model) {
        Optional<PogrupowaneStatystykiZawodnikow> optionalPlayer = pogrupowaneRepository.getPogrupowaneStatystykiZawodnikowByPlayerIdAndSeason(id, year);

        if (optionalPlayer.isPresent()) {
            PogrupowaneStatystykiZawodnikow player = optionalPlayer.get();
            Optional<SredniaDruzyny> avgPlayer = sredniaDruzynyRepository.getSredniaDruzynyByTeamIdAndSeason(player.getTeamId(), player.getSeason());
            if (avgPlayer.isPresent()) {
                SredniaDruzyny avgTeam = avgPlayer.get();
                model.addAttribute("player", player);
                model.addAttribute("name", player.getImie());
                model.addAttribute("player0", avgTeam);
            }
            else model.addAttribute("noPlayer", "Nie ma takiego zawodnika");
        }
        else model.addAttribute("noPlayer", "Nie ma takiego zawodnika");
        return "playerView2";
    }

    private SredniaDruzynyPozycjeUwzglednione avgStats(Iterable<SredniaDruzynyPozycjeUwzglednione> avgTeams) {
        double dis = 0.0, pis = 0.0, fii = 0.0, okp = 0.0, summ = 0.0;
        for(SredniaDruzynyPozycjeUwzglednione team: avgTeams) {
            summ++;
            dis += team.getDryblingSkutecznosc();
            pis += team.getPodaniaKreatywnosc();
            fii += team.getFizycznoscInterakcje();
            okp += team.getObronaKotrolaPrzeciwnika();
        }
        SredniaDruzynyPozycjeUwzglednione avgTeam = new SredniaDruzynyPozycjeUwzglednione();
        avgTeam.setTeamName("Średnia");
        avgTeam.setSeason(avgTeams.iterator().next().getSeason());
        avgTeam.setDryblingSkutecznosc(dis / summ);
        avgTeam.setPodaniaKreatywnosc(pis / summ);
        avgTeam.setFizycznoscInterakcje(fii / summ);
        avgTeam.setObronaKotrolaPrzeciwnika(okp / summ);
        return avgTeam;
    }

    @GetMapping("/teams/{id}/{year}")
    public String getTeam(@PathVariable Long id, @PathVariable Long year, Model model) {
        Optional<SredniaDruzynyPozycjeUwzglednione> opTeam = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamIdAndSeason(id, year);

        if (opTeam.isPresent()) {
            SredniaDruzynyPozycjeUwzglednione team = opTeam.get();
            Iterable<SredniaDruzynyPozycjeUwzglednione> avgTeams = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednionesBySeason(year);
            SredniaDruzynyPozycjeUwzglednione avgTeam = avgStats(avgTeams);
            model.addAttribute("player", team);
            model.addAttribute("name", team.getTeamName());
            model.addAttribute("player0", avgTeam);
        }
        else model.addAttribute("noPlayer", "Nie ma takiej drużyny");
        return "playerView2";
    }

    @GetMapping("/compare/year/{year}/teams/{teamA}&{teamB}")
    public String compareTeams(@PathVariable Long year, @PathVariable Long teamA, @PathVariable Long teamB, Model model) {
        Optional<SredniaDruzynyPozycjeUwzglednione> optionalTeamA = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamIdAndSeason(teamA, year);
        if (optionalTeamA.isPresent()) {
            Optional<SredniaDruzynyPozycjeUwzglednione> optionalTeamB = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamIdAndSeason(teamB, year);
            if (optionalTeamB.isPresent()) {
                SredniaDruzynyPozycjeUwzglednione TeamA = optionalTeamA.get();
                SredniaDruzynyPozycjeUwzglednione TeamB = optionalTeamB.get();
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

    @GetMapping("/teams")
    public String showAvaiableTeams(Model model) {
        Iterable<StatystykiDruzyny> teams = teamStatsRepository.getDistinctTeams();
        model.addAttribute("teams", teams);
        return "teams";
    }
    @PostMapping("/compareTeams")
    public String compare(@RequestParam("TeamA") Long idA, @RequestParam("TeamB") Long idB, Model model) {
        compareRaiting(model, idA, idB);
        return "teamView2";
    }
    @GetMapping("/standingsLeague/{year}&{leagueId}")
    public String showStandings(@PathVariable int year, @PathVariable int leagueId, Model model) {
        Iterable<SredniaZeWszystkiego> teamsNoPos = avgAllRepository.getSredniaZeWszystkiegoByLeagueIdAndSeasonAndCzyUwzglednionePozycjeOrderByRaitingDesc(leagueId, year, false);
        Iterable<SredniaZeWszystkiego> teamsPos = avgAllRepository.getSredniaZeWszystkiegoByLeagueIdAndSeasonAndCzyUwzglednionePozycjeOrderByRaitingDesc(leagueId, year, true);
        Iterable<RealnePozycjeTabela> teamsReal = realnePozycjeRepository.getRealnePozycjeTabelaByLeagueIdAndYearOrderByPozycja(leagueId, year);
        model.addAttribute("teamsNoPos", teamsNoPos);
        model.addAttribute("teamsPos", teamsPos);
        model.addAttribute("teamsReal", teamsReal);
        return "standingsLeague";
    }

    @GetMapping("/admin/allInOne")
    public String showAdminAll(Model model) {
        Iterable<SredniaDruzyny> allTeams = sredniaDruzynyRepository.findAll();
        Iterable<League> leagues = leaguesRepository.findAll();
        model.addAttribute("teams", allTeams);
        model.addAttribute("leagues", leagues);
        return "index";
    }


}
