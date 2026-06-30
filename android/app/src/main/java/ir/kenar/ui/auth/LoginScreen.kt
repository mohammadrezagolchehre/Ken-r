package ir.kenar.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.kenar.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is AuthState.Success) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.login_phone_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        if (state is AuthState.OtpSent || state is AuthState.Error) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(stringResource(R.string.login_otp_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
        }

        if (state is AuthState.Error) {
            Text(
                text = (state as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (state is AuthState.OtpSent || state is AuthState.Error) {
                    viewModel.verifyOtp(phone, code)
                } else {
                    viewModel.requestOtp(phone)
                }
            },
            enabled = state !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = if (state is AuthState.OtpSent || state is AuthState.Error)
                        stringResource(R.string.login_verify_button)
                    else
                        stringResource(R.string.login_send_otp_button)
                )
            }
        }
    }
}