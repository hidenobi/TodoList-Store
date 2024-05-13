package com.padi.todo_list.common.extension

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Base64
import android.widget.RemoteViews
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.padi.todo_list.common.utils.ADD_ACTION
import com.padi.todo_list.common.utils.DateTimeFormat
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.service.WidgetStandardService
import com.padi.todo_list.ui.main.ACTION_UPDATE_UI_TASK_ACTIVITY
import com.padi.todo_list.ui.task.TaskFragment
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Bundle.serializableArrayList(key: String): ArrayList<T>? {
    val serializableList = serializable(key) as? ArrayList<*>
    return serializableList?.filterIsInstance<T>()?.toArrayList()
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    val arrayList = ArrayList<T>()
    arrayList.addAll(this)
    return arrayList
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun Long.formatLongToTime(dateTimeFormat: DateTimeFormat): String {
    val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(dateTimeFormat.value)
    return localDateTime.atZone(ZoneId.systemDefault()).format(formatter)
}
fun LocalDateTime.formatLocalDateTime(dateTimeFormat: DateTimeFormat): String {
    return this.format(DateTimeFormatter.ofPattern(dateTimeFormat.value))
}
fun Long.convertEpochToDateTime(epochTime: Long): String {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime), ZoneId.systemDefault())
    return dateTime.toString()
}

val Int.toDp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.toPx: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun RemoteViews.setOnClickPendingIntent(
    viewId: Int,
    context: Context,
    cls: Class<*>,
) {
    val pendingIntent = Intent(context, cls).run {
        action = ADD_ACTION
        PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            this,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
    this.setOnClickPendingIntent(viewId, pendingIntent)
}
fun Context.changeContextByLanguageCode(): Context {
    val languageCode = AppPrefs.languageCode
    val config = resources.configuration
    val locale = if (languageCode == "zh-rTW") {
        Locale("zh", "TW")
    } else {
        Locale(languageCode)
    }
    config.setLocale(locale)
    return createConfigurationContext(config)
}

fun RemoteViews.setRemoteAdapter(
    viewId: Int,
    context: Context,
    inputData: ArrayList<EventTaskUI>,
    key: String,
) {
    val bundle = Bundle().apply {
        putParcelableArrayList(key, inputData)
    }
    val intent = Intent(context, WidgetStandardService::class.java).apply {
        putExtra(key, bundle)
        action = "${key}_${System.currentTimeMillis()}"
    }
    this.setRemoteAdapter(viewId, intent)
}
fun String.convertToBitmap(): Bitmap? {
    val decodedString: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}
fun encodeBitmap(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
 fun Context.sendUpdateTaskFragmentIntent() {
    val intent = Intent().apply {
        action = TaskFragment.ACTION_UPDATE_UI_TASK_FRAGMENT
    }
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}
fun Context.sendUpdateTaskActivityIntent() {
    val intent = Intent().apply {
        action = ACTION_UPDATE_UI_TASK_ACTIVITY
    }
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}

fun String.toUri(): Uri = Uri.parse(this)

fun Context.setUpLanguage(): Context {
    val languageCode = AppPrefs.languageCode
    val config = resources.configuration
    val locale = if (languageCode == "zh-rTW") {
        Locale("zh", "TW")
    } else {
        Locale(languageCode)
    }
    config.setLocale(locale)
    return createConfigurationContext(config)
}

fun List<OffsetTimeUI>.deepCopy(): ArrayList<OffsetTimeUI> {
    return this.mapTo(arrayListOf()) { it.copy() }
}