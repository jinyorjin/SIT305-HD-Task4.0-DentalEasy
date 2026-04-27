package com.eunjin.dentaleasy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final List<HistoryItem> items = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public void submitItems(List<HistoryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvType.setText("tooth".equals(item.type) ? "Tooth" : "Search");
        holder.tvTimestamp.setText(dateFormat.format(new Date(item.timestamp)));

        String preview = item.description == null ? "" : item.description;
        if (preview.length() > 140) {
            preview = preview.substring(0, 140) + "...";
        }
        holder.tvDescription.setText(preview);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvTimestamp;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvHistoryType);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDescription = itemView.findViewById(R.id.tvHistoryDescription);
            tvTimestamp = itemView.findViewById(R.id.tvHistoryTimestamp);
        }
    }

    public static class HistoryItem {
        public final String type;
        public final String title;
        public final String description;
        public final long timestamp;

        public HistoryItem(String type, String title, String description, long timestamp) {
            this.type = type;
            this.title = title;
            this.description = description;
            this.timestamp = timestamp;
        }
    }
}
