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
import java.time.LocalDateTime;

public class centering extends AppCompatActivity {

    private Voyage currentVoyage;
    private Flight currentFlight;
    private int currentFlightIndex = 0;

    // UI элементы
    private TextView flightTitle, massValue, centeringValue;
    private EditText remainingInput, fuelInput, passengersInput, cargoInput;
    private ImageButton btnBack, btnForward;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centering);

        // Получаем данные из Intent
        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");
        currentFlightIndex = getIntent().getIntExtra("flight_index", 0);
        currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);

        flightTitle = findViewById(R.id.flightTitle);
        massValue = findViewById(R.id.massValue);
        centeringValue = findViewById(R.id.centeringValue);
        remainingInput = findViewById(R.id.remainingInput);
        fuelInput = findViewById(R.id.fuelInput);
        passengersInput = findViewById(R.id.passengersInput);
        cargoInput = findViewById(R.id.cargoInput);
        btnBack = findViewById(R.id.btnBack);
        btnForward = findViewById(R.id.btnForward);
        btnNext = findViewById(R.id.btnNext);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCalculations();
            }
        };

        remainingInput.addTextChangedListener(textWatcher);
        fuelInput.addTextChangedListener(textWatcher);
        passengersInput.addTextChangedListener(textWatcher);
        cargoInput.addTextChangedListener(textWatcher);

        btnBack.setOnClickListener(v -> navigateBack());
        btnForward.setOnClickListener(v -> navigateForward());
        btnNext.setOnClickListener(v -> saveAndProceed());

        updateUI();
    }

    private void updateUI() {
        // Установка заголовка
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        // Заполнение полей, если данные уже есть
        if (currentFlight.remaining > 0) {
            remainingInput.setText(String.valueOf(currentFlight.remaining));
        }
        if (currentFlight.refueled > 0) {
            fuelInput.setText(String.valueOf(currentFlight.refueled));
        }
        if (currentFlight.people > 0) {
            passengersInput.setText(String.valueOf(currentFlight.people));
        }
        if (currentFlight.cargo > 0) {
            cargoInput.setText(String.valueOf(currentFlight.cargo));
        }

        updateCalculations();
    }

    private void updateCalculations() {
        try {
            // Получаем текущие значения из полей ввода
            double remaining = getDoubleFromEditText(remainingInput);
            double refueled = getDoubleFromEditText(fuelInput);
            int people = getIntFromEditText(passengersInput);
            double cargo = getDoubleFromEditText(cargoInput);

            // Расчет общей массы
            double totalMass = currentVoyage.emptyAircraftMass + cargo + remaining +
                    refueled + (people * currentVoyage.averagePassengerMass);

            massValue.setText(String.format("%.1f кг", totalMass));

            // Расчет центровки (упрощенная формула)
            double massEffect = (totalMass - currentVoyage.emptyAircraftMass) / 50 + currentVoyage.baseCentering;
            centeringValue.setText(String.format("%.2f", massEffect));

            // Подсветка опасных значений центровки
            if (massEffect < 15 || massEffect > 35) {
                centeringValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                centeringValue.setTextColor(getResources().getColor(android.R.color.black));
            }

        } catch (NumberFormatException e) {
            // Игнорируем ошибки при частичном вводе
        }
    }

    private double getDoubleFromEditText(EditText editText) throws NumberFormatException {
        String text = editText.getText().toString();
        if (text.isEmpty()) return 0;
        return Double.parseDouble(text);
    }

    private int getIntFromEditText(EditText editText) throws NumberFormatException {
        String text = editText.getText().toString();
        if (text.isEmpty()) return 0;
        return Integer.parseInt(text);
    }

    private void saveAndProceed() {
        try {
            // Сохранение введенных данных
            currentFlight.remaining = getDoubleFromEditText(remainingInput);
            currentFlight.refueled = getDoubleFromEditText(fuelInput);
            currentFlight.people = getIntFromEditText(passengersInput);
            currentFlight.cargo = getDoubleFromEditText(cargoInput);

            // Переход к следующему экрану
            Intent intent = new Intent(this, first.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack() {
        if (currentFlightIndex > 1) {
            saveData();
            currentFlightIndex--;
            currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);
            updateUI();
        }
    }

    private void navigateForward() {
        if (currentFlightIndex < currentVoyage.flights.size()) {
            saveData();
            currentFlightIndex++;
            currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);
            updateUI();
        }
    }

    private void saveData() {
        try {
            currentFlight.remaining = getDoubleFromEditText(remainingInput);
            currentFlight.refueled = getDoubleFromEditText(fuelInput);
            currentFlight.people = getIntFromEditText(passengersInput);
            currentFlight.cargo = getDoubleFromEditText(cargoInput);
        } catch (NumberFormatException e) {
            // Игнорируем ошибки при сохранении
        }
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }
}