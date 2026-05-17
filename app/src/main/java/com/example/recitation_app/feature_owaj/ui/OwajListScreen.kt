package com.example.recitation_app.feature_owaj.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.recitation_app.core.ui.EmptyView
import com.example.recitation_app.core.ui.ErrorView
import com.example.recitation_app.core.ui.LoadingView
import com.example.recitation_app.domain.model.Owaj

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwajListScreen(
    onBackClick: () -> Unit,
    onOwajClick: (String) -> Unit,
    viewModel: OwajViewModel = viewModel()
) {
    val state by viewModel.listState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "ওয়াজ", 
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
            when (val listState = state) {
                is OwajListState.Loading -> LoadingView()
                is OwajListState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(listState.owajs) { owaj ->
                            OwajItem(
                                owaj = owaj, 
                                onClick = { 
                                    Log.d("FLOW", "OwajListScreen: Item clicked: ${owaj.title} (id: ${owaj.id})")
                                    onOwajClick(owaj.id) 
                                }
                            )
                        }
                    }
                }
                is OwajListState.Error -> ErrorView(
                    message = listState.message,
                    onRetry = { viewModel.loadOwajs() }
                )
                is OwajListState.Empty -> EmptyView(message = "কোন ওয়াজ পাওয়া যায়নি")
            }
        }
    }
}

@Composable
fun OwajItem(
    owaj: Owaj,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = owaj.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = owaj.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = owaj.speakerName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                if (owaj.durationText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "সময়: ${owaj.durationText}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
