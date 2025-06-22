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

        // Получаем данные из Intent
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

        // Пункт
        LocalDateTime point = currentVoyage.flights.get(currentVoyage.flights.size() - 1).landingTime;
        if (point != null){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            textPoint.setText(point.format(dateTimeFormatter));
        }

        // Полётное
        int flightTime = 0;
        for (int i = 1; i < currentVoyage.flights.size(); i++){
            flightTime += currentVoyage.flights.get(i).flightTime + currentVoyage.flights.get(i).groundTime;
        }
        textFlightTime.setText(flightTime);

        // Стоянка
        int parking = 0;
        for (int i = 1; i < currentVoyage.flights.size(); i++){
            parking += currentVoyage.flights.get(i).parkingTime;
        }
        textParking.setText(parking);

        // Задержка
        if (currentVoyage.plannedTakeoffTime != null){
            textDelay.setText(String.valueOf(Duration.between(
                    currentVoyage.plannedTakeoffTime,
                    currentVoyage.takeoffTime
            ).toMinutes()));
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