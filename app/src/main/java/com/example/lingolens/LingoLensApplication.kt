package com.example.lingolens

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import com.example.lingolens.domain.repository.NotificationSettingsRepository
import com.example.lingolens.notification.NotificationHelper
import com.example.lingolens.notification.NotificationScheduler
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class LingoLensApplication : Application() {
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var notificationScheduler: NotificationScheduler
    @Inject lateinit var notificationSettingsRepository: NotificationSettingsRepository

    override fun onCreate() {
        super.onCreate()
        runCatching {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        }
        notificationHelper.createChannels()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            notificationScheduler.sync(notificationSettingsRepository.getSettings())
        }
    }
}
