package com.siyam.travelschedulemanager.ui.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserApprovalAdapter extends RecyclerView.Adapter<UserApprovalAdapter.UserViewHolder> {
    private List<User> users = new ArrayList<>();
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onUserAction(User user, String action);
    }

    public UserApprovalAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_approval, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView textUsername;
        private TextView textEmail;
        private TextView textRole;
        private Button buttonApprove;
        private Button buttonReject;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.text_username);
            textEmail = itemView.findViewById(R.id.text_email);
            textRole = itemView.findViewById(R.id.text_role);
            buttonApprove = itemView.findViewById(R.id.button_approve);
            buttonReject = itemView.findViewById(R.id.button_reject);
        }

        public void bind(User user, OnUserActionListener listener) {
            textUsername.setText(user.getUsername());
            textEmail.setText(user.getEmail());
            textRole.setText("Role: " + user.getRole());

            buttonApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserAction(user, "approve");
                }
            });

            buttonReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserAction(user, "reject");
                }
            });
        }
    }
}
