package com.siyam.travelschedulemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.siyam.travelschedulemanager.data.firebase.UserRepository;
import com.siyam.travelschedulemanager.model.User;

import java.util.ArrayList;
import java.util.List;

public class AdminViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<List<User>> pendingUsers = new MutableLiveData<>();
    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AdminViewModel() {
        this.userRepository = new UserRepository();
    }

    public LiveData<List<User>> getPendingUsers() {
        return pendingUsers;
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadPendingUsers() {
        userRepository.getPendingUsers()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        // Client-side filtering for PENDING status
                        if ("PENDING".equals(user.getStatus())) {
                            userList.add(user);
                        }
                    }
                    pendingUsers.setValue(userList);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load pending users: " + e.getMessage());
                });
    }

    public void loadAllUsers() {
        userRepository.getAllUsers()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    allUsers.setValue(userList);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load users: " + e.getMessage());
                });
    }

    public void loadApprovedUsers() {
        userRepository.getApprovedUsers()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        // Client-side filtering for APPROVED status only
                        if ("APPROVED".equals(user.getStatus())) {
                            userList.add(user);
                        }
                    }
                    allUsers.setValue(userList);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load users: " + e.getMessage());
                });
    }

    public void approveUser(String uid) {
        userRepository.updateUserStatus(uid, "APPROVED", null)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("User approved successfully");
                    loadPendingUsers();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to approve user: " + e.getMessage());
                });
    }

    public void rejectUser(String uid, String reason) {
        userRepository.updateUserStatus(uid, "REJECTED", reason)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("User rejected");
                    loadPendingUsers();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to reject user: " + e.getMessage());
                });
    }

    public void updateUserRole(String uid, String role) {
        userRepository.updateUserRole(uid, role)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("User role updated successfully");
                    loadAllUsers();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to update user role: " + e.getMessage());
                });
    }

    public void lockUser(String uid, int minutes) {
        userRepository.lockAccount(uid, minutes)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("User locked for " + minutes + " minutes");
                    loadAllUsers();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to lock user: " + e.getMessage());
                });
    }

    public void unlockUser(String uid) {
        userRepository.unlockAccount(uid)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("User unlocked successfully");
                    loadAllUsers();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to unlock user: " + e.getMessage());
                });
    }

    public void changeUserRole(String uid, String newRole) {
        userRepository.updateUserRole(uid, newRole)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("User role updated to " + newRole);
                    loadAllUsers();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to change role: " + e.getMessage());
                });
    }}
