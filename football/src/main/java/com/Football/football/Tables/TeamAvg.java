package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TeamAvg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id druzyny")
    private TeamStats teamStats;

    @Column(name = "Sezon rozgrywek")
    private Long season;

    @Column(name = "Z uwzględnieniem pozycji")
    private boolean czyUwzglednionePozycje;

    public TeamAvg() {
    }
    @Column(name = "raiting druzyny")
    private double raiting;

}
