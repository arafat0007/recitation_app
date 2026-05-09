package com.example.recitation_app.feature_home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recitation_app.core.ui.LargeBengaliButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSurahClick: () -> Unit,
    onDoaClick: () -> Unit,
    onOwajClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "নেক আমল",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LargeBengaliButton(
                text = "সুরা",
                onClick = onSurahClick
            )
            
            LargeBengaliButton(
                text = "দোয়া",
                onClick = onDoaClick
            )
            
            LargeBengaliButton(
                text = "ওয়াজ",
                onClick = onOwajClick
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            LargeBengaliButton(
                text = "প্রিয় তালিকা",
                onClick = onFavoritesClick
            )
            
            LargeBengaliButton(
                text = "সেটিংস",
                onClick = onSettingsClick
            )
        }
    }
}
