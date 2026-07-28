package com.example.hiddengem.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.hiddengem.ui.theme.Amber
import com.example.hiddengem.ui.theme.Dusk

@Composable
fun LoginScreen(navController: NavController) {
    val auth = remember { FirebaseAuth.getInstance() }
    var isRegister by remember { mutableStateOf(false) }   // false = Log in, true = Register
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    fun goToMap() {
        navController.navigate("map") { popUpTo("login") { inclusive = true } }
    }

    fun submit() {
        error = null
        if (email.isBlank() || password.isBlank()) { error = "Enter an email and password"; return }
        if (password.length < 6) { error = "Password must be at least 6 characters"; return }
        loading = true
        val task = if (isRegister)
            auth.createUserWithEmailAndPassword(email.trim(), password)
        else
            auth.signInWithEmailAndPassword(email.trim(), password)

        task.addOnSuccessListener { loading = false; goToMap() }
            .addOnFailureListener {
                loading = false
                error = it.message ?: if (isRegister) "Sign up failed" else "Login failed"
            }
    }

    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(48.dp))
        Text("Hidden Gem", style = MaterialTheme.typography.headlineLarge, color = Dusk)
        Text(
            if (isRegister) "Create your account" else "Welcome back",
            style = MaterialTheme.typography.bodyLarge, color = Dusk
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            email, { email = it }, label = { Text("Email") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            password, { password = it }, label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { submit() }, enabled = !loading, modifier = Modifier.fillMaxWidth()) {
            if (loading)
                CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            else
                Text(if (isRegister) "Create account" else "Log in")
        }

        TextButton(onClick = { isRegister = !isRegister; error = null }, modifier = Modifier.fillMaxWidth()) {
            Text(
                if (isRegister) "Already have an account? Log in" else "New here? Create an account",
                color = Amber
            )
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}