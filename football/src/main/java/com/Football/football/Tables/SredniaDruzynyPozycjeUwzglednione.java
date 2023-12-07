package com.Football.football.Tables;

import jakarta.persistence.*;

@Entity
public class SredniaDruzynyPozycjeUwzglednione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id drużyny")
    private Long teamId;
    @Column(name = "Drybling i Skuteczność")
    private Double dryblingSkutecznosc;
    @Column(name = "Fizyczność i Interakcje")
    private Double fizycznoscInterakcje;
    @Column(name = "Sezon rozgrywek")
    private Long season;
    @Column(name = "Obrona i kotnrola przeciwnika")
    private Double obronaKotrolaPrzeciwnika;
    @Column(name = "podania i kreatywność")
    private Double podaniaKreatywnosc;

    public Double getDryblingSkutecznosc() {
        return dryblingSkutecznosc;
    }

    public void setDryblingSkutecznosc(Double dryblingSkutecznosc) {
        this.dryblingSkutecznosc = dryblingSkutecznosc;
    }

    public Double getFizycznoscInterakcje() {
        return fizycznoscInterakcje;
    }

    public void setFizycznoscInterakcje(Double fizycznoscInterakcje) {
        this.fizycznoscInterakcje = fizycznoscInterakcje;
    }

    public Long getSeason() {
        return season;
    }

    public void setSeason(Long season) {
        this.season = season;
    }

    public Double getObronaKotrolaPrzeciwnika() {
        return obronaKotrolaPrzeciwnika;
    }

    public void setObronaKotrolaPrzeciwnika(Double obronaKotrolaPrzeciwnika) {
        this.obronaKotrolaPrzeciwnika = obronaKotrolaPrzeciwnika;
    }

    public Double getPodaniaKreatywnosc() {
        return podaniaKreatywnosc;
    }

    public void setPodaniaKreatywnosc(Double podaniaKreatywnosc) {
        this.podaniaKreatywnosc = podaniaKreatywnosc;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getTeamId() {
        return teamId;
    }
}
