package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RealnePozycjeTabela {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Pozycja w tabeli")
    private int pozycja;

    @Column(name = "id druzyny")
    private int teamId;

    @Column(name = "id ligi")
    private int leagueId;

    @Column(name = "nazwa druzyny")
    private String teamName;

    @Column(name = "rok rozgrywek")
    private int year;

    @Column(name = "punkty")
    private  int points;

}
