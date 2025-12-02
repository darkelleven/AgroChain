const mongoose = require('mongoose');

const contractSchema = new mongoose.Schema({
  listingId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Listing',
    required: true
  },
  farmerId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  buyerId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  offerId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Offer',
    required: true
  },
  totalValue: {
    type: Number,
    required: true,
    min: 0
  },
  status: {
    type: String,
    enum: ['OFFERED', 'ESCROW_LOCKED', 'COMPLETED', 'RELEASED', 'CANCELLED'],
    default: 'OFFERED'
  },
  escrowLocked: {
    type: Boolean,
    default: false
  },
  transporterId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    default: null
  },
  transporterAccepted: {
    type: Boolean,
    default: null
  },
  updatedAt: {
    type: Date,
    default: Date.now
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('Contract', contractSchema);




