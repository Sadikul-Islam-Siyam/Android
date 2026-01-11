# Desktop App CORS Configuration

Add this code to your Javalin server to allow Android app access:

```java
// In your main server file where you create Javalin instance:

app.before("/api/*", ctx -> {
    ctx.header("Access-Control-Allow-Origin", "*");
    ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
});

app.options("/api/*", ctx -> ctx.status(200));
```

That's it! No other changes needed.

**Optional:** If using physical Android device (not emulator), allow port 8080 in Windows Firewall:
```powershell
netsh advfirewall firewall add rule name="Javalin API" dir=in action=allow protocol=TCP localport=8080
```
