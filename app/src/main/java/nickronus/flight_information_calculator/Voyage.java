package nickronus.flight_information_calculator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Voyage {
    public String name;
    public List<Flight> flights;
    public double emptyAircraftMass;  // Масса пустого борта (кг)
    public double averagePassengerMass;  // Средняя масса пассажира (кг)
    public LocalDateTime takeoffTime;  // Время взлёта
    public LocalDateTime preFlightTime;  // Предполётное время
    public LocalDateTime postFlightTime;  // Послеполётное время

    public Voyage(String name, double emptyAircraftMass, double averagePassengerMass) {
        this.name = name;
        this.emptyAircraftMass = emptyAircraftMass;
        this.averagePassengerMass = averagePassengerMass;
        this.flights = new ArrayList<>();
    }
}