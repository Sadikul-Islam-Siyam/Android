# 🚌 Travel Schedule Manager - Android App

A comprehensive Android application for managing travel schedules across Bangladesh with intelligent multi-leg route finding, automatic journey planning, and seamless desktop API integration.

---

## 📱 Overview

Travel Schedule Manager is a full-featured Android app that helps users plan and manage their bus and train journeys across all 64 districts of Bangladesh. The app integrates with a desktop REST API to fetch real-time schedule data and uses advanced algorithms to find optimal multi-leg routes.

---

## ✨ Key Features

### 🔐 **User Authentication**
- Firebase Authentication (Email/Password)
- Secure user registration and login
- Profile management
- Role-based access (User, Developer, Master)

### 🗺️ **Automatic Route Finder** ⭐ FLAGSHIP FEATURE
- **Smart Algorithm**: Depth-First Search (DFS) based route finding
- **Multi-Leg Routes**: Automatically finds 1, 2, and 3-leg journey combinations
- **Connection Validation**: Ensures 15-min to 12-hour transfer times between legs
- **All 64 Districts**: Complete coverage of Bangladesh districts
- **Case-Insensitive**: Works with any spelling variation
- **Fare Optimization**: Routes sorted by cheapest fare first
- **Save to Plans**: Convert found routes to saved travel plans

**Example**: Search "Khulna to Chattogram" and get routes like:
- Direct: Khulna → Chattogram (if available)
- 2-Leg: Khulna → Dhaka → Chattogram
- 3-Leg: Khulna → Rajshahi → Dhaka → Chattogram

### 📝 **Travel Plan Management**
- **Create Plans**: Build custom multi-leg journeys
  - Search schedules by origin/destination
  - Filter by transport type (Bus/Train/All)
  - Add multiple legs with validation
  - Real-time fare and duration calculation
- **View Plans**: Browse all saved travel plans
  - Card-based list view
  - Quick access to plan details
- **Edit Plans**: Modify existing plans
  - Load complete plan data
  - Update name, date, or legs
  - Seamless Firebase sync
- **Delete Plans**: Remove unwanted plans with confirmation
- **Plan Details**: Complete journey breakdown
  - Leg-by-leg display
  - Transport type icons
  - Times, fares, operators
  - Total fare and duration

### 🔍 **Schedule Search**
- Search bus and train schedules from desktop database
- Autocomplete district names
- Filter by transport type (Bus/Train/All)
- Real-time data loading
- Manual route selection with validation

### 🔗 **Desktop API Integration**
- **REST API**: Retrofit 2 + OkHttp
- **Endpoints**: `/api/schedules` (GET)
- **Response**: List<UnifiedScheduleDTO>
- **Connection Methods**:
  - WiFi: Same network (192.168.0.x)
  - USB: ADB port forwarding
- **Features**:
  - Automatic schedule loading
  - 30-second timeout
  - Detailed error logging
  - Connection retry support

### 📊 **Data Management**
- **Firebase Firestore**: Cloud database for plans
- **Local Caching**: Schedule data optimization
- **Offline Support**: View saved plans offline
- **Sync**: Real-time updates across devices

---

## 🏗️ Technical Architecture

### **Technology Stack**
- **Language**: Java
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Material Design 3
- **Backend**: Firebase (Authentication + Firestore)
- **API**: Retrofit 2 + OkHttp + Gson
- **Navigation**: Android Navigation Component
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)

### **Key Components**

#### **Route Finding Algorithm**
```
Algorithm: Depth-First Search (DFS) with Backtracking
Time Complexity: O(V^maxLegs) where V = cities
Space Complexity: O(V) for visited set

Features:
- Explores all possible paths from source to destination
- Prevents cycles using visited set
- Validates connection times (15-min to 12-hour buffer)
- Sorts results by total fare
- Limits output to top 10 routes
```

#### **MVVM Architecture**
```
View (Fragments) → ViewModel (Business Logic) → Repository (Data Source)
                ↓
            LiveData (Reactive Updates)
                ↓
            UI Updates
```

#### **API Integration**
```
RetrofitClient (Singleton)
    ↓
ApiService (Interface)
    ↓
UnifiedScheduleDTO (Model)
    ↓
RouteGraph (Algorithm)
    ↓
Display Results
```

---

## 🚀 Installation & Setup

### **Prerequisites**
1. Android Studio (Latest version)
2. Android device or emulator (Android 7.0+)
3. Desktop REST API running (for schedule data)
4. Firebase project configured

### **Step 1: Clone & Open**
```bash
git clone <repository-url>
cd Android
# Open in Android Studio
```

### **Step 2: Configure Firebase**
1. Download `google-services.json` from Firebase Console
2. Place in `app/` directory
3. Enable Authentication (Email/Password)
4. Create Firestore database

### **Step 3: Configure Desktop API**
Edit `RetrofitClient.java` (Line 27):

**Option A - WiFi** (Recommended):
```java
private static final String DEFAULT_BASE_URL = "http://192.168.0.144:8080/api/";
// Replace 192.168.0.144 with your desktop IP
```

**Option B - USB**:
```java
private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/";
```
Then run: `adb reverse tcp:8080 tcp:8080`

### **Step 4: Build & Run**
```bash
# Clean and build
.\gradlew clean assembleDebug

# Install on device
.\gradlew installDebug

# Or use Android Studio Run button
```

---

## 📖 User Guide

### **Getting Started**

1. **Register/Login**
   - Open app
   - Create account with email/password
   - Wait for admin approval (if required)

2. **Load Schedules**
   - Ensure desktop app is running
   - Open "Automatic Route Finder" or "Create Plan"
   - Schedules load automatically
   - Toast shows: "✓ Loaded X schedules from desktop"

### **Finding Routes**

1. **Automatic Route Finder** (Recommended)
   ```
   1. Select origin (e.g., Khulna)
   2. Select destination (e.g., Chattogram)
   3. Tap "Find Routes"
   4. View all route options (1-3 legs)
   5. Tap "Save Plan" on preferred route
   ```

2. **Manual Planning**
   ```
   1. Go to "Create Travel Plan"
   2. Enter plan name and date
   3. Search for origin → destination
   4. Select route from search results
   5. Add to journey
   6. Repeat for multi-leg journeys
   7. Tap "Save Plan" FAB
   ```

### **Managing Plans**

- **View**: Tap on any plan card
- **Edit**: Long press → Select "Edit Plan"
- **Delete**: Long press → Select "Delete Plan" → Confirm

---

## 🔧 Configuration

### **Network Setup**

#### **WiFi Connection**
1. Connect both devices to same WiFi
2. Find desktop IP: `ipconfig` (Windows) or `ifconfig` (Mac/Linux)
3. Update `RetrofitClient.java` with desktop IP
4. Ensure port 8080 is not blocked by firewall

#### **USB Connection**
1. Connect Android device via USB
2. Enable USB debugging
3. Run: `adb reverse tcp:8080 tcp:8080`
4. Use `http://localhost:8080/api/` in code

### **District Names**
All 64 Bangladesh districts supported:
- Correct spellings: Chattogram (not Chittagong), Barishal, Cumilla, Jashore
- Case-insensitive search
- Autocomplete dropdown

### **API Endpoint Structure**
```
GET http://192.168.0.144:8080/api/schedules

Response:
[
  {
    "type": "BUS",
    "name": "Hanif Enterprise",
    "start": "dhaka",
    "destination": "chattogram",
    "startTime": "08:00",
    "arrivalTime": "14:30",
    "fare": 550.0,
    "duration": "6h 30m",
    "offDay": "NONE"
  },
  ...
]
```

---

## 🐛 Troubleshooting

### **No schedules loading**
```
Problem: Toast shows "Connection failed"
Solutions:
✓ Verify desktop app is running on port 8080
✓ Check both devices on same network (192.168.0.x)
✓ Try: curl http://192.168.0.144:8080/api/schedules
✓ Check firewall settings
✓ Use USB connection as fallback
```

### **No routes found**
```
Problem: "No routes found between X and Y"
Solutions:
✓ Verify desktop database has required routes
✓ Check city name spelling matches database
✓ Review Logcat for detailed search info
✓ Ensure schedules loaded (check toast notification)
✓ Try direct route first (e.g., Dhaka → Chattogram)
```

### **Connection validation errors**
```
Problem: "Invalid connection: Only X minutes..."
Solutions:
✓ Connection times must be 15 min to 12 hours apart
✓ Check schedule times in desktop database
✓ Overnight connections supported (adds 24 hours)
```

### **Build errors**
```
Problem: Compilation fails
Solutions:
✓ Sync Gradle: File → Sync Project with Gradle Files
✓ Clean build: Build → Clean Project
✓ Invalidate caches: File → Invalidate Caches / Restart
✓ Check google-services.json is present
```

---

## 📊 Project Structure

```
Android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/siyam/travelschedulemanager/
│   │   │   │   ├── algorithm/
│   │   │   │   │   └── RouteGraph.java              # DFS route finding
│   │   │   │   ├── data/
│   │   │   │   │   ├── firebase/
│   │   │   │   │   │   └── AuthRepository.java      # Firebase auth
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   ├── ApiService.java          # REST endpoints
│   │   │   │   │   │   ├── RetrofitClient.java      # HTTP client
│   │   │   │   │   │   └── dto/
│   │   │   │   │   │       └── UnifiedScheduleDTO.java
│   │   │   │   │   └── repository/
│   │   │   │   │       └── PlanRepository.java      # Firestore plans
│   │   │   │   ├── model/
│   │   │   │   │   ├── Plan.java                    # Plan model
│   │   │   │   │   └── Schedule.java                # Schedule model
│   │   │   │   ├── ui/
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   │   └── RegisterActivity.java
│   │   │   │   │   ├── plan/
│   │   │   │   │   │   ├── CreatePlanFragment.java  # Manual planning
│   │   │   │   │   │   ├── PlansFragment.java       # Plans list
│   │   │   │   │   │   └── PlanDetailFragment.java  # Plan details
│   │   │   │   │   ├── route/
│   │   │   │   │   │   └── AutomaticRouteFinderFragment.java # Auto routes
│   │   │   │   │   └── dashboard/
│   │   │   │   │       ├── user/UserHomeFragment.java
│   │   │   │   │       ├── developer/DeveloperHomeFragment.java
│   │   │   │   │       └── master/MasterHomeFragment.java
│   │   │   │   ├── util/
│   │   │   │   │   ├── Constants.java               # 64 districts
│   │   │   │   │   └── DateUtils.java               # Time calculations
│   │   │   │   └── viewmodel/
│   │   │   │       ├── PlanViewModel.java
│   │   │   │       └── ScheduleViewModel.java
│   │   │   ├── res/
│   │   │   │   ├── layout/                           # All XML layouts
│   │   │   │   ├── navigation/                       # Nav graphs
│   │   │   │   ├── values/
│   │   │   │   │   └── arrays.xml                    # District list
│   │   │   │   └── drawable/                         # Icons & images
│   │   │   └── AndroidManifest.xml
│   │   └── google-services.json                      # Firebase config
│   └── build.gradle.kts                              # App dependencies
├── gradle/
│   └── libs.versions.toml                            # Version catalog
├── build.gradle.kts                                  # Project config
└── README.md                                         # This file
```

---

## 🔑 Key Classes Reference

### **RouteGraph.java**
```java
// Main route finding algorithm
public List<List<UnifiedScheduleDTO>> findRoutes(String source, String destination, int maxLegs)
- Builds adjacency list from schedules
- Uses DFS to explore all paths
- Validates connection times
- Returns top 10 routes sorted by fare
```

### **AutomaticRouteFinderFragment.java**
```java
// Automatic route finder UI
- Loads schedules from desktop API
- Accepts user input (origin/destination)
- Calls RouteGraph algorithm
- Displays route options
- Saves selected routes as plans
```

### **CreatePlanFragment.java**
```java
// Manual plan creation UI
- Searches schedules from API
- Manual leg selection
- 30-min connection validation
- Destination continuity check
- Edit existing plans
```

### **PlanViewModel.java**
```java
// Plan management business logic
- CRUD operations for plans
- LiveData for reactive UI
- Firebase Firestore integration
```

### **RetrofitClient.java**
```java
// HTTP client singleton
- Manages API connections
- 30-second timeout
- Logging interceptor
- Base URL configuration
```

### **DateUtils.java**
```java
// Time calculation utilities
- calculateDuration(start, end): Returns minutes
- formatDuration(minutes): Returns "Xh Ym"
- isValidTransferTime(arrival, departure, buffer): Validates connections
```

---

## 🎯 Features Checklist

### ✅ **Mandatory Features Implemented**
- [x] **Edit Plans**: Load and update existing plans
- [x] **View Plans**: Display all saved plans with details
- [x] **Delete Plans**: Remove plans with confirmation
- [x] **Plan Details**: Complete journey information
- [x] **Automatic Route Finder**: DFS-based multi-leg route finding
- [x] **Duration Calculation**: Accurate time calculations with validation

### ✅ **Additional Features**
- [x] Desktop REST API integration (WiFi + USB)
- [x] All 64 Bangladesh districts
- [x] Case-insensitive search
- [x] Firebase Authentication
- [x] Firestore database
- [x] Material Design 3 UI
- [x] Role-based access control
- [x] Multi-leg route support (1-3 legs)
- [x] Connection time validation (15-min to 12-hour)
- [x] Fare optimization
- [x] Detailed logging for debugging

---

## 🧪 Testing

### **Test Scenarios**

#### **Test 1: Direct Route**
```
Origin: Dhaka
Destination: Chattogram
Expected: Shows direct bus/train routes
```

#### **Test 2: Multi-Leg Route**
```
Origin: Khulna
Destination: Chattogram
Expected: Shows 2-leg route (Khulna → Dhaka → Chattogram)
```

#### **Test 3: No Route**
```
Origin: Dhaka
Destination: [City with no connection]
Expected: "No routes found" with helpful message
```

#### **Test 4: Create Plan**
```
1. Search Dhaka → Chattogram
2. Add to journey
3. Search Chattogram → Cox's Bazar
4. Add to journey (should validate connection time)
5. Save plan
Expected: Plan saved with 2 legs, total fare/duration calculated
```

#### **Test 5: Edit Plan**
```
1. Long press on saved plan
2. Select "Edit Plan"
3. Change plan name
4. Save
Expected: Plan updated (not duplicated)
```

### **Debugging**

Enable detailed logging:
```bash
# View all logs
adb logcat -s RouteFinderAPI RouteGraph

# Filter by tag
adb logcat | grep "RouteFinderAPI"
```

---

## 📞 Support & Contact

### **Common Issues**

1. **Desktop API not responding**
   - Check desktop app is running
   - Verify network connectivity
   - Test endpoint: `curl http://192.168.0.144:8080/api/schedules`

2. **Routes not found**
   - Verify desktop database has required routes
   - Check Logcat for available cities
   - Ensure city names match database

3. **Firebase errors**
   - Verify google-services.json is present
   - Check Firebase Console for service status
   - Ensure Authentication and Firestore are enabled

### **Configuration Files**

- **API Endpoint**: `RetrofitClient.java` (Line 27)
- **Firebase**: `google-services.json`
- **Districts**: `arrays.xml` and `Constants.java`
- **Dependencies**: `build.gradle.kts`

---

## 📄 License

This project is part of an academic assignment.

---

## 🙏 Acknowledgments

- Firebase for backend services
- Material Design 3 for UI components
- Retrofit + OkHttp for networking
- Android Navigation Component

---

## 📌 Version History

**v1.0** (January 12, 2026)
- Initial release
- Complete CRUD for travel plans
- Automatic route finder with DFS algorithm
- Desktop API integration
- All 64 Bangladesh districts
- Multi-leg route support (1-3 legs)
- Connection validation (15-min to 12-hour buffer)
- Firebase Authentication + Firestore
- Material Design 3 UI

---

## 🚀 Quick Start Guide

### **5-Minute Setup**

1. **Install APK**
   ```bash
   adb install app-debug.apk
   ```

2. **Configure Desktop**
   - Run desktop app on port 8080
   - Note desktop IP address

3. **Update Android App**
   - Edit `RetrofitClient.java` with desktop IP
   - Rebuild: `.\gradlew assembleDebug`

4. **Test**
   - Open app → Login/Register
   - Go to "Automatic Route Finder"
   - Search: Dhaka → Chattogram
   - Should show routes!

---

**Built with ❤️ for seamless travel planning across Bangladesh**

**Status**: ✅ Production Ready | All Features Implemented | Tested & Working
