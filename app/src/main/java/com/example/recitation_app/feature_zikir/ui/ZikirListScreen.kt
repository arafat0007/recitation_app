package com.example.recitation_app.feature_zikir.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recitation_app.core.ui.BigBackButton
import com.example.recitation_app.core.ui.EmptyView
import com.example.recitation_app.core.ui.ErrorView
import com.example.recitation_app.core.ui.LoadingView
import com.example.recitation_app.domain.model.Zikir

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZikirListScreen(
    onBackClick: () -> Unit,
    viewModel: ZikirViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadZikirs()
    }

    // Initialize the local audio player when the screen appears.
    LaunchedEffect(Unit) {
        viewModel.initializePlayer(context)
    }

    // Release the player when leaving the screen.
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPlayback()
            viewModel.releasePlayer()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "যিকির",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    BigBackButton(onClick = onBackClick)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.height(100.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.loading -> LoadingView()
                state.error != null -> ErrorView(
                    message = state.error!!,
                    onRetry = { viewModel.loadZikirs() }
                )
                state.items.isEmpty() -> EmptyView(message = "কোনো যিকির নেই")
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items) { zikir ->
                            ZikirItemCard(
                                zikir = zikir,
                                isCurrentlyPlaying = state.currentlyPlayingId == zikir.id,
                                isPlaying = state.isPlaying,
                                onPlayPause = { viewModel.togglePlayPause(zikir) }
                            )
                        }
                    }
                }
            }

            // Error snackbar
            state.errorMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("বন্ধ")
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        }
    }
}

@Composable
fun ZikirItemCard(
    zikir: Zikir,
    isCurrentlyPlaying: Boolean,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    val containerColor = if (isCurrentlyPlaying) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentlyPlaying) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title and time column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = zikir.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⏱ ${zikir.time}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                if (isCurrentlyPlaying && isPlaying) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "▶ প্লে হচ্ছে",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
                if (isCurrentlyPlaying && !isPlaying) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⏸ থামানো হয়েছে",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Play/Pause button (rounded rectangle)
            Button(
                onClick = onPlayPause,
                modifier = Modifier
                    .width(104.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentlyPlaying) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    contentColor = if (isCurrentlyPlaying) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isCurrentlyPlaying && isPlaying) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "পজ",
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "প্লে",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}