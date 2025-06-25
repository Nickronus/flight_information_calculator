package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class next extends AppCompatActivity {

    private Voyage currentVoyage;
    private int currentFlightIndex = 0;

    // UI элементы
    private TextView flightTitle;
    private ImageButton btnBack, btnForward;
    private Button btnContinueFlight, btnFinishFlight, btnBackBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        // Получаем данные из Intent
        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");
        currentFlightIndex = getIntent().getIntExtra("flight_index", 0);

        // Инициализация UI элементов
        flightTitle = findViewById(R.id.flightTitle);
        btnBack = findViewById(R.id.btnBack);
        btnForward = findViewById(R.id.btnForward);
        btnContinueFlight = findViewById(R.id.btnContinueFlight);
        btnFinishFlight = findViewById(R.id.btnFinishFlight);
        btnBackBottom = findViewById(R.id.btnBackBottom);

        // Установка заголовка
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        // Установка обработчиков событий
        btnBack.setOnClickListener(v -> navigateBack());
        btnForward.setOnClickListener(v -> navigateForward());
        btnBackBottom.setOnClickListener(v -> navigateBack());

        btnContinueFlight.setOnClickListener(v -> startNewFlight());
        btnFinishFlight.setOnClickListener(v -> finishVoyage());
    }

    private void startNewFlight() {
        // Создаем новый полет и добавляем в рейс
        Flight newFlight = new Flight(0, 0, 0, 0, 0, 0, 0, null);
        currentVoyage.flights.add(newFlight);

        // Переходим к первому экрану для нового полета
        Intent intent = new Intent(this, centering.class);
        intent.putExtra("voyage", currentVoyage);
        intent.putExtra("flight_index", currentVoyage.flights.size());
        startActivity(intent);
    }

    private void finishVoyage() {
        Intent intent = new Intent(this, calculate.class);
        intent.putExtra("voyage", currentVoyage);
        intent.putExtra("flight_index", currentVoyage.flights.size());
        startActivity(intent);
        finish();
    }

    private void navigateBack() {
        if (currentFlightIndex > 1) {
            currentFlightIndex--;
            updateUI();
        }
    }

    private void navigateForward() {
        if (currentFlightIndex < currentVoyage.flights.size()) {
            currentFlightIndex++;
            Intent intent = new Intent(this, centering.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);
        }
    }

    private void updateUI() {
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }
}