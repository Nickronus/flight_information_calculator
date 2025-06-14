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
    private EditText preFlightHours, preFlightMinutes;
    private EditText takeoffDate, takeoffHours, takeoffMinutes;
    private ImageButton btnForward;
    private Button btnTakeoff, btnBackBottom, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Получаем данные из Intent
        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");
        currentFlightIndex = getIntent().getIntExtra("flight_index", 0);
        currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);

        // Инициализация UI элементов
        flightTitle = findViewById(R.id.flightTitle);
        preFlightMinutes = findViewById(R.id.preFlightMinutes);
        takeoffDate = findViewById(R.id.takeoffDate);
        takeoffHours = findViewById(R.id.takeoffHours);
        takeoffMinutes = findViewById(R.id.takeoffMinutes);
        btnForward = findViewById(R.id.btnForward);
        btnTakeoff = findViewById(R.id.btnTakeoff);
        btnBackBottom = findViewById(R.id.btnBackBottom);
        btnNext = findViewById(R.id.btnNext);

        // Установка заголовка
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        preFlightMinutes.setText(String.format("%02d", currentVoyage.preFlightTime));

        if (currentVoyage.takeoffTime != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            takeoffDate.setText(currentVoyage.takeoffTime.format(dateFormatter));
            takeoffHours.setText(String.format("%02d", currentVoyage.takeoffTime.getHour()));
            takeoffMinutes.setText(String.format("%02d", currentVoyage.takeoffTime.getMinute()));
        }

        // Установка обработчиков событий
        btnForward.setOnClickListener(v -> navigateForward());
        btnBackBottom.setOnClickListener(v -> navigateBack());
        btnNext.setOnClickListener(v -> saveAndProceed());
        btnTakeoff.setOnClickListener(v -> setTakeoffTimeToNow());

        // Добавляем валидацию для полей ввода времени
        addTimeValidation(takeoffHours, 23);
        addTimeValidation(takeoffMinutes, 59);
    }

    private void addTimeValidation(EditText editText, int maxValue) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
                        // Игнорируем
                    }
                }
            }
        });
    }

    private void setTakeoffTimeToNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        takeoffDate.setText(now.format(dateFormatter));
        takeoffHours.setText(String.format("%02d", now.getHour()));
        takeoffMinutes.setText(String.format("%02d", now.getMinute()));

        Toast.makeText(this, "Установлено текущее время", Toast.LENGTH_SHORT).show();
    }

    private void saveAndProceed() {
        try {
            // Сохранение предварительного времени полёта
            currentVoyage.preFlightTime = preFlightMinutes.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(preFlightMinutes.getText().toString());

            // Сохранение даты и времени взлёта
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(takeoffDate.getText().toString(), dateFormatter);

            int takeoffH = takeoffHours.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(takeoffHours.getText().toString());
            int takeoffM = takeoffMinutes.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(takeoffMinutes.getText().toString());

            currentVoyage.takeoffTime = LocalDateTime.of(date, LocalTime.of(takeoffH, takeoffM));

            // Переход к следующему экрану
            Intent intent = new Intent(this, stay.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);
        } catch (DateTimeParseException | NumberFormatException e) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack() {
        saveData();
        if (currentFlightIndex > 1) {
            currentFlightIndex--;
            currentFlight = currentVoyage.flights.get(currentFlightIndex - 2);
            updateUI();
        } else {
            finish(); // Возвращаемся к предыдущей активности, если это первый полёт
        }
    }

    private void navigateForward() {
        saveData();
        if (currentFlightIndex < currentVoyage.flights.size()) {
            currentFlightIndex++;
            currentFlight = currentVoyage.flights.get(currentFlightIndex);
            updateUI();
        }
    }

    private void saveData() {
        try {
            // Сохранение предварительного времени полёта
            if (!preFlightMinutes.getText().toString().isEmpty()) {
                currentVoyage.preFlightTime = Integer.parseInt(preFlightMinutes.getText().toString());
            }

            // Сохранение даты и времени взлёта
            if (!takeoffDate.getText().toString().isEmpty() &&
                    !takeoffHours.getText().toString().isEmpty() &&
                    !takeoffMinutes.getText().toString().isEmpty()) {

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate date = LocalDate.parse(takeoffDate.getText().toString(), dateFormatter);
                int takeoffH = Integer.parseInt(takeoffHours.getText().toString());
                int takeoffM = Integer.parseInt(takeoffMinutes.getText().toString());
                currentVoyage.takeoffTime = LocalDateTime.of(date, LocalTime.of(takeoffH, takeoffM));
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            // Игнорируем ошибки при сохранении
        }
    }

    private void updateUI() {
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        preFlightMinutes.setText(String.format("%02d", currentVoyage.preFlightTime));

        if (currentVoyage.takeoffTime != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            takeoffDate.setText(currentVoyage.takeoffTime.format(dateFormatter));
            takeoffHours.setText(String.format("%02d", currentVoyage.takeoffTime.getHour()));
            takeoffMinutes.setText(String.format("%02d", currentVoyage.takeoffTime.getMinute()));
        }
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }
}