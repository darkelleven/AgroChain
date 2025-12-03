const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();

const app = express();

// Global error handlers to surface silent crashes
process.on('uncaughtException', (err) => {
  console.error('💥 Uncaught Exception:', err && err.stack ? err.stack : err);
});

process.on('unhandledRejection', (reason, promise) => {
  console.error('💥 Unhandled Rejection at:', promise, 'reason:', reason);
});

// Middleware
app.use(cors({
  origin: '*', // Allow all origins for development
  credentials: true
}));

// Request logging middleware
app.use((req, res, next) => {
  console.log(`[${new Date().toISOString()}] ${req.method} ${req.path} - IP: ${req.ip || req.connection.remoteAddress}`);
  next();
});

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// MongoDB Connection
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/agrochain';
let isMongoConnected = false;

// Export connection status for use in routes
app.locals.isMongoConnected = () => isMongoConnected;

// Connect to MongoDB with retry logic
const connectMongoDB = async () => {
  try {
    // Remove deprecated options `useNewUrlParser` and `useUnifiedTopology`.
    // The MongoDB Node.js driver and Mongoose v6+ handle these by default.
    await mongoose.connect(MONGODB_URI, {
      serverSelectionTimeoutMS: 5000, // Timeout after 5s instead of 30s
    });
    isMongoConnected = true;
    console.log('✅ Connected to MongoDB');
    console.log(`   Database: ${MONGODB_URI.replace(/\/\/.*@/, '//***@')}`);
  } catch (error) {
    isMongoConnected = false;
    console.error('❌ MongoDB connection error:', error.message);
    console.warn('⚠️  Server will continue without MongoDB connection');
    console.warn('⚠️  API endpoints requiring database will fail until MongoDB is available');
    console.warn('\n📝 To fix this:');
    console.warn('   1. Install MongoDB: https://www.mongodb.com/try/download/community');
    console.warn('   2. Or use MongoDB Atlas (cloud): https://www.mongodb.com/cloud/atlas');
    console.warn('   3. Or set MONGODB_URI in .env file\n');
    
    // Retry connection every 10 seconds
    setTimeout(() => {
      console.log('🔄 Retrying MongoDB connection...');
      connectMongoDB();
    }, 10000);
  }
};

// Handle MongoDB disconnection
mongoose.connection.on('disconnected', () => {
  isMongoConnected = false;
  console.warn('⚠️  MongoDB disconnected. Attempting to reconnect...');
  setTimeout(connectMongoDB, 5000);
});

mongoose.connection.on('reconnected', () => {
  isMongoConnected = true;
  console.log('✅ MongoDB reconnected');
});

// Start MongoDB connection
connectMongoDB();

// Routes
app.use('/api/auth', require('./routes/auth'));
app.use('/api/users', require('./routes/users'));
app.use('/api/listings', require('./routes/listings'));
app.use('/api/offers', require('./routes/offers'));
app.use('/api/contracts', require('./routes/contracts'));
app.use('/api/notifications', require('./routes/notifications'));
app.use('/api/chats', require('./routes/chats'));
app.use('/api/activity', require('./routes/activity'));
// Debug routes (development only)
app.use('/api/debug', require('./routes/debug'));

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ 
    status: 'OK', 
    message: 'AgroChain Backend Server is running',
    timestamp: new Date().toISOString(),
    mongodb: {
      connected: isMongoConnected,
      status: isMongoConnected ? 'connected' : 'disconnected'
    },
    clientIp: req.ip || req.connection.remoteAddress
  });
});

// Connection test endpoint
app.get('/test-connection', (req, res) => {
  res.json({
    success: true,
    message: 'Connection successful!',
    timestamp: new Date().toISOString(),
    serverIp: req.socket.localAddress,
    clientIp: req.ip || req.connection.remoteAddress,
    host: req.get('host'),
    userAgent: req.get('user-agent')
  });
});

// Root endpoint
app.get('/', (req, res) => {
  res.json({ 
    message: 'Welcome to AgroChain Backend API',
    version: '1.0.0',
    endpoints: {
      health: '/health',
      auth: '/api/auth',
      users: '/api/users',
      listings: '/api/listings',
      offers: '/api/offers',
      contracts: '/api/contracts',
      notifications: '/api/notifications',
      chats: '/api/chats',
      activity: '/api/activity'
    }
  });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(err.status || 500).json({
    error: {
      message: err.message || 'Internal Server Error',
      status: err.status || 500
    }
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({ error: 'Route not found' });
});

const PORT = process.env.PORT || 3000;
const HOST = process.env.HOST || '0.0.0.0'; // Listen on all interfaces for emulator access

const server = app.listen(PORT, HOST, () => {
  console.log(`🚀 Server is running on ${HOST}:${PORT}`);
  console.log(`📍 API Base URL: http://localhost:${PORT}`);
  console.log(`📍 Emulator URL: http://10.0.2.2:${PORT}`);
  console.log(`📍 Test endpoint: http://localhost:${PORT}/test-connection`);
  console.log(`\n⚠️  If connection fails, check:`);
  console.log(`   1. Windows Firewall allows connections on port ${PORT}`);
  console.log(`   2. No other service is using port ${PORT}`);
  console.log(`   3. Server is restarted after this change\n`);
});

// Handle server errors
server.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.error(`❌ Port ${PORT} is already in use. Please stop the other service or use a different port.`);
  } else {
    console.error('❌ Server error:', err);
  }
  process.exit(1);
});

// Log when a client connects
server.on('connection', (socket) => {
  console.log(`🔌 New connection from ${socket.remoteAddress}:${socket.remotePort}`);
});

module.exports = app;



