package com.Football.football.Controllers.Players;

import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Tables.PlayerStats;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerStatsService playerService;

    @GetMapping("/{id}")
    public String getPlayer(@PathVariable Long id, Model model) {
        Optional<PlayerStats> player = playerService.getPlayerById(id);
        if (player.isPresent()) {
            model.addAttribute("player", player.get());
        } else {
            model.addAttribute("noPlayer", "Nie ma takiego gracza");
        }
        return "playerView";
    }

    @GetMapping("/averageRatings")
    public String getAveragePlayerRatings(Model model) {
        Iterable<PlayerStats> players = playerService.getAveragePlayerRatings();
        model.addAttribute("players", players);
        return "averagePlayerRatings";
    }

    @PostMapping("/save")
    public String savePlayer(PlayerStats player) {
        playerService.savePlayer(player);
        return "redirect:/players";
    }

    @GetMapping("/get/{id}&{year}&{leagueId}")
    public String give(@PathVariable Long id, @PathVariable Long year, @PathVariable Long leagueId, Model model) throws IOException, InterruptedException, JSONException {
        model.addAttribute("team", playerService.updatePlayerStats(id, year, leagueId));
        return "index";
    }

    @GetMapping("/getPlayersByLeague/{year}&{leagueId}")
    public String givePlayers(@PathVariable Long year, @PathVariable Long leagueId, Model model) throws IOException, InterruptedException, JSONException {
        playerService.updatePlayersLeague(year, leagueId);
        return "index";
    }

    @PutMapping("/update/{id}")
    public String updatePlayer(@PathVariable Long id, PlayerStats player) {
        playerService.updatePlayer(id, player);
        return "redirect:/players";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return "redirect:/players";
    }
}
