package nickronus.flight_information_calculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VoyageAdapter extends RecyclerView.Adapter<VoyageAdapter.VoyageViewHolder> {

    private List<Voyage> voyages;
    private final OnVoyageClickListener voyageClickListener;
    private final OnEditClickListener editClickListener;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnVoyageClickListener {
        void onVoyageClick(Voyage voyage);
    }

    public interface OnEditClickListener {
        void onEditClick(Voyage voyage);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Voyage voyage);
    }

    public VoyageAdapter(List<Voyage> voyages,
                         OnVoyageClickListener voyageClickListener,
                         OnEditClickListener editClickListener,
                         OnDeleteClickListener deleteClickListener) {
        this.voyages = voyages;
        this.voyageClickListener = voyageClickListener;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public VoyageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voyage, parent, false);
        return new VoyageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoyageViewHolder holder, int position) {
        Voyage voyage = voyages.get(position);
        holder.bind(voyage, voyageClickListener, editClickListener, deleteClickListener);
    }

    @Override
    public int getItemCount() {
        return voyages.size();
    }

    // Метод для удаления элемента
    public void removeItem(int position) {
        voyages.remove(position);
        notifyItemRemoved(position);
    }

    static class VoyageViewHolder extends RecyclerView.ViewHolder {
        private final TextView voyageNameTextView;
        private final Button editButton;
        private final Button deleteButton;

        public VoyageViewHolder(@NonNull View itemView) {
            super(itemView);
            voyageNameTextView = itemView.findViewById(R.id.voyageNameTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Voyage voyage,
                         OnVoyageClickListener voyageClickListener,
                         OnEditClickListener editClickListener,
                         OnDeleteClickListener deleteClickListener) {
            voyageNameTextView.setText(voyage.name);

            itemView.setOnClickListener(v -> voyageClickListener.onVoyageClick(voyage));
            editButton.setOnClickListener(v -> editClickListener.onEditClick(voyage));
            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    deleteClickListener.onDeleteClick(voyage);
                }
            });
        }
    }
}