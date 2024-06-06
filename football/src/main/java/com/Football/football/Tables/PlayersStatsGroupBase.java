package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class PlayersStatsGroupBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id pilkarza")
    private PlayerStats playerStats;
    @Column(name = "Imie i nazwisko")
    private String imie;
    @Column(name = "pozycja")
    private String pozycja;
    @Column(name = "Drybling i Skuteczność")
    private Double dryblingSkutecznosc;
    @Column(name = "Fizyczność i Interakcje")
    private Double fizycznoscInterakcje;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id druzyny")
    private TeamStats teamStats;
    @Column(name = "Sezon rozgrywek")
    private Long season;
    @Column(name = "Obrona i kotnrola przeciwnika")
    private Double obronaKotrolaPrzeciwnika;
    @Column(name = "podania i kreatywność")
    private Double podaniaKreatywnosc;
}
