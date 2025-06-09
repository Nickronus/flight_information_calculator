package nickronus.flight_information_calculator;

import java.time.LocalDateTime;
import java.io.Serializable;

public class Flight implements Serializable {
    public double remaining;       // Остаток (кг)
    public double refueled;        // Заправлено (кг)
    public int people;             // Люди (количество)
    public double cargo;           // Груз (кг)
    public int groundTime;         // Земля (минуты)
    public int parkingTime;        // Стоянка (минуты)
    public int flightTime;         // Время полёта (минуты)
    public LocalDateTime landingTime; // Время посадки

    public Flight(double remaining, double refueled, int people, double cargo,
                  int groundTime, int parkingTime, int flightTime, LocalDateTime landingTime) {
        this.remaining = remaining;
        this.refueled = refueled;
        this.people = people;
        this.cargo = cargo;
        this.groundTime = groundTime;
        this.parkingTime = parkingTime;
        this.flightTime = flightTime;
        this.landingTime = landingTime;
    }
}