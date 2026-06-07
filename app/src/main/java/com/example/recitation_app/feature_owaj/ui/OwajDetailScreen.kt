package com.example.recitation_app.feature_owaj.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recitation_app.core.ui.ErrorView
import com.example.recitation_app.core.ui.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwajDetailScreen(
    owajId: String,
    onBackClick: () -> Unit,
    viewModel: OwajViewModel = viewModel()
) {
    val state by viewModel.detailState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(owajId) {
        viewModel.loadOwajById(owajId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "ওয়াজ বিস্তারিত", 
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp) 
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(84.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.height(100.dp)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val detailState = state) {
                is OwajDetailState.Loading -> LoadingView()
                is OwajDetailState.Success -> {
                    val owaj = detailState.owaj
                    val isWatched = detailState.isWatched
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        YouTubePlayerCompose(
                            videoId = owaj.youtubeVideoId,
                            lifecycleOwner = lifecycleOwner,
                            modifier = Modifier.height(250.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Watched Status Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "দেখা হয়েছে?",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                )
                                Row {
                                    Button(
                                        onClick = { viewModel.toggleWatched(owajId, true) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isWatched) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                        ),
                                        modifier = Modifier.height(56.dp)
                                    ) {
                                        Text("হ্যাঁ", fontSize = 18.sp, color = if (isWatched) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.toggleWatched(owajId, false) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (!isWatched) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface
                                        ),
                                        modifier = Modifier.height(56.dp)
                                    ) {
                                        Text("না", fontSize = 18.sp, color = if (!isWatched) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = owaj.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            lineHeight = 36.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = owaj.speakerName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 22.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "বিস্তারিত:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = owaj.description,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                lineHeight = 28.sp
                            )
                        )
                    }
                }
                is OwajDetailState.Error -> ErrorView(
                    message = detailState.message,
                    onRetry = { viewModel.loadOwajById(owajId) }
                )
            }
        }
    }
}
