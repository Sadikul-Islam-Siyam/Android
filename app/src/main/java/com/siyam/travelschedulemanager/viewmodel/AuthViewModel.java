package com.siyam.travelschedulemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.data.firebase.UserRepository;
import com.siyam.travelschedulemanager.data.firebase.AuditLogRepository;
import com.siyam.travelschedulemanager.model.User;
import com.siyam.travelschedulemanager.model.AuditLog;
import com.siyam.travelschedulemanager.util.Constants;

import java.util.Date;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
        this.userRepository = new UserRepository();
        this.auditLogRepository = new AuditLogRepository();
    }

    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * Register new user with default USER role
     */
    public void register(String username, String email, String password) {
        registerWithRole(username, email, password, Constants.ROLE_USER);
    }
    
    /**
     * Register new user with specified role
     */
    public void registerWithRole(String username, String email, String password, String role) {
        authRepository.register(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    
                    // Auto-approve master@travel.com as MASTER role
                    final String finalRole;
                    final String status;
                    final String successMessage;
                    
                    if (email.equalsIgnoreCase("master@travel.com")) {
                        finalRole = Constants.ROLE_MASTER;
                        status = Constants.STATUS_APPROVED;
                        successMessage = "Registration successful. You can now login.";
                    } else {
                        finalRole = role;
                        status = Constants.STATUS_PENDING;
                        successMessage = "Registration successful. Awaiting approval.";
                    }
                    
                    User user = new User(uid, username, email, finalRole, status);
                    
                    userRepository.createUser(user)
                            .addOnSuccessListener(aVoid -> {
                                currentUser.setValue(user);
                                authResult.setValue(new AuthResult(true, successMessage));
                            })
                            .addOnFailureListener(e -> {
                                error.setValue("Failed to create user profile: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    error.setValue("Registration failed: " + e.getMessage());
                });
    }

    /**
     * Sign in user
     */
    public void signIn(String email, String password) {
        // Special handling for master account
        if (email.equalsIgnoreCase("master@travel.com") && password.equals("master123")) {
            // Try to login with Firebase first
            authRepository.signIn(email, password)
                    .addOnSuccessListener(result -> {
                        String uid = result.getUser().getUid();
                        ensureMasterAccountExists(uid, email);
                    })
                    .addOnFailureListener(e -> {
                        // If login fails, try to register the master account
                        authRepository.register(email, password)
                                .addOnSuccessListener(result -> {
                                    String uid = result.getUser().getUid();
                                    ensureMasterAccountExists(uid, email);
                                })
                                .addOnFailureListener(registerError -> {
                                    error.setValue("Failed to create master account: " + registerError.getMessage());
                                });
                    });
            return;
        }
        
        // Normal login flow for other users
        authRepository.signIn(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    
                    userRepository.getUser(uid)
                            .addOnSuccessListener(documentSnapshot -> {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    // Check account status
                                    if (user.getStatus().equals(Constants.STATUS_PENDING)) {
                                        authRepository.signOut();
                                        error.setValue("Your account is pending approval.");
                                        return;
                                    }
                                    
                                    if (user.getStatus().equals(Constants.STATUS_REJECTED)) {
                                        authRepository.signOut();
                                        error.setValue("Your account was rejected: " + user.getRejectionReason());
                                        return;
                                    }
                                    
                                    if (user.getStatus().equals(Constants.STATUS_LOCKED)) {
                                        Timestamp lockUntil = user.getLockUntil();
                                        if (lockUntil != null && lockUntil.toDate().after(new Date())) {
                                            authRepository.signOut();
                                            error.setValue("Account locked until " + lockUntil.toDate());
                                            return;
                                        } else {
                                            // Unlock account
                                            userRepository.unlockAccount(uid);
                                        }
                                    }
                                    
                                    // Reset failed attempts on successful login
                                    if (user.getFailedAttempts() > 0) {
                                        userRepository.resetFailedAttempts(uid);
                                    }
                                    
                                    // Create audit log for successful login
                                    auditLogRepository.createAuditLog(
                                        user.getUid(),
                                        user.getUsername(),
                                        user.getRole(),
                                        Constants.ACTION_LOGIN,
                                        "user",
                                        user.getUid(),
                                        "Successful login"
                                    );
                                    
                                    currentUser.setValue(user);
                                    authResult.setValue(new AuthResult(true, "Login successful"));
                                } else {
                                    error.setValue("User profile not found");
                                }
                            })
                            .addOnFailureListener(e -> {
                                error.setValue("Failed to load user profile: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    // Try to get user and increment failed attempts
                    userRepository.getUserByEmail(email)
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                                    if (user != null) {
                                        userRepository.incrementFailedAttempts(user.getUid(), user.getFailedAttempts());
                                    }
                                }
                            });
                    
                    error.setValue("Login failed: " + e.getMessage());
                });
    }
    
    /**
     * Ensure master account exists in Firestore
     */
    private void ensureMasterAccountExists(String uid, String email) {
        userRepository.getUser(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user == null) {
                        // Create master account in Firestore
                        User masterUser = new User(uid, "Master Admin", email,
                                Constants.ROLE_MASTER, Constants.STATUS_APPROVED);
                        userRepository.createUser(masterUser)
                                .addOnSuccessListener(aVoid -> {
                                    // Create audit log for master account creation
                                    auditLogRepository.createAuditLog(
                                        masterUser.getUid(),
                                        masterUser.getUsername(),
                                        masterUser.getRole(),
                                        Constants.ACTION_LOGIN,
                                        "user",
                                        masterUser.getUid(),
                                        "Master account created and logged in"
                                    );
                                    
                                    currentUser.setValue(masterUser);
                                    authResult.setValue(new AuthResult(true, "Master account created and logged in"));
                                })
                                .addOnFailureListener(e -> {
                                    error.setValue("Failed to create master profile: " + e.getMessage());
                                });
                    } else {
                        // Update to ensure MASTER role and APPROVED status
                        user.setRole(Constants.ROLE_MASTER);
                        user.setStatus(Constants.STATUS_APPROVED);
                        userRepository.updateUser(uid, user)
                                .addOnSuccessListener(aVoid -> {
                                    // Create audit log for master login
                                    auditLogRepository.createAuditLog(
                                        user.getUid(),
                                        user.getUsername(),
                                        user.getRole(),
                                        Constants.ACTION_LOGIN,
                                        "user",
                                        user.getUid(),
                                        "Master login successful"
                                    );
                                    
                                    currentUser.setValue(user);
                                    authResult.setValue(new AuthResult(true, "Login successful"));
                                })
                                .addOnFailureListener(e -> {
                                    currentUser.setValue(user);
                                    authResult.setValue(new AuthResult(true, "Login successful"));
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to verify master account: " + e.getMessage());
                });
    }

    /**
     * Sign out
     */
    public void signOut() {
        // Create audit log before signing out
        User logoutUser = currentUser.getValue();
        if (logoutUser != null) {
            auditLogRepository.createAuditLog(
                logoutUser.getUid(),
                logoutUser.getUsername(),
                logoutUser.getRole(),
                Constants.ACTION_LOGOUT,
                "user",
                logoutUser.getUid(),
                "User logged out"
            );
        }
        
        authRepository.signOut();
        currentUser.setValue(null);
        authResult.setValue(new AuthResult(true, "Signed out"));
    }

    /**
     * Load current user
     */
    public void loadCurrentUser() {
        String uid = authRepository.getCurrentUserId();
        if (uid != null) {
            userRepository.getUser(uid)
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        currentUser.setValue(user);
                    })
                    .addOnFailureListener(e -> {
                        error.setValue("Failed to load user: " + e.getMessage());
                    });
        }
    }

    /**
     * Send password reset email
     */
    public void sendPasswordReset(String email) {
        authRepository.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    authResult.setValue(new AuthResult(true, "Password reset email sent"));
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to send password reset email: " + e.getMessage());
                });
    }

    /**
     * Check if user is logged in
     */
    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    // Result class for authentication operations
    public static class AuthResult {
        public final boolean success;
        public final String message;

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
