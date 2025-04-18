package com.example.payapp.ui.theme.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance() //For creating database in Firestore

//    /** 🔹 Add Student Data */
//    fun addStudent(studentId: String, studentData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
//        db.collection("students").document(studentId)
//            .set(studentData)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Student added successfully!")
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error adding student", e)
//                onFailure(e)
//            }
//    }

//    /** 🔹 Fetch Student Profile */
//    fun fetchCustomerProfile(userId: String, onSuccess: (Map<String, Any>) -> Unit, onFailure: () -> Unit) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("accounts").document(userId).get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    Log.d("Firestore", "Document: ${document.data}") // ✅ Log data
//                    onSuccess(document.data ?: emptyMap())
//                } else {
//                    Log.e("Firestore", "No such document")
//                    onFailure()
//                }
//            }
//            .addOnFailureListener {
//                Log.e("Firestore", "Error fetching document", it)
//                onFailure()
//            }
//    }


    suspend fun updateAccountBalance(userId: String, newBalance: Long) {
        try {
            db.collection("accounts").document(userId)
                .update("account_balance", newBalance)
                .await()
            Log.d("Firestore", "Account balance updated successfully!")
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating account balance", e)
        }
    }

    suspend fun processTransaction(senderId: String, receiverId: String, amount: Long) {
        val senderRef = db.collection("accounts").document(senderId)
        val receiverRef = db.collection("accounts").document(receiverId)

        db.runTransaction { transaction ->
            val senderSnapshot = transaction.get(senderRef)
            val receiverSnapshot = transaction.get(receiverRef)

            val senderBalance = senderSnapshot.getLong("account_balance") ?: 0
            val receiverBalance = receiverSnapshot.getLong("account_balance") ?: 0

            if (senderBalance >= amount) {
                transaction.update(senderRef, "account_balance", senderBalance - amount)
                transaction.update(receiverRef, "account_balance", receiverBalance + amount)
            } else {
                throw Exception("Insufficient Balance")
            }
        }.await()  // Use `await()` for coroutine support
    }


//    /** 🔹 Fetch All Students (For Admin) */
//    fun fetchAllStudents(onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: (Exception) -> Unit) {
//        db.collection("students")
//            .get()
//            .addOnSuccessListener { result ->
//                val studentList = result.documents.mapNotNull { it.data }
//                onSuccess(studentList)
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error fetching students", e)
//                onFailure(e)
//            }
//    }
}