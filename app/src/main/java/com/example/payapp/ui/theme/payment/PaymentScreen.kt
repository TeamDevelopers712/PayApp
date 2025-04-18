package com.example.payapp.ui.theme.payment

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun PaymentScreen(navController: NavController, senderId: String, receiverId: String) {
    var amount by remember { mutableStateOf("") }
    var showPinDialog by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Send Money", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Receiver ID: $receiverId", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Enter Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null && amountValue > 0) {
                    sendMoney(senderId, receiverId, amountValue, navController)
                } else {
                    Log.e("PaymentScreen", "Invalid amount entered")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Money")
        }

        if (showPinDialog) {
            PinDialog(
                onConfirm = { enteredPin ->
                    pin = enteredPin
                    verifyAndSendMoney(senderId, receiverId, amount.toDouble(), pin, navController)  // Correctly call here
                    showPinDialog = false
                },
                onDismiss = { showPinDialog = false }
            )
        }
    }
}

@Composable
fun PinDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var enteredPin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter PIN") },
        text = {
            Column {
                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = { enteredPin = it },
                    label = { Text("PIN") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(enteredPin) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun verifyAndSendMoney(senderId: String, receiverId: String, amount: Double, enteredPin: String, navController: NavController) {
    val firestore = Firebase.firestore

    // Get stored PIN from the Firestore document for the sender
    val senderRef = firestore.collection("accounts").document(senderId)
    senderRef.get()
        .addOnSuccessListener { document ->
            val storedPin = document.getString("pin")
            if (storedPin == enteredPin) {
                sendMoney(senderId, receiverId, amount, navController)
            } else {

            }
        }
        .addOnFailureListener { e ->
            Log.e("PaymentScreen", "Error verifying PIN: ${e.message}")
        }
}


fun sendMoney(senderId: String, receiverId: String, amount: Double, navController: NavController) {
    val firestore = Firebase.firestore

    firestore.runTransaction { transaction ->
        val senderRef = firestore.collection("accounts").document(senderId)
        val receiverRef = firestore.collection("accounts").document(receiverId)

        val senderSnapshot = transaction.get(senderRef)
        val receiverSnapshot = transaction.get(receiverRef)

        val senderBalance = senderSnapshot.getDouble("account_balance") ?: 0.0
        val receiverBalance = receiverSnapshot.getDouble("account_balance") ?: 0.0

        Log.d("Payment", "💰 Before - Sender: $senderBalance | Receiver: $receiverBalance")

        if (senderBalance >= amount) {
            val newSenderBalance = senderBalance - amount
            val newReceiverBalance = receiverBalance + amount

            Log.d("Payment", "✅ After - Sender: $newSenderBalance | Receiver: $newReceiverBalance")

            // ✅ Update only `account_balance`
            transaction.update(senderRef, "account_balance", newSenderBalance)
            transaction.update(receiverRef, "account_balance", newReceiverBalance)
        } else {
            throw Exception("❌ Insufficient balance!")
        }
    }.addOnSuccessListener {
        Log.d("Payment", "✅ Transaction successful!")
        navController.navigate("dashboard/$senderId") {
            popUpTo("dashboard/$senderId") { inclusive = true }
        }
    }.addOnFailureListener { e ->
        Log.e("Payment", "❌ Transaction failed: ${e.message}")
    }
}

//ToDo
//Improve the logic of payment