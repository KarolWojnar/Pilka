package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TeamGroupAvgWPos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id ligi")
    private int leagueId;
    @Column(name = "id drużyny")
    private Long teamId;
    @Column(name = "Drybling i Skuteczność")
    private Double dryblingSkutecznosc;
    @Column(name = "Fizyczność i Interakcje")
    private Double fizycznoscInterakcje;
    @Column(name = "Sezon rozgrywek")
    private Long season;
    @Column(name = "Obrona i kotnrola przeciwnika")
    private Double obronaKotrolaPrzeciwnika;
    @Column(name = "podania i kreatywność")
    private Double podaniaKreatywnosc;
    @Column(name = "nazwa drużyny")
    private String teamName;
}
