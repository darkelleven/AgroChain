const mongoose = require('mongoose');

const activityLogSchema = new mongoose.Schema({
  description: {
    type: String,
    required: true
  },
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    default: null
  },
  timestamp: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('ActivityLog', activityLogSchema);




