package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //DbHelper dbh = DbHelper.getInstance(this);
       // dbh.recreateDatabase();
    }

    public void onButtonGoClick(View view) {
        Intent intent = new Intent(MainActivity.this, start.class);
        startActivity(intent);
    }

    public void onButtonHistoryClick(View view) {
        Intent intent = new Intent(MainActivity.this, activity_voyage_history.class);
        startActivity(intent);
    }

    public void onButtonInfoClick(View view) {
        Intent intent = new Intent(MainActivity.this, info.class);
        startActivity(intent);
    }

}