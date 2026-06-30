package ir.kenar.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class Session(val token: String, val userId: String)

private val Context.sessionDataStore by preferencesDataStore(name = "session")

@Singleton
class SessionStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val session: Flow<Session?> = context.sessionDataStore.data.map { prefs ->
        val token = prefs[TOKEN] ?: return@map null
        Session(token = token, userId = prefs[USER_ID].orEmpty())
    }

    suspend fun currentToken(): String? = session.firstOrNull()?.token

    suspend fun save(session: Session) {
        context.sessionDataStore.edit { prefs ->
            prefs[TOKEN] = session.token
            prefs[USER_ID] = session.userId
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }

    private companion object {
        val TOKEN = stringPreferencesKey("token")
        val USER_ID = stringPreferencesKey("user_id")
    }
}