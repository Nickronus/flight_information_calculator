package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class calculate extends AppCompatActivity {

    private Voyage currentVoyage;
    private int currentFlightIndex = 0;
    private Flight currentFlight;

    private EditText remainingFuelInput;
    private EditText postFlightMinutes;
    private Button btnCalculate, btnBackBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");
        currentFlightIndex = getIntent().getIntExtra("flight_index", 0);
        currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);

        remainingFuelInput = findViewById(R.id.remainingFuelInput);
        postFlightMinutes = findViewById(R.id.postFlightMinutes);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnBackBottom = findViewById(R.id.btnBackBottom);

        if (currentVoyage.remaining > 0) {
            remainingFuelInput.setText(String.valueOf(currentVoyage.remaining));
        }
        if (currentVoyage.postFlightTime > 0) {
            postFlightMinutes.setText(String.valueOf(currentVoyage.postFlightTime));
        }

        btnCalculate.setOnClickListener(v -> calculateAndShowResults());
        btnBackBottom.setOnClickListener(v -> navigateBack());
    }

    private void calculateAndShowResults() {
        try {
            saveInputData();
            DbHelper dbh = DbHelper.getInstance(this);
            dbh.addVoyage(currentVoyage);

            Intent intent = new Intent(this, results.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInputData() {
        try {
            if (!remainingFuelInput.getText().toString().isEmpty()) {
                currentVoyage.remaining = Integer.parseInt(remainingFuelInput.getText().toString());
            }

            if (!postFlightMinutes.getText().toString().isEmpty()) {
                currentVoyage.postFlightTime = Integer.parseInt(postFlightMinutes.getText().toString());
            }
            DbHelper dbh = DbHelper.getInstance(this);
            dbh.addVoyage(currentVoyage);
        } catch (NumberFormatException e) {
            throw e;
        }
    }

    private void navigateBack() {
        saveInputData();
        finish();
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }
}