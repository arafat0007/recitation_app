package com.example.recitation_app.feature_doa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recitation_app.core.ui.BigBackButton
import com.example.recitation_app.core.ui.LargeBengaliButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoaCategoryScreen(
    onBackClick: () -> Unit,
    onPostSalahAdhkarClick: () -> Unit,
    onZikirClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "দোয়া ও যিকির",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LargeBengaliButton(
                text = "নামাজের আমল",
                onClick = onPostSalahAdhkarClick
            )
            LargeBengaliButton(
                text = "যিকির",
                onClick = onZikirClick
            )
        }
    }
}
