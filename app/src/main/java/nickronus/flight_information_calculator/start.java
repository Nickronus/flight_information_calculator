package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Import View
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class start extends AppCompatActivity {
    private Voyage currentVoyage;
    private EditText etFlightName, etEmptyMass, etPassengerMass, baseCentering;
    private Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация UI
        etFlightName = findViewById(R.id.editTextText3);
        etEmptyMass = findViewById(R.id.numberInput5);
        etPassengerMass = findViewById(R.id.numberInput6);
        baseCentering = findViewById(R.id.baseCenteringInput);
        buttonStart = findViewById(R.id.buttonStartGo3);

        // Получаем объект Voyage из Intent
        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");

        // Если Voyage не был передан, создаем новый
        if (currentVoyage == null) {
            currentVoyage = new Voyage("", 0, 0);
            Flight firstFlight = new Flight(0, 0, 0, 0, 0, 0, 0, null);
            currentVoyage.flights.add(firstFlight);
        }

        // Заполнение полей, если данные уже есть
        etFlightName.setText(currentVoyage.name);
        etEmptyMass.setText(String.valueOf(currentVoyage.emptyAircraftMass));
        etPassengerMass.setText(String.valueOf(currentVoyage.averagePassengerMass));
        baseCentering.setText(String.valueOf(currentVoyage.baseCentering));

        // Установка слушателя нажатий на кнопку "Старт"
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartButtonClick(v);
            }
        });
    }

    public void onStartButtonClick(View view) {
        String name = etFlightName.getText().toString();
        String emptyAircraftMassText = etEmptyMass.getText().toString();
        String emptyManMassText = etPassengerMass.getText().toString();
        String currentVoyageText = baseCentering.getText().toString();

        if (name.isEmpty() || emptyAircraftMassText.isEmpty() || emptyManMassText.isEmpty() || currentVoyageText.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double emptyAircraftMass = Double.parseDouble(emptyAircraftMassText);
            double emptyManMass = Double.parseDouble(emptyManMassText);
            int baseCentering = Integer.parseInt(currentVoyageText);

            currentVoyage.name = name;
            currentVoyage.emptyAircraftMass = emptyAircraftMass;
            currentVoyage.averagePassengerMass = emptyManMass;
            currentVoyage.baseCentering = baseCentering;

            DbHelper dbh = DbHelper.getInstance(this);
            dbh.addVoyage(currentVoyage);

            Intent intent = new Intent(start.this, centering.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", 1);
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Неверный формат числа", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onCancelButtonClick() {
        Intent intent = new Intent(start.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
