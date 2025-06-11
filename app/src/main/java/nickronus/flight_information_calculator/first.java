package nickronus.flight_information_calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDateTime;


public class first extends AppCompatActivity {

    private Voyage currentVoyage;
    private EditText preFligthHours, getPreFligthMin;
    private EditText takeoffHours, takeoffMin;
    private LocalDateTime takeoffDate;
    private Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preFligthHours = findViewById(R.id.preFlightHours);
        getPreFligthMin = findViewById((R.id.preFlightMinutes));
        takeoffHours = findViewById(R.id.takeoffHours);
        takeoffMin = findViewById(R.id.takeoffMinutes);
        //takeoffDate = findViewById(R.id.takeoffDate);


    }
}