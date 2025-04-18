package com.example.payapp.ui.theme.firestore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirestoreViewModel : ViewModel() {
    private val firestoreHelper = FirestoreHelper()

    private val _customerData = MutableStateFlow<Map<String, Any>?>(null)
    val customerData = _customerData.asStateFlow()

    /** 🔹 Listen for real-time changes in customer data */
    fun fetchCustomerData(userId: String) {
        val db = Firebase.firestore
        val userRef = db.collection("accounts").document(userId)

        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Firestore", "Error fetching data: ${e.message}")
                _customerData.value = emptyMap()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.data?.let {
                    // Ensure account_balance is always treated as Double
                    it.toMutableMap().apply {
                        this["account_balance"] = (this["account_balance"] as? Double) ?: 0.0
                    }
                } ?: emptyMap()

                Log.d("Firestore", "Data loaded: $data")
                _customerData.value = data
            } else {
                Log.w("Firestore", "Account document not found")
                _customerData.value = emptyMap()
            }
        }
    }




//    /** 🔹 Update Account Balance */
//    fun updateAccountBalance(userId: String, newBalance: Long) {
//        viewModelScope.launch {
//            firestoreHelper.updateAccountBalance(userId, newBalance)
//            fetchCustomerData(userId) // ✅ Refresh UI after update
//        }
//    }

    /** 🔹 Make a Transaction */
    fun makeTransaction(senderId: String, receiverId: String, amount: Double, onComplete: () -> Unit) {
        val db = Firebase.firestore
        val senderRef = db.collection("accounts").document(senderId)
        val receiverRef = db.collection("accounts").document(receiverId)

        db.runTransaction { transaction ->
            val senderSnapshot = transaction.get(senderRef)
            val receiverSnapshot = transaction.get(receiverRef)

            val senderBalance = senderSnapshot.getDouble("account_balance") ?: 0.0
            val receiverBalance = receiverSnapshot.getDouble("account_balance") ?: 0.0

            if (senderBalance < amount) throw Exception("Insufficient balance")

            val newSenderBalance = senderBalance - amount
            val newReceiverBalance = receiverBalance + amount

            transaction.update(senderRef, "account_balance", newSenderBalance)
            transaction.update(receiverRef, "account_balance", newReceiverBalance)
        }.addOnSuccessListener {
            Log.d("Transaction", "Transaction successful")
            onComplete()
        }.addOnFailureListener { e ->
            Log.e("Transaction", "Transaction failed: ${e.message}")
        }
    }

    fun addBalance(userId: String, amount: Double, onComplete: () -> Unit) {
        val db = Firebase.firestore
        val userRef = db.collection("accounts").document(userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentBalance = snapshot.getDouble("account_balance") ?: 0.0
            transaction.update(userRef, "account_balance", currentBalance + amount)
        }.addOnSuccessListener {
            onComplete()
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to add balance", it)
        }
    }

}
