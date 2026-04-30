package com.eunjin.dentaleasy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        holder.btnShare.setOnClickListener(v -> shareHistoryItem(holder.itemView.getContext(), item));
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
        Button btnShare;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvHistoryType);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDescription = itemView.findViewById(R.id.tvHistoryDescription);
            tvTimestamp = itemView.findViewById(R.id.tvHistoryTimestamp);
            btnShare = itemView.findViewById(R.id.btnShareHistory);
        }
    }

    private void shareHistoryItem(Context context, HistoryItem item) {
        String safeTitle = item.title == null ? "" : item.title;
        String safeDescription = item.description == null ? "" : item.description;
        String shareText = "DentalEasy Result 🦷\n\n"
                + "Title: " + safeTitle + "\n\n"
                + safeDescription + "\n\n"
                + "Shared from DentalEasy App";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        Intent chooser = Intent.createChooser(shareIntent, "Share dental information");

        // Only add FLAG_ACTIVITY_NEW_TASK when the context is not an Activity.
        // On API 36 (Android 16), launching from a non-Activity context without
        // this flag throws an exception. When context IS an Activity, the flag is
        // unnecessary and can interfere with the back stack.
        if (!(context instanceof Activity)) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {
            context.startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(context, "Unable to open share options.", Toast.LENGTH_SHORT).show();
            Log.e("SHARE_ERROR", "Share failed", e);
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
