const express = require('express');
const router = express.Router();
const Listing = require('../models/Listing');

// Get all listings
router.get('/', async (req, res) => {
  try {
    const { type, location, search, sortBy = 'createdAt', sortOrder = 'desc' } = req.query;
    
    let query = {};
    
    if (type) {
      query.type = new RegExp(type, 'i');
    }
    
    if (location) {
      query.location = new RegExp(location, 'i');
    }
    
    if (search) {
      query.$text = { $search: search };
    }
    
    const sortOptions = {};
    sortOptions[sortBy] = sortOrder === 'desc' ? -1 : 1;
    
    const listings = await Listing.find(query)
      .populate('ownerId', 'name email role')
      .sort(sortOptions);
    
    res.json(listings);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get listing by ID
router.get('/:id', async (req, res) => {
  try {
    const listing = await Listing.findById(req.params.id)
      .populate('ownerId', 'name email role');
    
    if (!listing) {
      return res.status(404).json({ error: 'Listing not found' });
    }
    
    res.json(listing);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Create listing
router.post('/', async (req, res) => {
  try {
    const listing = new Listing(req.body);
    await listing.save();
    
    const populatedListing = await Listing.findById(listing._id)
      .populate('ownerId', 'name email role');
    
    res.status(201).json(populatedListing);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Update listing
router.put('/:id', async (req, res) => {
  try {
    const listing = await Listing.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true, runValidators: true }
    ).populate('ownerId', 'name email role');
    
    if (!listing) {
      return res.status(404).json({ error: 'Listing not found' });
    }
    
    res.json(listing);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Delete listing
router.delete('/:id', async (req, res) => {
  try {
    const listing = await Listing.findByIdAndDelete(req.params.id);
    
    if (!listing) {
      return res.status(404).json({ error: 'Listing not found' });
    }
    
    res.json({ message: 'Listing deleted successfully' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get listings by owner
router.get('/owner/:ownerId', async (req, res) => {
  try {
    const listings = await Listing.find({ ownerId: req.params.ownerId })
      .populate('ownerId', 'name email role')
      .sort({ createdAt: -1 });
    
    res.json(listings);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;




