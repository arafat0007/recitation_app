package com.example.recitation_app.feature_doa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recitation_app.core.ui.*
import com.example.recitation_app.domain.model.PostSalahAdhkar
import com.example.recitation_app.domain.model.Salah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostSalahAdhkarScreen(
    viewModel: DoaViewModel,
    onBackClick: () -> Unit,
    onAdhkarClick: (String, String) -> Unit
) {
    val selectedSalah by viewModel.selectedSalah.collectAsState()
    val state by viewModel.postSalahAdhkarState.collectAsState()
    val completedAdhkars by viewModel.completedAdhkars.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state is PostSalahAdhkarState.Selection) "নামাজের পরের আমল" else selectedSalah.displayName,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    BigBackButton(
                        onClick = {
                            if (state is PostSalahAdhkarState.Selection) {
                                onBackClick()
                            } else {
                                viewModel.showSelection()
                            }
                        }
                    )
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
            when (val currentState = state) {
                is PostSalahAdhkarState.Selection -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Salah.values().forEach { salah ->
                            LargeBengaliButton(
                                text = salah.displayName,
                                onClick = { viewModel.selectSalah(salah) }
                            )
                        }
                    }
                }
                is PostSalahAdhkarState.Loading -> LoadingView()
                is PostSalahAdhkarState.Error -> ErrorView(
                    message = currentState.message,
                    onRetry = { viewModel.showSelection() }
                )
                is PostSalahAdhkarState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentState.items) { adhkar ->
                            AdhkarItemRow(
                                adhkar = adhkar,
                                isCompleted = completedAdhkars.contains("${selectedSalah.key}_${adhkar.id}"),
                                onClick = { onAdhkarClick(adhkar.id, selectedSalah.key) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdhkarItemRow(
    adhkar: PostSalahAdhkar,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = adhkar.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = adhkar.pronunciation,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
