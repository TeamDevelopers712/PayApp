package com.example.payapp.ui.theme.entryManagement

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current

    val backgroundColor = Color(0xFF000060) // Deep blue
    val textColor = Color.White
    val underlineColor = Color.White.copy(alpha = 0.5f)
    val buttonBorder = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SIGN UP",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("EMAIL", color = textColor.copy(alpha = 0.8f)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = underlineColor,
                    unfocusedBorderColor = underlineColor,
                    cursorColor = textColor,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = textColor.copy(alpha = 0.8f),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("PASSWORD", color = textColor.copy(alpha = 0.8f)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = underlineColor,
                    unfocusedBorderColor = underlineColor,
                    cursorColor = textColor,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = textColor.copy(alpha = 0.8f),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) pin = it
                },
                label = { Text("4-DIGIT PIN", color = textColor.copy(alpha = 0.8f)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = underlineColor,
                    unfocusedBorderColor = underlineColor,
                    cursorColor = textColor,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = textColor.copy(alpha = 0.8f),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedButton(
                onClick = { registerUser(email, password, pin, context, navController)  },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, buttonBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = textColor
                )
            ) {
                Text("REGISTER", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))


            TextButton(onClick = { navController.popBackStack() }) {
                Text("Already have an account? Log in", color = textColor)
            }
        }
    }
}


//@Composable
//fun SignupScreen(navController: NavController, authViewModel: AuthViewModel) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var passwordVisible by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Sign Up") },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00796B), titleContentColor = Color.White)
//            )
//        }
//    ) { paddingValues ->
//        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), contentAlignment = Alignment.Center) {
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Create an Account", fontSize = 24.sp, color = Color(0xFF00796B))
//                Spacer(modifier = Modifier.height(16.dp))
//
//                OutlinedTextField(
//                    value = email,
//                    onValueChange = { email = it },
//                    label = { Text("Email") },
//                    modifier = Modifier.fillMaxWidth(),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    label = { Text("Password") },
//                    modifier = Modifier.fillMaxWidth(),
//                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Button(
//                    onClick = { registerUser(email, password, context, navController) },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
//                ) {
//                    Text("Register", color = Color.White)
//                }
//            }
//        }
//    }
//}


fun registerUser(
    email: String,
    password: String,
    pin: String,
    context: Context,
    navController: NavController
) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val userId = result.user?.uid
            saveStudentData(userId, email, pin)
            navController.navigate("login")
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Signup Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
}


fun saveStudentData(userId: String?, email: String, pin: String) {
    val customerData = hashMapOf(
        "name" to "New User",
        "email" to email,
        "account_balance" to 0,
        "fees_paid" to false,
        "pin" to pin
    )


    FirebaseFirestore.getInstance().collection("accounts").document(userId!!)
        .set(customerData)
}