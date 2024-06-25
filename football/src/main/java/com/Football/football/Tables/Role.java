package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<CoachTeam> coachTeams;
}
