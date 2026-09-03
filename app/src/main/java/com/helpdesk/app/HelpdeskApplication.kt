package com.helpdesk.app

import android.app.Application
import com.helpdesk.app.core.di.appModules
import com.helpdesk.app.core.di.coreModule
import com.helpdesk.app.core.di.dataModule
import com.helpdesk.app.core.di.domainModule
import com.helpdesk.app.core.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class HelpdeskApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@HelpdeskApplication)
            modules(listOf(coreModule, dataModule, domainModule, presentationModule))
        }
    }
}
