const express = require('express');
const router = express.Router();
const ActivityLog = require('../models/ActivityLog');

// Get all activity logs
router.get('/', async (req, res) => {
  try {
    const { userId, limit = 50 } = req.query;
    
    let query = {};
    if (userId) query.userId = userId;
    
    const activities = await ActivityLog.find(query)
      .populate('userId', 'name email role')
      .sort({ timestamp: -1 })
      .limit(parseInt(limit));
    
    res.json(activities);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Create activity log
router.post('/', async (req, res) => {
  try {
    const activity = new ActivityLog(req.body);
    await activity.save();
    
    const populatedActivity = await ActivityLog.findById(activity._id)
      .populate('userId', 'name email role');
    
    res.status(201).json(populatedActivity);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;




