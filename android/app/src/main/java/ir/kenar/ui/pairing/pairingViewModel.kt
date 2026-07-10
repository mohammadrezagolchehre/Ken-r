package ir.kenar.ui.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.kenar.core.network.ApiClient
import ir.kenar.core.network.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

sealed class PairingState {
    object Idle : PairingState()
    object Loading : PairingState()
    data class InviteCreated(val code: String) : PairingState()
    object Paired : PairingState()
    data class Error(val message: String) : PairingState()
}

@HiltViewModel
class PairingViewModel @Inject constructor(
    private val api: ApiClient,
) : ViewModel() {

    private val _state = MutableStateFlow<PairingState>(PairingState.Idle)
    val state: StateFlow<PairingState> = _state

    fun createInvite() {
        viewModelScope.launch {
            _state.value = PairingState.Loading
            try {
                val resp = api.post(path = "/v1/invites")
                val code = resp.getString("code")
                _state.value = PairingState.InviteCreated(code)
            } catch (e: ApiException) {
                _state.value = PairingState.Error(e.message ?: "خطا")
            }
        }
    }

    fun acceptInvite(code: String) {
        viewModelScope.launch {
            _state.value = PairingState.Loading
            try {
                api.post(
                    path = "/v1/invites/accept",
                    body = JSONObject().put("code", code),
                )
                _state.value = PairingState.Paired
            } catch (e: ApiException) {
                _state.value = PairingState.Error(e.message ?: "خطا")
            }
        }
    }
}
