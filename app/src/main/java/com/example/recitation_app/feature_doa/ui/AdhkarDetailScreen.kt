package com.example.recitation_app.feature_doa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recitation_app.core.player.AudioPlayerManager
import com.example.recitation_app.core.ui.*
import com.example.recitation_app.domain.model.PostSalahAdhkar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhkarDetailScreen(
    adhkarId: String,
    salahKey: String,
    viewModel: DoaViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val playerManager = remember { AudioPlayerManager(context) }
    
    DisposableEffect(Unit) {
        onDispose { playerManager.release() }
    }

    val adhkarFlow = remember(adhkarId) { viewModel.getAdhkarById(adhkarId) }
    val adhkar by adhkarFlow.collectAsState(initial = null)
    
    val isPlaying by playerManager.isPlaying.collectAsState()
    val currentRepeat by playerManager.currentRepeat.collectAsState()
    val isFinished by playerManager.isFinished.collectAsState()

    LaunchedEffect(isFinished) {
        if (isFinished) {
            viewModel.markAsCompleted(adhkarId, salahKey)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        adhkar?.title ?: "বিস্তারিত",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp, fontWeight = FontWeight.Bold)
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
        if (adhkar == null) {
            LoadingView(modifier = Modifier.padding(paddingValues))
        } else {
            val item = adhkar!!
            val repeatCount = item.counts[salahKey] ?: 1

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Playback Controls & Counter at the TOP
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Counter
                        Text(
                            text = "${toBengaliDigits(if (isFinished) repeatCount else (currentRepeat + 1))}/${toBengaliDigits(repeatCount)} বার",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Play/Pause Button
                            Button(
                                onClick = {
                                    if (isPlaying) {
                                        playerManager.pause()
                                    } else {
                                        if (currentRepeat == 0 && !isFinished) {
                                            playerManager.playRawResource(item.audio, repeatCount)
                                        } else {
                                            playerManager.resume()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .height(72.dp)
                                    .weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFinished) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                if (isFinished) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                } else if (isPlaying) {
                                    Text("||", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isFinished) "সম্পন্ন" else if (isPlaying) "থামান" else "শুনুন",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            // Restart Button
                            IconButton(
                                onClick = {
                                    playerManager.stop()
                                    playerManager.playRawResource(item.audio, repeatCount)
                                },
                                modifier = Modifier.size(72.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Restart",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Collapsible Sections
                
                // 1. Pronunciation (Expanded by default)
                ExpandableCard(title = "উচ্চারণ", initialExpanded = true) {
                    Text(
                        text = item.pronunciation,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 2. Meaning (Expanded by default)
                ExpandableCard(title = "অর্থ", initialExpanded = true) {
                    Text(
                        text = item.meaning,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // 3. Arabic (Collapsed by default)
                ExpandableCard(title = "আরবি", initialExpanded = false) {
                    Text(
                        text = item.arabic,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 32.sp,
                            lineHeight = 48.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                // Reference
                Text(
                    text = "সূত্র: ${item.reference}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun toBengaliDigits(input: Int): String {
    val digits = mapOf(
        '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
        '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
    )
    return input.toString().map { digits[it] ?: it }.joinToString("")
}
