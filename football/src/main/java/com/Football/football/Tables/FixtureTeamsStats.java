package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class FixtureTeamsStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name ="id spotkania")
    private int fixtureId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id druzyny")
    private TeamStats teamStats;
    @Column(name = "data spotkania")
    private LocalDateTime fixtureDate;
    @Column(name = "rating")
    private double rating;
    @Column(name = "spalone")
    private double offside;
    @Column(name = "strzaly")
    private double shots;
    @Column(name = "strzaly celne")
    private double shotsOnGoal;
    @Column(name = "gole")
    private double goals;
    @Column(name = "gole stracone")
    private double goalsConceded;
    @Column(name = "asysyty")
    private double asists;
    @Column(name = "podania")
    private double passes;
    @Column(name = "podania kluczowe")
    private double keyPasses;
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
