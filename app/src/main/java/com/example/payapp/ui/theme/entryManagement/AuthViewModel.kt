package com.example.payapp.ui.theme.entryManagement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginState = _loginState.asStateFlow()

    /** 🔹 Handle Login */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginResult.Loading

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val userId = result.user?.uid
                    if (userId != null) {
                        _loginState.value = LoginResult.Success(userId)
                    } else {
                        _loginState.value = LoginResult.Failure("User ID not found")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AuthViewModel", "Login failed: ${e.message}")
                    _loginState.value = LoginResult.Failure(e.message ?: "Unknown error")
                }
        }
    }

    /** 🔹 Handle Logout */
    fun logout() {
        auth.signOut()
        _loginState.value = LoginResult.Idle
    }
}

/** 🔹 Sealed Class to Handle Login States */
sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    data class Success(val userId: String) : LoginResult()
    data class Failure(val errorMessage: String) : LoginResult()
}

//Ui -> ViewModel <- Backend/Database