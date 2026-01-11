# Quick Setup and Testing Guide

## 🚀 Quick Start

### 1. Configure Base URL (IMPORTANT!)

Before running the app, set the correct base URL:

**For Testing on Emulator:**
```java
// File: app/src/main/java/com/siyam/travelschedulemanager/data/remote/RetrofitClient.java
// Line ~25
private static final String BASE_URL = "http://10.0.2.2:8080/api/";
```

**For Testing on Physical Device:**
```java
// Use your PC's IP address (find it with `ipconfig` command)
private static final String BASE_URL = "http://192.168.1.XXX:8080/api/";
```

### 2. Build and Install

```powershell
# Navigate to project directory
cd e:\GitHub\Android

# Build the app
.\gradlew assembleDebug

# Connect your device and install
.\gradlew installDebug
```

### 3. Start Desktop App

Make sure your desktop JavaFX app is running on port 8080 with CORS enabled.

### 4. Test the Flow

#### A. Registration
1. Open Android app
2. Click "Register"
3. Fill form:
   - Username: john
   - Full Name: John Doe
   - Email: john@example.com
   - Role: Developer
   - Password: password123
4. Click "Register"
5. See "Registration Submitted - Pending Approval"

#### B. Approve User (Desktop)
1. Login to desktop app as Master admin
2. Go to "Manage Users"
3. Find "john" in pending users
4. Click "Approve"

#### C. Login (Android)
1. Back to Android app
2. Login screen:
   - Username: john
   - Password: password123
3. Click "Login"
4. Redirected to Developer Dashboard

#### D. Search Routes
1. Navigate to "Route Finder"
2. Enter:
   - From: Dhaka
   - To: Chittagong
3. Click "Find Optimal Routes"
4. View results list

## 📱 Testing Checklist

- [ ] App builds successfully
- [ ] App installs on device
- [ ] Desktop app running on port 8080
- [ ] Network connectivity (same WiFi for physical device)
- [ ] Registration works
- [ ] Admin approval works (desktop)
- [ ] Login works
- [ ] Token persists (close and reopen app)
- [ ] Route search works
- [ ] Results display correctly

## 🔧 Common Issues

### Cannot connect to server
- Check BASE_URL is correct
- Verify desktop app is running
- Check Windows Firewall (allow port 8080)
- For physical device: PC and phone on same WiFi

### Registration pending forever
- Login to desktop app as Master
- Approve user from "Manage Users" section

### No routes found
- Add schedule data in desktop app
- Check district names match (use "Chittagong")

## 📝 Test Accounts

After first registration, you can create these test accounts:

**Master Admin:**
- Username: master
- Password: master123
- Role: MASTER

**Developer:**
- Username: developer
- Password: dev123
- Role: DEVELOPER

**Regular User:**
- Username: user1
- Password: user123
- Role: USER

## 🎯 Key Features Working

✅ REST API Authentication
✅ Token-based session management
✅ Role-based dashboard access
✅ Route search with filters
✅ Admin approval workflow
✅ Account status checking

## 📦 APK Location

After building:
```
e:\GitHub\Android\app\build\outputs\apk\debug\app-debug.apk
```

You can copy this APK to your phone and install directly.

## 🔗 Important Links

- Full Documentation: `ANDROID_REST_INTEGRATION_COMPLETE.md`
- API Endpoints: See documentation file
- Base URL Configuration: `RetrofitClient.java`
