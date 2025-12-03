@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.agrochain.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agrochain.AgroChainViewModel
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
import com.example.agrochain.ui.state.AgroChainUiState
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

enum class MainTab {
    HOME, CHAT, SELL, GUIDE, PROFILE
}

@Composable
fun AgroChainApp(viewModel: AgroChainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.currentUser == null) {
            LoginScreen(
                uiState = uiState,
                onLogin = { email, password, role -> viewModel.login(email, password, role) },
                onRegister = { name, email, password, role -> viewModel.register(name, email, password, role) }
            )
        } else {
            DashboardScreen(
                uiState = uiState,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun LoginScreen(
    uiState: AgroChainUiState,
    onLogin: (String, String, UserRole) -> Unit,
    onRegister: (String, String, String, UserRole) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var role by rememberSaveable { mutableStateOf(UserRole.FARMER) }
    var showRegister by rememberSaveable { mutableStateOf(false) }

    if (showRegister) {
        RegisterScreen(
            uiState = uiState,
            onRegister = onRegister,
            onNavigateToLogin = { showRegister = false }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AgroChain Exchange",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Role-aware smart contracting for oilseed by-product trading.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            enabled = !uiState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        RoleDropdown(
            currentRole = role,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onRoleSelected = {
                role = it
                expanded = false
            }
        )

        if (uiState.loginError != null) {
            Text(
                text = uiState.loginError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = { onLogin(email.trim(), password, role) },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign In")
            }
        }

        TextButton(
            onClick = { showRegister = true },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Don't have an account? Register")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleDropdown(
    currentRole: UserRole,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRoleSelected: (UserRole) -> Unit
) {
    val roles = listOf(UserRole.FARMER, UserRole.TRANSPORTER, UserRole.BUYER, UserRole.ADMIN)
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        OutlinedTextField(
            value = currentRole.name.lowercase(Locale.ENGLISH).replaceFirstChar { it.titlecase(Locale.ENGLISH) },
            onValueChange = {},
            label = { Text("Role") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.name.lowercase(Locale.ENGLISH).replaceFirstChar { it.titlecase(Locale.ENGLISH) }) },
                    onClick = { onRoleSelected(role) }
                )
            }
        }
    }
}

@Composable
private fun RegisterScreen(
    uiState: AgroChainUiState,
    onRegister: (String, String, String, UserRole) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var role by rememberSaveable { mutableStateOf(UserRole.FARMER) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Join AgroChain Exchange",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            enabled = !uiState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            enabled = !uiState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        RoleDropdown(
            currentRole = role,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onRoleSelected = {
                role = it
                expanded = false
            }
        )

        if (uiState.loginError != null) {
            Text(
                text = uiState.loginError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (password.isNotBlank() && confirmPassword.isNotBlank() && password != confirmPassword) {
            Text(
                text = "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = { 
                if (password == confirmPassword && password.length >= 6) {
                    onRegister(name.trim(), email.trim(), password, role)
                }
            },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            enabled = !uiState.isLoading && 
                     name.isNotBlank() && 
                     email.isNotBlank() && 
                     password.isNotBlank() && 
                     password == confirmPassword &&
                     password.length >= 6
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Register")
            }
        }

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Already have an account? Sign In")
        }
    }
}

@Composable
private fun DashboardScreen(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel
) {
    val user = uiState.currentUser ?: return
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    var selectedChatId by remember { mutableStateOf<String?>(null) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    val snackbarState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.lastEscrowUpdate) {
        uiState.lastEscrowUpdate?.let {
            snackbarState.showSnackbar("Escrow status updated at $it")
        }
    }

    LaunchedEffect(user.id) {
        viewModel.fetchListings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (selectedTab) {
                            MainTab.HOME -> "AgroChain"
                            MainTab.SELL -> "Sell Your Product"
                            MainTab.GUIDE -> "Guide"
                            MainTab.PROFILE -> "Profile"
                            MainTab.CHAT -> {
                                val chat = selectedChatId?.let { 
                                    viewModel.getChatsForCurrentUser().firstOrNull { it.id == selectedChatId }
                                }
                                chat?.let {
                                    viewModel.getOtherParticipant(it)?.name ?: "Chat"
                                } ?: "Chat"
                            }
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    // Notification icon in top right corner for all screens
                    val unreadCount = viewModel.notificationsForCurrentUser().size
                    IconButton(onClick = { showNotificationsDialog = true }) {
                        Box {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
                                )
                            }
                        }
                    }
                    
                    if (selectedTab == MainTab.HOME) {
                        Icon(
                            Icons.Default.EnergySavingsLeaf,
                            contentDescription = "AgroChain Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (selectedTab == MainTab.PROFILE) {
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Logout")
                        }
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == MainTab.HOME,
                    onClick = { selectedTab = MainTab.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.CHAT,
                    onClick = { 
                        selectedTab = MainTab.CHAT
                        selectedChatId = null // Show chat list (Instagram-style)
                    },
                    icon = {
                        val chats = viewModel.getChatsForCurrentUser()
                        val unreadCount = chats.sumOf { it.unreadCount }
                        Box {
                            Icon(Icons.Default.Message, contentDescription = "Chat")
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
                                )
                            }
                        }
                    },
                    label = { Text("Chat") }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.SELL,
                    onClick = { selectedTab = MainTab.SELL },
                    icon = { Icon(Icons.Default.Sell, contentDescription = "Sell") },
                    label = { Text("Sell") }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.GUIDE,
                    onClick = { selectedTab = MainTab.GUIDE },
                    icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Guide") },
                    label = { Text("Guide") }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.PROFILE,
                    onClick = { selectedTab = MainTab.PROFILE },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                MainTab.HOME -> HomeScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    searchQuery = "",
                    onChatClick = { 
                    selectedChatId = it
                    selectedTab = MainTab.CHAT
                })
                MainTab.CHAT -> {
                    selectedChatId?.let { chatId ->
                        val chat = viewModel.getChatsForCurrentUser().firstOrNull { it.id == chatId }
                        chat?.let {
                            ChatScreen(
                                chat = it,
                                otherUser = viewModel.getOtherParticipant(it),
                                messages = viewModel.getChatMessages(chatId),
                                currentUser = user,
                                onSendMessage = { message ->
                                    viewModel.sendMessage(
                                        receiverId = if (it.participant1Id == user.id) it.participant2Id else it.participant1Id,
                                        message = message,
                                        listingId = it.listingId
                                    )
                                },
                                onBack = { 
                                    selectedTab = MainTab.HOME
                                    viewModel.markChatAsRead(chatId)
                                }
                            )
                        } ?: run {
                            // No chat selected, show chat list
                            ChatListScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onChatSelected = { 
                                    selectedChatId = it
                                }
                            )
                        }
                    } ?: run {
                        // No chat selected, show chat list
                        ChatListScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onChatSelected = { 
                                selectedChatId = it
                            }
                        )
                    }
                }
                MainTab.SELL -> SellScreen(uiState, viewModel)
                MainTab.GUIDE -> GuideScreen()
                MainTab.PROFILE -> ProfileScreen(uiState, viewModel, onChatClick = { 
                    selectedChatId = it
                    selectedTab = MainTab.CHAT
                })
            }
        }
        
        // Notification Dialog
        if (showNotificationsDialog) {
            NotificationsDialog(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { showNotificationsDialog = false },
                onAcceptOffer = { listingId, offerId -> 
                    viewModel.acceptOffer(listingId, offerId)
                },
                onOpenChat = { chatId ->
                    selectedChatId = chatId
                    selectedTab = MainTab.CHAT
                    showNotificationsDialog = false
                }
            )
        }
    }
}

// New Tab Screens
@Composable
private fun HomeScreen(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel,
    searchQuery: String,
    onChatClick: (String) -> Unit
) {
    val user = uiState.currentUser ?: return
    // Show all listings including own listings
    val allListings = uiState.listings.sortedByDescending { it.createdAt }
    var checkoutListing by remember { mutableStateOf<Listing?>(null) }
    var showOfferDialogFor by remember { mutableStateOf<Listing?>(null) }
    var showPaymentSuccess by remember { mutableStateOf(false) }
    val filteredListings = if (searchQuery.isBlank()) {
        allListings
    } else {
        allListings.filter { listing ->
            listing.type.contains(searchQuery, ignoreCase = true) ||
                listing.location.contains(searchQuery, ignoreCase = true)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Section
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Welcome back, ${user.name}!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Discover premium oilseed by-products",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        SectionTitle("All Listings")
        if (allListings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No listings available yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            if (searchQuery.isNotBlank()) {
                Text(
                    "${filteredListings.size} result${if (filteredListings.size == 1) "" else "s"} for \"$searchQuery\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (filteredListings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No listings match \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                filteredListings.forEach { listing ->
                    val owner = uiState.availableUsers.firstOrNull { it.id == listing.ownerId }
                    ListingCardWithChat(
                        listing = listing,
                        owner = owner,
                        currentUser = user,
                        onChatClick = {
                            owner?.let {
                                viewModel.startChat(it.id, listing.id)
                                val chatId = viewModel.getChatsForCurrentUser().firstOrNull { chat ->
                                    (chat.participant1Id == user.id && chat.participant2Id == it.id) ||
                                        (chat.participant1Id == it.id && chat.participant2Id == user.id)
                                }?.id
                                chatId?.let(onChatClick)
                            }
                        },
                        onBuyNow = {
                            checkoutListing = listing
                        },
                        onMakeOffer = {
                            showOfferDialogFor = listing
                        }
                    )
                }
            }
        }

        // Contracts Section
        val contracts = viewModel.contractsForCurrentUser()
        if (contracts.isNotEmpty()) {
            SectionTitle("My Contracts")
            ContractsSection(
                contracts = contracts,
                canAdvanceEscrow = user.role != UserRole.BUYER,
                onAdvance = { viewModel.releaseEscrow(it) },
                onCancel = { viewModel.cancelContract(it) }
            )
        }
    }

    checkoutListing?.let { listing ->
        PaymentGatewayDialog(
            listing = listing,
            onDismiss = { checkoutListing = null },
            onProceed = {
                viewModel.buyDirectly(listing.id)
                checkoutListing = null
                showPaymentSuccess = true
            }
        )
    }

    showOfferDialogFor?.let { listing ->
        OfferDialog(
            listing = listing,
            onDismiss = { showOfferDialogFor = null },
            onSubmit = { price, message ->
                viewModel.makeOffer(listing.id, price, message)
                showOfferDialogFor = null
            }
        )
    }

    if (showPaymentSuccess) {
        PaymentSuccessDialog(onDismiss = { showPaymentSuccess = false })
    }
}


@Composable
private fun SellScreen(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel
) {
    val user = uiState.currentUser ?: return
    // Any user can create listings now

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ListingFormCard(onSubmit = { type, qty, quality, price, location, description, imageUrl, moisture, protein, storage, packaging ->
            viewModel.createListing(type, qty, quality, price, location, description, imageUrl, moisture, protein, storage, packaging)
        })
        
        SectionTitle("My Listings")
        viewModel.myListings().forEach { listing ->
            ListingCard(
                listing = listing,
                highlight = true,
                primaryActionLabel = null,
                onPrimaryAction = null,
                onSecondaryAction = null
            )
        }
        
        if (viewModel.pendingOffersForCurrentUser().isNotEmpty()) {
            SectionTitle("Pending Offers - Accept or Reject")
            viewModel.pendingOffersForCurrentUser().forEach { offer ->
                OfferDecisionCard(
                    offer = offer,
                    listing = uiState.listings.firstOrNull { it.id == offer.listingId },
                    onAccept = { listingId, offerId -> viewModel.acceptOffer(listingId, offerId) }
                )
            }
        }
    }
}

@Composable
private fun NotificationsScreen(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel,
    onAcceptOffer: (String, String) -> Unit,
    onOpenChat: (String) -> Unit
) {
    val user = uiState.currentUser ?: return
    val notifications = viewModel.notificationsForCurrentUser().sortedByDescending { it.timestamp }
    val offersOnMyListings = uiState.offers.filter { offer ->
        val ownerId = uiState.listings.firstOrNull { it.id == offer.listingId }?.ownerId
        ownerId == user.id
    }
    val mySubmittedOffers = uiState.offers.filter { it.buyerId == user.id }
    val transportRequests = if (user.role == UserRole.TRANSPORTER) {
        viewModel.getTransportRequestsForCurrentUser()
    } else {
        emptyList()
    }
    
    // Combine all notifications: offers received, offers sent, transport requests, and platform notifications
    val allNotifications = mutableListOf<Pair<String, Any>>() // Pair of type and item
    
    // Add transport requests for transporters
    transportRequests.forEach { contract ->
        allNotifications.add("transport_request" to contract)
    }
    
    // Add offers received as notifications
    offersOnMyListings.forEach { offer ->
        allNotifications.add("offer_received" to offer)
    }
    
    // Add offers sent as notifications
    if (user.role == UserRole.BUYER) {
        mySubmittedOffers.forEach { offer ->
            allNotifications.add("offer_sent" to offer)
        }
    }
    
    // Add platform notifications
    notifications.forEach { notification ->
        allNotifications.add("platform" to notification)
    }
    
    // Sort by timestamp (most recent first)
    allNotifications.sortByDescending { (type, item) ->
        when (type) {
            "transport_request" -> {
                val contract = item as Contract
                contract.updatedAt.toEpochMilli()
            }
            "offer_received", "offer_sent" -> {
                val offer = item as Offer
                offer.createdAt.toEpochMilli()
            }
            "platform" -> {
                val notif = item as NotificationItem
                notif.timestamp.toEpochMilli()
            }
            else -> 0L
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle("All Notifications")
        
        if (allNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "No notifications yet.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            allNotifications.forEach { (type, item) ->
                when (type) {
                    "transport_request" -> {
                        val contract = item as Contract
                        val listing = uiState.listings.firstOrNull { it.id == contract.listingId }
                        TransportRequestCard(
                            contract = contract,
                            listing = listing,
                            onAccept = { viewModel.acceptTransportRequest(contract.id) },
                            onReject = { viewModel.rejectTransportRequest(contract.id) }
                        )
                    }
                    "offer_received" -> {
                        val offer = item as Offer
                        val listing = uiState.listings.firstOrNull { it.id == offer.listingId }
                        val buyer = uiState.availableUsers.firstOrNull { it.id == offer.buyerId }
                        OfferAlertCard(
                            offer = offer,
                            listing = listing,
                            counterpartyName = buyer?.name ?: "Buyer",
                            actionLabel = "Accept & Notify",
                            onAction = {
                                listing?.let { onAcceptOffer(it.id, offer.id) }
                            },
                            onChat = {
                                buyer?.let { receiver ->
                                    viewModel.startChat(receiver.id, listing?.id)
                                    val chatId = viewModel.getChatsForCurrentUser().firstOrNull { chat ->
                                        (chat.participant1Id == user.id && chat.participant2Id == receiver.id) ||
                                            (chat.participant1Id == receiver.id && chat.participant2Id == user.id)
                                    }?.id
                                    chatId?.let(onOpenChat)
                                }
                            }
                        )
                    }
                    "offer_sent" -> {
                        val offer = item as Offer
                        val listing = uiState.listings.firstOrNull { it.id == offer.listingId }
                        val seller = listing?.ownerId?.let { ownerId ->
                            uiState.availableUsers.firstOrNull { it.id == ownerId }
                        }
                        OfferAlertCard(
                            offer = offer,
                            listing = listing,
                            counterpartyName = seller?.name ?: "Seller",
                            actionLabel = null,
                            onAction = null,
                            onChat = seller?.let { contact ->
                                {
                                    viewModel.startChat(contact.id, listing?.id)
                                    val chatId = viewModel.getChatsForCurrentUser().firstOrNull { chat ->
                                        (chat.participant1Id == user.id && chat.participant2Id == contact.id) ||
                                            (chat.participant1Id == contact.id && chat.participant2Id == user.id)
                                    }?.id
                                    chatId?.let(onOpenChat)
                                }
                            },
                            statusLabel = "Waiting for seller response"
                        )
                    }
                    "platform" -> {
                        val notification = item as NotificationItem
                        NotificationCard(notification = notification)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsDialog(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel,
    onDismiss: () -> Unit,
    onAcceptOffer: (String, String) -> Unit,
    onOpenChat: (String) -> Unit
) {
    val user = uiState.currentUser ?: return
    val notifications = viewModel.notificationsForCurrentUser().sortedByDescending { it.timestamp }
    val offersOnMyListings = uiState.offers.filter { offer ->
        val ownerId = uiState.listings.firstOrNull { it.id == offer.listingId }?.ownerId
        ownerId == user.id
    }
    val mySubmittedOffers = uiState.offers.filter { it.buyerId == user.id }
    val transportRequests = if (user.role == UserRole.TRANSPORTER) {
        viewModel.getTransportRequestsForCurrentUser()
    } else {
        emptyList()
    }
    
    // Combine all notifications
    val allNotifications = mutableListOf<Pair<String, Any>>()
    
    transportRequests.forEach { contract ->
        allNotifications.add("transport_request" to contract)
    }
    
    offersOnMyListings.forEach { offer ->
        allNotifications.add("offer_received" to offer)
    }
    
    if (user.role == UserRole.BUYER) {
        mySubmittedOffers.forEach { offer ->
            allNotifications.add("offer_sent" to offer)
        }
    }
    
    notifications.forEach { notification ->
        allNotifications.add("platform" to notification)
    }
    
    allNotifications.sortByDescending { (type, item) ->
        when (type) {
            "transport_request" -> {
                val contract = item as Contract
                contract.updatedAt.toEpochMilli()
            }
            "offer_received", "offer_sent" -> {
                val offer = item as Offer
                offer.createdAt.toEpochMilli()
            }
            "platform" -> {
                val notif = item as NotificationItem
                notif.timestamp.toEpochMilli()
            }
            else -> 0L
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Notifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (allNotifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "No notifications yet.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    allNotifications.forEach { (type, item) ->
                        when (type) {
                            "transport_request" -> {
                                val contract = item as Contract
                                val listing = uiState.listings.firstOrNull { it.id == contract.listingId }
                                TransportRequestCard(
                                    contract = contract,
                                    listing = listing,
                                    onAccept = { 
                                        viewModel.acceptTransportRequest(contract.id)
                                    },
                                    onReject = { 
                                        viewModel.rejectTransportRequest(contract.id)
                                    }
                                )
                            }
                            "offer_received" -> {
                                val offer = item as Offer
                                val listing = uiState.listings.firstOrNull { it.id == offer.listingId }
                                val buyer = uiState.availableUsers.firstOrNull { it.id == offer.buyerId }
                                OfferAlertCard(
                                    offer = offer,
                                    listing = listing,
                                    counterpartyName = buyer?.name ?: "Buyer",
                                    actionLabel = "Accept & Notify",
                                    onAction = {
                                        listing?.let { onAcceptOffer(it.id, offer.id) }
                                    },
                                    onChat = {
                                        buyer?.let { receiver ->
                                            viewModel.startChat(receiver.id, listing?.id)
                                            val chatId = viewModel.getChatsForCurrentUser().firstOrNull { chat ->
                                                (chat.participant1Id == user.id && chat.participant2Id == receiver.id) ||
                                                    (chat.participant1Id == receiver.id && chat.participant2Id == user.id)
                                            }?.id
                                            chatId?.let(onOpenChat)
                                        }
                                    }
                                )
                            }
                            "offer_sent" -> {
                                val offer = item as Offer
                                val listing = uiState.listings.firstOrNull { it.id == offer.listingId }
                                val seller = listing?.ownerId?.let { ownerId ->
                                    uiState.availableUsers.firstOrNull { it.id == ownerId }
                                }
                                OfferAlertCard(
                                    offer = offer,
                                    listing = listing,
                                    counterpartyName = seller?.name ?: "Seller",
                                    actionLabel = null,
                                    onAction = null,
                                    onChat = seller?.let { contact ->
                                        {
                                            viewModel.startChat(contact.id, listing?.id)
                                            val chatId = viewModel.getChatsForCurrentUser().firstOrNull { chat ->
                                                (chat.participant1Id == user.id && chat.participant2Id == contact.id) ||
                                                    (chat.participant1Id == contact.id && chat.participant2Id == user.id)
                                            }?.id
                                            chatId?.let(onOpenChat)
                                        }
                                    },
                                    statusLabel = "Waiting for seller response"
                                )
                            }
                            "platform" -> {
                                val notification = item as NotificationItem
                                NotificationCard(notification = notification)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun GuideScreen() {
    val videos = listOf(
        VideoItem(
            id = "1",
            title = "Getting Started with AgroChain",
            description = "Learn how to navigate the platform and create your first listing",
            videoUrl = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4",
            thumbnailUrl = "https://via.placeholder.com/400x225?text=Video+1"
        ),
        VideoItem(
            id = "2",
            title = "Smart Contracts & Escrow Explained",
            description = "Understand how smart contracts and escrow work in AgroChain",
            videoUrl = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_2mb.mp4",
            thumbnailUrl = "https://via.placeholder.com/400x225?text=Video+2"
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Guide & Tutorials",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            "Watch these videos to learn how to use AgroChain effectively",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        videos.forEach { video ->
            VideoCard(video = video)
        }
    }
}

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String
)

@Composable
private fun VideoCard(video: VideoItem) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Video Thumbnail/Player Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Video Player",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        video.videoUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Video Info
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    video.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel,
    onChatClick: (String) -> Unit
) {
    val user = uiState.currentUser ?: return
    val chats = viewModel.getChatsForCurrentUser()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Profile Header
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user.name.first().toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${user.role.name.lowercase(Locale.ENGLISH).replaceFirstChar { it.titlecase(Locale.ENGLISH) }} • ${user.email}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (user.verified) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Verified Account",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Chats Section
        if (chats.isNotEmpty()) {
            SectionTitle("My Conversations")
            chats.forEach { chat ->
                val otherUser = viewModel.getOtherParticipant(chat)
                otherUser?.let {
                    ChatListItem(
                        chat = chat,
                        otherUser = it,
                        unreadCount = chat.unreadCount,
                        onClick = {
                            viewModel.startChat(it.id, chat.listingId)
                            onChatClick(chat.id)
                        }
                    )
                }
            }
        }

        // Stats
        SectionTitle("Statistics")
        var showMyListings by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatTile(
                title = "Listings",
                value = viewModel.myListings().size.toString(),
                icon = Icons.Default.PriceChange,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showMyListings = true }
            )
            StatTile(
                title = "Contracts",
                value = viewModel.contractsForCurrentUser().size.toString(),
                icon = Icons.Default.Lock,
                modifier = Modifier.weight(1f)
            )
        }
        
        if (showMyListings) {
            MyListingsDialog(
                listings = viewModel.myListings(),
                onDismiss = { showMyListings = false }
            )
        }

        // Activity Log
        SectionTitle("Recent Activity")
        ActivityLogPanel(entries = uiState.activityLog.take(5))
    }
}

@Composable
private fun ChatListScreen(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel,
    onChatSelected: (String) -> Unit
) {
    val user = uiState.currentUser ?: return
    val chats = viewModel.getChatsForCurrentUser()
    var chatSearch by rememberSaveable { mutableStateOf("") }
    val contacts = chats.mapNotNull { viewModel.getOtherParticipant(it) }
    val filteredChats = if (chatSearch.isBlank()) {
        chats
    } else {
        chats.filter { chat ->
            val otherUser = viewModel.getOtherParticipant(chat)
            otherUser?.name?.contains(chatSearch, ignoreCase = true) == true
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Messages",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Instagram-style inbox for quick farmer & buyer chats",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            value = chatSearch,
            onValueChange = { chatSearch = it },
            placeholder = { Text("Search conversations") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        if (contacts.isNotEmpty()) {
            ChatStoriesRow(contacts = contacts)
        }
        
        if (filteredChats.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Message,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (chatSearch.isBlank()) "No conversations yet" else "No chats match \"$chatSearch\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredChats.forEach { chat ->
                    val otherUser = viewModel.getOtherParticipant(chat)
                    otherUser?.let {
                        ChatListItem(
                            chat = chat,
                            otherUser = it,
                            unreadCount = chat.unreadCount,
                            onClick = {
                                onChatSelected(chat.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(
    chat: Chat,
    otherUser: User?,
    messages: List<ChatMessage>,
    currentUser: User,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    var messageText by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(messages.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            otherUser?.name ?: "Unknown User",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (otherUser != null) {
                            Text(
                                otherUser.role.name.lowercase(Locale.ENGLISH).replaceFirstChar { it.titlecase(Locale.ENGLISH) },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No messages yet. Start the conversation!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    messages.forEach { message ->
                        MessageBubble(
                            message = message,
                            isFromCurrentUser = message.senderId == currentUser.id
                        )
                    }
                }
            }

            // Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    maxLines = 3
                )
                Button(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    },
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(Icons.Default.Message, contentDescription = "Send")
                }
            }
        }
    }
}

// Helper Components
@Composable
private fun ListingCardWithChat(
    listing: Listing,
    owner: User?,
    currentUser: User,
    onChatClick: () -> Unit,
    onBuyNow: () -> Unit,
    onMakeOffer: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) }
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Image and Title Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Product Image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (listing.imageUrl.isNotBlank()) {
                        // In a real app, use Coil to load images
                        // For now, show a placeholder
                        Icon(
                            Icons.Default.PriceChange,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            Icons.Default.PriceChange,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        listing.type,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${listing.quantityTons} tons • ${listing.quality}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "₹${listing.priceExpectationPerTon} / ton",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Description
            if (listing.description.isNotBlank()) {
                Text(
                    listing.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Details Grid
            if (listing.moistureContent.isNotBlank() || listing.proteinContent.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (listing.moistureContent.isNotBlank()) {
                        DetailChip("Moisture: ${listing.moistureContent}")
                    }
                    if (listing.proteinContent.isNotBlank()) {
                        DetailChip("Protein: ${listing.proteinContent}")
                    }
                }
            }

            // Additional Info
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("📍 ${listing.location}", style = MaterialTheme.typography.bodySmall)
                owner?.let {
                    Text("👤 Seller: ${it.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (listing.storageCondition.isNotBlank()) {
                    Text("📦 Storage: ${listing.storageCondition}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (listing.packaging.isNotBlank()) {
                    Text("📋 Packaging: ${listing.packaging}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    text = "Listed ${formatter.format(listing.createdAt.atZone(ZoneId.systemDefault()))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            // Action Buttons
            if (currentUser.id != listing.ownerId) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onBuyNow, modifier = Modifier.weight(1f)) {
                        Text("Buy Now")
                    }
                    OutlinedButton(onClick = onMakeOffer, modifier = Modifier.weight(1f)) {
                        Text("Offer")
                    }
                    OutlinedButton(onClick = onChatClick, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Chat")
                    }
                }
            } else {
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("My Listing")
                }
            }
        }
    }
}

@Composable
private fun PaymentGatewayDialog(
    listing: Listing,
    onDismiss: () -> Unit,
    onProceed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Secure Test Payment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "You're about to buy ${listing.type} (${listing.quantityTons}T) at ₹${listing.priceExpectationPerTon}/T."
                )
                Text(
                    "A sandbox gateway will open. Tap “Continue with test mode” to simulate the payment and instantly notify the farmer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onProceed) {
                Text("Continue with Test Mode")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PaymentSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Payment Successful") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your test payment went through.")
                Text(
                    "The farmer has been notified and escrow has been locked for this transaction.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun DetailChip(text: String) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun OfferAlertCard(
    offer: Offer,
    listing: Listing?,
    counterpartyName: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    onChat: (() -> Unit)?,
    statusLabel: String? = null
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                listing?.type ?: "Listing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "From $counterpartyName • ₹${offer.offerPricePerTon} per ton",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Message: ${offer.message.ifBlank { "No message" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            statusLabel?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (onAction != null && actionLabel != null) {
                    Button(
                        onClick = onAction,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(actionLabel)
                    }
                }
                if (onChat != null) {
                    OutlinedButton(
                        onClick = onChat,
                        modifier = Modifier.weight(if (onAction != null && actionLabel != null) 1f else 1f)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Message")
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationItem) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    notification.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .format(notification.timestamp.atZone(ZoneId.systemDefault())),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TransportRequestCard(
    contract: Contract,
    listing: Listing?,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Transport Request",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                listing?.type ?: "Product",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Contract Value: ₹${contract.totalValue}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Payment completed. Please accept or reject this transport request.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept")
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject")
                }
            }
        }
    }
}

@Composable
private fun ChatListItem(
    chat: Chat,
    otherUser: User,
    unreadCount: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    otherUser.name.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    otherUser.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                chat.lastMessage?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatStoriesRow(contacts: List<User>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        items(contacts.distinctBy { it.id }) { contact ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.widthIn(max = 72.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        contact.name.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    contact.name.split(" ").first(),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isFromCurrentUser: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    if (isFromCurrentUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp)
        ) {
            Text(
                message.message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isFromCurrentUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FarmerProcessorDashboard(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel
) {
    CardColumn {
        ListingFormCard(onSubmit = { type, qty, quality, price, location, description, imageUrl, moisture, protein, storage, packaging ->
            viewModel.createListing(type, qty, quality, price, location, description, imageUrl, moisture, protein, storage, packaging)
        })
        SectionTitle("My Listings")
        uiState.currentUser?.let { user ->
            viewModel.myListings().forEach { listing ->
                ListingCard(
                    listing = listing,
                    highlight = listing.ownerRole == UserRole.FARMER,
                    primaryActionLabel = null,
                    onPrimaryAction = null,
                    onSecondaryAction = null
                )
            }
        }
        SectionTitle("Offers awaiting action")
        viewModel.pendingOffersForCurrentUser().forEach { offer ->
            OfferDecisionCard(
                offer = offer,
                listing = uiState.listings.firstOrNull { it.id == offer.listingId },
                onAccept = { listingId, offerId -> viewModel.acceptOffer(listingId, offerId) }
            )
        }
    }
}

@Composable
private fun BuyerDashboard(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel
) {
    val listings = viewModel.filteredListings()
    var showOfferDialogFor by remember { mutableStateOf<Listing?>(null) }

    CardColumn {
        MarketplaceFilterBar(
            state = uiState,
            listingTypes = viewModel.marketplaceTypes(),
            onQueryChange = { query -> viewModel.updateMarketplaceFilters(query, uiState.marketplaceTypeFilter, uiState.marketplaceSortDescending) },
            onFilterSelected = { filter -> viewModel.updateMarketplaceFilters(uiState.marketplaceSearchQuery, filter, uiState.marketplaceSortDescending) },
            onSortChange = { sortDesc -> viewModel.updateMarketplaceFilters(uiState.marketplaceSearchQuery, uiState.marketplaceTypeFilter, sortDesc) }
        )

        SectionTitle("Live Marketplace")
        if (listings.isEmpty()) {
            Text("No listings match your filters yet. Try adjusting the filters.")
        } else {
            listings.forEach { listing ->
                ListingCard(
                    listing = listing,
                    highlight = false,
                    primaryActionLabel = "Make Offer",
                    onPrimaryAction = { showOfferDialogFor = listing },
                    onSecondaryAction = null
                )
            }
        }

        SectionTitle("My Offers")
        viewModel.pendingOffersForCurrentUser().forEach { offer ->
            OfferSummaryCard(
                offer = offer,
                listing = uiState.listings.firstOrNull { it.id == offer.listingId }
            )
        }
    }

    showOfferDialogFor?.let { listing ->
        OfferDialog(
            listing = listing,
            onDismiss = { showOfferDialogFor = null },
            onSubmit = { price, message ->
                viewModel.makeOffer(listing.id, price, message)
                showOfferDialogFor = null
            }
        )
    }
}

@Composable
private fun AdminDashboard(
    uiState: AgroChainUiState,
    viewModel: AgroChainViewModel
) {
    CardColumn {
        SectionTitle("Verification Console")
        uiState.availableUsers.forEach { user ->
            AdminUserRow(user = user, onToggle = { viewModel.toggleUserVerification(user.id) })
        }

        SectionTitle("Platform Pulse")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatTile(
                title = "Listings",
                value = uiState.listings.size.toString(),
                icon = Icons.Default.PriceChange,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                title = "Offers",
                value = uiState.offers.size.toString(),
                icon = Icons.Default.ShoppingCart,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                title = "Contracts",
                value = uiState.contracts.size.toString(),
                icon = Icons.Default.Lock,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ListingFormCard(
    onSubmit: (String, Double, String, Double, String, String, String, String, String, String, String) -> Unit
) {
    var type by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var quality by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var moistureContent by rememberSaveable { mutableStateOf("") }
    var proteinContent by rememberSaveable { mutableStateOf("") }
    var storageCondition by rememberSaveable { mutableStateOf("") }
    var packaging by rememberSaveable { mutableStateOf("") }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("List Oilseed By-product", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (e.g. Mustard Husk)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity (tons)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = quality, onValueChange = { quality = it }, label = { Text("Quality / Grade") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price expectation (₹ per ton)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL (optional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = moistureContent, onValueChange = { moistureContent = it }, label = { Text("Moisture Content (optional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = proteinContent, onValueChange = { proteinContent = it }, label = { Text("Protein Content (optional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = storageCondition, onValueChange = { storageCondition = it }, label = { Text("Storage Condition (optional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = packaging, onValueChange = { packaging = it }, label = { Text("Packaging (optional)") }, modifier = Modifier.fillMaxWidth())
            
            Button(
                onClick = {
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    onSubmit(type, qty, quality, priceValue, location, description, imageUrl, moistureContent, proteinContent, storageCondition, packaging)
                    type = ""
                    quantity = ""
                    quality = ""
                    price = ""
                    location = ""
                    description = ""
                    imageUrl = ""
                    moistureContent = ""
                    proteinContent = ""
                    storageCondition = ""
                    packaging = ""
                },
                enabled = type.isNotBlank() && quantity.isNotBlank() && price.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publish listing")
            }
        }
    }
}

@Composable
private fun ListingCard(
    listing: Listing,
    highlight: Boolean,
    primaryActionLabel: String?,
    onPrimaryAction: (() -> Unit)?,
    onSecondaryAction: (() -> Unit)?
) {
    val formatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) }
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (highlight) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(listing.type, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${listing.quantityTons} tons • ${listing.quality}", style = MaterialTheme.typography.bodyMedium)
            Text("Expectation: ₹${listing.priceExpectationPerTon} / ton", style = MaterialTheme.typography.bodyMedium)
            Text("Location: ${listing.location}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Listed ${formatter.format(listing.createdAt.atZone(ZoneId.systemDefault()))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (primaryActionLabel != null && onPrimaryAction != null) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onPrimaryAction) {
                        Text(primaryActionLabel)
                    }
                    if (onSecondaryAction != null) {
                        OutlinedButton(onClick = onSecondaryAction) {
                            Text("Details")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferDecisionCard(
    offer: Offer,
    listing: Listing?,
    onAccept: (String, String) -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Offer on ${listing?.type ?: "Listing"}", style = MaterialTheme.typography.titleMedium)
            Text("Bid: ₹${offer.offerPricePerTon} / ton", fontWeight = FontWeight.Bold)
            Text("Message: ${offer.message.ifBlank { "—" }}", style = MaterialTheme.typography.bodySmall)
            Button(onClick = { onAccept(offer.listingId, offer.id) }) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Accept & Lock Escrow")
            }
        }
    }
}

@Composable
private fun OfferSummaryCard(
    offer: Offer,
    listing: Listing?
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(listing?.type ?: "Listing", style = MaterialTheme.typography.titleMedium)
            Text("Your offer: ₹${offer.offerPricePerTon} / ton")
            Text("Status: Awaiting seller decision", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarketplaceFilterBar(
    state: AgroChainUiState,
    listingTypes: List<String>,
    onQueryChange: (String) -> Unit,
    onFilterSelected: (String?) -> Unit,
    onSortChange: (Boolean) -> Unit
) {
    var typeExpanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.marketplaceSearchQuery,
            onValueChange = onQueryChange,
            label = { Text("Search by type or location") },
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = it }
        ) {
            OutlinedTextField(
                value = state.marketplaceTypeFilter ?: "All product types",
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter by type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            DropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All types") },
                    onClick = {
                    onFilterSelected(null)
                    typeExpanded = false
                    }
                )
                listingTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            onFilterSelected(type)
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = state.marketplaceSortDescending,
                onClick = { onSortChange(true) },
                label = { Text("Highest price") },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            )
            SegmentedButton(
                selected = !state.marketplaceSortDescending,
                onClick = { onSortChange(false) },
                label = { Text("Lowest price") },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            )
        }
    }
}

@Composable
private fun OfferDialog(
    listing: Listing,
    onDismiss: () -> Unit,
    onSubmit: (Double, String) -> Unit
) {
    var price by rememberSaveable { mutableStateOf("${listing.priceExpectationPerTon}") }
    var note by rememberSaveable { mutableStateOf("Ready for immediate pickup") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(price.toDoubleOrNull() ?: listing.priceExpectationPerTon, note)
                }
            ) { Text("Send offer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Offer for ${listing.type}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Offer price per ton (₹)") }
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Message") }
                )
            }
        }
    )
}

@Composable
private fun NotificationsPanel(notifications: List<NotificationItem>) {
    if (notifications.isEmpty()) return
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionTitle("Alerts & Notifications")
            notifications.take(4).forEach { notification ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(notification.message, style = MaterialTheme.typography.bodyMedium)
                }
                Divider()
            }
        }
    }
}

@Composable
private fun ContractsSection(
    contracts: List<Contract>,
    canAdvanceEscrow: Boolean,
    onAdvance: (String) -> Unit,
    onCancel: (String) -> Unit
) {
    CardColumn {
        SectionTitle("Smart Contracts & Escrow")
        if (contracts.isEmpty()) {
            Text("No smart contracts yet. Once an offer is accepted, escrow will show here.")
        } else {
            contracts.forEach { contract ->
                ContractCard(
                    contract = contract,
                    canAdvance = canAdvanceEscrow,
                    onAdvance = { onAdvance(contract.id) },
                    onCancel = { onCancel(contract.id) }
                )
            }
        }
    }
}

@Composable
private fun ContractCard(
    contract: Contract,
    canAdvance: Boolean,
    onAdvance: () -> Unit,
    onCancel: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Contract ${contract.id.take(6)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Total value: ₹${contract.totalValue}")
            Text("Status: ${contract.status.name}", color = MaterialTheme.colorScheme.primary)
            if (canAdvance) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAdvance, enabled = contract.status != ContractStatus.RELEASED) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (contract.status == ContractStatus.RELEASED) "Closed" else "Advance Escrow")
                    }
                    TextButton(onClick = onCancel) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
private fun ActivityLogPanel(entries: List<ActivityLogEntry>) {
    CardColumn {
        SectionTitle("Activity Log")
        entries.take(10).forEach { entry ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(entry.description, style = MaterialTheme.typography.bodyMedium)
                Text(entry.timestamp.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Divider()
        }
    }
}

@Composable
private fun FutureTechSection(
    aiMessage: String,
    iotMessage: String,
    onFeedback: (String) -> Unit
) {
    CardColumn {
        SectionTitle("Scalability Roadmap")
        PlaceholderCard(
            title = "AI/ML Price Forecasting",
            description = aiMessage,
            icon = Icons.Default.SmartToy,
            accent = MaterialTheme.colorScheme.secondary
        )
        PlaceholderCard(
            title = "IoT Quality Monitoring",
            description = iotMessage,
            icon = Icons.Default.EnergySavingsLeaf,
            accent = MaterialTheme.colorScheme.tertiary
        )
        var feedback by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = feedback,
            onValueChange = { feedback = it },
            label = { Text("Ideas for future intelligence") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                onFeedback(feedback)
                feedback = ""
            },
            enabled = feedback.isNotBlank(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Record roadmap note")
        }
    }
}

@Composable
private fun PlaceholderCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent)
            }
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun AdminUserRow(
    user: User,
    onToggle: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                Text("${user.role} • ${if (user.verified) "Verified" else "Pending"}", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onToggle) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(if (user.verified) "Revoke" else "Approve")
            }
        }
    }
}

@Composable
private fun MyListingsDialog(
    listings: List<Listing>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("My Listings") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (listings.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No listings yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    listings.forEach { listing ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(listing.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("${listing.quantityTons} tons • ₹${listing.priceExpectationPerTon}/ton", style = MaterialTheme.typography.bodySmall)
                                Text("Location: ${listing.location}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .defaultMinSize(minHeight = 120.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null)
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CardColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun HomeSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search by product or location") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Composable function that accepts data from ViewModel and renders it
 * This is a reusable component that displays the key information from AgroChainUiState
 */
@Composable
fun ViewModelDataRenderer(
    uiState: AgroChainUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current User Section
        uiState.currentUser?.let { user ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Current User",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Name: ${user.name}", style = MaterialTheme.typography.bodyMedium)
                    Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Role: ${user.role.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (user.verified) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Verified",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Statistics Section
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Platform Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatTile(
                        title = "Listings",
                        value = uiState.listings.size.toString(),
                        icon = Icons.Default.PriceChange,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        title = "Offers",
                        value = uiState.offers.size.toString(),
                        icon = Icons.Default.ShoppingCart,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        title = "Contracts",
                        value = uiState.contracts.size.toString(),
                        icon = Icons.Default.Lock,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatTile(
                        title = "Notifications",
                        value = uiState.notifications.size.toString(),
                        icon = Icons.Default.Notifications,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        title = "Chats",
                        value = uiState.chats.size.toString(),
                        icon = Icons.Default.Message,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        title = "Users",
                        value = uiState.availableUsers.size.toString(),
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Listings Section
        if (uiState.listings.isNotEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Recent Listings (${uiState.listings.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    uiState.listings.take(5).forEach { listing ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    listing.type,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "${listing.quantityTons} tons • ₹${listing.priceExpectationPerTon}/ton",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    listing.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (listing != uiState.listings.take(5).last()) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }

        // Recent Contracts Section
        if (uiState.contracts.isNotEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Active Contracts (${uiState.contracts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    uiState.contracts.take(5).forEach { contract ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Contract ${contract.id.take(8)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Value: ₹${contract.totalValue}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Status: ${contract.status.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (contract != uiState.contracts.take(5).last()) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }

        // Recent Notifications Section
        if (uiState.notifications.isNotEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Recent Notifications (${uiState.notifications.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    uiState.notifications.take(5).forEach { notification ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    notification.message,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                                        .format(notification.timestamp.atZone(ZoneId.systemDefault())),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (notification != uiState.notifications.take(5).last()) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }

        // Activity Log Section
        if (uiState.activityLog.isNotEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Recent Activity (${uiState.activityLog.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    uiState.activityLog.take(5).forEach { entry ->
                        Text(
                            entry.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (entry != uiState.activityLog.take(5).last()) {
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
