# Andro1 - Incoming Goods Management System

A modern Android application with ASP.NET MVC backend for recording incoming goods and tracking them through company processes.

## Features

### Android App
- **Barcode Scanning**: Use the camera to scan barcodes and automatically display order information
- **Order Management**:
  - Report arrival of goods
  - Report completion of processing
  - Close orders
  - Take and upload photos to orders
- **Real-time Updates**: View order status, arrival dates, and completion dates

### Backend (ASP.NET MVC Web API)
- RESTful API for order management
- In-memory database with sample data
- Photo upload support with Base64 encoding
- CORS enabled for Android app communication

## Project Structure

```
Andro1/
├── Backend/                    # ASP.NET MVC Web API
│   ├── Controllers/           # API Controllers
│   ├── Models/                # Data models
│   ├── DTOs/                  # Data transfer objects
│   ├── Data/                  # Database context
│   └── Program.cs             # Application entry point
│
└── AndroidApp/                # Android Application
    ├── app/
    │   ├── src/main/
    │   │   ├── java/com/incominggoods/app/
    │   │   │   ├── models/    # Data models
    │   │   │   ├── api/       # API client
    │   │   │   ├── MainActivity.kt
    │   │   │   └── OrderDetailsActivity.kt
    │   │   ├── res/           # Resources (layouts, strings, etc.)
    │   │   └── AndroidManifest.xml
    │   └── build.gradle       # App dependencies
    └── build.gradle           # Project configuration
```

## Setup Instructions

### Prerequisites
- .NET 10 SDK
- Android Studio or Gradle
- Android SDK (API 24 or higher)
- Physical Android device or emulator

### Backend Setup

1. Navigate to the Backend directory:
```bash
cd Backend
```

2. Restore dependencies:
```bash
dotnet restore
```

3. Build the project:
```bash
dotnet build
```

4. Run the backend server:
```bash
dotnet run
```

The server will start on `http://localhost:5000` (or the port shown in the console).

### Android App Setup

1. Navigate to the AndroidApp directory:
```bash
cd AndroidApp
```

2. Update the API base URL in `app/src/main/java/com/incominggoods/app/api/ApiClient.kt`:
   - For Android Emulator: Use `http://10.0.2.2:5000/`
   - For Physical Device: Use your computer's IP address (e.g., `http://192.168.1.100:5000/`)

3. Build the app:
```bash
./gradlew build
```

4. Install on device/emulator:
```bash
./gradlew installDebug
```

Or open the project in Android Studio and run it from there.

## Usage

### Sample Barcodes

The backend includes sample data with the following barcodes you can test:

- `123456789` - Office Supplies - Box of Pens
- `987654321` - Computer Equipment - Laptop
- `555666777` - Furniture - Office Chairs (Set of 5) [Already arrived]

### Using the App

1. **Launch the app**: The camera will open automatically for barcode scanning
2. **Scan a barcode**: Point the camera at a barcode (you can display one on another screen)
3. **View order details**: Order information will be displayed automatically
4. **Manage the order**:
   - Tap "Report Arrival" to mark goods as arrived
   - Tap "Report Completion" to mark processing as complete (only after arrival)
   - Tap "Close Order" to close the order
   - Tap "Take and Upload Photo" to capture and upload a photo

### API Endpoints

- `GET /api/orders/barcode/{barcode}` - Get order by barcode
- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders/{id}/arrival` - Report arrival
- `POST /api/orders/{id}/completion` - Report completion
- `POST /api/orders/{id}/close` - Close order
- `POST /api/orders/{id}/photos` - Upload photo

## Technology Stack

### Backend
- ASP.NET Core 10.0
- Entity Framework Core (In-Memory Database)
- RESTful API design
- CORS support

### Android
- Kotlin
- AndroidX libraries
- CameraX for camera functionality
- ML Kit for barcode scanning
- Retrofit for API communication
- Coroutines for async operations
- Material Design components

## Security & Permissions

The Android app requires the following permissions:
- `CAMERA` - For barcode scanning and taking photos
- `INTERNET` - For API communication
- `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE` - For photo handling

## Development Notes

- The backend uses an in-memory database, so data is lost when the server restarts
- Photos are stored in the `Backend/uploads` directory
- The app uses `usesCleartextTraffic="true"` for HTTP communication (development only)
- For production, use HTTPS and proper security measures

## Testing

You can test the backend API using tools like:
- Postman
- cURL
- The included `IncomingGoodsBackend.http` file (if using VS Code with REST Client extension)

Example cURL commands:
```bash
# Get order by barcode
curl http://localhost:5000/api/orders/barcode/123456789

# Report arrival
curl -X POST http://localhost:5000/api/orders/1/arrival

# Report completion
curl -X POST http://localhost:5000/api/orders/1/completion
```

## Future Enhancements

- Persistent database (SQL Server, PostgreSQL)
- User authentication and authorization
- Offline mode support
- Advanced photo gallery
- Order search and filtering
- Barcode generation
- Export/Report functionality
- Push notifications
