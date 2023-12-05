package com.Football.football.Tables;

import jakarta.persistence.*;

@Entity
public class StatystykiZawodnika {

    public StatystykiZawodnika() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    @Column(name = "id gracza", unique = false)
    private Long playerId;

    private String imie;
    private String nazwisko;
    @Column(name = "id Drużyny")
    private Long teamId;
    @Column(name = "sezon rozgrywek")
    private Long season;

    public Long getSeason() {
        return season;
    }

    public void setSeason(Long season) {
        this.season = season;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    private double wiek;
    private double wzrost;
    private double waga;
    private String kraj;
    private double wystepy;
    private double minuty;
    private String pozycja;
    private double rating;
    private double strzaly;
    @Column(name = "Strzały celne")
    private double strzalyCelne;
    private double gole;
    private double podania;
    @Column(name = "dokladnosc podan w procentach")
    private double dokladnoscPodan;
    @Column(name = "Podania kluczowe")
    private double podaniaKluczowe;
    private double asysty;
    private double pojedynki;
    @Column(name = "Pojedynki wygrane")
    private double pojedynkiWygrane;
    @Column(name = "Próby przechwytu")
    private double probyPrzechwytu;
    @Column(name = "Przechwyty udane")
    private double przechwytyUdane;
    private double dryblingi;
    @Column(name = "Dryblingi udane")
    private double dryblingiWygrane;
    @Column(name = "Faule na zawodniku")
    private double fauleNaZawodniku;
    @Column(name = "Faule popełnione")
    private double faulePopelnione;
    @Column(name = "Kartki żółte")
    private double kartkiZolte;
    @Column(name = "Kartki czerwone")
    private double kartkiCzerwone;
    @Column(name = "Czy zawodnik jest kontuzjowany")
    private boolean czyKontuzjowany;
    public double getDokladnoscPodan() {
        return dokladnoscPodan;
    }
    public void setDokladnoscPodan(double dokladnoscPodan) {
        this.dokladnoscPodan = dokladnoscPodan;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public double getWiek() {
        return wiek;
    }

    public void setWiek(double wiek) {
        this.wiek = wiek;
    }

    public double getWzrost() {
        return wzrost;
    }

    public void setWzrost(double wzrost) {
        this.wzrost = wzrost;
    }

    public double getWaga() {
        return waga;
    }

    public void setWaga(double waga) {
        this.waga = waga;
    }

    public String getKraj() {
        return kraj;
    }

    public void setKraj(String kraj) {
        this.kraj = kraj;
    }

    public double getWystepy() {
        return wystepy;
    }

    public void setWystepy(double wystepy) {
        this.wystepy = wystepy;
    }

    public double getMinuty() {
        return minuty;
    }

    public void setMinuty(double minuty) {
        this.minuty = minuty;
    }

    public String getPozycja() {
        return pozycja;
    }

    public void setPozycja(String pozycja) {
        this.pozycja = pozycja;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getStrzaly() {
        return strzaly;
    }

    public void setStrzaly(double strzaly) {
        this.strzaly = strzaly;
    }

    public double getStrzalyCelne() {
        return strzalyCelne;
    }

    public void setStrzalyCelne(double strzalyCelne) {
        this.strzalyCelne = strzalyCelne;
    }

    public double getGole() {
        return gole;
    }

    public void setGole(double gole) {
        this.gole = gole;
    }

    public double getPodania() {
        return podania;
    }

    public void setPodania(double podania) {
        this.podania = podania;
    }

    public double getPodaniaKluczowe() {
        return podaniaKluczowe;
    }

    public void setPodaniaKluczowe(double podaniaKluczowe) {
        this.podaniaKluczowe = podaniaKluczowe;
    }

    public double getAsysty() {
        return asysty;
    }

    public void setAsysty(double asysty) {
        this.asysty = asysty;
    }

    public double getPojedynki() {
        return pojedynki;
    }

    public void setPojedynki(double pojedynki) {
        this.pojedynki = pojedynki;
    }

    public double getPojedynkiWygrane() {
        return pojedynkiWygrane;
    }

    public void setPojedynkiWygrane(double pojedynkiWygrane) {
        this.pojedynkiWygrane = pojedynkiWygrane;
    }

    public double getProbyPrzechwytu() {
        return probyPrzechwytu;
    }

    public void setProbyPrzechwytu(double probyPrzechwytu) {
        this.probyPrzechwytu = probyPrzechwytu;
    }

    public double getPrzechwytyUdane() {
        return przechwytyUdane;
    }

    public void setPrzechwytyUdane(double przechwytyUdane) {
        this.przechwytyUdane = przechwytyUdane;
    }

    public double getDryblingi() {
        return dryblingi;
    }

    public void setDryblingi(double dryblingi) {
        this.dryblingi = dryblingi;
    }

    public double getDryblingiWygrane() {
        return dryblingiWygrane;
    }

    public void setDryblingiWygrane(double dryblingiWygrane) {
        this.dryblingiWygrane = dryblingiWygrane;
    }

    public double getFauleNaZawodniku() {
        return fauleNaZawodniku;
    }

    public void setFauleNaZawodniku(double fauleNaZawodniku) {
        this.fauleNaZawodniku = fauleNaZawodniku;
    }

    public double getFaulePopelnione() {
        return faulePopelnione;
    }

    public void setFaulePopelnione(double faulePopelnione) {
        this.faulePopelnione = faulePopelnione;
    }

    public double getKartkiZolte() {
        return kartkiZolte;
    }

    public void setKartkiZolte(double kartkiZolte) {
        this.kartkiZolte = kartkiZolte;
    }

    public double getKartkiCzerwone() {
        return kartkiCzerwone;
    }

    public void setKartkiCzerwone(double kartkiCzerwone) {
        this.kartkiCzerwone = kartkiCzerwone;
    }

    public boolean isCzyKontuzjowany() {
        return czyKontuzjowany;
    }

    public void setCzyKontuzjowany(boolean czyKontuzjowany) {
        this.czyKontuzjowany = czyKontuzjowany;
    }

}
