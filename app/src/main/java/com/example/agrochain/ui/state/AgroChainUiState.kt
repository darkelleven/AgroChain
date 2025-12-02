package com.example.agrochain.ui.state

import com.example.agrochain.model.ActivityLogEntry
import com.example.agrochain.model.Chat
import com.example.agrochain.model.ChatMessage
import com.example.agrochain.model.Contract
import com.example.agrochain.model.Listing
import com.example.agrochain.model.NotificationItem
import com.example.agrochain.model.Offer
import com.example.agrochain.model.User
import java.time.Instant

data class AgroChainUiState(
    val currentUser: User? = null,
    val availableUsers: List<User> = emptyList(),
    val listings: List<Listing> = emptyList(),
    val offers: List<Offer> = emptyList(),
    val contracts: List<Contract> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val activityLog: List<ActivityLogEntry> = emptyList(),
    val aiForecastMessage: String = "AI-driven price forecasting will plug in here.",
    val iotPlaceholderMessage: String = "IoT quality monitoring device data will stream into this feed.",
    val marketplaceSearchQuery: String = "",
    val marketplaceTypeFilter: String? = null,
    val marketplaceSortDescending: Boolean = true,
    val lastEscrowUpdate: Instant? = null,
    val loginError: String? = null,
    val isLoading: Boolean = false,
    val chats: List<Chat> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList()

)

