package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class stay extends AppCompatActivity {

    private Voyage currentVoyage;
    private Flight currentFlight;
    private int currentFlightIndex = 0;

    private TextView flightTitle;
    private EditText groundMinutes, parkingMinutes;
    private ImageButton btnBack, btnForward;
    private Button btnBackBottom, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stay);

        currentVoyage = (Voyage) getIntent().getSerializableExtra("voyage");
        currentFlightIndex = getIntent().getIntExtra("flight_index", 0);
        currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);

        flightTitle = findViewById(R.id.flightTitle);
        groundMinutes = findViewById(R.id.groundMinutes);
        parkingMinutes = findViewById(R.id.parkingMinutes);
        btnBack = findViewById(R.id.btnBack);
        btnForward = findViewById(R.id.btnForward);
        btnBackBottom = findViewById(R.id.btnBackBottom);
        btnNext = findViewById(R.id.btnNext);

        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        if (currentFlight.groundTime > 0) {
            groundMinutes.setText(String.valueOf(currentFlight.groundTime));
        }
        if (currentFlight.parkingTime > 0) {
            parkingMinutes.setText(String.valueOf(currentFlight.parkingTime));
        }

        btnBack.setOnClickListener(v -> navigateBack());
        btnForward.setOnClickListener(v -> navigateForward());
        btnBackBottom.setOnClickListener(v -> saveAndBack());
        btnNext.setOnClickListener(v -> saveAndProceed());
    }

    private  void save() {
        try {
            if (!groundMinutes.getText().toString().isEmpty()) {
                currentFlight.groundTime = Integer.parseInt(groundMinutes.getText().toString());
            }

            if (!parkingMinutes.getText().toString().isEmpty()) {
                currentFlight.parkingTime = Integer.parseInt(parkingMinutes.getText().toString());
            }
        } catch (NumberFormatException e) {

        }
    }

    private void saveAndProceed() {
        save();
        try {
            DbHelper dbh = DbHelper.getInstance(this);
            dbh.addVoyage(currentVoyage);

            Intent intent = new Intent(this, landing.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);
        } catch (NumberFormatException e) {

        }
    }

    private void saveAndBack(){
        save();
        try {
            Intent intent = new Intent(this, centering.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);
        } catch (NumberFormatException e) {

        }
    }

    private void navigateBack() {
        saveData();
        if (currentFlightIndex > 1) {
            currentFlightIndex--;
            currentFlight = currentVoyage.flights.get(currentFlightIndex - 1);
            updateUI();
        }
    }

    private void navigateForward() {
        saveData();
        if (currentFlightIndex < currentVoyage.flights.size()) {
            currentFlightIndex++;
            currentFlight = currentVoyage.flights.get(currentFlightIndex);
            Intent intent = new Intent(this, centering.class);
            intent.putExtra("voyage", currentVoyage);
            intent.putExtra("flight_index", currentFlightIndex);
            startActivity(intent);
        }
    }

    private void saveData() {
        try {
            if (!groundMinutes.getText().toString().isEmpty()) {
                currentFlight.groundTime = Integer.parseInt(groundMinutes.getText().toString());
            }
            if (!parkingMinutes.getText().toString().isEmpty()) {
                currentFlight.parkingTime = Integer.parseInt(parkingMinutes.getText().toString());
            }
        } catch (NumberFormatException e) {
        }
    }

    private void updateUI() {
        flightTitle.setText(String.format("Полёт %d", currentFlightIndex));

        if (currentFlight.groundTime > 0) {
            groundMinutes.setText(String.valueOf(currentFlight.groundTime));
        } else {
            groundMinutes.setText("");
        }

        if (currentFlight.parkingTime > 0) {
            parkingMinutes.setText(String.valueOf(currentFlight.parkingTime));
        } else {
            parkingMinutes.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }
}