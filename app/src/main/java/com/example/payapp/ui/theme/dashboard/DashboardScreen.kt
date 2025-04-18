package com.example.payapp.ui.theme.dashboard

import android.util.Log
import android.graphics.Bitmap
import android.graphics.Color
import android.content.Intent
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.payapp.ui.theme.firestore.FirestoreViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
//Extra
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.painter.Painter
import com.example.payapp.ui.theme.QRandBarcode.generateQRCode


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun DashboardScreen(
    userId: String,
    viewModel: FirestoreViewModel = viewModel(),
    navController: NavController
) {
    val customerData by viewModel.customerData.collectAsState()
    var receiverId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val uid = firebaseUser?.uid ?: "Unknown UID"

    // Generate QR Code only once
    val qrBitmap by remember(uid) { mutableStateOf(generateQRCode(uid)) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // 🎨 Indigo & White Theme Colors
    val DeepIndigo = ComposeColor(0xFF2C2C84)
    val LightIndigo = ComposeColor(0xFF3C3C9A)
    val White = ComposeColor.White
    val LightGray = ComposeColor(0xFFB3B3B3)
    val Green = ComposeColor(0xFF00C853)

    // Fetch data when screen loads
    LaunchedEffect(userId) {
        viewModel.fetchCustomerData(userId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = DeepIndigo,
                    titleContentColor = White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                customerData?.let { data ->
                    val name = data["name"] as? String ?: "Unknown"
                    val accountBalance = data["account_balance"] as? Double ?: 0.0

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 💳 Account Info Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepIndigo)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Welcome, $name",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "UID: $uid",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Light,
                                    color = LightGray,
                                    modifier = Modifier.clickable {
                                        clipboardManager.setText(AnnotatedString(uid))
                                        Toast.makeText(context, "UID copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Your Balance: ₹$accountBalance",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🧾 QR Code
                        qrBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier.size(200.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔍 Scan QR Button
                        Button(
                            onClick = { navController.navigate("qr_scanner") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LightIndigo),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Scan QR Code", color = White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔑 Receiver ID Input
                        OutlinedTextField(
                            value = receiverId,
                            onValueChange = { receiverId = it },
                            label = { Text("Receiver ID", color = LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LightIndigo,
                                unfocusedBorderColor = LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 💸 Amount Input
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Amount", color = LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LightIndigo,
                                unfocusedBorderColor = LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        var pin by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = pin,
                            onValueChange = { pin = it },
                            label = { Text("Enter PIN", color = LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LightIndigo,
                                unfocusedBorderColor = LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))


                        // 🟢 Send Money Button
                        Button(
                            onClick = {
                                val amountDouble = amount.toDoubleOrNull()
                                if (receiverId.isNotEmpty() && amountDouble != null && amountDouble > 0 && pin.length >= 4) {
                                    if (receiverId != uid) {
                                        // You can later validate the PIN from Firestore or secure source
                                        viewModel.makeTransaction(uid, receiverId, amountDouble) {
                                            viewModel.fetchCustomerData(uid)
                                        }
                                    } else {
                                        Toast.makeText(context, "You cannot send money to yourself", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Invalid input. Check receiver, amount, or PIN", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Green),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Send Money", color = White)
                        }



                    }
                } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}





//@Composable
//fun DashboardScreen(userId: String, viewModel: FirestoreViewModel = viewModel(), navController: NavController) {
//    val customerData by viewModel.customerData.collectAsState()
//    var receiverId by remember { mutableStateOf("") }
//    var amount by remember { mutableStateOf("") }
//    var isLoading by remember { mutableStateOf(true) }
//
//    val firebaseUser = FirebaseAuth.getInstance().currentUser
//    val uid = firebaseUser?.uid ?: "Unknown UID"
//
//    // Generate QR Code only once (Fix UI blocking issue)
//    val qrBitmap by remember(uid) { mutableStateOf(generateQRCode(uid)) }
//
//    val context = LocalContext.current
//    val clipboardManager = LocalClipboardManager.current
//
//    // Fetch account balance when screen loads
//    LaunchedEffect(userId) {
//        viewModel.fetchCustomerData(userId)
//        isLoading = false
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Dashboard") },
//                colors = TopAppBarDefaults.smallTopAppBarColors(
//                    containerColor = ComposeColor(0xFF00796B),
//                    titleContentColor = ComposeColor.White
//                )
//            )
//        }
//    ) { paddingValues ->
//        Box(modifier = Modifier
//            .fillMaxSize()
//            .padding(paddingValues)
//            .padding(16.dp)
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            } else {
//                customerData?.let { data ->
//                    val name = data["name"] as? String ?: "Unknown"
//                    val accountBalance = data["account_balance"] as? Double ?: 0.0 //customerData?.get("account_balance") data["account_balance"]
//
//                    Column(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text("Welcome, $name", fontSize = 26.sp, fontWeight = FontWeight.Bold)
//
//                        Text(
//                            "UID: $uid",
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Light,
//                            color = ComposeColor.Gray,
//                            modifier = Modifier.clickable {
//                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(uid))
//                                Toast.makeText(context, "UID copied to clipboard", Toast.LENGTH_SHORT).show()
//                            }
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Text("Your Balance: ₹$accountBalance", fontSize = 24.sp, fontWeight = FontWeight.Bold)
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        qrBitmap?.let {
//                            Image(
//                                bitmap = it.asImageBitmap(),
//                                contentDescription = "QR Code",
//                                modifier = Modifier.size(200.dp)
//                            )
//                        }
//
//                        Button(
//                            onClick = { navController.navigate("qr_scanner") },
//                            modifier = Modifier.fillMaxWidth(),
//                            colors = ButtonDefaults.buttonColors(containerColor = ComposeColor(0xFF00796B))
//                        ) {
//                            Text("Scan QR Code", color = ComposeColor.White)
//                        }
//
//                        OutlinedTextField(
//                            value = receiverId,
//                            onValueChange = { receiverId = it },
//                            label = { Text("Receiver ID") },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                        OutlinedTextField(
//                            value = amount,
//                            onValueChange = { amount = it },
//                            label = { Text("Amount") },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        Button(
//                            onClick = {
//                                val amountLong = amount.toDoubleOrNull()
//                                if (receiverId.isNotEmpty() && amountLong != null && amountLong > 0) {
//                                    if (receiverId != uid) {
//                                        viewModel.makeTransaction(uid, receiverId, amountLong) {
//                                            viewModel.fetchCustomerData(uid) // ✅ Refresh balance after transaction
//                                        }
//                                    } else {
//                                        Toast.makeText(context, "You cannot send money to yourself", Toast.LENGTH_SHORT).show()
//                                    }
//                                } else {
//                                    Toast.makeText(context, "Invalid amount or receiver ID", Toast.LENGTH_SHORT).show()
//                                }
//                            },
//                            modifier = Modifier.fillMaxWidth(),
//                            colors = ButtonDefaults.buttonColors(containerColor = ComposeColor(0xFF00796B))
//                        ) {
//                            Text("Send2Money", color = ComposeColor.White)
//                        }
//                    }
//                } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            }
//        }
//    }
//}



fun generateQRCode(text: String): Bitmap? {
    val writer = QRCodeWriter()
    return try {
        val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bmp
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}
