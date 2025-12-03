const express = require('express');
const router = express.Router();
const Listing = require('../models/Listing');

// Return the most recent listing (for debugging)
router.get('/latest-listing', async (req, res) => {
  try {
    const listing = await Listing.findOne()
      .populate('ownerId', 'name email role')
      .sort({ createdAt: -1 });

    if (!listing) {
      return res.status(404).json({ error: 'No listings found' });
    }

    res.json(listing);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;
