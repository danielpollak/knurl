package com.knurl.app

import android.app.Application
import com.knurl.app.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class KnurlApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this, applicationScope) }
}
