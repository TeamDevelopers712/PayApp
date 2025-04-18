package com.example.payapp.ui.theme.QRandBarcode

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.zxing.integration.android.IntentIntegrator
//
//@Composable
//fun ScanQRCodeScreen(onScanResult: (String) -> Unit) {
//    val context = LocalContext.current
//    val activity = context as? Activity // Get activity from context
//
//    val scannerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        val data: Intent? = result.data
//        val scannedData = IntentIntegrator.parseActivityResult(result.resultCode, data)?.contents
//        if (scannedData != null) {
//            onScanResult(scannedData)
//        } else {
//            Toast.makeText(context, "Scan Failed!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center
//    ) {
//        Button(
//            onClick = {
//                activity?.let {
//                    val scanIntent = IntentIntegrator(it).setOrientationLocked(false).createScanIntent()
//                    scannerLauncher.launch(scanIntent)
//                } ?: Toast.makeText(context, "Error: Activity not found!", Toast.LENGTH_SHORT).show()
//            }
//        ) {
//            Text("Scan QR Code")
//        }
//    }
//}
