package com.Football.football.Tables;

import jakarta.persistence.*;

@Entity
public class StatystykiSpotkan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public StatystykiSpotkan() {}

    @Column(name = "Data spotkania")
    private String dataSpotkania;
    private String wynik;
    @Column(unique = true, name = "id Spotkania")
    private int idSpotkania;
    private String druzyny;
    @Column(name = "Strzały celne")
    private double strzalyCelne;
    @Column(name = "Strzały niecelne")
    private double strzalyNiecelne;
    private double strzaly;
    @Column(name = "Strzały zablokowane")
    private double strzalyZablokowane;
    @Column(name = "Strzały zza pola karnego")
    private double strzalyZzaPolaKarnego;
    @Column(name = "Strzały z pola karnego")
    private double strzalyZPolaKarnego;
    private double faule;
    @Column(name = "Rzuty rożne")
    private double rzutRozne;
    private double spalone;
    @Column(name = "Posiadanie piłki w procentach")
    private double posiadaniePilkiWProcentach;
    @Column(name = "Kartki żółte")
    private double kartkiZolte;
    @Column(name = "Kartki  czerwone")
    private double kartkiCzerwone;
    @Column(name = "Obrony bramkarza")
    private double obronyBramkarza;
    private double podania;
    @Column(name = "Podania celne")
    private double podaniaCelne;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataSpotkania() {
        return dataSpotkania;
    }

    public void setDataSpotkania(String dataSpotkania) {
        this.dataSpotkania = dataSpotkania;
    }

    public String getWynik() {
        return wynik;
    }

    public void setWynik(String wynik) {
        this.wynik = wynik;
    }

    public int getIdSpotkania() {
        return idSpotkania;
    }

    public void setIdSpotkania(int idSpotkania) {
        this.idSpotkania = idSpotkania;
    }

    public String getDruzyny() {
        return druzyny;
    }

    public void setDruzyny(String druzyny) {
        this.druzyny = druzyny;
    }

    public double getStrzalyCelne() {
        return strzalyCelne;
    }

    public void setStrzalyCelne(double strzalyCelne) {
        this.strzalyCelne = strzalyCelne;
    }

    public double getStrzalyNiecelne() {
        return strzalyNiecelne;
    }

    public void setStrzalyNiecelne(double strzalyNiecelne) {
        this.strzalyNiecelne = strzalyNiecelne;
    }

    public double getStrzaly() {
        return strzaly;
    }

    public void setStrzaly(double strzaly) {
        this.strzaly = strzaly;
    }

    public double getStrzalyZablokowane() {
        return strzalyZablokowane;
    }

    public void setStrzalyZablokowane(double strzalyZablokowane) {
        this.strzalyZablokowane = strzalyZablokowane;
    }

    public double getStrzalyZzaPolaKarnego() {
        return strzalyZzaPolaKarnego;
    }

    public void setStrzalyZzaPolaKarnego(double strzalyZzaPolaKarnego) {
        this.strzalyZzaPolaKarnego = strzalyZzaPolaKarnego;
    }

    public double getStrzalyZPolaKarnego() {
        return strzalyZPolaKarnego;
    }

    public void setStrzalyZPolaKarnego(double strzalyZPolaKarnego) {
        this.strzalyZPolaKarnego = strzalyZPolaKarnego;
    }

    public double getFaule() {
        return faule;
    }

    public void setFaule(double faule) {
        this.faule = faule;
    }

    public double getRzutRozne() {
        return rzutRozne;
    }

    public void setRzutRozne(double rzutRozne) {
        this.rzutRozne = rzutRozne;
    }

    public double getSpalone() {
        return spalone;
    }

    public void setSpalone(double spalone) {
        this.spalone = spalone;
    }

    public double getPosiadaniePilkiWProcentach() {
        return posiadaniePilkiWProcentach;
    }

    public void setPosiadaniePilkiWProcentach(double posiadaniePilkiWProcentach) {
        this.posiadaniePilkiWProcentach = posiadaniePilkiWProcentach;
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

    public double getObronyBramkarza() {
        return obronyBramkarza;
    }

    public void setObronyBramkarza(double obronyBramkarza) {
        this.obronyBramkarza = obronyBramkarza;
    }

    public double getPodania() {
        return podania;
    }

    public void setPodania(double podania) {
        this.podania = podania;
    }

    public double getPodaniaCelne() {
        return podaniaCelne;
    }

    public void setPodaniaCelne(double podaniaCelne) {
        this.podaniaCelne = podaniaCelne;
    }


}
