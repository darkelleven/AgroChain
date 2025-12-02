const express = require('express');
const router = express.Router();
const Chat = require('../models/Chat');
const ChatMessage = require('../models/ChatMessage');

// Get all chats for a user
router.get('/user/:userId', async (req, res) => {
  try {
    const chats = await Chat.find({
      $or: [
        { participant1Id: req.params.userId },
        { participant2Id: req.params.userId }
      ]
    })
      .populate('participant1Id', 'name email role')
      .populate('participant2Id', 'name email role')
      .populate('listingId')
      .sort({ lastMessageTime: -1 });
    
    res.json(chats);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get or create chat
router.post('/', async (req, res) => {
  try {
    const { participant1Id, participant2Id, listingId } = req.body;
    
    // Check if chat already exists
    let chat = await Chat.findOne({
      $or: [
        { participant1Id, participant2Id },
        { participant1Id: participant2Id, participant2Id: participant1Id }
      ]
    })
      .populate('participant1Id', 'name email role')
      .populate('participant2Id', 'name email role')
      .populate('listingId');
    
    if (!chat) {
      chat = new Chat({
        participant1Id,
        participant2Id,
        listingId: listingId || null
      });
      await chat.save();
      
      chat = await Chat.findById(chat._id)
        .populate('participant1Id', 'name email role')
        .populate('participant2Id', 'name email role')
        .populate('listingId');
    }
    
    res.json(chat);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get messages for a chat
router.get('/:chatId/messages', async (req, res) => {
  try {
    const messages = await ChatMessage.find({ chatId: req.params.chatId })
      .populate('senderId', 'name email role')
      .populate('receiverId', 'name email role')
      .sort({ timestamp: 1 });
    
    res.json(messages);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Send message
router.post('/:chatId/messages', async (req, res) => {
  try {
    const { senderId, receiverId, message } = req.body;
    
    const chatMessage = new ChatMessage({
      chatId: req.params.chatId,
      senderId,
      receiverId,
      message
    });
    await chatMessage.save();
    
    // Update chat's last message
    await Chat.findByIdAndUpdate(req.params.chatId, {
      lastMessage: message,
      lastMessageTime: chatMessage.timestamp,
      $inc: { unreadCount: 1 }
    });
    
    const populatedMessage = await ChatMessage.findById(chatMessage._id)
      .populate('senderId', 'name email role')
      .populate('receiverId', 'name email role');
    
    res.status(201).json(populatedMessage);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Mark chat as read
router.patch('/:chatId/read', async (req, res) => {
  try {
    const chat = await Chat.findByIdAndUpdate(
      req.params.chatId,
      { unreadCount: 0 },
      { new: true }
    )
      .populate('participant1Id', 'name email role')
      .populate('participant2Id', 'name email role')
      .populate('listingId');
    
    if (!chat) {
      return res.status(404).json({ error: 'Chat not found' });
    }
    
    res.json(chat);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;




