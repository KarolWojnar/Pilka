package com.Football.football.Tables;

import jakarta.persistence.*;

import java.net.DatagramPacket;

@Entity
public class PogrupowaneStatystykiZawodnikow {
    @Id
    @Column(name = "id piłkarza")
    private Long id;
    @Column(name = "Imie i nazwisko")
    private String imie;
    @Column(name = "pozycja")
    private String pozycja;
    @Column(name = "Drybling i Skuteczność")
    private Double dryblingSkutecznosc;
    @Column(name = "Fizyczność i Interakcje")
    private Double fizycznoscInterakcje;
    @Column(name = "id Drużyny")
    private Long teamId;
    @Column(name = "Sezon rozgrywek")
    private Long season;
    @Column(name = "Obrona i kotnrola przeciwnika")
    private Double obronaKotrolaPrzeciwnika;

    @Column(name = "podania i kreatywność")
    private Double podaniaKreatywnosc;
    public String getPozycja() {
        return pozycja;
    }

    public void setPozycja(String pozycja) {
        this.pozycja = pozycja;
    }
    public PogrupowaneStatystykiZawodnikow() {
    }
    public String getImie() {
        return imie;
    }
    public void setImie(String imie) {
        this.imie = imie;
    }
    public Double getPodaniaKreatywnosc() {
        return podaniaKreatywnosc;
    }
    public void setPodaniaKreatywnosc(Double podaniaKreatywnosc) {
        this.podaniaKreatywnosc = podaniaKreatywnosc;
    }
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
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Double getObronaKotrolaPrzeciwnika() {
        return obronaKotrolaPrzeciwnika;
    }
    public void setObronaKotrolaPrzeciwnika(Double obronaKotrolaPrzeciwnika) {
        this.obronaKotrolaPrzeciwnika = obronaKotrolaPrzeciwnika;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getSeason() {
        return season;
    }

    public void setSeason(Long season) {
        this.season = season;
    }
}
