package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class FixturesStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id spotkania")
    private int fixtureId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id druzyny")
    private TeamStats teamStats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id zawodnika")
    private PlayerStats playerStats;
    @Column(name = "data spotkania")
    private LocalDateTime fixtureDate;
    @Column(name = "imie i nazwisko zawodnika")
    private String name;
    @Column(name = "minuty")
    private int minutes;
    @Column(name = "pozycja")
    private String position;
    @Column(name = "rating")
    private double rating;
    @Column(name = "spalone")
    private int offside;
    @Column(name = "strzaly")
    private int shots;
    @Column(name = "strzaly celne")
    private int shotsOnGoal;
    @Column(name = "gole")
    private int goals;
    @Column(name = "gole stracone")
    private int goalsConceded;
    @Column(name = "asysyty")
    private int asists;
    @Column(name = "obrony")
    private int saves;
    @Column(name = "podania")
    private int passes;
    @Column(name = "podania kluczowe")
    private int keyPasses;
    @Column(name = "celnosc podan")
    private double accuracyPasses;
    @Column(name = "odbiory piłki")
    private double interceptions;
    @Column(name = "zablokowane akcje")
    private double blocks;
    @Column(name = "proby przejec")
    private double totalTackles;
    @Column(name = "pojedynki")
    private double duels;
    @Column(name = "pojedynki wygrane")
    private double duelsWon;
    @Column(name = "dryblingi")
    private double dribbles;
    @Column(name = "dryblingi wygrane")
    private double dribblesWon;
    @Column(name = "faule popełnione")
    private double foulsCommited;
    @Column(name = "faule na zawodniku")
    private double foulsDrawn;
    @Column(name = "kartki czerwone")
    private double redCards;
    @Column(name = "kartki zolte")
    private double yellowCards;
    @Column(name = "karne wywalczone")
    private double penaltyWon;
    @Column(name = "karne spowodowane")
    private double penaltyCommited;
    @Column(name = "karne strzelone")
    private double penaltyScored;
    @Column(name = "karne nietrafione")
    private double penaltyMissed;
    @Column(name = "karne obronione")
    private double penaltySaves;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id druzyny przeciwnika")
    private TeamStats enemyStats;

}
