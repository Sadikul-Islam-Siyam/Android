package com.siyam.travelschedulemanager.ui.history.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.AuditLog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AuditLogAdapter extends RecyclerView.Adapter<AuditLogAdapter.AuditLogViewHolder> {
    private List<AuditLog> auditLogs = new ArrayList<>();

    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AuditLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audit_log, parent, false);
        return new AuditLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditLogViewHolder holder, int position) {
        AuditLog log = auditLogs.get(position);
        holder.bind(log);
    }

    @Override
    public int getItemCount() {
        return auditLogs.size();
    }

    static class AuditLogViewHolder extends RecyclerView.ViewHolder {
        private TextView textAction;
        private TextView textUser;
        private TextView textEntity;
        private TextView textDetails;
        private TextView textTimestamp;

        public AuditLogViewHolder(@NonNull View itemView) {
            super(itemView);
            textAction = itemView.findViewById(R.id.text_action);
            textUser = itemView.findViewById(R.id.text_user);
            textEntity = itemView.findViewById(R.id.text_entity);
            textDetails = itemView.findViewById(R.id.text_details);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
        }

        public void bind(AuditLog log) {
            textAction.setText(log.getAction());
            textUser.setText(log.getUserName() + " (" + log.getUserRole() + ")");
            textEntity.setText("Entity: " + log.getEntityType());
            
            if (log.getDetails() != null && !log.getDetails().isEmpty()) {
                textDetails.setVisibility(View.VISIBLE);
                textDetails.setText(log.getDetails());
            } else {
                textDetails.setVisibility(View.GONE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
            if (log.getTimestamp() != null) {
                textTimestamp.setText(sdf.format(log.getTimestamp().toDate()));
            }
        }
    }
}
