package com.eunjin.dentaleasy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExampleTermAdapter extends RecyclerView.Adapter<ExampleTermAdapter.ViewHolder> {

    private final String[] terms;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String term);
    }

    public ExampleTermAdapter(String[] terms, OnItemClickListener listener) {
        this.terms = terms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_example_term, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String term = terms[position];
        holder.tvTerm.setText(term);
        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(term);
            }
        });
    }

    @Override
    public int getItemCount() {
        return terms.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvTerm;

        public ViewHolder(View view) {
            super(view);
            tvTerm = view.findViewById(R.id.tvTerm);
        }
    }
}
