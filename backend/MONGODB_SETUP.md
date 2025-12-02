# MongoDB Setup Guide

## Issue
The server is trying to connect to MongoDB but can't find it running.

## Solution Options

### Option 1: Install MongoDB Locally (Windows)

1. **Download MongoDB Community Server**
   - Visit: https://www.mongodb.com/try/download/community
   - Select Windows as your platform
   - Download and run the installer

2. **Install MongoDB**
   - Run the installer (msi file)
   - Choose "Complete" installation
   - Select "Install MongoDB as a Service"
   - Default port: 27017

3. **Verify Installation**
   - Open Command Prompt/PowerShell
   - Run: `mongod --version`
   - Check if MongoDB service is running:
     ```
     Get-Service MongoDB
     ```

4. **Start MongoDB Service** (if not running)
   ```
   net start MongoDB
   ```
   Or from Services: Search "Services" → Find "MongoDB" → Start

5. **Test Connection**
   - Open a new terminal
   - Run: `mongosh` (or `mongo` on older versions)
   - You should see MongoDB shell

### Option 2: Use MongoDB Atlas (Cloud - Recommended for Development)

MongoDB Atlas is free and doesn't require local installation.

1. **Create Account**
   - Visit: https://www.mongodb.com/cloud/atlas/register
   - Sign up for free account

2. **Create Cluster**
   - Click "Build a Database"
   - Choose FREE tier (M0)
   - Select a cloud provider and region
   - Create cluster (takes a few minutes)

3. **Create Database User**
   - Go to "Database Access" → "Add New Database User"
   - Create username and password
   - Save credentials securely

4. **Whitelist IP Address**
   - Go to "Network Access" → "Add IP Address"
   - Click "Allow Access from Anywhere" (for development) OR
   - Add your current IP address

5. **Get Connection String**
   - Go to "Database" → Click "Connect" on your cluster
   - Choose "Connect your application"
   - Copy the connection string
   - Replace `<password>` with your database user password
   - Replace `<database>` with `agrochain`

6. **Update Your .env File**
   - Create a `.env` file in the `backend` folder (copy from `.env.example` if exists)
   - Add your MongoDB Atlas connection string:
     ```
     MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/agrochain?retryWrites=true&w=majority
     ```

### Option 3: Use Docker (If you have Docker installed)

```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

## Verify Setup

After setting up MongoDB, restart your server:

```bash
cd backend
npm start
```

You should see:
```
✅ Connected to MongoDB
```

## Check Connection Status

Visit: http://localhost:3000/health

You'll see MongoDB connection status in the response.

## Troubleshooting

### MongoDB not starting on Windows
- Check if port 27017 is already in use:
  ```
  netstat -ano | findstr :27017
  ```
- Check Windows Event Viewer for MongoDB errors
- Make sure MongoDB service has proper permissions

### Connection timeout
- Check firewall settings
- Verify MongoDB is listening on port 27017:
  ```
  netstat -ano | findstr :27017
  ```

### For MongoDB Atlas
- Make sure your IP is whitelisted
- Verify username and password are correct
- Check if cluster is running (not paused)


