package ir.kenar.ui.pairing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.kenar.R

@Composable
fun PairingScreen(
    onPaired: () -> Unit,
    viewModel: PairingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var partnerCode by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is PairingState.Paired) onPaired()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.pairing_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(Modifier.height(32.dp))

        // بخش ساخت کد دعوت
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(R.string.pairing_your_code_label))
                Spacer(Modifier.height(8.dp))

                if (state is PairingState.InviteCreated) {
                    Text(
                        text = (state as PairingState.InviteCreated).code,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    Button(
                        onClick = { viewModel.createInvite() },
                        enabled = state !is PairingState.Loading,
                    ) {
                        Text(stringResource(R.string.pairing_create_button))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(stringResource(R.string.pairing_or))

        Spacer(Modifier.height(24.dp))

        // بخش وارد کردن کد partner
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.pairing_enter_code_label))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = partnerCode,
                    onValueChange = { partnerCode = it },
                    label = { Text(stringResource(R.string.pairing_code_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.acceptInvite(partnerCode) },
                    enabled = partnerCode.isNotBlank() && state !is PairingState.Loading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.pairing_accept_button))
                }
            }
        }

        if (state is PairingState.Error) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = (state as PairingState.Error).message,
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (state is PairingState.Loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}