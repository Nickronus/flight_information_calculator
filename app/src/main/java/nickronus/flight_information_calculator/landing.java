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

public class landing extends AppCompatActivity {

    private Voyage currentVoyage;
    private Flight currentFlight;
    private int currentFlightIndex = 0;

    // UI элементы
    private TextView flightTitle;
    private EditText flightMinutes;
    private EditText landingDate, landingHours, landingMinutes;
    private ImageButton btnBack, btnForward;
    private Button btnLanding, btnBackBottom, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Получаем данные из Intent
        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");
        currentFlightIndex = getIntent().getIntExtra("flight_index", 0);
        currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);

        // Инициализация UI элементов
        flightTitle = findViewById(R.id.flightTitle);
        flightMinutes = findViewById(R.id.flightMinutes);
        landingDate = findViewById(R.id.landingDate);
        landingHours = findViewById(R.id.landingHours);
        landingMinutes = findViewById(R.id.landingMinutes);
        btnBack = findViewById(R.id.btnBack);
        btnForward = findViewById(R.id.btnForward);
        btnLanding = findViewById(R.id.btnLanding);
        btnBackBottom = findViewById(R.id.btnBackBottom);
        btnNext = findViewById(R.id.btnNext);

        // Установка заголовка
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        // Заполнение полей, если данные уже есть
        if (currentFlight.flightTime > 0) {
            flightMinutes.setText(String.valueOf(currentFlight.flightTime));
        }

        if (currentFlight.landingTime != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            landingDate.setText(currentFlight.landingTime.format(dateFormatter));
            landingHours.setText(String.format("%02d", currentFlight.landingTime.getHour()));
            landingMinutes.setText(String.format("%02d", currentFlight.landingTime.getMinute()));
        }

        // Установка обработчиков событий
        btnBack.setOnClickListener(v -> navigateBack());
        btnForward.setOnClickListener(v -> navigateForward());
        btnBackBottom.setOnClickListener(v -> saveAndBack());
        btnNext.setOnClickListener(v -> saveAndProceed());
        btnLanding.setOnClickListener(v -> setLandingTimeToNow());

        // Добавляем валидацию для полей ввода времени
        addTimeValidation(flightMinutes, 999); // Максимальное время полета 999 минут
        addTimeValidation(landingHours, 23);
        addTimeValidation(landingMinutes, 59);
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

    private void setLandingTimeToNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        landingDate.setText(now.format(dateFormatter));
        landingHours.setText(String.format("%02d", now.getHour()));
        landingMinutes.setText(String.format("%02d", now.getMinute()));

        Toast.makeText(this, "Установлено текущее время посадки", Toast.LENGTH_SHORT).show();
    }

    private void save(){
        try {
            // Сохранение времени полёта
            if (!flightMinutes.getText().toString().isEmpty()) {
                currentFlight.flightTime = Integer.parseInt(flightMinutes.getText().toString());
            }

            // Сохранение даты и времени посадки
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(landingDate.getText().toString(), dateFormatter);

            int landingH = landingHours.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(landingHours.getText().toString());
            int landingM = landingMinutes.getText().toString().isEmpty() ? 0 :
                    Integer.parseInt(landingMinutes.getText().toString());

            currentFlight.landingTime = LocalDateTime.of(date, LocalTime.of(landingH, landingM));
            } catch (DateTimeParseException | NumberFormatException e) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
            }
        }

    private void saveAndProceed() {
        save();
        try {
            // Переход к следующему экрану
            Intent intent = new Intent(this, next.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);
        } catch (DateTimeParseException | NumberFormatException e) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAndBack() {
        save();
        try {
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
            // Сохранение времени полёта
            if (!flightMinutes.getText().toString().isEmpty()) {
                currentFlight.flightTime = Integer.parseInt(flightMinutes.getText().toString());
            }

            // Сохранение даты и времени посадки
            if (!landingDate.getText().toString().isEmpty() &&
                    !landingHours.getText().toString().isEmpty() &&
                    !landingMinutes.getText().toString().isEmpty()) {

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate date = LocalDate.parse(landingDate.getText().toString(), dateFormatter);
                int landingH = Integer.parseInt(landingHours.getText().toString());
                int landingM = Integer.parseInt(landingMinutes.getText().toString());
                currentFlight.landingTime = LocalDateTime.of(date, LocalTime.of(landingH, landingM));
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            // Игнорируем ошибки при сохранении
        }
    }

    private void updateUI() {
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        if (currentFlight.flightTime > 0) {
            flightMinutes.setText(String.valueOf(currentFlight.flightTime));
        } else {
            flightMinutes.setText("");
        }

        if (currentFlight.landingTime != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            landingDate.setText(currentFlight.landingTime.format(dateFormatter));
            landingHours.setText(String.format("%02d", currentFlight.landingTime.getHour()));
            landingMinutes.setText(String.format("%02d", currentFlight.landingTime.getMinute()));
        } else {
            landingDate.setText("");
            landingHours.setText("");
            landingMinutes.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }
}