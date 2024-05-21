package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class PlayerStats {
    public PlayerStats() {}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id gracza", unique = false)
    private Long playerId;
    private String imie;
    private String nazwisko;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id druzyny")
    private TeamStats teamStats;
    @Column(name = "sezon rozgrywek")
    private Long season;
    private double wiek;
    private double wzrost;
    private double waga;
    private String kraj;
    private double wystepy;
    private double minuty;
    private String pozycja;
    private double rating;
    private double strzaly;
    @Column(name = "Strzały celne")
    private double strzalyCelne;
    private double gole;
    private double podania;
    @Column(name = "dokladnosc podan w procentach")
    private double dokladnoscPodan;
    @Column(name = "Podania kluczowe")
    private double podaniaKluczowe;
    private double asysty;
    private double pojedynki;
    @Column(name = "Pojedynki wygrane")
    private double pojedynkiWygrane;
    @Column(name = "Próby przechwytu")
    private double probyPrzechwytu;
    @Column(name = "Przechwyty udane")
    private double przechwytyUdane;
    private double dryblingi;
    @Column(name = "Dryblingi udane")
    private double dryblingiWygrane;
    @Column(name = "Faule na zawodniku")
    private double fauleNaZawodniku;
    @Column(name = "Faule popełnione")
    private double faulePopelnione;
    @Column(name = "Kartki żółte")
    private double kartkiZolte;
    @Column(name = "Kartki czerwone")
    private double kartkiCzerwone;
    @Column(name = "Czy zawodnik jest kontuzjowany")
    private boolean czyKontuzjowany;
    @OneToMany(mappedBy = "team_stats", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FixturesStats> fixtures;

}
