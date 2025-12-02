# AgroChain Backend Server

Backend API server for AgroChain Android application built with Node.js, Express, and MongoDB.

## Features

- ✅ RESTful API endpoints
- ✅ MongoDB database integration
- ✅ User authentication (JWT)
- ✅ CRUD operations for all entities
- ✅ Real-time notifications
- ✅ Chat system
- ✅ Activity logging

## Tech Stack

### Backend
- **Runtime:** Node.js
- **Framework:** Express.js 4.18.2
- **Database:** MongoDB (with Mongoose ODM 8.0.3)
- **Authentication:** JSON Web Tokens (JWT) via jsonwebtoken 9.0.2
- **Password Hashing:** bcryptjs 2.4.3
- **Validation:** express-validator 7.0.1
- **CORS:** cors 2.8.5
- **Environment Variables:** dotenv 16.3.1
- **Development Tools:** nodemon 3.0.2 (auto-reload during development)

### Frontend (Android App)
- **Language:** Kotlin 1.9.23
- **UI Framework:** Jetpack Compose with Material Design 3
- **Architecture:**
  - MVVM (Model-View-ViewModel) pattern
  - Jetpack Compose Navigation 2.8.3
  - Lifecycle-aware components
- **Async Operations:** Kotlin Coroutines 1.8.1
- **Android SDK:**
  - Minimum SDK: 26 (Android 8.0)
  - Target SDK: 34 (Android 14)
  - Compile SDK: 34
- **Build Tools:**
  - Android Gradle Plugin 8.13.1
  - Kotlin Compiler Extension 1.5.11
  - JDK 17

### Database & Storage
- **Primary Database:** MongoDB
  - Document-based NoSQL database
  - Flexible schema for agricultural marketplace data
  - Supports complex queries and relationships

### Deployment & Infrastructure
- **Backend Deployment Options:**
  - Railway
  - Heroku
  - Render
  - MongoDB Atlas (cloud database)

### Security
- JWT-based authentication
- Bcrypt password hashing (salt rounds)
- CORS configuration
- Request validation middleware
- Environment variable management

### Development Tools
- **Backend:**
  - nodemon for auto-reload
  - dotenv for configuration management
- **Frontend:**
  - Android Studio
  - Gradle build system
  - Kotlin LSP support

## Prerequisites

- Node.js (v14 or higher)
- MongoDB (local or MongoDB Atlas)
- npm or yarn

## Installation

1. **Clone or navigate to the backend directory:**
   ```bash
   cd backend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Set up environment variables:**
   ```bash
   cp .env.example .env
   ```
   
   Edit `.env` and add your MongoDB connection string:
   ```env
   MONGODB_URI=mongodb://localhost:27017/agrochain
   PORT=3000
   JWT_SECRET=your-super-secret-jwt-key
   NODE_ENV=development
   ```

4. **Start MongoDB (if using local):**
   ```bash
   # On macOS/Linux
   mongod
   
   # On Windows
   mongod.exe
   ```

5. **Run the server:**
   ```bash
   # Development mode (with auto-reload)
   npm run dev
   
   # Production mode
   npm start
   ```

The server will start on `http://localhost:3000`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user

### Users
- `GET /api/users` - Get all users
- `GET /api/users/:id` - Get user by ID
- `PATCH /api/users/:id/verification` - Update user verification status

### Listings
- `GET /api/listings` - Get all listings (with filters: type, location, search, sortBy, sortOrder)
- `GET /api/listings/:id` - Get listing by ID
- `POST /api/listings` - Create new listing
- `PUT /api/listings/:id` - Update listing
- `DELETE /api/listings/:id` - Delete listing
- `GET /api/listings/owner/:ownerId` - Get listings by owner

### Offers
- `GET /api/offers` - Get all offers (with filters: listingId, buyerId, status)
- `GET /api/offers/:id` - Get offer by ID
- `POST /api/offers` - Create new offer
- `PATCH /api/offers/:id/status` - Update offer status
- `GET /api/offers/listing/:listingId` - Get offers for a listing

### Contracts
- `GET /api/contracts` - Get all contracts (with filters: farmerId, buyerId, transporterId, status)
- `GET /api/contracts/:id` - Get contract by ID
- `POST /api/contracts` - Create new contract
- `PUT /api/contracts/:id` - Update contract
- `PATCH /api/contracts/:id/status` - Update contract status

### Notifications
- `GET /api/notifications/user/:userId` - Get notifications for user (with filter: read)
- `POST /api/notifications` - Create notification
- `PATCH /api/notifications/:id/read` - Mark notification as read
- `PATCH /api/notifications/user/:userId/read-all` - Mark all notifications as read
- `DELETE /api/notifications/:id` - Delete notification

### Chats
- `GET /api/chats/user/:userId` - Get all chats for user
- `POST /api/chats` - Get or create chat
- `GET /api/chats/:chatId/messages` - Get messages for chat
- `POST /api/chats/:chatId/messages` - Send message
- `PATCH /api/chats/:chatId/read` - Mark chat as read

### Activity Logs
- `GET /api/activity` - Get activity logs (with filters: userId, limit)
- `POST /api/activity` - Create activity log

### Health Check
- `GET /health` - Server health check
- `GET /` - API information

## Example API Requests

### Register User
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "FARMER"
  }'
```

### Login
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123",
    "role": "FARMER"
  }'
```

### Create Listing
```bash
curl -X POST http://localhost:3000/api/listings \
  -H "Content-Type: application/json" \
  -d '{
    "ownerId": "user_id_here",
    "ownerRole": "FARMER",
    "type": "Mustard Husk",
    "quantityTons": 8.5,
    "quality": "Grade A",
    "priceExpectationPerTon": 8200,
    "location": "Jaipur, Rajasthan",
    "description": "Premium quality mustard husk"
  }'
```

## Deployment Options

### Option 1: Railway (Recommended for Quick Deployment)

1. **Install Railway CLI:**
   ```bash
   npm install -g @railway/cli
   ```

2. **Login to Railway:**
   ```bash
   railway login
   ```

3. **Initialize project:**
   ```bash
   railway init
   ```

4. **Add MongoDB service:**
   - Go to Railway dashboard
   - Click "New" → "Database" → "MongoDB"
   - Copy the connection string

5. **Set environment variables:**
   ```bash
   railway variables set MONGODB_URI=${{ MongoDB.MONGO_URL }}
   railway variables set JWT_SECRET=x7z9#mK2$pL9
   railway variables set PORT=3000
   ```

6. **Deploy:**
   ```bash
   railway up
   ```

### Option 2: Heroku

1. **Install Heroku CLI:**
   - Download from https://devcenter.heroku.com/articles/heroku-cli

2. **Login:**
   ```bash
   heroku login
   ```

3. **Create app:**
   ```bash
   heroku create agrochain-backend
   ```

4. **Add MongoDB Atlas:**
   - Go to https://www.mongodb.com/cloud/atlas
   - Create free cluster
   - Get connection string
   - Add to Heroku config vars

5. **Set environment variables:**
   ```bash
   heroku config:set MONGODB_URI=your_mongodb_atlas_connection_string
   heroku config:set JWT_SECRET=your-secret-key
   ```

6. **Deploy:**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   heroku git:remote -a agrochain-backend
   git push heroku main
   ```

### Option 3: Render

1. **Create account at https://render.com**

2. **Create new Web Service:**
   - Connect your GitHub repository
   - Build command: `npm install`
   - Start command: `npm start`

3. **Add MongoDB:**
   - Create MongoDB database on Render
   - Copy connection string

4. **Set environment variables in Render dashboard:**
   - `MONGODB_URI`
   - `JWT_SECRET`
   - `PORT` (auto-set by Render)

### Option 4: MongoDB Atlas Setup

1. **Create MongoDB Atlas account:**
   - Go to https://www.mongodb.com/cloud/atlas
   - Sign up for free tier

2. **Create cluster:**
   - Choose free M0 tier
   - Select region closest to you

3. **Configure database access:**
   - Go to "Database Access"
   - Create database user
   - Set username and password

4. **Configure network access:**
   - Go to "Network Access"
   - Add IP address (0.0.0.0/0 for all, or your server IP)

5. **Get connection string:**
   - Go to "Database" → "Connect"
   - Choose "Connect your application"
   - Copy connection string
   - Replace `<password>` with your database password

6. **Update .env:**
   ```env
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/agrochain?retryWrites=true&w=majority
   ```

## Connecting Android App to Backend

1. **Update Android app to use Retrofit or Ktor for API calls**

2. **Add base URL in Android app:**
   ```kotlin
   const val BASE_URL = "https://your-backend-url.com/api/"
   ```

3. **Create API service interface**

4. **Update ViewModel to fetch data from API instead of local state**

## Testing

Test the API using:
- Postman
- cURL
- Thunder Client (VS Code extension)
- Your Android app

## Troubleshooting

### MongoDB Connection Issues
- Check if MongoDB is running (local)
- Verify connection string format
- Check network access (MongoDB Atlas)
- Verify credentials

### Port Already in Use
- Change PORT in .env
- Kill process using the port: `lsof -ti:3000 | xargs kill`

### CORS Issues
- CORS is enabled for all origins in development
- For production, update CORS settings in `server.js`

## License

ISC


