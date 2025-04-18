package com.example.payapp.ui.theme.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.payapp.ui.theme.dashboard.DashboardScreen
import com.example.payapp.ui.theme.entryManagement.AuthViewModel
import com.example.payapp.ui.theme.entryManagement.LoginScreen
import com.example.payapp.ui.theme.entryManagement.SignupScreen
import com.example.payapp.ui.theme.firestore.FirestoreViewModel
import com.example.payapp.ui.theme.payment.PaymentScreen
import com.example.payapp.ui.theme.qrscanner.QrScannerScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel // 🔹 AuthViewModel passed from MainActivity
) {
    val firestoreViewModel: FirestoreViewModel = viewModel() // ✅ Initialize FirestoreViewModel

    NavHost(
        navController = navController,
        startDestination = "login" // ✅ Ensure correct start destination
    ) {

        /** 🔹 Login Screen */
        composable("login") {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        /** 🔹 Signup Screen */
        composable("signup") {
            SignupScreen(navController = navController, authViewModel = authViewModel)
        }

        /** 🔹 Dashboard Screen (Requires userId) */
        composable("dashboard/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                DashboardScreen(
                    userId = userId, viewModel = firestoreViewModel,
                    navController = navController,
                ) // ✅ Pass ViewModel
            }
        }

        /** 🔹 Calling for Scanner() */
        composable("qr_scanner") {
            QrScannerScreen(navController = navController) { scannedId ->
                Log.d("QRScanner", "Scanned ID: $scannedId") // Debugging log

                if (scannedId.isNotEmpty()) {
                    navController.navigate("payment_screen/$scannedId")
                } else {
                    Log.e("QRScanner", "Invalid QR Code scanned")
                }
            }
        }


        composable("payment_screen/{receiverId}") { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: return@composable
            val senderId = FirebaseAuth.getInstance().currentUser?.uid // ✅ Get sender ID

            if (senderId != null) {
                PaymentScreen(navController, senderId, receiverId)
            } else {
                Log.e("PaymentScreen", "User not logged in!")
                navController.navigate("login") // Redirect to login if user is not authenticated
            }
        }

    }
}