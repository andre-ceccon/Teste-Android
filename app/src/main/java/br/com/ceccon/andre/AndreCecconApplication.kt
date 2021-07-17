package br.com.ceccon.andre

import android.app.Application
import androidx.preference.PreferenceManager
import br.com.ceccon.andre.utils.ThemeManager
import timber.log.Timber

class AndreCecconApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        initTheme()
    }

    private fun initTheme() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        runCatching {
            ThemeManager.applyTheme(requireNotNull(preferences.getString("theme_key", "")))
        }.onFailure { exception ->
            Timber.e("Theme Manager: $exception")
        }
    }
}