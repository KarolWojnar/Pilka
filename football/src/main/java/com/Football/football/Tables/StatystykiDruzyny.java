package com.Football.football.Tables;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class StatystykiDruzyny {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "suma spotkan")
    private Double sumaSpotkan;

    @Column(name = "Nazwa drużyny")
    private String teamName;

    @Column(name = "id drużyny")
    private Long teamId;
    @Column(name = "sezon")
    private Long season;

    public StatystykiDruzyny() {}
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
    @Column(name = "id ligi")
    private int leagueId;
}
