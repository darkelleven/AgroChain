# Quick Start Guide

## Current Status

Your server is now configured to:
- ✅ Start even if MongoDB is not running
- ✅ Automatically retry MongoDB connection every 10 seconds
- ✅ Log connection status in the `/health` endpoint
- ✅ Listen on all network interfaces (accessible from Android emulator)

## Next Steps

### Option 1: Start Server Without MongoDB (Testing Connection Only)

The server will start but API endpoints requiring database will fail:

```bash
cd backend
npm start
```

You should see:
```
⚠️  Server will continue without MongoDB connection
🚀 Server is running on 0.0.0.0:3000
```

**Test the connection from Android emulator:**
- URL: `http://10.0.2.2:3000/test-connection`
- This will work even without MongoDB!

### Option 2: Set Up MongoDB (Required for Full Functionality)

**Easiest: Use MongoDB Atlas (Cloud - Free)**

1. Sign up at: https://www.mongodb.com/cloud/atlas/register
2. Create a free cluster (takes ~5 minutes)
3. Create database user
4. Whitelist your IP (or use 0.0.0.0/0 for testing)
5. Get connection string
6. Create `backend/.env` file:
   ```
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/agrochain
   ```
7. Restart server

**Or Install MongoDB Locally:**

See `MONGODB_SETUP.md` for detailed instructions.

## Verify Everything Works

1. **Check server status:**
   ```
   http://localhost:3000/health
   ```
   
   Response should show:
   ```json
   {
     "status": "OK",
     "mongodb": {
       "connected": true/false,
       "status": "connected" or "disconnected"
     }
   }
   ```

2. **Test from Android emulator:**
   ```
   http://10.0.2.2:3000/test-connection
   ```

3. **Check server logs:**
   - You should see connection logs when requests arrive
   - Look for: `🔌 New connection from ...`

## Troubleshooting

- **Connection timeout:** See `troubleshoot-connection.ps1` script
- **MongoDB errors:** See `MONGODB_SETUP.md`
- **Port in use:** Change PORT in `.env` file or stop other service

## Important Notes

- The server now starts without MongoDB (for connection testing)
- API endpoints that need database will return errors until MongoDB is connected
- Server automatically retries MongoDB connection every 10 seconds
- You can test network connectivity even without MongoDB!


