package nickronus.flight_information_calculator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import java.time.format.DateTimeFormatter;


public class activity_flight_details extends AppCompatActivity {

    private TextView remainingEditText;
    private TextView refueledEditText;
    private TextView peopleEditText;
    private TextView cargoEditText;
    private TextView groundTimeEditText;
    private TextView parkingTimeEditText;
    private TextView flightTimeEditText;
    private TextView landingTimeEditText;

    private Flight currentFlight;
    private Voyage currentVoyage;
    private int flightPosition;
    private boolean isNewFlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_details);

        remainingEditText = findViewById(R.id.remainingEditText);
        refueledEditText = findViewById(R.id.refueledEditText);
        peopleEditText = findViewById(R.id.peopleEditText);
        cargoEditText = findViewById(R.id.cargoEditText);
        groundTimeEditText = findViewById(R.id.groundTimeEditText);
        parkingTimeEditText = findViewById(R.id.parkingTimeEditText);
        flightTimeEditText = findViewById(R.id.flightTimeEditText);
        landingTimeEditText = findViewById(R.id.landingTimeEditText);

        Button backButton = findViewById(R.id.backButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        Intent intent = getIntent();
        currentVoyage = (Voyage) intent.getSerializableExtra("voyage");
        currentFlight = currentVoyage.flights.get(0);
        flightPosition = 1;

        backButton.setOnClickListener(v -> navigateBack());
        nextButton.setOnClickListener(v -> navigateNext());
        cancelButton.setOnClickListener(v -> cancel());

        populateFields();
    }

    private void populateFields() {
        remainingEditText.setText(String.valueOf(currentFlight.remaining));
        refueledEditText.setText(String.valueOf(currentFlight.refueled));
        peopleEditText.setText(String.valueOf(currentFlight.people));
        cargoEditText.setText(String.valueOf(currentFlight.cargo));
        groundTimeEditText.setText(String.valueOf(currentFlight.groundTime));
        parkingTimeEditText.setText(String.valueOf(currentFlight.parkingTime));
        flightTimeEditText.setText(String.valueOf(currentFlight.flightTime));

        if (currentFlight.landingTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            landingTimeEditText.setText(currentFlight.landingTime.format(formatter));
        }
    }

    private void navigateBack() {
        if (flightPosition == 1){
            Intent intent = new Intent(this, activity_voyage_history.class);
            startActivity(intent);
            finish();
        } else {
            flightPosition --;
            currentFlight = currentVoyage.flights.get(flightPosition - 1);
            populateFields();
        }
    }

    private void navigateNext() {
        if (flightPosition == currentVoyage.flights.size()){
            Intent intent = new Intent(this, results.class);
            intent.putExtra("voyage", currentVoyage);
            startActivity(intent);
            finish();
        } else {
            flightPosition ++;
            currentFlight = currentVoyage.flights.get(flightPosition - 1);
            populateFields();
        }
    }

    private void cancel() {
        Intent intent = new Intent(this, activity_voyage_history.class);
        startActivity(intent);
        finish();
    }
}