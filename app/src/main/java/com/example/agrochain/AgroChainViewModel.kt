package com.example.agrochain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrochain.model.ActivityLogEntry
import com.example.agrochain.model.Chat
import com.example.agrochain.model.ChatMessage
import com.example.agrochain.model.Contract
import com.example.agrochain.model.ContractStatus
import com.example.agrochain.model.Listing
import com.example.agrochain.model.NotificationItem
import com.example.agrochain.model.Offer
import com.example.agrochain.model.User
import com.example.agrochain.model.UserRole
import com.example.agrochain.repository.AuthRepository
import com.example.agrochain.network.ApiClient
import com.example.agrochain.ui.state.AgroChainUiState
import java.time.Instant
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AgroChainViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    init {
        // Try to restore persisted user session on startup
        viewModelScope.launch {
            try {
                val storedUser = authRepository.getStoredUser()
                if (storedUser != null) {
                    _uiState.update { it.copy(currentUser = storedUser) }
                    // Fetch listings immediately after restoring session
                    fetchListings()
                    addActivity("Restored session for ${storedUser.name}")
                }
            } catch (_: Exception) {
                // ignore restore errors
            }
        }
    }

    private val _uiState = MutableStateFlow(
        AgroChainUiState(
            activityLog = listOf(ActivityLogEntry(description = "Platform initialized • ${Instant.now()}"))
        )
    )
    val uiState: StateFlow<AgroChainUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(loginError = null, isLoading = true) }
            
            val result = authRepository.login(email, password, role)
            
            if (result.isSuccess) {
                val (user, token) = result.getOrThrow()
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        loginError = null,
                        isLoading = false
                    )
                }
                addActivity("${user.name} logged in as ${user.role.name.lowercase(Locale.ENGLISH)}")
            } else {
                val exception = result.exceptionOrNull()
                _uiState.update {
                    it.copy(
                        loginError = exception?.message ?: "Login failed. Please check your credentials.",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(loginError = null, isLoading = true) }
            
            val result = authRepository.register(name, email, password, role)
            
            if (result.isSuccess) {
                val (user, token) = result.getOrThrow()
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        loginError = null,
                        isLoading = false
                    )
                }
                addActivity("${user.name} registered and logged in as ${user.role.name.lowercase(Locale.ENGLISH)}")
            } else {
                val exception = result.exceptionOrNull()
                _uiState.update {
                    it.copy(
                        loginError = exception?.message ?: "Registration failed. Please try again.",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val currentName = _uiState.value.currentUser?.name
            authRepository.logout()
            _uiState.update { it.copy(currentUser = null, listings = emptyList()) }
            if (currentName != null) {
                addActivity("$currentName logged out")
            }
        }
    }

    fun fetchListings() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getAllListings()
                if (response.isSuccessful && response.body() != null) {
                    val gson = com.google.gson.Gson()
                    val raw = response.body() ?: emptyList()
                    val parsed = raw.mapNotNull { jsonObj ->
                        try {
                            // Normalize ownerId which may be a populated object or a string
                            val ownerIdElement = when {
                                jsonObj.has("ownerId") -> jsonObj.get("ownerId")
                                jsonObj.has("owner") -> jsonObj.get("owner")
                                else -> null
                            }

                            val ownerId = when {
                                ownerIdElement == null || ownerIdElement.isJsonNull -> null
                                ownerIdElement.isJsonPrimitive -> ownerIdElement.asString
                                ownerIdElement.isJsonObject -> {
                                    val obj = ownerIdElement.asJsonObject
                                    when {
                                        obj.has("_id") -> obj.get("_id").asString
                                        obj.has("id") -> obj.get("id").asString
                                        else -> null
                                    }
                                }
                                else -> null
                            } ?: ""

                            Listing(
                                id = if (jsonObj.has("_id")) jsonObj.get("_id").asString else if (jsonObj.has("id")) jsonObj.get("id").asString else java.util.UUID.randomUUID().toString(),
                                ownerId = ownerId,
                                ownerRole = com.example.agrochain.model.UserRole.valueOf(jsonObj.get("ownerRole").asString),
                                type = jsonObj.get("type").asString,
                                quantityTons = jsonObj.get("quantityTons").asDouble,
                                quality = jsonObj.get("quality").asString,
                                priceExpectationPerTon = jsonObj.get("priceExpectationPerTon").asDouble,
                                location = jsonObj.get("location").asString,
                                description = if (jsonObj.has("description") && !jsonObj.get("description").isJsonNull) jsonObj.get("description").asString else "",
                                imageUrl = if (jsonObj.has("imageUrl") && !jsonObj.get("imageUrl").isJsonNull) jsonObj.get("imageUrl").asString else "",
                                moistureContent = if (jsonObj.has("moistureContent") && !jsonObj.get("moistureContent").isJsonNull) jsonObj.get("moistureContent").asString else "",
                                proteinContent = if (jsonObj.has("proteinContent") && !jsonObj.get("proteinContent").isJsonNull) jsonObj.get("proteinContent").asString else "",
                                storageCondition = if (jsonObj.has("storageCondition") && !jsonObj.get("storageCondition").isJsonNull) jsonObj.get("storageCondition").asString else "",
                                packaging = if (jsonObj.has("packaging") && !jsonObj.get("packaging").isJsonNull) jsonObj.get("packaging").asString else "",
                                createdAt = java.time.Instant.now()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    _uiState.update { it.copy(listings = parsed) }
                }
            } catch (e: Exception) {
                // Log error silently, listings remain as is
            }
        }
    }

    fun createListing(
        type: String, 
        quantityTons: Double, 
        quality: String, 
        pricePerTon: Double, 
        location: String,
        description: String = "",
        imageUrl: String = "",
        moistureContent: String = "",
        proteinContent: String = "",
        storageCondition: String = "",
        packaging: String = ""
    ) {
        val user = _uiState.value.currentUser ?: return

        viewModelScope.launch {
            try {
                val listing = Listing(
                    ownerId = user.id,
                    ownerRole = user.role,
                    type = type,
                    quantityTons = quantityTons,
                    quality = quality,
                    priceExpectationPerTon = pricePerTon,
                    location = location,
                    description = description,
                    imageUrl = imageUrl,
                    moistureContent = moistureContent,
                    proteinContent = proteinContent,
                    storageCondition = storageCondition,
                    packaging = packaging
                )

                val response = ApiClient.apiService.createListing(listing)
                if (response.isSuccessful && response.body() != null) {
                    val gson = com.google.gson.Gson()
                    val jsonObj = response.body()!!
                    val createdListing = try {
                        // Map JsonObject response into Listing as above
                        val ownerIdElement = when {
                            jsonObj.has("ownerId") -> jsonObj.get("ownerId")
                            jsonObj.has("owner") -> jsonObj.get("owner")
                            else -> null
                        }
                        val ownerId = when {
                            ownerIdElement == null || ownerIdElement.isJsonNull -> ""
                            ownerIdElement.isJsonPrimitive -> ownerIdElement.asString
                            ownerIdElement.isJsonObject -> {
                                val obj = ownerIdElement.asJsonObject
                                when {
                                    obj.has("_id") -> obj.get("_id").asString
                                    obj.has("id") -> obj.get("id").asString
                                    else -> ""
                                }
                            }
                            else -> ""
                        }

                        Listing(
                            id = if (jsonObj.has("_id")) jsonObj.get("_id").asString else if (jsonObj.has("id")) jsonObj.get("id").asString else java.util.UUID.randomUUID().toString(),
                            ownerId = ownerId,
                            ownerRole = com.example.agrochain.model.UserRole.valueOf(jsonObj.get("ownerRole").asString),
                            type = jsonObj.get("type").asString,
                            quantityTons = jsonObj.get("quantityTons").asDouble,
                            quality = jsonObj.get("quality").asString,
                            priceExpectationPerTon = jsonObj.get("priceExpectationPerTon").asDouble,
                            location = jsonObj.get("location").asString,
                            description = if (jsonObj.has("description") && !jsonObj.get("description").isJsonNull) jsonObj.get("description").asString else "",
                            imageUrl = if (jsonObj.has("imageUrl") && !jsonObj.get("imageUrl").isJsonNull) jsonObj.get("imageUrl").asString else "",
                            moistureContent = if (jsonObj.has("moistureContent") && !jsonObj.get("moistureContent").isJsonNull) jsonObj.get("moistureContent").asString else "",
                            proteinContent = if (jsonObj.has("proteinContent") && !jsonObj.get("proteinContent").isJsonNull) jsonObj.get("proteinContent").asString else "",
                            storageCondition = if (jsonObj.has("storageCondition") && !jsonObj.get("storageCondition").isJsonNull) jsonObj.get("storageCondition").asString else "",
                            packaging = if (jsonObj.has("packaging") && !jsonObj.get("packaging").isJsonNull) jsonObj.get("packaging").asString else "",
                            createdAt = java.time.Instant.now()
                        )
                    } catch (e: Exception) {
                        listing
                    }

                    _uiState.update {
                        it.copy(
                            listings = listOf(createdListing) + it.listings,
                            activityLog = listOf(activityEntry("${user.name} listed $type (${quantityTons}T) from $location")) + it.activityLog
                        )
                    }
                    broadcastNotification(
                        recipientIds = _uiState.value.availableUsers.map(User::id),
                        message = "New ${listing.type} listing (${listing.quantityTons}T) from ${user.name}"
                    )
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun makeOffer(listingId: String, offerPrice: Double, message: String) {
        val buyer = _uiState.value.currentUser ?: return
        // Any user can make offers now

        val listing = _uiState.value.listings.firstOrNull { it.id == listingId } ?: return
        val offer = Offer(
            listingId = listingId,
            buyerId = buyer.id,
            offerPricePerTon = offerPrice,
            message = message
        )

        _uiState.update {
            it.copy(
                offers = listOf(offer) + it.offers,
                activityLog = listOf(activityEntry("${buyer.name} offered ₹${offerPrice} /T on ${listing.type}")) + it.activityLog
            )
        }

        broadcastNotification(
            recipientIds = listOf(listing.ownerId),
            message = "${buyer.name} offered ₹${offer.offerPricePerTon}/T for your ${listing.type} listing"
        )
    }

    fun buyDirectly(listingId: String) {
        val buyer = _uiState.value.currentUser ?: return
        val listing = _uiState.value.listings.firstOrNull { it.id == listingId } ?: return
        if (listing.ownerId == buyer.id) return // Can't buy your own listing

        // Create an offer at listing price and auto-accept it
        val offer = Offer(
            listingId = listingId,
            buyerId = buyer.id,
            offerPricePerTon = listing.priceExpectationPerTon,
            message = "Direct purchase at listing price"
        )

        val contract = Contract(
            listingId = listingId,
            farmerId = listing.ownerId,
            buyerId = buyer.id,
            offerId = offer.id,
            totalValue = listing.priceExpectationPerTon * listing.quantityTons,
            status = ContractStatus.ESCROW_LOCKED,
            escrowLocked = true,
            updatedAt = Instant.now()
        )

        _uiState.update {
            it.copy(
                offers = listOf(offer) + it.offers,
                contracts = listOf(contract) + it.contracts,
                activityLog = listOf(activityEntry("${buyer.name} purchased ${listing.type} directly at ₹${listing.priceExpectationPerTon}/T")) + it.activityLog,
                lastEscrowUpdate = Instant.now()
            )
        }

        broadcastNotification(
            recipientIds = listOf(listing.ownerId),
            message = "${buyer.name} purchased your ${listing.type} listing directly. Escrow locked."
        )
    }

    fun acceptOffer(listingId: String, offerId: String) {
        val user = _uiState.value.currentUser ?: return
        if (user.role != UserRole.FARMER && user.role != UserRole.TRANSPORTER) return

        val offers = _uiState.value.offers
        val offer = offers.firstOrNull { it.id == offerId && it.listingId == listingId } ?: return
        val listing = _uiState.value.listings.firstOrNull { it.id == listingId && it.ownerId == user.id } ?: return

        val contract = Contract(
            listingId = listingId,
            farmerId = listing.ownerId,
            buyerId = offer.buyerId,
            offerId = offer.id,
            totalValue = offer.offerPricePerTon * listing.quantityTons,
            status = ContractStatus.ESCROW_LOCKED,
            escrowLocked = true,
            updatedAt = Instant.now()
        )

        _uiState.update {
            it.copy(
                offers = offers.filterNot { existing -> existing.id == offerId },
                contracts = listOf(contract) + it.contracts,
                activityLog = listOf(activityEntry("Offer accepted for ${listing.type}. Escrow locked.")) + it.activityLog,
                lastEscrowUpdate = Instant.now()
            )
        }

        broadcastNotification(
            recipientIds = listOf(offer.buyerId),
            message = "${user.name} accepted your offer on ${listing.type}. Escrow secured."
        )
    }

    fun releaseEscrow(contractId: String) {
        val actor = _uiState.value.currentUser ?: return
        val contracts = _uiState.value.contracts.toMutableList()
        val idx = contracts.indexOfFirst { it.id == contractId }
        if (idx < 0) return

        val existing = contracts[idx]
        val newStatus = when (existing.status) {
            ContractStatus.ESCROW_LOCKED -> ContractStatus.COMPLETED
            ContractStatus.COMPLETED -> ContractStatus.RELEASED
            else -> ContractStatus.RELEASED
        }

        // When payment is complete (status becomes COMPLETED), assign transporter and notify
        var transporterId: String? = existing.transporterId
        if (newStatus == ContractStatus.COMPLETED && existing.transporterId == null) {
            // Assign first available transporter
            val transporters = _uiState.value.availableUsers.filter { it.role == UserRole.TRANSPORTER }
            transporterId = transporters.firstOrNull()?.id
        }

        contracts[idx] = existing.copy(
            status = newStatus,
            escrowLocked = newStatus == ContractStatus.ESCROW_LOCKED,
            transporterId = transporterId,
            updatedAt = Instant.now()
        )

        _uiState.update {
            it.copy(
                contracts = contracts,
                activityLog = listOf(activityEntry("${actor.name} progressed contract ${existing.id.take(6)} → $newStatus")) + it.activityLog,
                lastEscrowUpdate = Instant.now()
            )
        }

        val notificationRecipients = mutableListOf(existing.buyerId, existing.farmerId)
        
        // Notify transporter when payment is complete
        if (newStatus == ContractStatus.COMPLETED && transporterId != null) {
            val listing = _uiState.value.listings.firstOrNull { it.id == existing.listingId }
            broadcastNotification(
                recipientIds = listOf(transporterId),
                message = "Contract ${existing.id.take(6)} payment completed. Please accept or reject the transport request for ${listing?.type ?: "product"}."
            )
            notificationRecipients.add(transporterId)
        }

        broadcastNotification(
            recipientIds = notificationRecipients,
            message = "Contract ${existing.id.take(6)} moved to $newStatus by ${actor.name}"
        )
    }

    fun cancelContract(contractId: String) {
        val actor = _uiState.value.currentUser ?: return
        val contracts = _uiState.value.contracts.toMutableList()
        val idx = contracts.indexOfFirst { it.id == contractId }
        if (idx < 0) return

        val existing = contracts[idx]
        contracts[idx] = existing.copy(status = ContractStatus.CANCELLED, escrowLocked = false, updatedAt = Instant.now())

        _uiState.update {
            it.copy(
                contracts = contracts,
                activityLog = listOf(activityEntry("Contract ${existing.id.take(6)} cancelled by ${actor.name}")) + it.activityLog
            )
        }

        broadcastNotification(
            recipientIds = listOf(existing.buyerId, existing.farmerId),
            message = "Contract ${existing.id.take(6)} cancelled"
        )
    }

    fun updateMarketplaceFilters(query: String, typeFilter: String?, sortDescending: Boolean) {
        _uiState.update {
            it.copy(
                marketplaceSearchQuery = query,
                marketplaceTypeFilter = typeFilter,
                marketplaceSortDescending = sortDescending
            )
        }
    }

    fun toggleUserVerification(userId: String) {
        val admin = _uiState.value.currentUser ?: return
        if (admin.role != UserRole.ADMIN) return

        val updatedUsers = _uiState.value.availableUsers.map { user ->
            if (user.id == userId) user.copy(verified = !user.verified) else user
        }

        val updatedCurrent = updatedUsers.firstOrNull { it.id == admin.id }

        _uiState.update {
            it.copy(
                availableUsers = updatedUsers,
                currentUser = updatedCurrent ?: admin
            )
        }

        val toggled = updatedUsers.first { it.id == userId }
        addActivity("Admin verified toggle for ${toggled.name}: ${toggled.verified}")
        broadcastNotification(
            recipientIds = listOf(userId),
            message = "Your account verification status is now ${if (toggled.verified) "APPROVED" else "PENDING"}"
        )
    }

    fun recordForecastFeedback(feedback: String) {
        addActivity("AI placeholder feedback: $feedback")
    }

    private fun broadcastNotification(recipientIds: List<String>, message: String) {
        if (recipientIds.isEmpty()) return
        val notificationBatch = recipientIds.map {
            NotificationItem(userId = it, message = message)
        }

        _uiState.update {
            it.copy(
                notifications = notificationBatch + it.notifications
            )
        }
    }

    private fun addActivity(message: String) {
        _uiState.update {
            it.copy(activityLog = listOf(activityEntry(message)) + it.activityLog)
        }
    }

    private fun activityEntry(message: String) = ActivityLogEntry(description = message)

    fun filteredListings(): List<Listing> {
        val state = _uiState.value
        var listings = state.listings
        state.marketplaceTypeFilter?.let { filter ->
            listings = listings.filter { it.type.equals(filter, ignoreCase = true) }
        }
        if (state.marketplaceSearchQuery.isNotBlank()) {
            val query = state.marketplaceSearchQuery.lowercase(Locale.ENGLISH)
            listings = listings.filter {
                it.type.lowercase(Locale.ENGLISH).contains(query) ||
                    it.location.lowercase(Locale.ENGLISH).contains(query)
            }
        }
        listings = if (state.marketplaceSortDescending) {
            listings.sortedByDescending { it.priceExpectationPerTon }
        } else {
            listings.sortedBy { it.priceExpectationPerTon }
        }
        return listings
    }

    fun pendingOffersForCurrentUser(): List<Offer> {
        val user = _uiState.value.currentUser ?: return emptyList()
        return when (user.role) {
            UserRole.FARMER, UserRole.TRANSPORTER -> _uiState.value.offers.filter { offer ->
                val listingOwner = _uiState.value.listings.firstOrNull { it.id == offer.listingId }?.ownerId
                listingOwner == user.id
            }
            UserRole.BUYER -> _uiState.value.offers.filter { it.buyerId == user.id }
            else -> emptyList()
        }
    }

    fun notificationsForCurrentUser(): List<NotificationItem> {
        val user = _uiState.value.currentUser ?: return emptyList()
        return _uiState.value.notifications.filter { it.userId == user.id }
    }

    fun contractsForCurrentUser(): List<Contract> {
        val user = _uiState.value.currentUser ?: return emptyList()
        // Any user can have contracts as buyer or seller
        return _uiState.value.contracts.filter { 
            it.buyerId == user.id || it.farmerId == user.id 
        }
    }

    fun myListings(): List<Listing> {
        val user = _uiState.value.currentUser ?: return emptyList()
        return _uiState.value.listings.filter { it.ownerId == user.id }
    }

    fun marketplaceTypes(): List<String> = _uiState.value.listings.map { it.type }.distinct()

    fun seedDemoData() {
        // Demo data seeding removed - data now comes from backend API
        // This function is kept for backward compatibility but does nothing
    }

    fun startChat(receiverId: String, listingId: String? = null) {
        val currentUser = _uiState.value.currentUser ?: return
        val existingChat = _uiState.value.chats.firstOrNull { chat ->
            (chat.participant1Id == currentUser.id && chat.participant2Id == receiverId) ||
            (chat.participant1Id == receiverId && chat.participant2Id == currentUser.id)
        }
        
        if (existingChat == null) {
            val newChat = Chat(
                participant1Id = currentUser.id,
                participant2Id = receiverId,
                listingId = listingId
            )
            _uiState.update {
                it.copy(chats = listOf(newChat) + it.chats)
            }
        }
    }

    fun sendMessage(receiverId: String, message: String, listingId: String? = null) {
        val sender = _uiState.value.currentUser ?: return
        if (message.isBlank()) return

        // Ensure chat exists
        var chat = _uiState.value.chats.firstOrNull { chat ->
            (chat.participant1Id == sender.id && chat.participant2Id == receiverId) ||
            (chat.participant1Id == receiverId && chat.participant2Id == sender.id)
        }

        if (chat == null) {
            chat = Chat(
                participant1Id = sender.id,
                participant2Id = receiverId,
                listingId = listingId
            )
            _uiState.update {
                it.copy(chats = listOf(chat) + it.chats)
            }
        }

        val chatMessage = ChatMessage(
            chatId = chat.id,
            senderId = sender.id,
            receiverId = receiverId,
            message = message.trim()
        )

        val updatedChat = chat.copy(
            lastMessage = message.trim(),
            lastMessageTime = chatMessage.timestamp,
            unreadCount = if (receiverId == chat.participant2Id) chat.unreadCount + 1 else chat.unreadCount
        )

        _uiState.update { state ->
            state.copy(
                chatMessages = listOf(chatMessage) + state.chatMessages,
                chats = state.chats.map { if (it.id == chat.id) updatedChat else it }
            )
        }

        broadcastNotification(
            recipientIds = listOf(receiverId),
            message = "New message from ${sender.name}"
        )
    }

    fun getChatMessages(chatId: String): List<ChatMessage> {
        return _uiState.value.chatMessages
            .filter { it.chatId == chatId }
            .sortedBy { it.timestamp }
    }

    fun getChatsForCurrentUser(): List<Chat> {
        val user = _uiState.value.currentUser ?: return emptyList()
        return _uiState.value.chats.filter {
            it.participant1Id == user.id || it.participant2Id == user.id
        }.sortedByDescending { it.lastMessageTime ?: Instant.EPOCH }
    }

    fun getOtherParticipant(chat: Chat): User? {
        val currentUserId = _uiState.value.currentUser?.id ?: return null
        val otherId = if (chat.participant1Id == currentUserId) chat.participant2Id else chat.participant1Id
        return _uiState.value.availableUsers.firstOrNull { it.id == otherId }
    }

    fun markChatAsRead(chatId: String) {
        val user = _uiState.value.currentUser ?: return
        _uiState.update { state ->
            val chat = state.chats.firstOrNull { it.id == chatId } ?: return@update state
            val updatedChat = if (chat.participant2Id == user.id) {
                chat.copy(unreadCount = 0)
            } else {
                chat
            }
            state.copy(
                chats = state.chats.map { if (it.id == chatId) updatedChat else it }
            )
        }
    }

    fun acceptTransportRequest(contractId: String) {
        val transporter = _uiState.value.currentUser ?: return
        if (transporter.role != UserRole.TRANSPORTER) return

        val contracts = _uiState.value.contracts.toMutableList()
        val idx = contracts.indexOfFirst { it.id == contractId && it.transporterId == transporter.id }
        if (idx < 0) return

        val existing = contracts[idx]
        contracts[idx] = existing.copy(
            transporterAccepted = true,
            updatedAt = Instant.now()
        )

        _uiState.update {
            it.copy(
                contracts = contracts,
                activityLog = listOf(activityEntry("${transporter.name} accepted transport request for contract ${existing.id.take(6)}")) + it.activityLog
            )
        }

        val listing = _uiState.value.listings.firstOrNull { it.id == existing.listingId }
        broadcastNotification(
            recipientIds = listOf(existing.buyerId, existing.farmerId),
            message = "${transporter.name} accepted the transport request for ${listing?.type ?: "product"}"
        )
    }

    fun rejectTransportRequest(contractId: String) {
        val transporter = _uiState.value.currentUser ?: return
        if (transporter.role != UserRole.TRANSPORTER) return

        val contracts = _uiState.value.contracts.toMutableList()
        val idx = contracts.indexOfFirst { it.id == contractId && it.transporterId == transporter.id }
        if (idx < 0) return

        val existing = contracts[idx]
        contracts[idx] = existing.copy(
            transporterAccepted = false,
            transporterId = null, // Remove transporter assignment
            updatedAt = Instant.now()
        )

        _uiState.update {
            it.copy(
                contracts = contracts,
                activityLog = listOf(activityEntry("${transporter.name} rejected transport request for contract ${existing.id.take(6)}")) + it.activityLog
            )
        }

        val listing = _uiState.value.listings.firstOrNull { it.id == existing.listingId }
        broadcastNotification(
            recipientIds = listOf(existing.buyerId, existing.farmerId),
            message = "${transporter.name} rejected the transport request for ${listing?.type ?: "product"}. Another transporter will be assigned."
        )
    }

    fun getTransportRequestsForCurrentUser(): List<Contract> {
        val user = _uiState.value.currentUser ?: return emptyList()
        if (user.role != UserRole.TRANSPORTER) return emptyList()
        return _uiState.value.contracts.filter { 
            it.transporterId == user.id && 
            it.transporterAccepted == null &&
            it.status == ContractStatus.COMPLETED
        }
    }
}

