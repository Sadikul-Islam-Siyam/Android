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
import com.siyam.travelschedulemanager.util.Constants;
import java.util.ArrayList;
import java.util.List;

public class UserLockAdapter extends RecyclerView.Adapter<UserLockAdapter.UserLockViewHolder> {
    private List<User> users = new ArrayList<>();
    private OnUserLockActionListener listener;

    public interface OnUserLockActionListener {
        void onUserAction(User user, String action);
    }

    public UserLockAdapter(OnUserLockActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserLockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_lock, parent, false);
        return new UserLockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserLockViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserLockViewHolder extends RecyclerView.ViewHolder {
        private TextView textUsername;
        private TextView textEmail;
        private TextView textStatus;
        private Button buttonLock;
        private Button buttonUnlock;

        public UserLockViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.text_username);
            textEmail = itemView.findViewById(R.id.text_email);
            textStatus = itemView.findViewById(R.id.text_status);
            buttonLock = itemView.findViewById(R.id.button_lock);
            buttonUnlock = itemView.findViewById(R.id.button_unlock);
        }

        public void bind(User user, OnUserLockActionListener listener) {
            textUsername.setText(user.getUsername());
            textEmail.setText(user.getEmail());
            
            boolean isLocked = Constants.STATUS_LOCKED.equals(user.getStatus());
            textStatus.setText("Status: " + user.getStatus());
            
            buttonLock.setVisibility(isLocked ? View.GONE : View.VISIBLE);
            buttonUnlock.setVisibility(isLocked ? View.VISIBLE : View.GONE);

            buttonLock.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserAction(user, "lock");
                }
            });

            buttonUnlock.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserAction(user, "unlock");
                }
            });
        }
    }
}
