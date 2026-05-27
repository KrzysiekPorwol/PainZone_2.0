package com.painzone.ui.train

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun TrainScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Trenuj", style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview
@Composable
private fun TrainScreenPreview() {
    PainZoneTheme {
        Surface { TrainScreen() }
    }
}
