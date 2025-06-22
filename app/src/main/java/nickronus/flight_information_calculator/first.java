package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class first extends AppCompatActivity {

    private Voyage currentVoyage;
    private Flight currentFlight;
    private int currentFlightIndex = 0;

    // UI элементы
    private TextView flightTitle;
    private EditText preFlightMinutes;
    private EditText takeoffDate, takeoffHours, takeoffMinutes;
    private EditText plannedTakeoffDate, plannedTakeoffHours, plannedTakeoffMinutes;
    private ImageButton btnForward;
    private Button btnTakeoff, btnBackBottom, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        initViews();
        getIntentData();
        setupUI();
        setupListeners();
    }

    private void initViews() {
        flightTitle = findViewById(R.id.flightTitle);
        preFlightMinutes = findViewById(R.id.preFlightMinutes);
        takeoffDate = findViewById(R.id.takeoffDate);
        takeoffHours = findViewById(R.id.takeoffHours);
        takeoffMinutes = findViewById(R.id.takeoffMinutes);
        plannedTakeoffDate = findViewById(R.id.plannedTakeoffDate);
        plannedTakeoffHours = findViewById(R.id.plannedTakeoffHours);
        plannedTakeoffMinutes = findViewById(R.id.plannedTakeoffMinutes);
        btnForward = findViewById(R.id.btnForward);
        btnTakeoff = findViewById(R.id.btnTakeoff);
        btnBackBottom = findViewById(R.id.btnBackBottom);
        btnNext = findViewById(R.id.btnNext);
    }

    private void getIntentData() {
        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");
        currentFlightIndex = getIntent().getIntExtra("flight_index", 0);
        currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);
    }

    private void setupUI() {
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        // Предполётное время
        preFlightMinutes.setText(String.valueOf(currentVoyage.preFlightTime));

        // Фактическое время взлёта
        if (currentVoyage.takeoffTime != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            takeoffDate.setText(currentVoyage.takeoffTime.format(dateFormatter));
            takeoffHours.setText(String.format("%02d", currentVoyage.takeoffTime.getHour()));
            takeoffMinutes.setText(String.format("%02d", currentVoyage.takeoffTime.getMinute()));
        }

        // Планируемое время взлёта
        if (currentVoyage.plannedTakeoffTime != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            plannedTakeoffDate.setText(currentVoyage.plannedTakeoffTime.format(dateFormatter));
            plannedTakeoffHours.setText(String.format("%02d", currentVoyage.plannedTakeoffTime.getHour()));
            plannedTakeoffMinutes.setText(String.format("%02d", currentVoyage.plannedTakeoffTime.getMinute()));
        }
    }

    private void setupListeners() {
        btnForward.setOnClickListener(v -> navigateForward());
        btnBackBottom.setOnClickListener(v -> navigateBack());
        btnNext.setOnClickListener(v -> saveAndProceed());
        btnTakeoff.setOnClickListener(v -> setCurrentTakeoffTime());

        // Валидация полей времени
        addTimeValidation(takeoffHours, 23);
        addTimeValidation(takeoffMinutes, 59);
        addTimeValidation(plannedTakeoffHours, 23);
        addTimeValidation(plannedTakeoffMinutes, 59);
    }

    private void addTimeValidation(EditText editText, int maxValue) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        int value = Integer.parseInt(s.toString());
                        if (value > maxValue) {
                            editText.setText(String.valueOf(maxValue));
                            editText.setSelection(editText.getText().length());
                        }
                    } catch (NumberFormatException e) {
                        editText.setText("");
                    }
                }
            }
        });
    }

    private void setCurrentTakeoffTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        takeoffDate.setText(now.format(dateFormatter));
        takeoffHours.setText(String.format("%02d", now.getHour()));
        takeoffMinutes.setText(String.format("%02d", now.getMinute()));

        Toast.makeText(this, "Установлено текущее время взлёта", Toast.LENGTH_SHORT).show();
    }

    private void saveAndProceed() {
        try {
            // Сохраняем все данные
            saveData();

            // Переход к следующему экрану
            Intent intent = new Intent(this, stay.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveData() {
        try {
            // Сохранение предварительного времени
            if (!preFlightMinutes.getText().toString().isEmpty()) {
                currentVoyage.preFlightTime = Integer.parseInt(preFlightMinutes.getText().toString());
            }

            // Сохранение фактического времени взлёта
            saveTakeoffTime();

            // Сохранение планируемого времени взлёта
            savePlannedTakeoffTime();

        } catch (Exception e) {
            // Игнорируем ошибки при сохранении
        }
    }

    private void saveTakeoffTime() throws DateTimeParseException, NumberFormatException {
        if (!takeoffDate.getText().toString().isEmpty() &&
                !takeoffHours.getText().toString().isEmpty() &&
                !takeoffMinutes.getText().toString().isEmpty()) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(takeoffDate.getText().toString(), dateFormatter);
            int hours = Integer.parseInt(takeoffHours.getText().toString());
            int minutes = Integer.parseInt(takeoffMinutes.getText().toString());

            currentVoyage.takeoffTime = LocalDateTime.of(date, LocalTime.of(hours, minutes));
        }
    }

    private void savePlannedTakeoffTime() {
        try {
            if (!plannedTakeoffDate.getText().toString().isEmpty() &&
                    !plannedTakeoffHours.getText().toString().isEmpty() &&
                    !plannedTakeoffMinutes.getText().toString().isEmpty()) {

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate date = LocalDate.parse(plannedTakeoffDate.getText().toString(), dateFormatter);
                int hours = Integer.parseInt(plannedTakeoffHours.getText().toString());
                int minutes = Integer.parseInt(plannedTakeoffMinutes.getText().toString());

                currentVoyage.plannedTakeoffTime = LocalDateTime.of(date, LocalTime.of(hours, minutes));
            } else {
                currentVoyage.plannedTakeoffTime = null;
            }
        } catch (Exception e) {
            currentVoyage.plannedTakeoffTime = null;
        }
    }

    private void navigateBack() {
        saveData();
        if (currentFlightIndex > 1) {
            currentFlightIndex--;
            currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);
            updateUI();
        } else {
            finish();
        }
    }

    private void navigateForward() {
        saveData();
        if (currentFlightIndex < currentVoyage.flights.size()) {
            currentFlightIndex++;
            currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);
            updateUI();
        }
    }

    private void updateUI() {
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        // Обновляем только если значения изменились
        if (!preFlightMinutes.getText().toString().equals(String.valueOf(currentVoyage.preFlightTime))) {
            preFlightMinutes.setText(String.valueOf(currentVoyage.preFlightTime));
        }

        // Фактическое время взлёта
        if (currentVoyage.takeoffTime != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String currentDate = takeoffDate.getText().toString();
            String newDate = currentVoyage.takeoffTime.format(dateFormatter);

            if (!currentDate.equals(newDate)) {
                takeoffDate.setText(newDate);
                takeoffHours.setText(String.format("%02d", currentVoyage.takeoffTime.getHour()));
                takeoffMinutes.setText(String.format("%02d", currentVoyage.takeoffTime.getMinute()));
            }
        }

        // Планируемое время взлёта
        if (currentVoyage.plannedTakeoffTime != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String currentDate = plannedTakeoffDate.getText().toString();
            String newDate = currentVoyage.plannedTakeoffTime.format(dateFormatter);

            if (!currentDate.equals(newDate)) {
                plannedTakeoffDate.setText(newDate);
                plannedTakeoffHours.setText(String.format("%02d", currentVoyage.plannedTakeoffTime.getHour()));
                plannedTakeoffMinutes.setText(String.format("%02d", currentVoyage.plannedTakeoffTime.getMinute()));
            }
        }
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }
}