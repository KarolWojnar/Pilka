package com.Football.football.Tables;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class TeamStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "suma spotkan")
    private Double sumaSpotkan;
    @Column(name = "Nazwa drużyny")
    private String teamName;
    @Column(name = "id_druzyny")
    private long teamId;
    @Column(name = "sezon")
    private Long season;
    public TeamStats() {}
    @Column(name = "Mecze domowe")
    private double meczeDomowe;
    @Column(name = "Mecze wyjazdowe")
    private double meczeWyjazdowe;
    @Column(name = "Wygrane na wyjeździe")
    private double wygraneNaWyjezdzie;
    @Column(name = "Wygrane w domu")
    private double wygraneWDomu;
    @Column(name = "Remisy w domu")
    private double remisyWDomu;
    @Column(name = "Remisy na wyjezdzie")
    private double remisyNaWyjezdzie;
    @Column(name = "Przegrane na wyjeździe")
    private double przegraneNaWyjezdzie;
    @Column(name = "Przegrane w domu")
    private double przegraneWDomu;
    @Column(name = "Gole strzelone w domu")
    private double goleStrzeloneWDomu;
    @Column(name = "Gole strzelone na wyjeździe")
    private double goleStrzeloneNaWyjezdzie;
    @Column(name = "Średnia goli strzelonych na wyjeździe")
    private double sredniaGoliStrzelonychNaWyjezdzie;
    @Column(name = "Średnia goli strzelonych w domu")
    private double sredniaGoliStrzelonychWDomu;
    @Column(name = "Gole stracone w domu")
    private double goleStraconeWDomu;
    @Column(name = "Gole stracone na wyjeździe")
    private double goleStraconeNaWyjezdzie;
    @Column(name = "Średnia goli straconych na wyjeździe")
    private double sredniaGoliStraconychNaWyjezdzie;
    @Column(name = "Średnia goli straconych w domu")
    private double sredniaGoliStraconychWDomu;
    @Column(name = "Czyste konta w domu")
    private double czysteKontaWDomu;
    @Column(name = "Czyste konta na wyjeździe")
    private double czysteKontaNaWyjezdzie;
    @Column(name = "Mecze bez gola w domu")
    private double meczeBezGolaWDomu;
    @Column(name = "Mecze bez gola na wyjeździe")
    private double meczeBezGolaNaWyjezdzie;
    @Column(name = "Karne strzelone")
    private double karneStrzelone;
    @Column(name = "Karne nie strzelone")
    private double karneNiestrzelone;
    @Column(name = "Formacja")
    private String formacja;
    @Column(name = "Ile razy w tej formacji")
    private double ileRazyWtejFormacji;
    @Column(name = "Kartki żółte")
    private double yellowCards;
    @Column(name = "Kartki czerwone")
    private double redCards;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private Leagues leagues;
    @OneToMany(mappedBy = "teamStats", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlayerStats> players;
    @OneToMany(mappedBy = "teamStats", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FixturesStats> fixtures;
    @OneToMany(mappedBy = "enemyStats", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FixturesStats> enemy;
}
