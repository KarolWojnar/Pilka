package com.Football.football.Tables;

import jakarta.persistence.*;

@Entity
public class SredniaZeWszystkiego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id drużyny")
    private Long teamId;

    public Long getSeason() {
        return season;
    }

    public void setSeason(Long season) {
        this.season = season;
    }

    @Column(name = "Sezon rozgrywek")
    private Long season;

    public boolean getCzyUwzglednionePozycje() {
        return czyUwzglednionePozycje;
    }

    public void setCzyUwzglednionePozycje(boolean czyUwzglednionePozycje) {
        this.czyUwzglednionePozycje = czyUwzglednionePozycje;
    }

    @Column(name = "Z uwzględnieniem pozycji")
    private boolean czyUwzglednionePozycje;

    public SredniaZeWszystkiego() {
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public double getRaiting() {
        return raiting;
    }

    public void setRaiting(double raiting) {
        this.raiting = raiting;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Column(name = "raiting druzyny")
    private double raiting;
    @Column(name = "Nazwa drużyny")
    private String teamName;
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

}
