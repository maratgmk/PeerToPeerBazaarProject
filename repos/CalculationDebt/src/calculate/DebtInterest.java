package calculate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DebtInterest {


    private double debt;
    private double rate;
    private long daysInterval;
    private LocalDate startDate;
    private LocalDate endDate;

    public DebtInterest(double debt, double rate, LocalDate startDate, LocalDate endDate) {
        this.debt = debt;
        this.rate = rate;
        this.daysInterval =  ChronoUnit.DAYS.between(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public double getDebt() {
        return this.debt;
    }

    public double getRate() {
        return this.rate;
    }

    public long getDaysInterval() {
        return daysInterval;
    }

    @Override
    public String toString() {
        return "InterestOfDebt{" +
                "debt=" + debt +
                ", rate=" + rate +
                ", interval=" + daysInterval +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    public int daysInYear(int year) {
        return this.isLeapYear(year) ? 366 : 365;
    }

    private boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    public double calculate() {

        double dailyRate = rate / (100 * daysInYear(startDate.getYear()));


        // Debugging output
        System.out.println("Debt: " + debt);
        System.out.println("Rate: " + rate);
        System.out.println("Annual Days: " + daysInYear(startDate.getYear()));
        System.out.println("Daily Rate: " + dailyRate);

        double interest = debt * dailyRate * daysInterval;
        // More debugging output
        System.out.println("Calculated Interest (before rounding): " + interest);


        return interest;

    }

    public static void main(String[] args) {
        long daysInterval1 = ChronoUnit.DAYS.between(LocalDate.of(2021,1,1), LocalDate.of(2021,3,21));
        long daysInterval2 = ChronoUnit.DAYS.between(LocalDate.of(2021,3,22), LocalDate.of(2021,4,25));
        long daysInterval3 = ChronoUnit.DAYS.between(LocalDate.of(2021,4,26), LocalDate.of(2021,6,14));
        long daysInterval4 = ChronoUnit.DAYS.between(LocalDate.of(2021,6,15), LocalDate.of(2021,7,25));
        long daysInterval5 = ChronoUnit.DAYS.between(LocalDate.of(2021,7,26), LocalDate.of(2021,9,12));
        long daysInterval6 = ChronoUnit.DAYS.between(LocalDate.of(2021,9,13), LocalDate.of(2021,10,24));
        long daysInterval7 = ChronoUnit.DAYS.between(LocalDate.of(2021,10,25), LocalDate.of(2021,12,19));
        long daysInterval8 = ChronoUnit.DAYS.between(LocalDate.of(2021,12,20), LocalDate.of(2022,2,13));
        long daysInterval9 = ChronoUnit.DAYS.between(LocalDate.of(2022,2,14), LocalDate.of(2022,2,27));
        long daysInterval10 = ChronoUnit.DAYS.between(LocalDate.of(2022,2,28), LocalDate.of(2022,4,10));
        long daysInterval11 = ChronoUnit.DAYS.between(LocalDate.of(2022,4,11), LocalDate.of(2022,5,3));
        long daysInterval12 = ChronoUnit.DAYS.between(LocalDate.of(2022,5,4), LocalDate.of(2022,5,26));
        long daysInterval13 = ChronoUnit.DAYS.between(LocalDate.of(2022,5,27), LocalDate.of(2022,6,13));
        long daysInterval14 = ChronoUnit.DAYS.between(LocalDate.of(2022,6,14), LocalDate.of(2022,7,24));
        long daysInterval15 = ChronoUnit.DAYS.between(LocalDate.of(2022,7,25), LocalDate.of(2022,9,18));
        long daysInterval16 = ChronoUnit.DAYS.between(LocalDate.of(2022,9,19), LocalDate.of(2023,7,23));
        long daysInterval17 = ChronoUnit.DAYS.between(LocalDate.of(2023,7,24), LocalDate.of(2023,8,14));
        long daysInterval18 = ChronoUnit.DAYS.between(LocalDate.of(2023,8,15), LocalDate.of(2023,9,17));
        long daysInterval19 = ChronoUnit.DAYS.between(LocalDate.of(2023,9,18), LocalDate.of(2023,10,29));
        long daysInterval20 = ChronoUnit.DAYS.between(LocalDate.of(2023,10,30), LocalDate.of(2023,12,17));
        long daysInterval21 = ChronoUnit.DAYS.between(LocalDate.of(2023,12,18), LocalDate.of(2024,7,28));
        long daysInterval22 = ChronoUnit.DAYS.between(LocalDate.of(2024,7,29), LocalDate.of(2024,9,15));
        long daysInterval23 = ChronoUnit.DAYS.between(LocalDate.of(2024,9,16), LocalDate.of(2024,10,27));
        long daysInterval24 = ChronoUnit.DAYS.between(LocalDate.of(2024,10,28), LocalDate.of(2025,6,8));
        long daysInterval25 = ChronoUnit.DAYS.between(LocalDate.of(2025,6,9), LocalDate.now());

        List<Long> daysIntervals = new ArrayList<>(List.of(daysInterval1,daysInterval2,daysInterval3,daysInterval4,daysInterval5,daysInterval6,
                daysInterval7,daysInterval8,daysInterval9,daysInterval10,daysInterval11,daysInterval12,daysInterval13,daysInterval14,daysInterval15,
                daysInterval16,daysInterval17,daysInterval18,daysInterval19,daysInterval20,daysInterval21,daysInterval22,daysInterval23,daysInterval24,daysInterval25));

        System.out.println("Интервалы действия ставки : " + daysIntervals);
        System.out.println();

        DebtInterest debtInterest1 = new DebtInterest(103982.00, 4.25, LocalDate.of(2021,1,1), LocalDate.of(2021,3,21));
        DebtInterest debtInterest2 = new DebtInterest(103982.00, 4.50, LocalDate.of(2021,3,22), LocalDate.of(2021,4,25));
        DebtInterest debtInterest3 = new DebtInterest(103982.00, 5.0, LocalDate.of(2021,4,26), LocalDate.of(2021,6,14));
        DebtInterest debtInterest4 = new DebtInterest(103982.00, 5.5, LocalDate.of(2021,6,15), LocalDate.of(2021,7,25));
        DebtInterest debtInterest5 = new DebtInterest(103982.00, 6.5, LocalDate.of(2021,7,26), LocalDate.of(2021,9,12));
        DebtInterest debtInterest6 = new DebtInterest(103982.00, 6.75, LocalDate.of(2021,9,13), LocalDate.of(2021,10,24));
        DebtInterest debtInterest7 = new DebtInterest(103982.00, 7.5, LocalDate.of(2021,10,25), LocalDate.of(2021,12,19));
        DebtInterest debtInterest8 = new DebtInterest(103982.00, 8.5, LocalDate.of(2021,12,20), LocalDate.of(2022,2,13));
        DebtInterest debtInterest9 = new DebtInterest(103982.00, 9.5, LocalDate.of(2022,2,14), LocalDate.of(2022,2,27));
        DebtInterest debtInterest10 = new DebtInterest(103982.00, 20.0,LocalDate.of(2022,2,28), LocalDate.of(2022,4,10));
        DebtInterest debtInterest11 = new DebtInterest(103982.00, 17.0, LocalDate.of(2022,4,11), LocalDate.of(2022,5,3));
        DebtInterest debtInterest12 = new DebtInterest(103982.00, 14.0, LocalDate.of(2022,5,4), LocalDate.of(2022,5,26));
        DebtInterest debtInterest13 = new DebtInterest(103982.00, 11.0, LocalDate.of(2022,5,27), LocalDate.of(2022,6,13));
        DebtInterest debtInterest14 = new DebtInterest(103982.00, 9.5, LocalDate.of(2022,6,14), LocalDate.of(2022,7,24));
        DebtInterest debtInterest15 = new DebtInterest(103982.00, 8.0, LocalDate.of(2022,7,25), LocalDate.of(2022,9,18));
        DebtInterest debtInterest16 = new DebtInterest(103982.00, 7.5, LocalDate.of(2022,9,19), LocalDate.of(2023,7,23));
        DebtInterest debtInterest17 = new DebtInterest(103982.00, 8.5, LocalDate.of(2023,7,24), LocalDate.of(2023,8,14));
        DebtInterest debtInterest18 = new DebtInterest(103982.00, 12.0, LocalDate.of(2023,8,15), LocalDate.of(2023,9,17));
        DebtInterest debtInterest19 = new DebtInterest(103982.00, 13.0, LocalDate.of(2023,9,18), LocalDate.of(2023,10,29));
        DebtInterest debtInterest20 = new DebtInterest(103982.00, 15.0, LocalDate.of(2023,10,30), LocalDate.of(2023,12,17));
        DebtInterest debtInterest21 = new DebtInterest(103982.00, 16.0,LocalDate.of(2023,12,18), LocalDate.of(2024,7,28));
        DebtInterest debtInterest22 = new DebtInterest(103982.00, 18.0,LocalDate.of(2024,7,29), LocalDate.of(2024,9,15));
        DebtInterest debtInterest23 = new DebtInterest(103982.00, 19.0,LocalDate.of(2024,9,16), LocalDate.of(2024,10,27));
        DebtInterest debtInterest24 = new DebtInterest(103982.00, 21.0,LocalDate.of(2024,10,28), LocalDate.of(2025,6,8));
        DebtInterest debtInterest25 = new DebtInterest(103982.00, 20.0,LocalDate.of(2025,6,9), LocalDate.now());

        double cal1 = debtInterest1.calculate();
        double cal2 = debtInterest2.calculate();
        double cal3 = debtInterest3.calculate();
        double cal4 = debtInterest4.calculate();
        double cal5 = debtInterest5.calculate();
        double cal6 = debtInterest6.calculate();
        double cal7 = debtInterest7.calculate();
        double cal8 = debtInterest8.calculate();
        double cal9 = debtInterest9.calculate();
        double cal10 = debtInterest10.calculate();
        double cal11 = debtInterest11.calculate();
        double cal12 = debtInterest12.calculate();
        double cal13 = debtInterest13.calculate();
        double cal14 = debtInterest14.calculate();
        double cal15 = debtInterest15.calculate();
        double cal16 = debtInterest16.calculate();
        double cal17 = debtInterest17.calculate();
        double cal18 = debtInterest18.calculate();
        double cal19 = debtInterest19.calculate();
        double cal20 = debtInterest20.calculate();
        double cal21 = debtInterest21.calculate();
        double cal22 = debtInterest22.calculate();
        double cal23 = debtInterest23.calculate();
        double cal24 = debtInterest24.calculate();
        double cal25 = debtInterest25.calculate();

        List<Double> result = List.of(cal1, cal2, cal3, cal4, cal5, cal6, cal7, cal8, cal9, cal10, cal11, cal12,
                cal13, cal14, cal15, cal16, cal17, cal18, cal19, cal20,cal21,cal22,cal23,cal24,cal25);
        System.out.println("За первый период  " + cal1);
        System.out.println("За второй период  " + cal2);
        System.out.println("За третий период  " + cal3);
        System.out.println("Список вычисленных процентов : " + result);
        System.out.println();

        System.out.println("Итоговая сумма всех вычисленных процентов =  " + result.stream().reduce(0.00, Double::sum ));
        System.out.println();

        double newDebt = debtInterest1.getDebt() + (result.stream().reduce(0.00, Double::sum));
        System.out.println("Новый долг : " + newDebt);
        System.out.println();

        System.out.println("Duration1 = " + daysInterval1 + " days in year " + debtInterest1.daysInYear(LocalDate.of(2021,1,1).getYear()) + " % " + debtInterest1.getRate());
        System.out.println("Duration2 = " + daysInterval2 + " days in year " + debtInterest2.daysInYear(LocalDate.of(2021,3,22).getYear()) + " % " + debtInterest2.getRate());
        System.out.println("Duration3 = " + daysInterval3 + " days in year " + debtInterest3.daysInYear(LocalDate.of(2021,4,26).getYear()) + " % " + debtInterest3.getRate());
        System.out.println("Duration4 = " + daysInterval4 + " days in year " + debtInterest4.daysInYear(LocalDate.of(2021,6,15).getYear()) + " % " + debtInterest4.getRate());
        System.out.println("Duration5 = " + daysInterval5 + " days in year " + debtInterest5.daysInYear(LocalDate.of(2021,7,26).getYear()) + " % " + debtInterest5.getRate());
        System.out.println("Duration6 = " + daysInterval6 + " days in year " + debtInterest6.daysInYear(LocalDate.of(2021,9,13).getYear()) + " % " + debtInterest6.getRate());
        System.out.println("Duration7 = " + daysInterval7 + " days in year " + debtInterest7.daysInYear(LocalDate.of(2021,10,25).getYear()) + " % " + debtInterest7.getRate());
        System.out.println("Duration8 = " + daysInterval8 + " days in year " + debtInterest8.daysInYear(LocalDate.of(2021,12,20).getYear()) + " % " + debtInterest8.getRate());
        System.out.println("Duration9 = " + daysInterval9 + " days in year " + debtInterest9.daysInYear(LocalDate.of(2022,2,14).getYear()) + " % " + debtInterest9.getRate());
        System.out.println("Duration10 = " + daysInterval10 + " days in year " + debtInterest10.daysInYear(LocalDate.of(2022,2,28).getYear()) + " % " + debtInterest10.getRate());
        System.out.println("Duration11 = " + daysInterval11 + " days in year " + debtInterest11.daysInYear(LocalDate.of(2022,4,11).getYear()) + " % " + debtInterest11.getRate());
        System.out.println("Duration12 = " + daysInterval12 + " days in year " + debtInterest12.daysInYear(LocalDate.of(2022,5,4).getYear()) + " % " + debtInterest12.getRate());
        System.out.println("Duration13 = " + daysInterval13 + " days in year " + debtInterest13.daysInYear(LocalDate.of(2022,5,27).getYear()) + " % " + debtInterest13.getRate());
        System.out.println("Duration14 = " + daysInterval14 + " days in year " + debtInterest14.daysInYear(LocalDate.of(2022,6,14).getYear()) + " % " + debtInterest14.getRate());
        System.out.println("Duration15 = " + daysInterval15 + " days in year " + debtInterest15.daysInYear(LocalDate.of(2022,7,25).getYear()) + " % " + debtInterest15.getRate());
        System.out.println("Duration16 = " + daysInterval16 + " days in year " + debtInterest16.daysInYear(LocalDate.of(2022,9,19).getYear()) + " % " + debtInterest16.getRate());
        System.out.println("Duration17 = " + daysInterval17 + " days in year " + debtInterest17.daysInYear(LocalDate.of(2023,7,24).getYear()) + " % " + debtInterest17.getRate());
        System.out.println("Duration18 = " + daysInterval18 + " days in year " + debtInterest18.daysInYear(LocalDate.of(2023,8,15).getYear()) + " % " + debtInterest18.getRate());
        System.out.println("Duration19 = " + daysInterval19 + " days in year " + debtInterest19.daysInYear(LocalDate.of(2023,9,18).getYear()) + " % " + debtInterest19.getRate());
        System.out.println("Duration20 = " + daysInterval20 + " days in year " + debtInterest20.daysInYear(LocalDate.of(2023,10,30).getYear()) + " % " + debtInterest20.getRate());
        System.out.println("Duration21 = " + daysInterval21 + " days in year " + debtInterest21.daysInYear(LocalDate.of(2023,12,18).getYear()) + " % " + debtInterest21.getRate());
        System.out.println("Duration22 = " + daysInterval22 + " days in year " + debtInterest22.daysInYear(LocalDate.of(2024,7,29).getYear()) + " % " + debtInterest22.getRate());
        System.out.println("Duration23 = " + daysInterval23 + " days in year " + debtInterest23.daysInYear(LocalDate.of(2024,9,16).getYear()) + " % " + debtInterest23.getRate());
        System.out.println("Duration24 = " + daysInterval24 + " days in year " + debtInterest24.daysInYear(LocalDate.of(2024,10,28).getYear()) + " % " + debtInterest24.getRate());
        System.out.println("Duration25 = " + daysInterval25 + " days in year " + debtInterest25.daysInYear(LocalDate.of(2025,6,9).getYear()) + " % " + debtInterest25.getRate());


    }




    }



