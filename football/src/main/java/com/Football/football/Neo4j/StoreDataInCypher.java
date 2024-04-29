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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StoreDataInCypher {


    private final TeamStatsService teamStatsService;

    private final PlayerStatsService playerStatsService;

    public void saveDatas() throws IOException {
        String text = getFootballData().toString();


        String path = "C:\\Users\\karol\\Desktop\\cypherCommand.txt";

        FileWriter fileWriter = new FileWriter(path);

        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(text);

        bufferedWriter.close();

        fileWriter.close();

    }

    private StringBuilder getFootballData() {
        StringBuilder query = new StringBuilder("");
        Iterable<StatystykiZawodnika> statystykiZawodnikow = playerStatsService.findAllPlayers();
        Iterable<StatystykiDruzyny> statystykiDruzyn = teamStatsService.findAllTeams();

        // Dodanie danych drużyn
        for (StatystykiDruzyny team : statystykiDruzyn) {
            double yellow = team.getKartkiZolteWMinucie0_15() + team.getKartkiZolteWMinucie16_30()
                    + team.getKartkiZolteWMinucie31_45() + team.getKartkiZolteWMinucie46_60()
                    + team.getKartkiZolteWMinucie61_75() + team.getKartkiZolteWMinucie76_90()
                    + team.getKartkiZolteWMinucie91_105();
            double red = team.getKartkiCzerwoneWMinucie0_15() + team.getKartkiCzerwoneWMinucie16_30()
                    + team.getKartkiCzerwoneWMinucie31_45() + team.getKartkiCzerwoneWMinucie46_60()
                    + team.getKartkiCzerwoneWMinucie61_75() + team.getKartkiCzerwoneWMinucie76_90()
                    + team.getKartkiCzerwoneWMinucie91_105();

            query.append("CREATE (t:Druzyna {")
                    .append("id: ").append(team.getTeamId()).append(", ")
                    .append("nazwa: '").append(team.getTeamName()).append("', ")
                    .append("sezon_rozgryweg: '").append(team.getSeason()).append("', ")
                    .append("gole_strzelone_w_domu: ").append(team.getGoleStrzeloneWDomu()).append(", ")
                    .append("gole_strzelone_na_wyjezdzie: ").append(team.getGoleStrzeloneNaWyjezdzie()).append(", ")
                    .append("gole_stracone_w_domu: ").append(team.getGoleStrzeloneWDomu()).append(", ")
                    .append("gole_stracone_na_wyjezdzie: ").append(team.getGoleStraconeNaWyjezdzie()).append(", ")
                    .append("żółte_kartki: ").append(yellow).append(", ")
                    .append("czerwone_kartki: ").append(red).append(", ")
                    .append("mecze_domowe: ").append(team.getMeczeDomowe()).append(", ")
                    .append("mecze_na_wyjezdzie: ").append(team.getMeczeWyjazdowe()).append(", ")
                    .append("srednia_goli_straconych_w_domu: ").append(team.getSredniaGoliStraconychWDomu()).append(", ")
                    .append("srednia_goli_strzelonych_w_domu: ").append(team.getGoleStrzeloneWDomu()).append(", ")
                    .append("srednia_goli_straconych_na_wyjezdzie: ").append(team.getSredniaGoliStraconychNaWyjezdzie()).append(", ")
                    .append("srednia_goli_strzelonych_na_wyjezdzie: ").append(team.getSredniaGoliStrzelonychNaWyjezdzie()).append(", ")
                    .append("wygrane_w_domu: ").append(team.getWygraneWDomu()).append(", ")
                    .append("wygrane_na_wyjezdzie: ").append(team.getWygraneNaWyjezdzie()).append(", ")
                    .append("przegrane_w_domu: ").append(team.getPrzegraneWDomu()).append(", ")
                    .append("przegrane_na_wyjezdzie: ").append(team.getPrzegraneNaWyjezdzie()).append(", ")
                    .append("remisy_w_domu: ").append(team.getRemisyWDomu()).append(", ")
                    .append("remisy_na_wyjezdzie: ").append(team.getRemisyNaWyjezdzie())
                    .append("})\n");
        }

        // Dodanie danych zawodników
        for (StatystykiZawodnika zawodnik : statystykiZawodnikow) {
            query.append("CREATE (p:Zawodnik {")
                    .append("id: ").append(zawodnik.getPlayerId()).append(", ")
                    .append("imie: '").append(zawodnik.getImie()).append("', ")
                    .append("nazwisko: '").append(zawodnik.getNazwisko()).append("', ")
                    .append("id_druzyny: ").append(zawodnik.getTeamId()).append(", ")
                    .append("sezon_rozgrywek: '").append(zawodnik.getSeason()).append("', ")
                    .append("wiek: ").append(zawodnik.getWiek()).append(", ")
                    .append("wzrost: ").append(zawodnik.getWzrost()).append(", ")
                    .append("waga: ").append(zawodnik.getWaga()).append(", ")
                    .append("kraj: '").append(zawodnik.getKraj()).append("', ")
                    .append("wystepy: ").append(zawodnik.getWystepy()).append(", ")
                    .append("minuty: ").append(zawodnik.getMinuty()).append(", ")
                    .append("pozycja: '").append(zawodnik.getPozycja()).append("', ")
                    .append("rating: ").append(zawodnik.getRating()).append(", ")
                    .append("strzaly: ").append(zawodnik.getStrzaly()).append(", ")
                    .append("strzaly_celne: ").append(zawodnik.getStrzalyCelne()).append(", ")
                    .append("gole: ").append(zawodnik.getGole()).append(", ")
                    .append("podania: ").append(zawodnik.getPodania()).append(", ")
                    .append("dokladnosc_podan: ").append(zawodnik.getDokladnoscPodan()).append(", ")
                    .append("podania_kluczowe: ").append(zawodnik.getPodaniaKluczowe()).append(", ")
                    .append("asysty: ").append(zawodnik.getAsysty()).append(", ")
                    .append("pojedynki: ").append(zawodnik.getPojedynki()).append(", ")
                    .append("pojedynki_wygrane: ").append(zawodnik.getPojedynkiWygrane()).append(", ")
                    .append("proby_przechwytu: ").append(zawodnik.getProbyPrzechwytu()).append(", ")
                    .append("przechwyty_udane: ").append(zawodnik.getPrzechwytyUdane()).append(", ")
                    .append("dryblingi: ").append(zawodnik.getDryblingi()).append(", ")
                    .append("dryblingi_wygrane: ").append(zawodnik.getDryblingiWygrane()).append(", ")
                    .append("faule_na_zawodniku: ").append(zawodnik.getFauleNaZawodniku()).append(", ")
                    .append("faule_popelnione: ").append(zawodnik.getFaulePopelnione()).append(", ")
                    .append("kartki_zolte: ").append(zawodnik.getKartkiZolte()).append(", ")
                    .append("kartki_czerwone: ").append(zawodnik.getKartkiCzerwone()).append(", ")
                    .append("czy_kontuzjowany: ").append(zawodnik.isCzyKontuzjowany())
                    .append("})\n");

            query.append("MATCH (z:Zawodnik{id: ").append(zawodnik.getPlayerId()).append("}), ")
                    .append("(t:Druzyna{id: ").append(zawodnik.getTeamId()).append("}) ")
                    .append("CREATE (z)-[:GRA_DLA]->(t)\n");
        }

        return query;
    }

}
