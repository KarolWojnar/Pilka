package com.Football.football.Tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class FixturesTeamGroup extends TeamGroupBase {
    @Column(name = "data spotkania")
    private LocalDateTime fixtureDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id spotkania")
    private FixtureTeamsStats fixtureTeamStats;
}
