# Desktop App REST API Configuration

## Required Changes to Enable Android App Access

### 1. Add CORS Support to Javalin Server

In your desktop app's main server initialization file (where you create the Javalin instance), add CORS configuration:

```java
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class YourServerClass {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            // Enable CORS for all origins (or specify your Android app)
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost(); // Allow all hosts (for development)
                    // Or for production: it.allowHost("10.0.2.2", "localhost");
                });
            });
        }).start(8080);

        // Your existing routes...
        app.get("/api/schedules/bus", ctx -> { /* ... */ });
        app.get("/api/schedules/train", ctx -> { /* ... */ });
        app.get("/api/schedules", ctx -> { /* ... */ });
        app.get("/api/routes", ctx -> { /* ... */ });
    }
}
```

### 2. Alternative: Manual CORS Headers

If the above doesn't work with your Javalin version, add CORS headers manually to each route:

```java
app.before("/api/*", ctx -> {
    ctx.header("Access-Control-Allow-Origin", "*");
    ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
});

app.options("/api/*", ctx -> {
    ctx.status(200);
});
```

### 3. Verify District Names Match

**CRITICAL:** Ensure your desktop app uses the same district names as the Android app.

#### Current Android App District Names:
- Dhaka
- **Chittagong** (not Chattogram)
- Khulna
- Rajshahi
- Sylhet
- Barisal
- Rangpur
- Mymensingh
- Comilla
- Narayanganj
- Gazipur
- Jessore
- Bogra
- Dinajpur
- Cox's Bazar
- Brahmanbaria
- Tangail
- Pabna
- Faridpur

**Option A:** Update your desktop app's database/code to use "Chittagong" instead of "Chattogram"

**Option B:** Update the Android app to use "Chattogram" (recommended for modern spelling)

### 4. Testing the Setup

1. **Start your desktop app** on port 8080
2. **Test CORS is working** from browser console:
   ```javascript
   fetch('http://localhost:8080/api/schedules/bus')
     .then(r => r.json())
     .then(console.log)
   ```

3. **Test from Android app:**
   - Physical device: Desktop must be accessible via your local network IP
   - Emulator: Use `http://10.0.2.2:8080/api` (special address for localhost)

### 5. Network Configuration

#### For Physical Device Testing:
1. Find your PC's local IP: `ipconfig` (Windows) or `ifconfig` (Mac/Linux)
2. Update Android app's base URL to: `http://YOUR_PC_IP:8080/api`
3. Ensure PC firewall allows port 8080 connections

#### For Emulator Testing:
- Use `http://10.0.2.2:8080/api` (Android emulator's special localhost alias)
- No firewall changes needed

### 6. Required REST API Endpoints

Ensure your desktop app has these endpoints returning the wrapper format `{value: [...], Count: X}`:

| Method | Endpoint | Response Format |
|--------|----------|----------------|
| GET | `/api/schedules/bus` | `{value: [BusScheduleDTO...], Count: int}` |
| GET | `/api/schedules/train` | `{value: [TrainScheduleDTO...], Count: int}` |
| GET | `/api/schedules` | `{value: [UnifiedScheduleDTO...], Count: int}` |
| GET | `/api/routes?start=X&destination=Y` | `{value: [UnifiedScheduleDTO...], Count: int}` |

### 7. Firewall Configuration (Windows)

If connection fails from physical device:

```powershell
# Allow port 8080 through Windows Firewall
netsh advfirewall firewall add rule name="Javalin REST API" dir=in action=allow protocol=TCP localport=8080
```

Or via Windows Defender Firewall GUI:
1. Open Windows Defender Firewall → Advanced Settings
2. Inbound Rules → New Rule
3. Port → TCP → 8080 → Allow the connection

### 8. Troubleshooting

**Connection Refused:**
- Desktop app not running
- Wrong IP address in Android app
- Firewall blocking port 8080

**CORS Error:**
- CORS not configured in desktop app
- Check browser console for exact error

**Empty Results:**
- District name mismatch (Chittagong vs Chattogram)
- Database has no matching data
- Check desktop app logs

**Data Format Error:**
- Ensure response wrapper: `{value: [...], Count: X}`
- Check field names match DTOs exactly
- Duration format: "4:00h" not "4h 0m"

### 9. Desktop App Dependencies

Ensure your `pom.xml` or `build.gradle` has:

```xml
<!-- Javalin -->
<dependency>
    <groupId>io.javalin</groupId>
    <artifactId>javalin</artifactId>
    <version>5.6.3</version> <!-- or your version -->
</dependency>

<!-- Gson for JSON -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

### 10. Quick Start Checklist

- [ ] Add CORS configuration to Javalin server
- [ ] Verify all endpoints return `{value: [...], Count: X}` format
- [ ] Check district names match (Chittagong vs Chattogram)
- [ ] Start desktop app on port 8080
- [ ] Configure firewall to allow port 8080
- [ ] Test API with curl or browser
- [ ] Update Android app's base URL if using physical device
- [ ] Launch Android app and test search

---

## Need Help?

If you encounter issues:
1. Check desktop app console for errors
2. Use browser DevTools Network tab to see actual responses
3. Test endpoints with Postman or curl first
4. Verify JSON response format matches exactly
