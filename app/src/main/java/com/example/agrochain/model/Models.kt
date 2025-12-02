package com.example.agrochain.model

import java.time.Instant
import java.util.UUID

enum class UserRole {
    FARMER,
    TRANSPORTER,
    BUYER,
    ADMIN
}

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val role: UserRole,
    val verified: Boolean = false
)

data class Listing(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val ownerRole: UserRole,
    val type: String,
    val quantityTons: Double,
    val quality: String,
    val priceExpectationPerTon: Double,
    val location: String,
    val description: String = "",
    val imageUrl: String = "",
    val moistureContent: String = "",
    val proteinContent: String = "",
    val storageCondition: String = "",
    val packaging: String = "",
    val createdAt: Instant = Instant.now()
)

data class Offer(
    val id: String = UUID.randomUUID().toString(),
    val listingId: String,
    val buyerId: String,
    val offerPricePerTon: Double,
    val message: String,
    val createdAt: Instant = Instant.now()
)

enum class ContractStatus {
    OFFERED,
    ESCROW_LOCKED,
    COMPLETED,
    RELEASED,
    CANCELLED
}

data class Contract(
    val id: String = UUID.randomUUID().toString(),
    val listingId: String,
    val farmerId: String,
    val buyerId: String,
    val offerId: String,
    val totalValue: Double,
    val status: ContractStatus = ContractStatus.OFFERED,
    val escrowLocked: Boolean = false,
    val transporterId: String? = null,
    val transporterAccepted: Boolean? = null,
    val updatedAt: Instant = Instant.now()
)

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val message: String,
    val timestamp: Instant = Instant.now()
)

data class ActivityLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val timestamp: Instant = Instant.now()
)

data class CredentialHint(
    val role: UserRole,
    val email: String,
    val password: String
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val read: Boolean = false
)

data class Chat(
    val id: String = UUID.randomUUID().toString(),
    val participant1Id: String,
    val participant2Id: String,
    val listingId: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: Instant? = null,
    val unreadCount: Int = 0
)

