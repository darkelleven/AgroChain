const mongoose = require('mongoose');

const chatSchema = new mongoose.Schema({
  participant1Id: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  participant2Id: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  listingId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Listing',
    default: null
  },
  lastMessage: {
    type: String,
    default: null
  },
  lastMessageTime: {
    type: Date,
    default: null
  },
  unreadCount: {
    type: Number,
    default: 0
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('Chat', chatSchema);




