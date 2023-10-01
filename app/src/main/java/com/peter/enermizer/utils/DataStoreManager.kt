package com.peter.enermizer.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    // Create the dataStore and give it a name same as user_pref
    // Create some keys we will use them to store and retrieve the data
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")
        val SETTINGS_IP_ADDRESS_KEY = stringPreferencesKey("SETTINGS_IPADDRESS")
        val SETTINGS_RELAY1_POWER_KEY = intPreferencesKey("SETTINGS_RELAY1POWER")
        val SETTINGS_RELAY2_POWER_KEY = intPreferencesKey("SETTINGS_RELAY2POWER")
    }

    // Store settings data
    // refer to the data store and using edit
    // we can store values using the keys
    suspend fun storeSettingsInfo(ipaddress: String, relay1Power: Int, relay2Power: Int) {
        context.dataStore.edit { preferences ->
            preferences[SETTINGS_IP_ADDRESS_KEY] = ipaddress
            preferences[SETTINGS_RELAY1_POWER_KEY] = relay1Power
            preferences[SETTINGS_RELAY2_POWER_KEY] = relay2Power
        }
    }

    // get the ipaddress from settings
    val settingsIPAddressFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SETTINGS_IP_ADDRESS_KEY] ?: ""
    }

    // get the relay1 power
    val settingsRelay1Power: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SETTINGS_RELAY1_POWER_KEY] ?: 0
    }

    // get the relay2 power
    val settingsRelay2Power: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SETTINGS_RELAY2_POWER_KEY] ?: 0
    }

}