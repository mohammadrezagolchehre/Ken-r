package ir.kenar.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.kenar.core.network.ApiClient
import ir.kenar.core.network.ApiException
import ir.kenar.data.session.Session
import ir.kenar.data.session.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object OtpSent : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ApiClient,
    private val sessionStore: SessionStore,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun requestOtp(phone: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                api.post(
                    path = "/v1/auth/otp/request",
                    body = JSONObject().put("phone", phone),
                    authenticated = false,
                )
                _state.value = AuthState.OtpSent
            } catch (e: ApiException) {
                _state.value = AuthState.Error(e.message ?: "خطا")
            }
        }
    }

    fun verifyOtp(phone: String, code: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val resp = api.post(
                    path = "/v1/auth/otp/verify",
                    body = JSONObject().put("phone", phone).put("code", code),
                    authenticated = false,
                )
                val token = resp.getString("token")
                val userId = resp.getString("user_id")
                sessionStore.save(Session(token = token, userId = userId))
                _state.value = AuthState.Success
            } catch (e: ApiException) {
                _state.value = AuthState.Error(e.message ?: "خطا")
            }
        }
    }
}