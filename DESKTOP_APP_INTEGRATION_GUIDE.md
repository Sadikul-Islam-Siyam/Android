# Desktop App Integration Guide

## 🔗 How to Connect Your Desktop App to the Android App

Your Android app is now ready to consume REST API endpoints from your desktop JavaFX application. Here's what you need to do:

---

## ✅ What Your Desktop App MUST Provide

### Required REST API Endpoints

Your desktop app needs to expose these HTTP endpoints on **port 8080**:

#### 1. **GET /api/schedules/bus** (Required)
Returns all bus schedules in JSON format.

**Expected Response Format:**
```json
[
  {
    "busName": "Hanif Enterprise",
    "start": "Dhaka",
    "destination": "Chittagong",
    "startTime": "08:00",
    "arrivalTime": "14:00",
    "fare": 550.0,
    "duration": "6h 0m"
  },
  {
    "busName": "Green Line",
    "start": "Dhaka",
    "destination": "Cox's Bazar",
    "startTime": "22:00",
    "arrivalTime": "08:00",
    "fare": 1200.0,
    "duration": "10h 0m"
  }
]
```

**Required Fields:**
- `busName` (String) - Name of the bus service
- `start` (String) - Origin city
- `destination` (String) - Destination city
- `startTime` (String) - Departure time (HH:mm format)
- `arrivalTime` (String) - Arrival time (HH:mm format)
- `fare` (double) - Ticket price
- `duration` (String) - Travel duration (e.g., "6h 30m")

---

#### 2. **GET /api/schedules/train** (Required)
Returns all train schedules with intermediate stops.

**Expected Response Format:**
```json
[
  {
    "trainName": "Subarna Express",
    "start": "Dhaka",
    "destination": "Chittagong",
    "startTime": "06:00",
    "arrivalTime": "12:30",
    "fare": 450.0,
    "duration": "6h 30m",
    "offDay": "None",
    "stops": [
      {
        "station": "Dhaka",
        "arrivalTime": "06:00",
        "departureTime": "06:00",
        "cumulativeFare": 0.0
      },
      {
        "station": "Comilla",
        "arrivalTime": "09:00",
        "departureTime": "09:10",
        "cumulativeFare": 150.0
      },
      {
        "station": "Chittagong",
        "arrivalTime": "12:30",
        "departureTime": "12:30",
        "cumulativeFare": 450.0
      }
    ]
  }
]
```

**Required Fields (Main):**
- `trainName` (String) - Name of the train
- `start` (String) - Origin station
- `destination` (String) - Final destination
- `startTime` (String) - Departure time (HH:mm)
- `arrivalTime` (String) - Arrival time (HH:mm)
- `fare` (double) - Full journey fare
- `duration` (String) - Total duration
- `offDay` (String) - Day when train doesn't run (e.g., "Friday", "None")
- `stops` (Array) - List of intermediate stations

**Required Fields (Stops):**
- `station` (String) - Station name
- `arrivalTime` (String) - Arrival time at this station
- `departureTime` (String) - Departure time from this station
- `cumulativeFare` (double) - Fare from origin to this station

---

#### 3. **GET /api/routes?start={start}&destination={destination}** (Required)
Search for routes between two cities (both bus and train).

**Example Request:**
```
GET /api/routes?start=Dhaka&destination=Chittagong
```

**Expected Response Format:**
```json
[
  {
    "type": "bus",
    "name": "Hanif Enterprise",
    "start": "Dhaka",
    "destination": "Chittagong",
    "startTime": "08:00",
    "arrivalTime": "14:00",
    "fare": 550.0,
    "duration": "6h 0m"
  },
  {
    "type": "train",
    "name": "Subarna Express",
    "start": "Dhaka",
    "destination": "Chittagong",
    "startTime": "06:00",
    "arrivalTime": "12:30",
    "fare": 450.0,
    "duration": "6h 30m",
    "offDay": "None"
  }
]
```

**Required Fields:**
- `type` (String) - "bus" or "train"
- `name` (String) - Service name
- `start` (String) - Origin
- `destination` (String) - Destination
- `startTime` (String) - Departure time
- `arrivalTime` (String) - Arrival time
- `fare` (double) - Ticket price
- `duration` (String) - Travel duration
- `offDay` (String) - Optional, only for trains

---

#### 4. **GET /api/health** (Optional but Recommended)
Health check endpoint to verify server is running.

**Expected Response:**
```json
{
  "status": "UP",
  "timestamp": "2026-01-11T10:30:00",
  "service": "Travel Schedule Manager API"
}
```

---

## 🛠️ Implementation Guide for Desktop App

### Step 1: Add Javalin Dependency (If Not Already Present)

Your desktop app likely already has Javalin. If not, add to `pom.xml`:

```xml
<dependency>
    <groupId>io.javalin</groupId>
    <artifactId>javalin</artifactId>
    <version>5.6.3</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
```

---

### Step 2: Create REST API Server Class

Here's a complete example for your desktop app:

```java
package com.yourapp.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;

public class RestApiServer {
    private Javalin app;
    private final ScheduleService scheduleService; // Your existing service
    private static final int PORT = 8080;

    public RestApiServer(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    public void start() {
        app = Javalin.create(config -> {
            config.enableCorsForAllOrigins(); // Allow Android app to connect
            config.jsonMapper(new JavalinJackson()); // JSON serialization
        }).start(PORT);

        System.out.println("✅ REST API Server started on http://localhost:" + PORT);

        // Define endpoints
        setupEndpoints();
    }

    private void setupEndpoints() {
        // 1. Get all bus schedules
        app.get("/api/schedules/bus", ctx -> {
            List<BusSchedule> buses = scheduleService.getAllBusSchedules();
            ctx.json(buses);
        });

        // 2. Get all train schedules
        app.get("/api/schedules/train", ctx -> {
            List<TrainSchedule> trains = scheduleService.getAllTrainSchedules();
            ctx.json(trains);
        });

        // 3. Search routes
        app.get("/api/routes", ctx -> {
            String start = ctx.queryParam("start");
            String destination = ctx.queryParam("destination");
            
            if (start == null || destination == null) {
                ctx.status(400).json(Map.of("error", "Missing start or destination"));
                return;
            }

            List<Object> routes = scheduleService.searchRoutes(start, destination);
            ctx.json(routes);
        });

        // 4. Health check
        app.get("/api/health", ctx -> {
            ctx.json(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "service", "Travel Schedule Manager API"
            ));
        });

        // Handle errors
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        });
    }

    public void stop() {
        if (app != null) {
            app.stop();
            System.out.println("❌ REST API Server stopped");
        }
    }
}
```

---

### Step 3: Integrate with Your JavaFX Application

In your main JavaFX application class:

```java
public class MainApp extends Application {
    private RestApiServer apiServer;
    private ScheduleService scheduleService;

    @Override
    public void start(Stage primaryStage) {
        // Initialize your existing services
        scheduleService = new ScheduleService(database);
        
        // Start REST API server
        apiServer = new RestApiServer(scheduleService);
        apiServer.start();
        
        // Your existing JavaFX UI setup
        // ...
    }

    @Override
    public void stop() throws Exception {
        // Stop API server when app closes
        if (apiServer != null) {
            apiServer.stop();
        }
        super.stop();
    }
}
```

---

### Step 4: Map Database Models to API Response Format

Create DTO classes that match the Android app's expected format:

```java
// BusScheduleDTO.java
public class BusScheduleDTO {
    private String busName;
    private String start;
    private String destination;
    private String startTime;
    private String arrivalTime;
    private double fare;
    private String duration;

    // Constructor from your database model
    public BusScheduleDTO(BusSchedule dbModel) {
        this.busName = dbModel.getName();
        this.start = dbModel.getOrigin();
        this.destination = dbModel.getDestination();
        this.startTime = dbModel.getDepartureTime();
        this.arrivalTime = dbModel.getArrivalTime();
        this.fare = dbModel.getPrice();
        this.duration = calculateDuration(dbModel); // Your logic
    }

    // Getters and setters...
}
```

---

## 🌐 Network Configuration

### For Testing on Emulator:
- Desktop app runs on: `http://localhost:8080`
- Android emulator connects to: `http://10.0.2.2:8080` (automatically configured)
- ✅ No changes needed

### For Testing on Physical Device:
1. **Connect both devices to same WiFi network**
2. **Find your desktop's IP address:**
   - Windows: Open CMD, run `ipconfig`, look for "IPv4 Address"
   - Example: `192.168.1.100`
3. **Allow firewall access:**
   - Windows: Allow port 8080 in Windows Defender Firewall
   - Command: `netsh advfirewall firewall add rule name="Travel API" dir=in action=allow protocol=TCP localport=8080`

4. **Update Android app's API base URL:**
   - Current default: `http://10.0.2.2:8080/api/`
   - Change to: `http://192.168.1.100:8080/api/` (use your desktop's IP)
   - ⚠️ Settings screen for this is TODO (you can manually change it in `RetrofitClient.java` for now)

---

## 🧪 Testing the Connection

### Step 1: Verify Desktop API is Running
Open browser or use curl:
```bash
# Test health endpoint
curl http://localhost:8080/api/health

# Test bus schedules
curl http://localhost:8080/api/schedules/bus

# Test search
curl "http://localhost:8080/api/routes?start=Dhaka&destination=Chittagong"
```

### Step 2: Test from Android App
1. Open the app on your device
2. Navigate to schedules screen
3. Check logcat for API calls:
   ```bash
   adb logcat | grep -i "ScheduleRepository\|RetrofitClient"
   ```

### Step 3: Check for Errors
- **Connection Refused**: Desktop app not running or firewall blocking
- **404 Not Found**: Endpoint URL mismatch
- **500 Internal Server Error**: Desktop app error (check server logs)
- **Timeout**: Network issue or wrong IP address

---

## 📋 Quick Checklist

- [ ] Javalin dependency added to desktop app
- [ ] REST API endpoints implemented:
  - [ ] `/api/schedules/bus`
  - [ ] `/api/schedules/train`
  - [ ] `/api/routes?start=X&destination=Y`
  - [ ] `/api/health` (optional)
- [ ] API server starts when desktop app launches
- [ ] JSON response format matches Android app's DTOs
- [ ] CORS enabled (for cross-origin requests)
- [ ] Port 8080 is accessible
- [ ] Firewall allows port 8080 (for physical device testing)
- [ ] Desktop and phone on same WiFi (for physical device)

---

## 🔧 Temporary Manual Configuration (Until Settings Screen is Added)

If testing on **physical device**, manually update the API URL:

**File**: `app/src/main/java/com/siyam/travelschedulemanager/data/remote/RetrofitClient.java`

**Change line ~24:**
```java
// FROM:
private static String BASE_URL = "http://10.0.2.2:8080/api/";

// TO:
private static String BASE_URL = "http://YOUR_DESKTOP_IP:8080/api/";
// Example: "http://192.168.1.100:8080/api/"
```

Then rebuild and reinstall:
```bash
.\gradlew installDebug
```

---

## 📞 Support

### Common Issues:

**Issue**: Android app shows "No internet connection"
- **Solution**: Check WiFi, ensure desktop app is running, verify IP address

**Issue**: "Server is not responding"
- **Solution**: Check desktop app logs, verify port 8080 is not blocked

**Issue**: Empty schedule list
- **Solution**: Check if desktop app's database has data, verify JSON format

**Issue**: "Route search requires internet connection"
- **Solution**: This is expected - route search only works online (by design)

---

## 🎯 What Happens After Integration

### ✅ When Online:
1. Android app fetches fresh data from desktop REST API
2. Data is cached locally for 24 hours
3. Changes in desktop app reflect immediately in Android app

### ✅ When Offline:
1. Android app uses cached data (last 24 hours)
2. Schedule viewing works normally
3. Route search is disabled (requires live API)

### ✅ Workflow:
```
Desktop App: Add/Edit/Delete schedules in SQLite database
      ↓
Android App: Fetches updates via REST API every time user opens app
      ↓
Android App: Caches data for offline access
      ↓
User: Can view schedules even without internet
```

---

## 🚀 Next Steps After Connection

1. **Test basic connectivity** - Verify health endpoint
2. **Test data fetching** - Load schedules in Android app
3. **Test search** - Search routes between cities
4. **Test offline mode** - Turn off WiFi, verify cached data works
5. **Add API URL settings screen** - Allow users to configure server IP

---

## 💡 Pro Tips

1. **Keep desktop app running** when testing Android app
2. **Check desktop app console** for incoming API requests
3. **Use Android Studio's Network Profiler** to debug API calls
4. **Enable HTTP logging** in RetrofitClient for debugging
5. **Test with real data** - Add diverse schedules in desktop app

---

## 📝 Summary

**What YOU need to do:**
1. ✅ Add REST API endpoints to your desktop JavaFX app (using Javalin)
2. ✅ Return JSON in the format specified above
3. ✅ Start API server on port 8080 when desktop app launches
4. ✅ Ensure firewall allows port 8080
5. ✅ (Optional) Add CORS support for better compatibility

**What's ALREADY done in Android app:**
- ✅ REST API client (Retrofit)
- ✅ Network monitoring
- ✅ Smart caching (24hr validity)
- ✅ Offline support
- ✅ Automatic error handling with cache fallback

Your Android app is ready to consume data - you just need to provide the REST API endpoints in your desktop app! 🎉
