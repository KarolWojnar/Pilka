package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Leagues {
    @Id
    private Long id;
    @Column(name = "id ligi")
    private int leagueId;
    @Column(name = "nazwa ligi")
    private String leagueName;
    @OneToMany(mappedBy = "leagues", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamStats> teams;
}
