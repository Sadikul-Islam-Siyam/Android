package com.siyam.travelschedulemanager.ui.approval.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.PendingRoute;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PendingRouteAdapter extends RecyclerView.Adapter<PendingRouteAdapter.PendingRouteViewHolder> {
    private List<PendingRoute> pendingRoutes = new ArrayList<>();
    private OnPendingRouteActionListener listener;

    public interface OnPendingRouteActionListener {
        void onPendingRouteAction(PendingRoute pendingRoute, String action);
    }

    public PendingRouteAdapter(OnPendingRouteActionListener listener) {
        this.listener = listener;
    }

    public void setPendingRoutes(List<PendingRoute> pendingRoutes) {
        this.pendingRoutes = pendingRoutes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PendingRouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_route, parent, false);
        return new PendingRouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRouteViewHolder holder, int position) {
        PendingRoute pendingRoute = pendingRoutes.get(position);
        holder.bind(pendingRoute, listener);
    }

    @Override
    public int getItemCount() {
        return pendingRoutes.size();
    }

    static class PendingRouteViewHolder extends RecyclerView.ViewHolder {
        private TextView textChangeType;
        private TextView textSubmittedBy;
        private TextView textDate;
        private TextView textStatus;
        private TextView textNotes;
        private Button buttonApprove;
        private Button buttonReject;

        public PendingRouteViewHolder(@NonNull View itemView) {
            super(itemView);
            textChangeType = itemView.findViewById(R.id.text_change_type);
            textSubmittedBy = itemView.findViewById(R.id.text_submitted_by);
            textDate = itemView.findViewById(R.id.text_date);
            textStatus = itemView.findViewById(R.id.text_status);
            textNotes = itemView.findViewById(R.id.text_notes);
            buttonApprove = itemView.findViewById(R.id.button_approve);
            buttonReject = itemView.findViewById(R.id.button_reject);
        }

        public void bind(PendingRoute pendingRoute, OnPendingRouteActionListener listener) {
            textChangeType.setText("Change Type: " + pendingRoute.getChangeType());
            textSubmittedBy.setText("Submitted by: " + pendingRoute.getSubmittedBy());
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            if (pendingRoute.getSubmittedAt() != null) {
                textDate.setText(sdf.format(pendingRoute.getSubmittedAt().toDate()));
            }
            
            textStatus.setText("Status: " + pendingRoute.getStatus());
            
            if (pendingRoute.getNotes() != null && !pendingRoute.getNotes().isEmpty()) {
                textNotes.setVisibility(View.VISIBLE);
                textNotes.setText("Notes: " + pendingRoute.getNotes());
            } else {
                textNotes.setVisibility(View.GONE);
            }

            // Only show buttons if status is PENDING
            if ("PENDING".equals(pendingRoute.getStatus())) {
                buttonApprove.setVisibility(View.VISIBLE);
                buttonReject.setVisibility(View.VISIBLE);

                buttonApprove.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onPendingRouteAction(pendingRoute, "approve");
                    }
                });

                buttonReject.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onPendingRouteAction(pendingRoute, "reject");
                    }
                });
            } else {
                buttonApprove.setVisibility(View.GONE);
                buttonReject.setVisibility(View.GONE);
            }
        }
    }
}
