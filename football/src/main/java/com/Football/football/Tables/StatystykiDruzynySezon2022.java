package com.Football.football.Tables;


import jakarta.persistence.*;

@Entity
public class StatystykiDruzynySezon2022 {

    @Id
    @Column(name = "id druzyny", unique = true)
    private Long id;

    public StatystykiDruzynySezon2022() {}
    @Column(name = "Mecze domowe")
    private double meczeDomowe;
    @Column(name = "Mecze wyjazdowe")
    private double meczeWyjazdowe;
    @Column(name = "Wygrane na wyjeździe")
    private double wygraneNaWyjezdzie;
    @Column(name = "Wygrane w domu")
    private double wygraneWDomu;
    @Column(name = "Remisy w domu")
    private double remisyWDomu;
    @Column(name = "Remisy na wyjezdzie")
    private double remisyNaWyjezdzie;
    @Column(name = "Przegrane na wyjeździe")
    private double przegraneNaWyjezdzie;

    @Column(name = "Przegrane w domu")
    private double przegraneWDomu;

    @Column(name = "Gole strzelone w domu")
    private double goleStrzeloneWDomu;

    @Column(name = "Gole strzelone na wyjeździe")
    private double goleStrzeloneNaWyjezdzie;

    @Column(name = "Średnia goli strzelonych na wyjeździe")
    private double sredniaGoliStrzelonychNaWyjezdzie;

    @Column(name = "Średnia goli strzelonych w domu")
    private double sredniaGoliStrzelonychWDomu;

    @Column(name = "Gole strzelone pomiędzy minutami 0-15")
    private double goleStrzelonePomiedzyMinutami0_15;

    @Column(name = "Gole strzelone pomiędzy minutami 16-30")
    private double goleStrzelonePomiedzyMinutami16_30;

    @Column(name = "Gole strzelone pomiędzy minutami 31-45")
    private double goleStrzelonePomiedzyMinutami31_45;

    @Column(name = "Gole strzelone pomiędzy minutami 46-60")
    private double goleStrzelonePomiedzyMinutami46_60;

    @Column(name = "Gole strzelone pomiędzy minutami 61-75")
    private double goleStrzelonePomiedzyMinutami61_75;

    @Column(name = "Gole strzelone pomiędzy minutami 76-90")
    private double goleStrzelonePomiedzyMinutami76_90;

    @Column(name = "Gole strzelone pomiędzy minutami 91-105")
    private double goleStrzelonePomiedzyMinutami91_105;

    @Column(name = "Gole stracone w domu")
    private double goleStraconeWDomu;

    @Column(name = "Gole stracone na wyjeździe")
    private double goleStraconeNaWyjezdzie;

    @Column(name = "Średnia goli straconych na wyjeździe")
    private double sredniaGoliStraconychNaWyjezdzie;

    @Column(name = "Średnia goli straconych w domu")
    private double sredniaGoliStraconychWDomu;

    @Column(name = "Gole stracone pomiędzy minutami 0-15")
    private double goleStraconePomiedzyMinutami0_15;

    @Column(name = "Gole stracone pomiędzy minutami 16-30")
    private double goleStraconePomiedzyMinutami16_30;

    @Column(name = "Gole stracone pomiędzy minutami 31-45")
    private double goleStraconePomiedzyMinutami31_45;

    @Column(name = "Gole stracone pomiędzy minutami 46-60")
    private double goleStraconePomiedzyMinutami46_60;

    @Column(name = "Gole stracone pomiędzy minutami 61-75")
    private double goleStraconePomiedzyMinutami61_75;

    @Column(name = "Gole stracone pomiędzy minutami 76-90")
    private double goleStraconePomiedzyMinutami76_90;

    @Column(name = "Gole stracone pomiędzy minutami 91-105")
    private double goleStraconePomiedzyMinutami91_105;

    @Column(name = "Czyste konta w domu")
    private double czysteKontaWDomu;

    @Column(name = "Czyste konta na wyjeździe")
    private double czysteKontaNaWyjezdzie;

    @Column(name = "Mecze bez gola w domu")
    private double meczeBezGolaWDomu;

    @Column(name = "Mecze bez gola na wyjeździe")
    private double meczeBezGolaNaWyjezdzie;

    @Column(name = "Karne strzelone")
    private double karneStrzelone;

    @Column(name = "Karne nie strzelone")
    private double karneNiestrzelone;

    @Column(name = "Formacja")
    private String formacja;

    @Column(name = "Ile razy w tej formacji")
    private double ileRazyWtejFormacji;

    @Column(name = "Kartki żółte w minutach 0-15")
    private double kartkiZolteWMinucie0_15;

    @Column(name = "Kartki żółte w minutach 16-30")
    private double kartkiZolteWMinucie16_30;

    @Column(name = "Kartki żółte w minutach 31-45")
    private double kartkiZolteWMinucie31_45;

    @Column(name = "Kartki żółte w minutach 46-60")
    private double kartkiZolteWMinucie46_60;

    @Column(name = "Kartki żółte w minutach 61-75")
    private double kartkiZolteWMinucie61_75;
    @Column(name = "Kartki żółte w minutach 76-90")
    private double kartkiZolteWMinucie76_90;
    @Column(name = "Kartki żółte w minutach 91-105")
    private double kartkiZolteWMinucie91_105;
    @Column(name = "Kartki czerwone w minutach 0-15")
    private double kartkiCzerwoneWMinucie0_15;
    @Column(name = "Kartki czerwone w minutach 16-30")
    private double kartkiCzerwoneWMinucie16_30;
    @Column(name = "Kartki czerwone w minutach 31-45")
    private double kartkiCzerwoneWMinucie31_45;
    @Column(name = "Kartki czerwone w minutach 46-60")
    private double kartkiCzerwoneWMinucie46_60;
    @Column(name = "Kartki czerwone w minutach 61-75")
    private double kartkiCzerwoneWMinucie61_75;
    @Column(name = "Kartki czerwone w minutach 76-90")
    private double kartkiCzerwoneWMinucie76_90;
    @Column(name = "Kartki czerwone w minutach 91-105")
    private double kartkiCzerwoneWMinucie91_105;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMeczeDomowe() {
        return meczeDomowe;
    }

    public void setMeczeDomowe(double meczeDomowe) {
        this.meczeDomowe = meczeDomowe;
    }

    public double getMeczeWyjazdowe() {
        return meczeWyjazdowe;
    }

    public void setMeczeWyjazdowe(double meczeWyjazdowe) {
        this.meczeWyjazdowe = meczeWyjazdowe;
    }

    public double getWygraneNaWyjezdzie() {
        return wygraneNaWyjezdzie;
    }

    public void setWygraneNaWyjezdzie(double wygraneNaWyjezdzie) {
        this.wygraneNaWyjezdzie = wygraneNaWyjezdzie;
    }

    public double getWygraneWDomu() {
        return wygraneWDomu;
    }

    public void setWygraneWDomu(double wygraneWDomu) {
        this.wygraneWDomu = wygraneWDomu;
    }

    public double getRemisyWDomu() {
        return remisyWDomu;
    }

    public void setRemisyWDomu(double remisyWDomu) {
        this.remisyWDomu = remisyWDomu;
    }

    public double getRemisyNaWyjezdzie() {
        return remisyNaWyjezdzie;
    }

    public void setRemisyNaWyjezdzie(double remisyNaWyjezdzie) {
        this.remisyNaWyjezdzie = remisyNaWyjezdzie;
    }

    public double getPrzegraneNaWyjezdzie() {
        return przegraneNaWyjezdzie;
    }

    public void setPrzegraneNaWyjezdzie(double przegraneNaWyjezdzie) {
        this.przegraneNaWyjezdzie = przegraneNaWyjezdzie;
    }

    public double getPrzegraneWDomu() {
        return przegraneWDomu;
    }

    public void setPrzegraneWDomu(double przegraneWDomu) {
        this.przegraneWDomu = przegraneWDomu;
    }

    public double getGoleStrzeloneWDomu() {
        return goleStrzeloneWDomu;
    }

    public void setGoleStrzeloneWDomu(double goleStrzeloneWDomu) {
        this.goleStrzeloneWDomu = goleStrzeloneWDomu;
    }

    public double getGoleStrzeloneNaWyjezdzie() {
        return goleStrzeloneNaWyjezdzie;
    }

    public void setGoleStrzeloneNaWyjezdzie(double goleStrzeloneNaWyjezdzie) {
        this.goleStrzeloneNaWyjezdzie = goleStrzeloneNaWyjezdzie;
    }

    public double getSredniaGoliStrzelonychNaWyjezdzie() {
        return sredniaGoliStrzelonychNaWyjezdzie;
    }

    public void setSredniaGoliStrzelonychNaWyjezdzie(double sredniaGoliStrzelonychNaWyjezdzie) {
        this.sredniaGoliStrzelonychNaWyjezdzie = sredniaGoliStrzelonychNaWyjezdzie;
    }

    public double getSredniaGoliStrzelonychWDomu() {
        return sredniaGoliStrzelonychWDomu;
    }

    public void setSredniaGoliStrzelonychWDomu(double sredniaGoliStrzelonychWDomu) {
        this.sredniaGoliStrzelonychWDomu = sredniaGoliStrzelonychWDomu;
    }

    public double getGoleStrzelonePomiedzyMinutami0_15() {
        return goleStrzelonePomiedzyMinutami0_15;
    }

    public void setGoleStrzelonePomiedzyMinutami0_15(double goleStrzelonePomiedzyMinutami0_15) {
        this.goleStrzelonePomiedzyMinutami0_15 = goleStrzelonePomiedzyMinutami0_15;
    }

    public double getGoleStrzelonePomiedzyMinutami16_30() {
        return goleStrzelonePomiedzyMinutami16_30;
    }

    public void setGoleStrzelonePomiedzyMinutami16_30(double goleStrzelonePomiedzyMinutami16_30) {
        this.goleStrzelonePomiedzyMinutami16_30 = goleStrzelonePomiedzyMinutami16_30;
    }

    public double getGoleStrzelonePomiedzyMinutami31_45() {
        return goleStrzelonePomiedzyMinutami31_45;
    }

    public void setGoleStrzelonePomiedzyMinutami31_45(double goleStrzelonePomiedzyMinutami31_45) {
        this.goleStrzelonePomiedzyMinutami31_45 = goleStrzelonePomiedzyMinutami31_45;
    }

    public double getGoleStrzelonePomiedzyMinutami46_60() {
        return goleStrzelonePomiedzyMinutami46_60;
    }

    public void setGoleStrzelonePomiedzyMinutami46_60(double goleStrzelonePomiedzyMinutami46_60) {
        this.goleStrzelonePomiedzyMinutami46_60 = goleStrzelonePomiedzyMinutami46_60;
    }

    public double getGoleStrzelonePomiedzyMinutami61_75() {
        return goleStrzelonePomiedzyMinutami61_75;
    }

    public void setGoleStrzelonePomiedzyMinutami61_75(double goleStrzelonePomiedzyMinutami61_75) {
        this.goleStrzelonePomiedzyMinutami61_75 = goleStrzelonePomiedzyMinutami61_75;
    }

    public double getGoleStrzelonePomiedzyMinutami76_90() {
        return goleStrzelonePomiedzyMinutami76_90;
    }

    public void setGoleStrzelonePomiedzyMinutami76_90(double goleStrzelonePomiedzyMinutami76_90) {
        this.goleStrzelonePomiedzyMinutami76_90 = goleStrzelonePomiedzyMinutami76_90;
    }

    public double getGoleStrzelonePomiedzyMinutami91_105() {
        return goleStrzelonePomiedzyMinutami91_105;
    }

    public void setGoleStrzelonePomiedzyMinutami91_105(double goleStrzelonePomiedzyMinutami91_105) {
        this.goleStrzelonePomiedzyMinutami91_105 = goleStrzelonePomiedzyMinutami91_105;
    }

    public double getGoleStraconeWDomu() {
        return goleStraconeWDomu;
    }

    public void setGoleStraconeWDomu(double goleStraconeWDomu) {
        this.goleStraconeWDomu = goleStraconeWDomu;
    }

    public double getGoleStraconeNaWyjezdzie() {
        return goleStraconeNaWyjezdzie;
    }

    public void setGoleStraconeNaWyjezdzie(double goleStraconeNaWyjezdzie) {
        this.goleStraconeNaWyjezdzie = goleStraconeNaWyjezdzie;
    }

    public double getSredniaGoliStraconychNaWyjezdzie() {
        return sredniaGoliStraconychNaWyjezdzie;
    }

    public void setSredniaGoliStraconychNaWyjezdzie(double sredniaGoliStraconychNaWyjezdzie) {
        this.sredniaGoliStraconychNaWyjezdzie = sredniaGoliStraconychNaWyjezdzie;
    }

    public double getSredniaGoliStraconychWDomu() {
        return sredniaGoliStraconychWDomu;
    }

    public void setSredniaGoliStraconychWDomu(double sredniaGoliStraconychWDomu) {
        this.sredniaGoliStraconychWDomu = sredniaGoliStraconychWDomu;
    }

    public double getGoleStraconePomiedzyMinutami0_15() {
        return goleStraconePomiedzyMinutami0_15;
    }

    public void setGoleStraconePomiedzyMinutami0_15(double goleStraconePomiedzyMinutami0_15) {
        this.goleStraconePomiedzyMinutami0_15 = goleStraconePomiedzyMinutami0_15;
    }

    public double getGoleStraconePomiedzyMinutami16_30() {
        return goleStraconePomiedzyMinutami16_30;
    }

    public void setGoleStraconePomiedzyMinutami16_30(double goleStraconePomiedzyMinutami16_30) {
        this.goleStraconePomiedzyMinutami16_30 = goleStraconePomiedzyMinutami16_30;
    }

    public double getGoleStraconePomiedzyMinutami31_45() {
        return goleStraconePomiedzyMinutami31_45;
    }

    public void setGoleStraconePomiedzyMinutami31_45(double goleStraconePomiedzyMinutami31_45) {
        this.goleStraconePomiedzyMinutami31_45 = goleStraconePomiedzyMinutami31_45;
    }

    public double getGoleStraconePomiedzyMinutami46_60() {
        return goleStraconePomiedzyMinutami46_60;
    }

    public void setGoleStraconePomiedzyMinutami46_60(double goleStraconePomiedzyMinutami46_60) {
        this.goleStraconePomiedzyMinutami46_60 = goleStraconePomiedzyMinutami46_60;
    }

    public double getGoleStraconePomiedzyMinutami61_75() {
        return goleStraconePomiedzyMinutami61_75;
    }

    public void setGoleStraconePomiedzyMinutami61_75(double goleStraconePomiedzyMinutami61_75) {
        this.goleStraconePomiedzyMinutami61_75 = goleStraconePomiedzyMinutami61_75;
    }

    public double getGoleStraconePomiedzyMinutami76_90() {
        return goleStraconePomiedzyMinutami76_90;
    }

    public void setGoleStraconePomiedzyMinutami76_90(double goleStraconePomiedzyMinutami76_90) {
        this.goleStraconePomiedzyMinutami76_90 = goleStraconePomiedzyMinutami76_90;
    }

    public double getGoleStraconePomiedzyMinutami91_105() {
        return goleStraconePomiedzyMinutami91_105;
    }

    public void setGoleStraconePomiedzyMinutami91_105(double goleStraconePomiedzyMinutami91_105) {
        this.goleStraconePomiedzyMinutami91_105 = goleStraconePomiedzyMinutami91_105;
    }

    public double getCzysteKontaWDomu() {
        return czysteKontaWDomu;
    }

    public void setCzysteKontaWDomu(double czysteKontaWDomu) {
        this.czysteKontaWDomu = czysteKontaWDomu;
    }

    public double getCzysteKontaNaWyjezdzie() {
        return czysteKontaNaWyjezdzie;
    }

    public void setCzysteKontaNaWyjezdzie(double czysteKontaNaWyjezdzie) {
        this.czysteKontaNaWyjezdzie = czysteKontaNaWyjezdzie;
    }

    public double getMeczeBezGolaWDomu() {
        return meczeBezGolaWDomu;
    }

    public void setMeczeBezGolaWDomu(double meczeBezGolaWDomu) {
        this.meczeBezGolaWDomu = meczeBezGolaWDomu;
    }

    public double getMeczeBezGolaNaWyjezdzie() {
        return meczeBezGolaNaWyjezdzie;
    }

    public void setMeczeBezGolaNaWyjezdzie(double meczeBezGolaNaWyjezdzie) {
        this.meczeBezGolaNaWyjezdzie = meczeBezGolaNaWyjezdzie;
    }

    public double getKarneStrzelone() {
        return karneStrzelone;
    }

    public void setKarneStrzelone(double karneStrzelone) {
        this.karneStrzelone = karneStrzelone;
    }

    public double getKarneNiestrzelone() {
        return karneNiestrzelone;
    }

    public void setKarneNiestrzelone(double karneNiestrzelone) {
        this.karneNiestrzelone = karneNiestrzelone;
    }

    public String getFormaca() {
        return formacja;
    }

    public void setFormaca(String formaca) {
        this.formacja = formaca;
    }

    public double getIleRazyWtejFormacji() {
        return ileRazyWtejFormacji;
    }

    public void setIleRazyWtejFormacji(double ileRazyWtejFormacji) {
        this.ileRazyWtejFormacji = ileRazyWtejFormacji;
    }

    public double getKartkiZolteWMinucie0_15() {
        return kartkiZolteWMinucie0_15;
    }

    public void setKartkiZolteWMinucie0_15(double kartkiZolteWMinucie0_15) {
        this.kartkiZolteWMinucie0_15 = kartkiZolteWMinucie0_15;
    }

    public double getKartkiZolteWMinucie16_30() {
        return kartkiZolteWMinucie16_30;
    }

    public void setKartkiZolteWMinucie16_30(double kartkiZolteWMinucie16_30) {
        this.kartkiZolteWMinucie16_30 = kartkiZolteWMinucie16_30;
    }

    public double getKartkiZolteWMinucie31_45() {
        return kartkiZolteWMinucie31_45;
    }

    public void setKartkiZolteWMinucie31_45(double kartkiZolteWMinucie31_45) {
        this.kartkiZolteWMinucie31_45 = kartkiZolteWMinucie31_45;
    }

    public double getKartkiZolteWMinucie46_60() {
        return kartkiZolteWMinucie46_60;
    }

    public void setKartkiZolteWMinucie46_60(double kartkiZolteWMinucie46_60) {
        this.kartkiZolteWMinucie46_60 = kartkiZolteWMinucie46_60;
    }

    public double getKartkiZolteWMinucie61_75() {
        return kartkiZolteWMinucie61_75;
    }

    public void setKartkiZolteWMinucie61_75(double kartkiZolteWMinucie61_75) {
        this.kartkiZolteWMinucie61_75 = kartkiZolteWMinucie61_75;
    }

    public double getKartkiZolteWMinucie76_90() {
        return kartkiZolteWMinucie76_90;
    }

    public void setKartkiZolteWMinucie76_90(double kartkiZolteWMinucie76_90) {
        this.kartkiZolteWMinucie76_90 = kartkiZolteWMinucie76_90;
    }

    public double getKartkiZolteWMinucie91_105() {
        return kartkiZolteWMinucie91_105;
    }

    public void setKartkiZolteWMinucie91_105(double kartkiZolteWMinucie91_105) {
        this.kartkiZolteWMinucie91_105 = kartkiZolteWMinucie91_105;
    }

    public double getKartkiCzerwoneWMinucie0_15() {
        return kartkiCzerwoneWMinucie0_15;
    }

    public void setKartkiCzerwoneWMinucie0_15(double kartkiCzerwoneWMinucie0_15) {
        this.kartkiCzerwoneWMinucie0_15 = kartkiCzerwoneWMinucie0_15;
    }

    public double getKartkiCzerwoneWMinucie16_30() {
        return kartkiCzerwoneWMinucie16_30;
    }

    public void setKartkiCzerwoneWMinucie16_30(double kartkiCzerwoneWMinucie16_30) {
        this.kartkiCzerwoneWMinucie16_30 = kartkiCzerwoneWMinucie16_30;
    }

    public double getKartkiCzerwoneWMinucie31_45() {
        return kartkiCzerwoneWMinucie31_45;
    }

    public void setKartkiCzerwoneWMinucie31_45(double kartkiCzerwoneWMinucie31_45) {
        this.kartkiCzerwoneWMinucie31_45 = kartkiCzerwoneWMinucie31_45;
    }

    public double getKartkiCzerwoneWMinucie46_60() {
        return kartkiCzerwoneWMinucie46_60;
    }

    public void setKartkiCzerwoneWMinucie46_60(double kartkiCzerwoneWMinucie46_60) {
        this.kartkiCzerwoneWMinucie46_60 = kartkiCzerwoneWMinucie46_60;
    }

    public double getKartkiCzerwoneWMinucie61_75() {
        return kartkiCzerwoneWMinucie61_75;
    }

    public void setKartkiCzerwoneWMinucie61_75(double kartkiCzerwoneWMinucie61_75) {
        this.kartkiCzerwoneWMinucie61_75 = kartkiCzerwoneWMinucie61_75;
    }

    public double getKartkiCzerwoneWMinucie76_90() {
        return kartkiCzerwoneWMinucie76_90;
    }

    public void setKartkiCzerwoneWMinucie76_90(double kartkiCzerwoneWMinucie76_90) {
        this.kartkiCzerwoneWMinucie76_90 = kartkiCzerwoneWMinucie76_90;
    }

    public double getKartkiCzerwoneWMinucie91_105() {
        return kartkiCzerwoneWMinucie91_105;
    }

    public void setKartkiCzerwoneWMinucie91_105(double kartkiCzerwoneWMinucie91_105) {
        this.kartkiCzerwoneWMinucie91_105 = kartkiCzerwoneWMinucie91_105;
    }

}
