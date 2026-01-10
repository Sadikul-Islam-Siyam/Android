package com.siyam.travelschedulemanager.ui.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.User;
import com.siyam.travelschedulemanager.util.Constants;
import java.util.ArrayList;
import java.util.List;

public class UserRoleAdapter extends RecyclerView.Adapter<UserRoleAdapter.UserRoleViewHolder> {
    private List<User> users = new ArrayList<>();
    private OnUserRoleChangeListener listener;

    public interface OnUserRoleChangeListener {
        void onRoleChange(User user, String newRole);
    }

    public UserRoleAdapter(OnUserRoleChangeListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserRoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_role, parent, false);
        return new UserRoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRoleViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserRoleViewHolder extends RecyclerView.ViewHolder {
        private TextView textUsername;
        private TextView textEmail;
        private TextView textCurrentRole;
        private AutoCompleteTextView roleSpinner;
        private Button buttonChange;

        public UserRoleViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.text_username);
            textEmail = itemView.findViewById(R.id.text_email);
            textCurrentRole = itemView.findViewById(R.id.text_current_role);
            roleSpinner = itemView.findViewById(R.id.role_spinner);
            buttonChange = itemView.findViewById(R.id.button_change_role);
        }

        public void bind(User user, OnUserRoleChangeListener listener) {
            textUsername.setText(user.getUsername());
            textEmail.setText(user.getEmail());
            textCurrentRole.setText("Current: " + user.getRole());

            String[] roles = {Constants.ROLE_USER, Constants.ROLE_DEVELOPER, Constants.ROLE_MASTER};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(), 
                android.R.layout.simple_dropdown_item_1line, roles);
            roleSpinner.setAdapter(adapter);
            roleSpinner.setText(user.getRole(), false);

            buttonChange.setOnClickListener(v -> {
                if (listener != null) {
                    String newRole = roleSpinner.getText().toString();
                    if (!newRole.equals(user.getRole())) {
                        listener.onRoleChange(user, newRole);
                    }
                }
            });
        }
    }
}
