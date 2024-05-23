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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id ligi")
    private Long leagueId;
    @Column(name = "nazwa ligi")
    private String leagueName;
    @Column(name = "kraj")
    private String country;
    @OneToMany(mappedBy = "leagues", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamStats> teams;
}
