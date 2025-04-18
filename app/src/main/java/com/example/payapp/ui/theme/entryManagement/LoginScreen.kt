package com.example.payapp.ui.theme.entryManagement

import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by authViewModel.loginState.collectAsState()

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
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LOGIN",
                fontSize = 24.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
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
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("PASSWORD", color = textColor.copy(alpha = 0.8f)) },
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
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedButton(
                onClick = { authViewModel.login(email, password) },
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
                Text("LOGIN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (loginState) {
                is LoginResult.Loading -> CircularProgressIndicator(color = Color.White)
                is LoginResult.Success -> {
                    val userId = (loginState as LoginResult.Success).userId
                    LaunchedEffect(userId) {
                        navController.navigate("dashboard/$userId") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                is LoginResult.Failure -> {
                    Text(
                        text = (loginState as LoginResult.Failure).errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don't have an account? Sign up", color = textColor)
            }
        }
    }
}




//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//
//@Composable
//fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    val loginState by authViewModel.loginState.collectAsState()
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = { authViewModel.login(email, password) }) {
//            Text("Login")
//        }
//
//        /** 🔹 Handle Login State */
//        when (loginState) {
//            is LoginResult.Loading -> CircularProgressIndicator()
//            is LoginResult.Success -> {
//                val userId = (loginState as LoginResult.Success).userId
//                LaunchedEffect(userId) {
//                    navController.navigate("dashboard/$userId") { popUpTo("login") { inclusive = true } }
//                }
//            }
//            is LoginResult.Failure -> {
//                Text(text = (loginState as LoginResult.Failure).errorMessage, color = MaterialTheme.colorScheme.error)
//            }
//            else -> {}
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        TextButton(onClick = { navController.navigate("signup") }) {
//            Text("Don't have an account? Sign up")
//        }
//    }
//}