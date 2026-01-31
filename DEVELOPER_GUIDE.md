# Developer Guide

## Getting Started

This guide will help you set up the development environment and understand the codebase.

---

## Prerequisites

### Backend Development
- .NET SDK 10.0 or later
- Visual Studio Code or Visual Studio 2022
- REST Client (optional, for testing API)

### Android Development
- Android Studio Arctic Fox or later
- JDK 17
- Android SDK (API 24+)
- Physical Android device or emulator

---

## Backend Architecture

### Project Structure
The backend follows a clean architecture pattern:

```
Backend/
├── Controllers/     # HTTP API endpoints
├── Models/         # Domain entities
├── DTOs/           # Data transfer objects
└── Data/           # Database context
```

### Key Components

#### 1. Data Models
- **Order**: Main entity representing an incoming goods order
- **OrderPhoto**: Photo attachments for orders

#### 2. Database Context
- Uses Entity Framework Core
- In-memory database for development
- Seed data automatically loaded on startup

#### 3. Controllers
- **OrdersController**: Handles all order-related operations
- RESTful design with clear HTTP verbs
- Comprehensive error handling

### API Design Principles
1. **RESTful**: Uses standard HTTP methods appropriately
2. **Consistent responses**: All responses follow same structure
3. **Validation**: Input validation on all endpoints
4. **Logging**: Comprehensive logging for debugging

### Adding New Features

#### Adding a new endpoint:
```csharp
[HttpPost("{id}/newaction")]
public async Task<ActionResult> NewAction(int id)
{
    var order = await _context.Orders.FindAsync(id);
    if (order == null)
        return NotFound();
    
    // Your logic here
    await _context.SaveChangesAsync();
    
    return Ok(new { message = "Success" });
}
```

#### Adding a new model:
```csharp
public class NewEntity
{
    public int Id { get; set; }
    public required string Name { get; set; }
}
```

Then add to DbContext:
```csharp
public DbSet<NewEntity> NewEntities { get; set; }
```

---

## Android Architecture

### Project Structure
```
app/src/main/java/com/incominggoods/app/
├── models/          # Data models
├── api/             # Network layer
├── MainActivity.kt  # Scanner screen
└── OrderDetailsActivity.kt  # Order management
```

### Key Components

#### 1. Network Layer
- **ApiService**: Retrofit interface defining API endpoints
- **ApiClient**: Retrofit client configuration
- Uses Kotlin coroutines for async operations

#### 2. Activities
- **MainActivity**: Camera-based barcode scanning
- **OrderDetailsActivity**: Order display and management

#### 3. Models
- Kotlin data classes
- Gson annotations for JSON parsing

### Design Patterns Used

#### 1. Repository Pattern (API Layer)
```kotlin
object ApiClient {
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
```

#### 2. Coroutines for Async Operations
```kotlin
CoroutineScope(Dispatchers.IO).launch {
    val response = ApiClient.apiService.getOrder(id)
    withContext(Dispatchers.Main) {
        // Update UI
    }
}
```

### Adding New Features

#### Adding a new API call:
```kotlin
// 1. Add to ApiService interface
@GET("api/orders/search/{query}")
suspend fun searchOrders(@Path("query") query: String): Response<List<Order>>

// 2. Call from Activity
CoroutineScope(Dispatchers.IO).launch {
    val response = ApiClient.apiService.searchOrders(query)
    withContext(Dispatchers.Main) {
        if (response.isSuccessful) {
            // Handle results
        }
    }
}
```

#### Adding a new screen:
1. Create layout XML in `res/layout/`
2. Create Activity class
3. Add to AndroidManifest.xml
4. Navigate using Intent

---

## Testing

### Backend Testing

#### Using cURL:
```bash
# Get all orders
curl http://localhost:5000/api/orders

# Get order by barcode
curl http://localhost:5000/api/orders/barcode/123456789

# Report arrival
curl -X POST http://localhost:5000/api/orders/1/arrival
```

#### Using the .http file:
Open `IncomingGoodsBackend.http` in VS Code with REST Client extension and click "Send Request"

### Android Testing

#### Testing on Emulator:
1. Start emulator
2. Run app from Android Studio
3. Use virtual camera for barcode scanning
4. Backend URL: `http://10.0.2.2:5000/`

#### Testing on Physical Device:
1. Enable USB debugging
2. Connect device
3. Update `ApiClient.kt` with your computer's IP
4. Run app from Android Studio

---

## Common Development Tasks

### Modifying the Order Model

1. **Backend**: Update `Models/Order.cs`
2. **DTO**: Update `DTOs/OrderDtos.cs`
3. **Android**: Update `models/Models.kt`
4. **Migration**: Delete and recreate database (in-memory auto-resets)

### Adding New Order Status

1. **Backend**: 
   - Update status in controller logic
   - Add to seed data if needed

2. **Android**:
   - Update UI to handle new status
   - Add appropriate button states

### Customizing UI

#### Android Colors:
Edit `app/src/main/res/values/colors.xml`

#### Android Strings:
Edit `app/src/main/res/values/strings.xml`

#### Android Theme:
Edit `app/src/main/res/values/themes.xml`

---

## Debugging

### Backend
1. Add breakpoints in Visual Studio/VS Code
2. Run with debugger (F5)
3. Check console output for logs

### Android
1. Use Android Studio debugger
2. View Logcat for runtime logs
3. Use Log.d/Log.e for custom logging:
   ```kotlin
   Log.d("TAG", "Debug message: $value")
   ```

---

## Performance Considerations

### Backend
- In-memory database is fast but non-persistent
- For production, switch to SQL Server/PostgreSQL
- Consider caching for frequently accessed data

### Android
- Images are compressed to 80% quality
- Base64 encoding increases size by ~33%
- Consider implementing pagination for large lists
- Use RecyclerView for scrollable lists

---

## Security Best Practices

### Backend
1. **Input Validation**: Always validate user input
2. **File Uploads**: Whitelist extensions, check size
3. **CORS**: Configure properly for production
4. **HTTPS**: Use in production

### Android
1. **Network Security**: Use network security config
2. **Permissions**: Request at runtime
3. **API Keys**: Store in BuildConfig, not code
4. **ProGuard**: Enable for release builds

---

## Troubleshooting

### Backend won't start
- Check port 5000 is not in use
- Verify .NET SDK is installed
- Check for compilation errors

### Android app crashes
- Check Logcat for stack traces
- Verify permissions are granted
- Ensure backend is running and accessible

### Cannot connect to backend
- **Emulator**: Use `10.0.2.2` instead of `localhost`
- **Device**: Use computer's IP address
- Check firewall settings
- Verify backend is running

### Barcode scanning not working
- Camera permission granted?
- Good lighting conditions?
- Barcode clearly visible?
- Supported barcode format?

---

## Code Style Guidelines

### Backend (C#)
- Use PascalCase for public members
- Use camelCase for private members
- Add XML comments for public APIs
- Follow Microsoft C# conventions

### Android (Kotlin)
- Use camelCase for variables
- Use PascalCase for classes
- One class per file
- Follow Kotlin coding conventions

---

## Resources

### Backend
- [ASP.NET Core Docs](https://docs.microsoft.com/aspnet/core)
- [Entity Framework Core](https://docs.microsoft.com/ef/core)
- [REST API Guidelines](https://restfulapi.net/)

### Android
- [Android Developers](https://developer.android.com)
- [Kotlin Docs](https://kotlinlang.org/docs)
- [CameraX](https://developer.android.com/training/camerax)
- [ML Kit](https://developers.google.com/ml-kit)
- [Retrofit](https://square.github.io/retrofit/)

---

## Contributing

1. Follow the existing code style
2. Add appropriate error handling
3. Update documentation
4. Test thoroughly before committing
5. Write meaningful commit messages

---

## Support

For questions or issues:
- Review this guide
- Check the README.md
- Examine existing code examples
- Review API documentation
