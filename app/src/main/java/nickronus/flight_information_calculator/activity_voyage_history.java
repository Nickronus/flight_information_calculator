package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;

public class activity_voyage_history extends AppCompatActivity
        implements VoyageAdapter.OnVoyageClickListener,
        VoyageAdapter.OnEditClickListener,
        VoyageAdapter.OnDeleteClickListener {

    private RecyclerView voyageRecyclerView;
    private VoyageAdapter adapter;
    private List<Voyage> voyages = new ArrayList<>();
    DbHelper dbh = null;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voyage_history);

        // Инициализация элементов UI
        voyageRecyclerView = findViewById(R.id.voyageRecyclerView);
        backButton = findViewById(R.id.backButton);

        // Настройка RecyclerView
        voyageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загрузка данных
        dbh = DbHelper.getInstance(this);
        voyages = dbh.getAllVoyages();

        // Настройка адаптера
        adapter = new VoyageAdapter(voyages, this, this, this);
        voyageRecyclerView.setAdapter(adapter);

        // Обработчик для кнопки "Назад"
        backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onVoyageClick(Voyage voyage) {
        openVoyageDetails(voyage);
    }

    @Override
    public void onEditClick(Voyage voyage) {
        openEditVoyage(voyage);
    }

    @Override
    public void onDeleteClick(Voyage voyage) {
        int position = voyages.indexOf(voyage);
        if (position != -1) {
            // Создаем диалог подтверждения
            new AlertDialog.Builder(this)
                    .setTitle("Подтверждение удаления")
                    .setMessage("Вы уверены, что хотите удалить рейс \"" + voyage.name + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        // Действие при подтверждении
                        voyages.remove(position);
                        adapter.notifyItemRemoved(position);

                        dbh.deleteVoyage(voyage.name);
                        Toast.makeText(this, "Рейс удален", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Отмена", (dialog, which) -> {
                        // Ничего не делаем, просто закрываем диалог
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


    private void openVoyageDetails(Voyage voyage) {
        Intent intent = new Intent(this, activity_flight_details.class);
        intent.putExtra("voyage", voyage);
        startActivity(intent);
    }

    private void openEditVoyage(Voyage voyage) {
        Intent intent = new Intent(this, start.class);
        intent.putExtra("voyage", voyage);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Закрываем активность и возвращаемся к предыдущей
        super.onBackPressed();
        finish();
    }
}