const mongoose = require('mongoose');

const listingSchema = new mongoose.Schema({
  ownerId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  ownerRole: {
    type: String,
    enum: ['FARMER', 'TRANSPORTER', 'BUYER', 'ADMIN'],
    required: true
  },
  type: {
    type: String,
    required: true,
    trim: true
  },
  quantityTons: {
    type: Number,
    required: true,
    min: 0
  },
  quality: {
    type: String,
    required: true,
    trim: true
  },
  priceExpectationPerTon: {
    type: Number,
    required: true,
    min: 0
  },
  location: {
    type: String,
    required: true,
    trim: true
  },
  description: {
    type: String,
    default: ''
  },
  imageUrl: {
    type: String,
    default: ''
  },
  moistureContent: {
    type: String,
    default: ''
  },
  proteinContent: {
    type: String,
    default: ''
  },
  storageCondition: {
    type: String,
    default: ''
  },
  packaging: {
    type: String,
    default: ''
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Index for search
listingSchema.index({ type: 'text', location: 'text', description: 'text' });

module.exports = mongoose.model('Listing', listingSchema);




