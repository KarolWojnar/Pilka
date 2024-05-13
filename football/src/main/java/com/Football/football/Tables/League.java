package com.Football.football.Tables;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class League {
    @Id
    private Long id;
    @Column(name = "id ligi")
    private int leagueId;
    @Column(name = "nazwa ligi")
    private String leagueName;
}
