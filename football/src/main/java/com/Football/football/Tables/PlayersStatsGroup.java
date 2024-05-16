package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PlayersStatsGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id piłkarza")
    private Long playerId;
    @Column(name = "Imie i nazwisko")
    private String imie;
    @Column(name = "pozycja")
    private String pozycja;
    @Column(name = "Drybling i Skuteczność")
    private Double dryblingSkutecznosc;
    @Column(name = "Fizyczność i Interakcje")
    private Double fizycznoscInterakcje;
    @Column(name = "id Drużyny")
    private Long teamId;
    @Column(name = "Sezon rozgrywek")
    private Long season;
    @Column(name = "Obrona i kotnrola przeciwnika")
    private Double obronaKotrolaPrzeciwnika;
    @Column(name = "podania i kreatywność")
    private Double podaniaKreatywnosc;
    @Column(name = "id_ligi")
    private int leagueId;

}
