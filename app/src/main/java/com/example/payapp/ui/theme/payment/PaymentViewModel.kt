package com.example.payapp.ui.theme.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PaymentViewModel : ViewModel() {

    private val firestore = Firebase.firestore

    fun sendMoney(userId: String, receiverId: String, amount: Double, navController: NavController, fetchCustomerData: (String) -> Unit) {
        val firestore = Firebase.firestore

        firestore.runTransaction { transaction ->
            val senderRef = firestore.collection("accounts").document(userId)
            val receiverRef = firestore.collection("accounts").document(receiverId)

            val senderSnapshot = transaction.get(senderRef)
            val receiverSnapshot = transaction.get(receiverRef)

            val senderBalance = senderSnapshot.getDouble("account_balance") ?: 0.0
            val receiverBalance = receiverSnapshot.getDouble("account_balance") ?: 0.0

            if (senderBalance >= amount) {
                val newSenderBalance = senderBalance - amount
                val newReceiverBalance = receiverBalance + amount

                transaction.update(senderRef, "account_balance", newSenderBalance)
                transaction.update(receiverRef, "account_balance", newReceiverBalance)
            } else {
                throw Exception("❌ Insufficient balance!")
            }
        }.addOnSuccessListener {
            Log.d("Payment", "✅ Transaction successful!")
            fetchCustomerData(userId) // 🔥 Refresh balance after transaction
            navController.navigate("dashboard/$userId") {
                popUpTo("dashboard/$userId") { inclusive = true }
            }
        }.addOnFailureListener { e ->
            Log.e("Payment", "❌ Transaction failed: ${e.message}")
        }
    }
}
