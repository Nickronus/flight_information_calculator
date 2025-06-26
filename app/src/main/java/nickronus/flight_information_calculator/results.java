package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDateTime;

public class results extends AppCompatActivity {

    private Voyage currentVoyage;
    private Flight currentFlight;
    private int currentFlightIndex = 0;

    private TextView textPoint;
    private TextView textFlightTime;
    private TextView textParking;
    private TextView textDelay;
    private TextView textTotalWork;
    private TextView textLandings;
    private TextView textRemaining;
    private TextView textNormConsumption;
    private TextView textActualConsumption;
    private TextView textSavings;
    private TextView textOverconsumption;
    private TextView textDoctorTime;

    private Button btnUnderstand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");

        textPoint = findViewById(R.id.textPoint);
        textFlightTime = findViewById(R.id.textFlightTime);
        textParking = findViewById(R.id.textParking);
        textDelay = findViewById(R.id.textDelay);
        textTotalWork = findViewById(R.id.textTotalWork);
        textLandings = findViewById(R.id.textLandings);
        textRemaining = findViewById(R.id.textRemaining);
        textNormConsumption = findViewById(R.id.textNormConsumption);
        textActualConsumption = findViewById(R.id.textActualConsumption);
        textSavings = findViewById(R.id.textSavings);
        textOverconsumption = findViewById(R.id.textOverconsumption);
        textDoctorTime = findViewById(R.id.textDoctorTime);

        Flight currentFlight = currentVoyage.flights.get(currentVoyage.flights.size() - 1);

        // Пункт
        LocalDateTime point = currentFlight.landingTime;
        if (point != null){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            textPoint.setText(point.format(dateTimeFormatter));
        }

        // Полётное
        int flightTime = 0;
        for (int i = 0; i < currentVoyage.flights.size(); i++){
            flightTime += currentVoyage.flights.get(i).flightTime + currentVoyage.flights.get(i).groundTime;
        }
        textFlightTime.setText(String.valueOf(flightTime));

        // Стоянка
        int parking = 0;
        for (int i = 0; i < currentVoyage.flights.size(); i++){
            parking += currentVoyage.flights.get(i).parkingTime;
        }
        textParking.setText(String.valueOf(parking));

        // Задержка
        if (currentVoyage.plannedTakeoffTime != null){
            textDelay.setText(String.valueOf(Duration.between(
                    currentVoyage.plannedTakeoffTime,
                    currentVoyage.takeoffTime
            ).toMinutes()));
        }

        // Время врача
        LocalDateTime doctorTime = null;
        if (currentVoyage.plannedTakeoffTime != null){
            doctorTime = currentVoyage.plannedTakeoffTime
                    .minusMinutes(currentVoyage.flights.get(0).groundTime)
                    .minusMinutes(currentVoyage.preFlightTime);
        } else {
            doctorTime = currentVoyage.takeoffTime
                    .minusMinutes(currentVoyage.flights.get(0).groundTime)
                    .minusMinutes(currentVoyage.preFlightTime);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        textDoctorTime.setText(doctorTime.format(formatter));

        // Общее рабочее
        int totalWork = (int)Duration.between(
                doctorTime,
                currentFlight.landingTime.plusMinutes(currentFlight.groundTime).
                plusMinutes(currentVoyage.postFlightTime)).toMinutes();
        textTotalWork.setText(String.valueOf(formatTime(totalWork)));

        // Посадок
        textLandings.setText(String.valueOf(currentVoyage.flights.size()));

        // Остаток
        textRemaining.setText(String.valueOf((currentVoyage.remaining)));

        // Расход
        int all = 0;
        int ground = 0;
        int fuel = 0;
        for (int i = 0; i < currentVoyage.flights.size(); i++){
            all += currentVoyage.flights.get(i).flightTime;
            ground += currentVoyage.flights.get(i).groundTime;
            fuel += currentVoyage.flights.get(i).refueled;
        }
        double normConsumption = 10.666 * all + 6 * ground;
        textNormConsumption.setText(String.valueOf(normConsumption));

        double actualConsumption = (fuel + currentVoyage.flights.get(0).remaining) - currentVoyage.remaining;
        textActualConsumption.setText(String.valueOf(actualConsumption));

        double saving = normConsumption - actualConsumption;
        if (saving > 0){
            textSavings.setText(String.valueOf(saving));
        }

        double overconsumption = actualConsumption - normConsumption;
        if (overconsumption > 0){
            textOverconsumption.setText(String.valueOf(overconsumption));
        }


        btnUnderstand = findViewById(R.id.btnUnderstand);
        btnUnderstand.setOnClickListener(v -> onUnderstandClick());
    }

    private void onUnderstandClick(){
        //TODO save
        Intent intent = new Intent(this, MainActivity.class);

        // Очищаем стек активностей и создаем новую задачу для MainActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String formatTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }
}