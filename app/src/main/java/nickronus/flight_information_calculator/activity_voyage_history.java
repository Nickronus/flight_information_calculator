package nickronus.flight_information_calculator;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class activity_voyage_history extends AppCompatActivity
        implements VoyageAdapter.OnVoyageClickListener,
        VoyageAdapter.OnEditClickListener,
        VoyageAdapter.OnDeleteClickListener {

    private RecyclerView voyageRecyclerView;
    private VoyageAdapter adapter;
    private List<Voyage> voyages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voyage_history);

        voyageRecyclerView = findViewById(R.id.voyageRecyclerView);
        voyageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSampleVoyages();

        adapter = new VoyageAdapter(voyages, this, this, this);
        voyageRecyclerView.setAdapter(adapter);
    }

    private void loadSampleVoyages() {
        voyages.add(new Voyage("Рейс #1 - Москва-Сочи", 5000, 80));
        voyages.add(new Voyage("Рейс #2 - СПб-Калининград", 5200, 85));
        voyages.add(new Voyage("Рейс #3 - Новосибирск-Владивосток", 4800, 75));
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
            // Здесь должна быть ваша логика удаления из базы данных
            voyages.remove(position);
            adapter.notifyItemRemoved(position);

            // Показать уведомление об удалении
            Toast.makeText(this, "Рейс удален", Toast.LENGTH_SHORT).show();
        }
    }

    private void openVoyageDetails(Voyage voyage) {
//        Intent intent = new Intent(this, VoyageDetailsActivity.class);
//        intent.putExtra("voyage", voyage);
//        startActivity(intent);
    }

    private void openEditVoyage(Voyage voyage) {
//        Intent intent = new Intent(this, EditVoyageActivity.class);
//        intent.putExtra("voyage", voyage);
//        startActivity(intent);
    }
}