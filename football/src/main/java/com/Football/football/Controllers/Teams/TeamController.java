package com.Football.football.Controllers.Teams;

import com.Football.football.Repositories.*;
import com.Football.football.Services.TeamStatsService;
import com.Football.football.Tables.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamStatsService teamStatsService;
    private final PlayerStatsGroupWPosRepo playerStatsGroupWPosRepo;
    private final TeamStatsRepo teamStatsRepository;
    private final TeamGroupAvgWPosRepo teamGroupAvgWPosRepo;
    private final TeamGroupAvgRepo teamGroupAvgRepo;
    private final LeaguesRepository leaguesRepository;
    private final TeamAvgRepo avgAllRepository;
    @GetMapping("/getStatsForSeason/{teamId}&{year}&{leagueId}")
    public String giveTeam(@PathVariable Long teamId, @PathVariable Long year, @PathVariable Long leagueId) throws JSONException, IOException, InterruptedException {
        teamStatsService.updateTeamStats(teamId, year, leagueId);
        return "index";
    }

    @GetMapping("/getLeagueAll/{leagueId}&{season}")
    public String getAllTeamsByLeague(@PathVariable Long leagueId, @PathVariable Long season) throws JSONException, IOException, InterruptedException {
        teamStatsService.getAllTeamsByLeague(leagueId, season);
        return "index";
    }

    @GetMapping("/{id}/{year}")
    public String getTeam(@PathVariable Long id, @PathVariable Long year, Model model) {
        Optional<TeamStats> opteam = teamStatsRepository.findFirstByTeamId(id);
        if (opteam.isPresent()) {
            Optional<TeamGroupAvgWPos> opTeam = teamGroupAvgWPosRepo.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(opteam.get(), year);
            if (opTeam.isPresent()) {
                TeamGroupAvgWPos team = opTeam.get();
                Iterable<TeamGroupAvgWPos> avgTeams = teamGroupAvgWPosRepo.getSredniaDruzynyPozycjeUwzglednionesBySeason(year);
                TeamGroupAvgWPos avgTeam = avgStats(avgTeams);
                model.addAttribute("player", team);
                model.addAttribute("playersByTeam", getPlayersByTeam(team));
                model.addAttribute("name", team.getTeamName());
                model.addAttribute("player0", avgTeam);
                model.addAttribute("isTeam", true);
            } else {
                model.addAttribute("noPlayer", "Nie ma takiej drużyny");
            }
        }
        return "coachProfile";
    }

    @GetMapping("/compare/year/{year}/teams/{teamA}&{teamB}")
    public String compareTeams(@PathVariable Long year, @PathVariable Long teamA, @PathVariable Long teamB, Model model) {
        Optional<TeamStats> optionalTeamStatsA = teamStatsRepository.findFirstByTeamId(teamA);
        Optional<TeamStats> optionalTeamStatsB = teamStatsRepository.findFirstByTeamId(teamB);
        if (optionalTeamStatsA.isPresent() && optionalTeamStatsB.isPresent()) {
            Optional<TeamGroupAvgWPos> optionalTeamA = teamGroupAvgWPosRepo.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(optionalTeamStatsA.get(), year);
            if (optionalTeamA.isPresent()) {
                Optional<TeamGroupAvgWPos> optionalTeamB = teamGroupAvgWPosRepo.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(optionalTeamStatsB.get(), year);
                if (optionalTeamB.isPresent()) {
                    TeamGroupAvgWPos TeamA = optionalTeamA.get();
                    TeamGroupAvgWPos TeamB = optionalTeamB.get();
                    model.addAttribute("TeamA", TeamA);
                    model.addAttribute("TeamB", TeamB);
                } else {
                    model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
                }
            } else {
                model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
            }
        }
        return "teamView";
    }

    @GetMapping("/compare/year/{year}/team/{teamId}")
    public String compareTeamWithAvg(@PathVariable Long year, @PathVariable Long teamId, Model model) {
        Optional<TeamStats> optionalTeamStats = teamStatsRepository.findFirstByTeamId(teamId);
        if (optionalTeamStats.isPresent()) {
            Optional<TeamGroupAvg> optionalTeamA = teamGroupAvgRepo.getSredniaDruzynyByTeamStatsAndSeason(optionalTeamStats.get(), year);
            if (optionalTeamA.isPresent()) {
                Optional<TeamGroupAvgWPos> optionalTeamB = teamGroupAvgWPosRepo.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(optionalTeamStats.get(), year);
                if (optionalTeamB.isPresent()) {
                    TeamGroupAvg TeamA = optionalTeamA.get();
                    TeamGroupAvgWPos TeamB = optionalTeamB.get();
                    TeamB.setTeamName(TeamB.getTeamName() + " z pozycją");
                    model.addAttribute("TeamA", TeamA);
                    model.addAttribute("TeamB", TeamB);
                } else {
                    model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
                }
            } else {
                model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
            }
        }
        return "teamView";
    }

    @GetMapping("/compare-raiting/teams/{teamA}&{teamB}")
    public String compareRaiting(Model model, @PathVariable Long teamA, @PathVariable Long teamB) {
        Optional<TeamStats> opTeamA = teamStatsRepository.findById(teamA);
        Optional<TeamStats> opTeamB = teamStatsRepository.findById(teamB);
        if (opTeamB.isPresent() && opTeamA.isPresent()) {
            Iterable<TeamAvg> teamsA = avgAllRepository.getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(opTeamA.get(), false);
            Iterable<TeamAvg> PosTeamsA = avgAllRepository.getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(opTeamA.get(), true);
            Iterable<TeamAvg> teamsB = avgAllRepository.getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(opTeamB.get(), false);
            Iterable<TeamAvg> PosTeamsB = avgAllRepository.getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(opTeamB.get(), true);

            Iterator<TeamAvg> optionalTeamA = teamsA.iterator();
            Iterator<TeamAvg> optionalTeamB = teamsB.iterator();
            Iterator<TeamAvg> PosOptionalTeamA = PosTeamsA.iterator();
            Iterator<TeamAvg> PosOptionalTeamB = PosTeamsB.iterator();

            if (optionalTeamA.hasNext() && optionalTeamB.hasNext() && PosOptionalTeamA.hasNext() && PosOptionalTeamB.hasNext()) {
                List<Double> raitings = teamStatsService.getAllRaitings(teamsA, teamsB, PosTeamsA, PosTeamsB);
                model.addAttribute("minRaiting", Collections.max(raitings));
                model.addAttribute("raitings", raitings);
                model.addAttribute("maxRaiting", Collections.min(raitings));
                model.addAttribute("avgRaiting", (Collections.max(raitings) + Collections.min(raitings)) / 2);
                model.addAttribute("teamsA", teamsA);
                model.addAttribute("teamsB", teamsB);
                model.addAttribute("PosTeamsA", PosTeamsA);
                model.addAttribute("PosTeamsB", PosTeamsB);
            } else {
                model.addAttribute("noCompare", "Brak druzyn do porownania");
            }
        }
        return "teamView2";
    }

    @PostMapping("/compareTeams")
    public String compare(@RequestParam("TeamA") Long idA, @RequestParam("TeamB") Long idB, Model model) {
        compareRaiting(model, idA, idB);
        return "teamView2";
    }

    @GetMapping("/admin/allInOne")
    public String showAdminAll(Model model) {
        Iterable<TeamGroupAvg> allTeams = teamGroupAvgRepo.findAll();
        Iterable<Leagues> leagues = leaguesRepository.findAll();
        model.addAttribute("teams", allTeams);
        model.addAttribute("leagues", leagues);
        return "index";
    }
    private TeamGroupAvgWPos avgStats(Iterable<TeamGroupAvgWPos> avgTeams) {
        double dis = 0.0, pis = 0.0, fii = 0.0, okp = 0.0, summ = 0.0;
        for(TeamGroupAvgWPos team: avgTeams) {
            summ++;
            dis += team.getDryblingSkutecznosc();
            pis += team.getPodaniaKreatywnosc();
            fii += team.getFizycznoscInterakcje();
            okp += team.getObronaKotrolaPrzeciwnika();
        }
        TeamGroupAvgWPos avgTeam = new TeamGroupAvgWPos();
        avgTeam.setTeamName("Średnia");
        avgTeam.setSeason(avgTeams.iterator().next().getSeason());
        avgTeam.setDryblingSkutecznosc(dis / summ);
        avgTeam.setPodaniaKreatywnosc(pis / summ);
        avgTeam.setFizycznoscInterakcje(fii / summ);
        avgTeam.setObronaKotrolaPrzeciwnika(okp / summ);
        return avgTeam;
    }

    private List<PlayersStatsGroupWPos> getPlayersByTeam(TeamGroupAvgWPos team) {
        return playerStatsGroupWPosRepo
                .getPlayerStatsGroupWPosByTeamStatsAndSeason(team.getTeamStats(), team.getSeason());
    }
}
