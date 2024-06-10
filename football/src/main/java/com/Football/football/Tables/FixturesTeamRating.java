package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class FixturesTeamRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id druzyny")
    private TeamStats teamStats;

    @Column(name = "Z uwzględnieniem pozycji")
    private boolean czyUwzglednionePozycje;

    @Column(name = "raiting druzyny")
    private double raiting;
    @Column(name = "data spotkania")
    private LocalDateTime fixtureDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id spotkania")
    private FixtureTeamsStats fixtureTeamStats;
}
