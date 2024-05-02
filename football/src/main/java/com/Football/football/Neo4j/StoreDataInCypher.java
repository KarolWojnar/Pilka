package com.Football.football.Neo4j;

import com.Football.football.Repositories.StatystykiZawodnikaRepository;
import com.Football.football.Repositories.TeamStatsRepository;
import com.Football.football.Services.PlayerStatsService;
import com.Football.football.Services.TeamStatsService;
import com.Football.football.Tables.StatystykiDruzyny;
import com.Football.football.Tables.StatystykiZawodnika;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.*;

@Service
@RequiredArgsConstructor
public class StoreDataInCypher {


    private final TeamStatsService teamStatsService;

    private final PlayerStatsService playerStatsService;

    public void saveDatas() throws IOException {
        String textPlayers2CSV = getFootballPlayersDataCSV().toString();
        String textTeams2CSV = getFootballTeamsDataCSV().toString();

        String pathPlayerCSV = "C:\\Users\\dpk\\Desktop\\pilka.csv";
        String pathTeamCSV = "C:\\Users\\dpk\\Desktop\\druzyna.csv";

        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(pathPlayerCSV), StandardCharsets.UTF_8));

        bufferedWriter.write(textPlayers2CSV);

        bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(pathTeamCSV), StandardCharsets.UTF_8));

        bufferedWriter.write(textTeams2CSV);

        bufferedWriter.close();

    }

    private StringBuilder getFootballPlayersDataCSV() {
        StringBuilder query = new StringBuilder("");
        query.append("id_zawodnika; imie; nazwisko; id_druzyny; sezon_rozgrywek; wiek; wzrost; waga; kraj;" +
                "wystepy; minuty; pozycja; rating; strzaly; strzaly_celne; gole;" +
                "podania; dokladnosc_podan; podania_kluczowe; asysty; pojedynki; pojedynki_wygrane;" +
                "proby_przechwytu; przechwyty_udane, dryblingi; dryblingi_wygrane; faule_na_zawodniku;" +
                "faule_popelnione; kartki_zolte; kartki_czerwone; czy_kontuzjowany\n");
        Iterable<StatystykiZawodnika> statystykiZawodnikow = playerStatsService.findAllPlayers();
        // Dodanie danych zawodników
        for (StatystykiZawodnika zawodnik : statystykiZawodnikow) {
            query.append(zawodnik.getPlayerId()).append("; ")
                    .append(zawodnik.getImie()).append("; ").append(zawodnik.getNazwisko()).append("; ")
                    .append(zawodnik.getTeamId()).append("; ").append(zawodnik.getSeason()).append("; ")
                    .append(zawodnik.getWiek()).append("; ").append(zawodnik.getWzrost()).append("; ")
                    .append(zawodnik.getWaga()).append("; ").append(zawodnik.getKraj()).append("; ")
                    .append(zawodnik.getWystepy()).append("; ").append(zawodnik.getMinuty()).append("; ")
                    .append(zawodnik.getPozycja()).append("; ").append(zawodnik.getRating()).append("; ")
                    .append(zawodnik.getStrzaly()).append("; ").append(zawodnik.getStrzalyCelne()).append("; ")
                    .append(zawodnik.getGole()).append("; ").append(zawodnik.getPodania()).append("; ")
                    .append(zawodnik.getDokladnoscPodan()).append("; ").append(zawodnik.getPodaniaKluczowe()).append("; ")
                    .append(zawodnik.getAsysty()).append("; ").append(zawodnik.getPojedynki()).append("; ")
                    .append(zawodnik.getPojedynkiWygrane()).append("; ").append(zawodnik.getProbyPrzechwytu()).append("; ")
                    .append(zawodnik.getPrzechwytyUdane()).append("; ").append(zawodnik.getDryblingi()).append("; ")
                    .append(zawodnik.getDryblingiWygrane()).append("; ").append(zawodnik.getFauleNaZawodniku()).append("; ")
                    .append(zawodnik.getFaulePopelnione()).append("; ").append(zawodnik.getKartkiZolte()).append("; ")
                    .append(zawodnik.getKartkiCzerwone()).append("; ").append(zawodnik.isCzyKontuzjowany())
                    .append("\n");
        }
        return query;
    }

    private StringBuilder getFootballTeamsDataCSV() {
        StringBuilder query = new StringBuilder("");
        query.append("id; nazwa; sezon_rozgrywek; gole_strzelone_w_domu; gole_strzelone_na_wyjezdzie; gole_stracone_w_domu; " +
                "gole_stracone_na_wyjezdzie; zolte_kartki; czerwone_kartki; mecze_domowe; mecze_na_wyjezdzie; " +
                "srednia_goli_straconych_w_domu; srednia_goli_strzelonych_w_domu; srednia_goli_straconych_na_wyjezdzie; " +
                "srednia_goli_strzelonych_na_wyjezdzie; wygrane_w_domu; wygrane_na_wyjezdzie; przegrane_w_domu; " +
                "przegrane_na_wyjezdzie; remisy_w_domu; remisy_na_wyjezdzie\n");

        Iterable<StatystykiDruzyny> statystykiDruzyn = teamStatsService.findAllTeams();

        for (StatystykiDruzyny team : statystykiDruzyn) {
            double yellow = team.getKartkiZolteWMinucie0_15() + team.getKartkiZolteWMinucie16_30()
                    + team.getKartkiZolteWMinucie31_45() + team.getKartkiZolteWMinucie46_60()
                    + team.getKartkiZolteWMinucie61_75() + team.getKartkiZolteWMinucie76_90()
                    + team.getKartkiZolteWMinucie91_105();
            double red = team.getKartkiCzerwoneWMinucie0_15() + team.getKartkiCzerwoneWMinucie16_30()
                    + team.getKartkiCzerwoneWMinucie31_45() + team.getKartkiCzerwoneWMinucie46_60()
                    + team.getKartkiCzerwoneWMinucie61_75() + team.getKartkiCzerwoneWMinucie76_90()
                    + team.getKartkiCzerwoneWMinucie91_105();

            query.append(team.getTeamId()).append("; ")
                    .append(team.getTeamName()).append("; ")
                    .append(team.getSeason()).append("; ")
                    .append(team.getGoleStrzeloneWDomu()).append("; ")
                    .append(team.getGoleStrzeloneNaWyjezdzie()).append("; ")
                    .append(team.getGoleStrzeloneWDomu()).append("; ")
                    .append(team.getGoleStraconeNaWyjezdzie()).append("; ")
                    .append(yellow).append("; ").append(red).append("; ")
                    .append(team.getMeczeDomowe()).append("; ")
                    .append(team.getMeczeWyjazdowe()).append("; ")
                    .append(team.getSredniaGoliStraconychWDomu()).append("; ")
                    .append(team.getGoleStrzeloneWDomu()).append("; ")
                    .append(team.getSredniaGoliStraconychNaWyjezdzie()).append("; ")
                    .append(team.getSredniaGoliStrzelonychNaWyjezdzie()).append("; ")
                    .append(team.getWygraneWDomu()).append("; ")
                    .append(team.getWygraneNaWyjezdzie()).append("; ")
                    .append(team.getPrzegraneWDomu()).append("; ")
                    .append(team.getPrzegraneNaWyjezdzie()).append("; ")
                    .append(team.getRemisyWDomu()).append("; ")
                    .append(team.getRemisyNaWyjezdzie())
                    .append("\n");
        }

        return query;
    }

}
