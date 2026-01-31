# Quick Start Guide

## Start Backend Server

```bash
cd Backend
dotnet run --urls "http://0.0.0.0:5000"
```

Backend will be available at: `http://localhost:5000`

---

## Test API Endpoints

### Get all orders
```bash
curl http://localhost:5000/api/orders
```

### Get order by barcode
```bash
curl http://localhost:5000/api/orders/barcode/123456789
```

### Report arrival
```bash
curl -X POST http://localhost:5000/api/orders/1/arrival
```

### Report completion
```bash
curl -X POST http://localhost:5000/api/orders/1/completion
```

### Close order
```bash
curl -X POST http://localhost:5000/api/orders/1/close
```

### Upload photo
```bash
curl -X POST http://localhost:5000/api/orders/1/photos \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "test.jpg",
    "base64Data": "your-base64-encoded-image-data"
  }'
```

---

## Sample Barcodes for Testing

- **123456789** - Office Supplies - Box of Pens (Pending)
- **987654321** - Computer Equipment - Laptop (Pending)
- **555666777** - Furniture - Office Chairs (Arrived)

---

## Build Android App

```bash
cd AndroidApp
./gradlew build
```

## Install on Device/Emulator

```bash
./gradlew installDebug
```

Or open in Android Studio and click Run.

---

## Configure Android for Your Environment

### For Android Emulator
No changes needed. Uses `http://10.0.2.2:5000/`

### For Physical Device
Edit `AndroidApp/app/src/main/java/com/incominggoods/app/api/ApiClient.kt`:

Change:
```kotlin
private const val BASE_URL = "http://10.0.2.2:5000/"
```

To:
```kotlin
private const val BASE_URL = "http://YOUR_COMPUTER_IP:5000/"
```

Replace `YOUR_COMPUTER_IP` with your actual IP address (e.g., 192.168.1.100)

---

## Common Issues

### Cannot connect to backend from Android
- **Emulator**: Must use `10.0.2.2` not `localhost`
- **Device**: Must use computer's IP address
- Check backend is running
- Check firewall allows connections on port 5000

### Barcode scanning not working
- Grant camera permission
- Ensure good lighting
- Barcode should be clear and in focus
- Supported formats: TEXT, PRODUCT, ISBN

### App crashes on photo upload
- Grant camera permission
- Ensure backend is running
- Check file size is under 10MB

---

## File Locations

### Backend
- **API Code**: `Backend/Controllers/OrdersController.cs`
- **Models**: `Backend/Models/`
- **Database**: In-memory (resets on restart)
- **Uploaded Photos**: `Backend/uploads/`

### Android
- **Main Scanner**: `AndroidApp/app/src/main/java/com/incominggoods/app/MainActivity.kt`
- **Order Details**: `AndroidApp/app/src/main/java/com/incominggoods/app/OrderDetailsActivity.kt`
- **API Client**: `AndroidApp/app/src/main/java/com/incominggoods/app/api/`

---

## Documentation

- **README.md** - Complete setup and usage guide
- **IMPLEMENTATION_SUMMARY.md** - Overview of what was implemented
- **DEVELOPER_GUIDE.md** - Detailed development guide
- **Backend/IncomingGoodsBackend.http** - API test file

---

## Support

Questions? Check the documentation files listed above!
