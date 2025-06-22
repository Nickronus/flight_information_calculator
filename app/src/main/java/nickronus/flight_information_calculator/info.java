package nickronus.flight_information_calculator;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Button amenButton = findViewById(R.id.amenButton);
        amenButton.setOnClickListener(v -> {
            finish();
        });
    }
}