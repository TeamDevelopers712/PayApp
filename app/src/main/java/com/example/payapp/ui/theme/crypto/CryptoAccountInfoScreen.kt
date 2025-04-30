package com.example.payapp.ui.theme.crypto

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.payapp.ui.theme.firestore.FirestoreViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

fun sendEther(credentials: Credentials?, recipientAddress: String, amountToSend: String, web3j: Web3j): String {
    // Check if credentials are null
    if (credentials == null) {
        throw IllegalArgumentException("Credentials cannot be null")
    }

    // Convert amount to Wei
    val amountInWei = Convert.toWei(amountToSend, Convert.Unit.ETHER).toBigInteger()

    // Construct the transaction
    val transaction = Transaction.createEtherTransaction(
        credentials.address,
        null, // Nonce will be automatically managed
        BigInteger.valueOf(20000000000L), // Gas price in Wei (20 Gwei)
        BigInteger.valueOf(21000), // Gas limit (standard for ETH transfer)
        recipientAddress,
        amountInWei
    )

    // Send the transaction
    return try {
        val response = web3j.ethSendTransaction(transaction).sendAsync().get() // Block until we get a result
        response.transactionHash ?: throw Exception("Failed to send transaction: Transaction hash is null")
    } catch (e: Exception) {
        throw Exception("Failed to send transaction: ${e.message}")
    }
}

fun getBalance(web3j: Web3j, address: String?): BigDecimal? {
    return try {
        val ethGetBalance: EthGetBalance = web3j.ethGetBalance(address, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).sendAsync().get()
        Convert.fromWei(ethGetBalance.balance.toString(), Convert.Unit.ETHER)
    } catch (e: Exception) {
        throw e // Rethrow the exception for upper-level handling
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoAccountInfoScreen(web3j: Web3j,viewModel: FirestoreViewModel = viewModel()) {
    var privateKey by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf<BigDecimal?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var recipientAddress by remember { mutableStateOf("") }
    var amountToSend by remember { mutableStateOf("") }
    var transactionMessage by remember { mutableStateOf<String?>(null) }


    val viewModel: FirestoreViewModel = viewModel()
    val customerData by viewModel.customerData.collectAsState()

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val uid = firebaseUser?.uid ?: "Unknown UID"

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(uid) {
        viewModel.fetchCustomerData(uid)
    }


    // Change background color to black
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Set Background to Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp), // Increase padding for more whitespace
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            customerData?.let { data ->
                val address = data["address"] as? String ?: "No Address Found"
                val privateKey = data["private_key"] as? String ?: "No Private Key Found"

                Text(
                    text = "Address: $address",
                    color = Color.White,
                    modifier = Modifier
                        .clickable {
                            clipboardManager.setText(AnnotatedString(address))
                            Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                )
                Text(
                    text = "Private Key: $privateKey",
                    color = Color.White,
                    modifier = Modifier
                        .clickable {
                            clipboardManager.setText(AnnotatedString(privateKey))
                            Toast.makeText(context, "Private Key copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))
            } ?: run {
                Text(text = "Loading customer data...", color = Color.Gray)
            }

            OutlinedTextField(
                value = privateKey,
                onValueChange = { privateKey = it },
                label = { Text("Private Key", color = Color.White) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(0.9f) // Make input fields narrower for an attractive look
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display balance or error message
            balance?.let {
                Text(text = "Balance: $it ETH", color = Color.White)
            } ?: errorMessage?.let { error ->
                Text(text = "Error: $error", color = Color.Red)
            }

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val credentials = Credentials.create(privateKey)
                            balance = getBalance(web3j, credentials.address)
                        } catch (e: Exception) {
                            errorMessage = e.message
                            balance = null
                        } finally {
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier
                    .fillMaxWidth(0.9f) // Make button narrower
                    .padding(vertical = 12.dp) // Add vertical padding for better touch area
            ) {
                Text("Get Account Info", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = recipientAddress,
                onValueChange = { recipientAddress = it },
                label = { Text("Recipient Address", color = Color.White) },
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amountToSend,
                onValueChange = { amountToSend = it },
                label = { Text("Amount (ETH)", color = Color.White) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    transactionMessage = null
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val credentials = Credentials.create(privateKey)
                            val transactionHash = sendEther(credentials, recipientAddress, amountToSend, web3j)
                            transactionMessage = "Transaction successful: $transactionHash"
                        } catch (e: Exception) {
                            transactionMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier
                    .fillMaxWidth(0.9f) // Make button narrower
                    .padding(vertical = 12.dp)
            ) {
                Text("Send ETH", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color.White) // Updated to show white progress indicator
            } else {
                transactionMessage?.let { message ->
                    Text(text = message, color = Color.White)
                }
            }
        }
    }
}