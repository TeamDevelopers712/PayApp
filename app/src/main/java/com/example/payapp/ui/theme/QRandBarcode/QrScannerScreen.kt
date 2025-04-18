package com.example.payapp.ui.theme.qrscanner

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.zxing.integration.android.IntentIntegrator

@Composable
fun QrScannerScreen(navController: NavController, onScanResult: (String) -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity

    val qrScannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data: Intent? = result.data
        val scanResult = IntentIntegrator.parseActivityResult(result.resultCode, data)?.contents

        if (!scanResult.isNullOrEmpty()) {
            Log.d("QRScanner", "✅ Scanned UID: $scanResult") // Debug log
            onScanResult(scanResult)
            navController.navigate("payment_screen/$scanResult") // Navigate to Payment Screen
        } else {
            Log.e("QRScanner", "❌ No QR Code detected.")
        }
    }

    // Function to start QR scanning
    fun startQRScan() {
        Log.d("QRScanner", "🚀 Starting QR Scanner...") // Debug log

        activity?.let {
            val intent = IntentIntegrator(it).apply {
                setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                setPrompt("Scan a QR Code")
                setBeepEnabled(true)
                setOrientationLocked(false) // Allow rotation
            }.createScanIntent()

            qrScannerLauncher.launch(intent) // ✅ Start scanner
        } ?: Log.e("QRScanner", "❌ Activity is null! Cannot start scanner.")
    }

    // Automatically start scan when screen loads
    LaunchedEffect(Unit) {
        startQRScan()
    }

    // UI for Debugging (In case auto scan fails)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Scanning QR Code...", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { startQRScan() }) {
                Text("Retry Scan")
            }
        }
    }
}
