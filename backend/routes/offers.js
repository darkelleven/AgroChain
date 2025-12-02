const express = require('express');
const router = express.Router();
const Offer = require('../models/Offer');
const Listing = require('../models/Listing');

// Get all offers
router.get('/', async (req, res) => {
  try {
    const { listingId, buyerId, status } = req.query;
    
    let query = {};
    if (listingId) query.listingId = listingId;
    if (buyerId) query.buyerId = buyerId;
    if (status) query.status = status;
    
    const offers = await Offer.find(query)
      .populate('listingId')
      .populate('buyerId', 'name email role')
      .sort({ createdAt: -1 });
    
    res.json(offers);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get offer by ID
router.get('/:id', async (req, res) => {
  try {
    const offer = await Offer.findById(req.params.id)
      .populate('listingId')
      .populate('buyerId', 'name email role');
    
    if (!offer) {
      return res.status(404).json({ error: 'Offer not found' });
    }
    
    res.json(offer);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Create offer
router.post('/', async (req, res) => {
  try {
    const offer = new Offer(req.body);
    await offer.save();
    
    const populatedOffer = await Offer.findById(offer._id)
      .populate('listingId')
      .populate('buyerId', 'name email role');
    
    res.status(201).json(populatedOffer);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Update offer status
router.patch('/:id/status', async (req, res) => {
  try {
    const { status } = req.body;
    const offer = await Offer.findByIdAndUpdate(
      req.params.id,
      { status },
      { new: true, runValidators: true }
    )
      .populate('listingId')
      .populate('buyerId', 'name email role');
    
    if (!offer) {
      return res.status(404).json({ error: 'Offer not found' });
    }
    
    res.json(offer);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get offers for a listing
router.get('/listing/:listingId', async (req, res) => {
  try {
    const offers = await Offer.find({ listingId: req.params.listingId })
      .populate('buyerId', 'name email role')
      .sort({ createdAt: -1 });
    
    res.json(offers);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;




