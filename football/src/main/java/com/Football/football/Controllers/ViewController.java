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
    private final TeamGroupAvgRepo sredniaDruzynyRepository;
    private final TeamGroupAvgWPosRepo srDruzynyPozycjeRepository;
    private final PlayerStatsGroupRepo pogrupowaneRepository;
    private final PlayerStatsGroupWPosRepo ppRepository;
    private final TeamStatsRepo teamStatsRepository;
    private final TeamStatsService teamStatsService;
    private final TeamAvgRepo avgAllRepository;
    private final LeaguesRepository leaguesRepository;
    private final PlayersStatsRepo playersStatsRepo;

    @GetMapping("/player/{id}/{year}")
    public String getProfilPlayer(@PathVariable Long id, @PathVariable Long year, Model model) {
        Optional<PlayerStats> optionalPlayerStats = playersStatsRepo.findById(id);
        if (optionalPlayerStats.isPresent()) {
            Optional<PlayersStatsGroup> optionalPlayer = pogrupowaneRepository
                    .getPogrupowaneStatystykiZawodnikowByPlayerStatsAndSeason(optionalPlayerStats.get(), year);

            if (optionalPlayer.isPresent()) {
                PlayersStatsGroup player = optionalPlayer.get();
                Optional<TeamGroupAvg> avgPlayer = sredniaDruzynyRepository
                        .getSredniaDruzynyByTeamStatsAndSeason(player.getTeamStats(), player.getSeason());
                if (avgPlayer.isPresent()) {
                    TeamGroupAvg avgTeam = avgPlayer.get();
                    model.addAttribute("player", player);
                    model.addAttribute("name", player.getImie());
                    model.addAttribute("player0", avgTeam);
                }
                else model.addAttribute("noPlayer", "Nie ma takiej druzyny");
            }
            else model.addAttribute("noPlayer", "Nie ma takiego zawodnika");
        }
        return "playerView2";
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

    @GetMapping("/teams/{id}/{year}")
    public String getTeam(@PathVariable Long id, @PathVariable Long year, Model model) {
        Optional<TeamStats> opteam = teamStatsRepository.findFirstByTeamId(id);
        if (opteam.isPresent()) {
            Optional<TeamGroupAvgWPos> opTeam = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(opteam.get(), year);

            if (opTeam.isPresent()) {
                TeamGroupAvgWPos team = opTeam.get();
                Iterable<TeamGroupAvgWPos> avgTeams = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednionesBySeason(year);
                TeamGroupAvgWPos avgTeam = avgStats(avgTeams);
                model.addAttribute("player", team);
                model.addAttribute("playersByTeam", getPlayersByTeam(team));
                model.addAttribute("name", team.getTeamName());
                model.addAttribute("player0", avgTeam);
                model.addAttribute("isTeam", true);
            }
            else model.addAttribute("noPlayer", "Nie ma takiej drużyny");
        }
        return "playerView2";
    }

    private List<PlayersStatsGroupWPos> getPlayersByTeam(TeamGroupAvgWPos team) {
        return ppRepository
                .getPogrypowaneStatsZawodPozycjeUwzglednioneByTeamStatsAndSeason(team.getTeamStats(), team.getSeason());
    }

    @GetMapping("/compare/year/{year}/teams/{teamA}&{teamB}")
    public String compareTeams(@PathVariable Long year, @PathVariable Long teamA, @PathVariable Long teamB, Model model) {
        Optional<TeamStats> optionalTeamStatsA = teamStatsRepository.findFirstByTeamId(teamA);
        Optional<TeamStats> optionalTeamStatsB = teamStatsRepository.findFirstByTeamId(teamB);
        if (optionalTeamStatsA.isPresent() && optionalTeamStatsB.isPresent()) {
            Optional<TeamGroupAvgWPos> optionalTeamA = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(optionalTeamStatsA.get(), year);
            if (optionalTeamA.isPresent()) {
                Optional<TeamGroupAvgWPos> optionalTeamB = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(optionalTeamStatsB.get(), year);
                if (optionalTeamB.isPresent()) {
                    TeamGroupAvgWPos TeamA = optionalTeamA.get();
                    TeamGroupAvgWPos TeamB = optionalTeamB.get();
                    model.addAttribute("TeamA", TeamA);
                    model.addAttribute("TeamB", TeamB);
                } else model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
            } else model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
        }
        return "teamView";
    }

    @GetMapping("/compare/year/{year}/team/{teamId}")
    public String compareTeams(@PathVariable Long year, @PathVariable Long teamId, Model model) {
        Optional<TeamStats> optionalTeamStats = teamStatsRepository.findFirstByTeamId(teamId);
        if (optionalTeamStats.isPresent()) {
            Optional<TeamGroupAvg> optionalTeamA = sredniaDruzynyRepository.getSredniaDruzynyByTeamStatsAndSeason(optionalTeamStats.get(), year);
            if (optionalTeamA.isPresent()) {
                Optional<TeamGroupAvgWPos> optionalTeamB = srDruzynyPozycjeRepository.getSredniaDruzynyPozycjeUwzglednioneByTeamStatsAndSeason(optionalTeamStats.get(), year);
                if (optionalTeamB.isPresent()) {
                    TeamGroupAvg TeamA = optionalTeamA.get();
                    TeamGroupAvgWPos TeamB = optionalTeamB.get();
                    TeamB.setTeamName(TeamB.getTeamName() + " z pozycją");
                    model.addAttribute("TeamA", TeamA);
                    model.addAttribute("TeamB", TeamB);
                } else model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
            } else model.addAttribute("noTeams", "Nie można porównać tych drużyn.");
        }
        return "teamView";
    }

    @GetMapping("/compare-raiting/teams/{teamA}&{teamB}")
    public String compareRaiting(Model model, @PathVariable Long teamA, @PathVariable Long teamB) {
        Optional<TeamStats> opTeamA = teamStatsRepository.findById(teamA);
        Optional<TeamStats> opTeamB = teamStatsRepository.findById(teamB);
        if (opTeamB.isPresent() && opTeamA.isPresent()) {
            Iterable<TeamAvg> teamsA = avgAllRepository
                    .getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(opTeamA.get(), false);
            Iterable<TeamAvg> PosTeamsA = avgAllRepository
                    .getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(opTeamA.get(), true);
            Iterable<TeamAvg> teamsB = avgAllRepository
                    .getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(opTeamB.get(), false);
            Iterable<TeamAvg> PosTeamsB = avgAllRepository
                    .getSredniaZeWszystkiegoByTeamStatsAndCzyUwzglednionePozycjeOrderBySeasonAsc(opTeamB.get(), true);

            Iterator<TeamAvg> optionalTeamA = teamsA.iterator();
            Iterator<TeamAvg> optionalTeamB = teamsB.iterator();

            Iterator<TeamAvg> PosOptionalTeamA = PosTeamsA.iterator();
            Iterator<TeamAvg> PosOptionalTeamB = PosTeamsB.iterator();

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

        }
        return "teamView2";
    }

    @GetMapping("/teams")
    public String showAvaiableTeams(Model model) {
        Iterable<TeamStats> teams = teamStatsRepository.getDistinctTeams();
        model.addAttribute("teams", teams);
        return "teams";
    }
    @PostMapping("/compareTeams")
    public String compare(@RequestParam("TeamA") Long idA, @RequestParam("TeamB") Long idB, Model model) {
        compareRaiting(model, idA, idB);
        return "teamView2";
    }

    @GetMapping("/admin/allInOne")
    public String showAdminAll(Model model) {
        Iterable<TeamGroupAvg> allTeams = sredniaDruzynyRepository.findAll();
        Iterable<Leagues> leagues = leaguesRepository.findAll();
        model.addAttribute("teams", allTeams);
        model.addAttribute("leagues", leagues);
        return "index";
    }
    @GetMapping("/getFixtures")
    public String showFixturesForm() {
        return "getFixtures";
    }


}
