const express = require('express');
const router = express.Router();
const Contract = require('../models/Contract');

// Get all contracts
router.get('/', async (req, res) => {
  try {
    const { farmerId, buyerId, transporterId, status } = req.query;
    
    let query = {};
    if (farmerId) query.farmerId = farmerId;
    if (buyerId) query.buyerId = buyerId;
    if (transporterId) query.transporterId = transporterId;
    if (status) query.status = status;
    
    const contracts = await Contract.find(query)
      .populate('listingId')
      .populate('farmerId', 'name email role')
      .populate('buyerId', 'name email role')
      .populate('transporterId', 'name email role')
      .populate('offerId')
      .sort({ updatedAt: -1 });
    
    res.json(contracts);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get contract by ID
router.get('/:id', async (req, res) => {
  try {
    const contract = await Contract.findById(req.params.id)
      .populate('listingId')
      .populate('farmerId', 'name email role')
      .populate('buyerId', 'name email role')
      .populate('transporterId', 'name email role')
      .populate('offerId');
    
    if (!contract) {
      return res.status(404).json({ error: 'Contract not found' });
    }
    
    res.json(contract);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Create contract
router.post('/', async (req, res) => {
  try {
    const contract = new Contract(req.body);
    await contract.save();
    
    const populatedContract = await Contract.findById(contract._id)
      .populate('listingId')
      .populate('farmerId', 'name email role')
      .populate('buyerId', 'name email role')
      .populate('transporterId', 'name email role')
      .populate('offerId');
    
    res.status(201).json(populatedContract);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Update contract
router.put('/:id', async (req, res) => {
  try {
    req.body.updatedAt = new Date();
    const contract = await Contract.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true, runValidators: true }
    )
      .populate('listingId')
      .populate('farmerId', 'name email role')
      .populate('buyerId', 'name email role')
      .populate('transporterId', 'name email role')
      .populate('offerId');
    
    if (!contract) {
      return res.status(404).json({ error: 'Contract not found' });
    }
    
    res.json(contract);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Update contract status
router.patch('/:id/status', async (req, res) => {
  try {
    const { status, escrowLocked } = req.body;
    const updateData = {
      status,
      updatedAt: new Date()
    };
    
    if (escrowLocked !== undefined) {
      updateData.escrowLocked = escrowLocked;
    }
    
    const contract = await Contract.findByIdAndUpdate(
      req.params.id,
      updateData,
      { new: true, runValidators: true }
    )
      .populate('listingId')
      .populate('farmerId', 'name email role')
      .populate('buyerId', 'name email role')
      .populate('transporterId', 'name email role')
      .populate('offerId');
    
    if (!contract) {
      return res.status(404).json({ error: 'Contract not found' });
    }
    
    res.json(contract);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;




