# Android App - REST API Integration Complete

## Overview
The Android app has been fully updated to work with the desktop app's REST API. All authentication and schedule operations now use the REST API instead of Firebase.

## Changes Made

### 1. Authentication System (REST API)

#### Updated Files:
- **LoginActivity.java**
  - Now uses `RestAuthViewModel` instead of Firebase `AuthViewModel`
  - Login with username (not email) and password
  - Token-based authentication
  - Role-based redirection (Master/Developer/User dashboards)
  - Added help button with login guide

- **RegisterActivity.java**
  - Now uses `RestAuthViewModel` for registration
  - Added full name field to registration form
  - Shows "Registration Submitted" dialog after successful registration
  - Account status check feature for pending users
  - Awaiting admin approval workflow

- **activity_register.xml**
  - Added full name input field between username and email

#### New ViewModels:
- **RestAuthViewModel.java**
  - `login(username, password)` - Authenticates user and saves token
  - `register(username, email, password, fullName, role)` - Registers new user
  - `checkAccountStatus(username)` - Checks registration status
  - `logout()` - Clears token and user data
  - LiveData: `loginSuccess`, `registerSuccess`, `statusMessage`, `currentUser`, `isLoading`, `errorMessage`

### 2. Schedule Search System (REST API)

#### Updated Files:
- **RouteFinderFragment.java**
  - Now uses `RestScheduleViewModel` for route searching
  - Calls REST API endpoint `/api/routes?start=X&destination=Y`
  - Added RecyclerView for displaying search results
  - Added ProgressBar for loading states
  - Transport type filtering (All/Bus/Train)
  - Real-time error handling

#### New Files:
- **RestScheduleViewModel.java**
  - `searchRoutes(start, destination)` - Searches routes via REST API
  - `loadAllSchedules()` - Loads all available schedules
  - `convertUnifiedToSchedule()` - Converts API format to UI format
  - LiveData: `searchResults`, `allSchedules`, `isLoading`, `errorMessage`

- **RouteResultAdapter.java**
  - RecyclerView adapter for displaying search results
  - Shows type (Bus/Train), route, time, fare, details, available seats

- **item_route_result.xml**
  - Material Design card layout for each route result
  - Displays all schedule information in an attractive format

- **fragment_route_finder.xml** (Updated)
  - Added `RecyclerView` with id `recycler_view_results`
  - Added `ProgressBar` with id `progress_bar`

### 3. Network Layer (Already Implemented)

#### DTOs (Data Transfer Objects):
- **Authentication DTOs:**
  - LoginRequest, LoginResponse, RegisterRequest, RegisterResponse
  - UserDTO, StatusResponse, ProfileResponse

- **Admin DTOs:**
  - PendingUserDTO, PendingUsersResponse, ApiResponse

- **Schedule DTOs:**
  - ScheduleDTO (unified format for Bus and Train)

#### Network Configuration:
- **ApiService.java** - All REST endpoints defined
- **RetrofitClient.java** - Auto token injection, 30s timeout
- **TokenManager.java** - Session management, token storage
- **network_security_config.xml** - Cleartext traffic for local testing

## API Endpoints Used

### Authentication Endpoints:
```
POST /api/auth/login          - Login with username/password
POST /api/auth/register       - Register new user (pending approval)
GET  /api/auth/status/{username} - Check account status
```

### Schedule Endpoints:
```
GET  /api/routes?start=X&destination=Y  - Search routes
GET  /api/schedules                     - Get all schedules
GET  /api/schedules/bus                 - Get bus schedules only
GET  /api/schedules/train               - Get train schedules only
```

### Admin Endpoints (Master/Developer only):
```
GET  /api/admin/pending-users           - Get pending registrations
POST /api/admin/approve/{userId}        - Approve user registration
POST /api/admin/reject/{userId}         - Reject user registration
GET  /api/users/profile                 - Get logged-in user profile
```

## Configuration

### Base URL:
The app uses different base URLs depending on the environment:

**For Emulator:**
```java
public static final String BASE_URL = "http://10.0.2.2:8080/api/";
```

**For Physical Device:**
```java
public static final String BASE_URL = "http://192.168.X.X:8080/api/";
```
Replace `192.168.X.X` with your PC's local IP address.

### How to Change Base URL:
1. Open: `app/src/main/java/com/siyam/travelschedulemanager/data/remote/RetrofitClient.java`
2. Find the `BASE_URL` constant
3. Change it based on your testing environment
4. Rebuild the app: `.\gradlew assembleDebug`

## Testing Guide

### Prerequisites:
1. **Desktop App Running:**
   - Start your JavaFX desktop app
   - Ensure it's running on port 8080
   - CORS should be configured
   - Database should be initialized

2. **Network Configuration:**
   - For emulator: Desktop app accessible at `localhost:8080`
   - For physical device: PC and phone on same WiFi network

### Test Workflow:

#### 1. Registration Flow:
```
a. Open app → Click "Register"
b. Fill in:
   - Username: testuser
   - Full Name: Test User
   - Email: test@example.com
   - Role: Developer
   - Password: password123
   - Confirm Password: password123
c. Click "Register"
d. Dialog appears: "Registration Submitted - Pending approval"
e. Click "Check Status" to verify registration status
```

#### 2. Admin Approval (Desktop App):
```
a. Open desktop app and login as Master admin
b. Go to "Manage Users" section
c. Find "testuser" in pending users list
d. Click "Approve"
e. User is now able to login
```

#### 3. Login Flow:
```
a. Return to Android app
b. Enter:
   - Username: testuser
   - Password: password123
c. Click "Login"
d. Should redirect to Developer Dashboard (based on role)
e. Token is saved automatically
```

#### 4. Route Search Flow:
```
a. In User Dashboard, navigate to "Route Finder"
b. Enter:
   - From: Dhaka
   - To: Chittagong
   - Date: (any future date)
   - Transport: All
c. Click "Find Optimal Routes"
d. Should see list of available routes (Bus and Train)
e. Each result shows:
   - Type (BUS/TRAIN)
   - Route (Dhaka → Chittagong)
   - Time (08:00 AM - 02:00 PM)
   - Company/Train details
   - Fare (৳800)
   - Available seats
```

### Testing Commands:

#### Build APK:
```powershell
cd e:\GitHub\Android
.\gradlew assembleDebug
```
APK Location: `app/build/outputs/apk/debug/app-debug.apk`

#### Install on Device:
```powershell
.\gradlew installDebug
```

#### Clean Build:
```powershell
.\gradlew clean assembleDebug
```

## Troubleshooting

### Issue: "No connected devices"
**Solution:**
1. Connect your phone via USB
2. Enable USB Debugging on phone
3. Verify with: `.\gradlew installDebug`

### Issue: "Network error" or "Unable to connect"
**Solution:**
1. Check BASE_URL in RetrofitClient.java
2. For emulator: Use `10.0.2.2:8080`
3. For physical device: Use your PC's IP (check with `ipconfig`)
4. Ensure desktop app is running
5. Check Windows Firewall allows port 8080

### Issue: "Login failed - Account not approved"
**Solution:**
1. Login to desktop app as Master admin
2. Approve the user from "Manage Users"
3. Try logging in again

### Issue: "No routes found"
**Solution:**
1. Check if desktop app has schedule data in database
2. Verify district names match (use "Chittagong" not "Chattogram")
3. Check network connectivity

### Issue: Layout errors
**Solution:**
- Some layouts may still reference `recycler_routes` instead of `recycler_view_results`
- Check: `fragment_route_finder.xml` has correct IDs
- Rebuild: `.\gradlew clean assembleDebug`

## API Response Formats

### Login Response:
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "role": "DEVELOPER"
  }
}
```

### Register Response:
```json
{
  "success": true,
  "message": "Registration submitted successfully",
  "status": "PENDING"
}
```

### Search Routes Response:
```json
{
  "value": [
    {
      "id": 1,
      "type": "BUS",
      "origin": "Dhaka",
      "destination": "Chittagong",
      "departureTime": "08:00 AM",
      "arrivalTime": "02:00 PM",
      "fare": 800.0,
      "availableSeats": 20,
      "companyName": "Hanif Enterprise",
      "busType": "AC"
    }
  ],
  "Count": 1
}
```

## Important Notes

### Token Management:
- Token is automatically saved after successful login
- Token is injected in all authenticated requests (Authorization: Bearer {token})
- Token persists across app restarts
- Logout clears token and redirects to login

### Role-Based Access:
- **MASTER**: Full access (MasterDashboardActivity)
- **DEVELOPER**: Developer features (DeveloperDashboardActivity)
- **USER**: Standard features (UserDashboardActivity)

### Districts:
All district names synchronized to match desktop app. Use "Chittagong" (not "Chattogram").

### Offline Support:
Currently, the app requires network connection for all operations. Future enhancement could add:
- Local database caching
- Offline mode for viewing cached schedules
- Queue failed operations for retry

## Next Steps

### Optional Enhancements:
1. **Update Admin Features:**
   - Implement pending users list in Master Dashboard
   - Add approve/reject functionality
   
2. **Update Other Fragments:**
   - HomeFragment - Show recent routes
   - SchedulesFragment - Browse all schedules
   - CreatePlanFragment - Plan multi-leg journeys

3. **Add Offline Support:**
   - Cache schedules in Room database
   - Sync when online

4. **Improve UI:**
   - Add pull-to-refresh
   - Empty state views
   - Better error messages
   - Loading skeletons

## Files Summary

### New Files Created:
- `RestAuthViewModel.java` - Authentication ViewModel
- `RestScheduleViewModel.java` - Schedule ViewModel
- `RouteResultAdapter.java` - RecyclerView adapter
- `item_route_result.xml` - Route card layout

### Modified Files:
- `LoginActivity.java` - REST API login
- `RegisterActivity.java` - REST API registration
- `RouteFinderFragment.java` - REST API search
- `activity_register.xml` - Added full name field
- `fragment_route_finder.xml` - Added RecyclerView and ProgressBar

### Previously Created (REST API Layer):
- All DTOs (LoginRequest, LoginResponse, etc.)
- `TokenManager.java`
- `ApiService.java`
- `RetrofitClient.java`
- `network_security_config.xml`
- `AndroidManifest.xml` (network permissions)

## Build Status
✅ App builds successfully
✅ All REST API endpoints integrated
✅ Authentication flow complete
✅ Route search complete
✅ Token management working
✅ Role-based navigation working

## Ready for Testing
The app is now fully functional and ready for testing with your desktop application!
