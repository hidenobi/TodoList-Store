package com.padi.todo_list.data.local.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.padi.todo_list.common.utils.BundleKey.DAY_MONTH_YEAR
import com.padi.todo_list.common.utils.CategoryConstants.NO_CATEGORY_ID
import com.padi.todo_list.common.utils.ScopeTask.ALL
import com.padi.todo_list.common.utils.TimeFormatValue

object AppPrefs {
    private lateinit var prefs: SharedPreferences
    private val LOCK = Any()
    private val gson: Gson by lazy { Gson() }
    private const val IS_FIRST_APP = "isFirstAPP"
    private const val PREF_NAME = "MyPreferences"
    private const val ARRANGE_OPTION = "SELECTION_OPTION"
    private const val OPTION_DAY = "OPTION_DAY"
    private const val NOTE_TITLE = "NOTE_TITLE"
    private const val NOTE_CONTENT = "NOTE_CONTENT"
    private const val RING_TONE = "RING_TONE"
    private const val TIME_FORMAT = "TIME_FORMAT"
    private const val ID_CATEGORY = "ID_CATEGORY"
    private const val ENABLE_ADD_TASK_FROM_NOTIFICATION = "ENABLE_ADD_TASK_FROM_NOTIFICATION"
    private const val ENABLE_TODO_REMINDER = "ENABLE_TODO_REMINDER"
    private const val WIDGET_STANDARD_SCOPE_TIME = "WIDGET_STANDARD_SCOPE_TIME"
    private const val WIDGET_STANDARD_SCOPE_CATEGORY = "WIDGET_STANDARD_SCOPE_CATEGORY"
    private const val WIDGET_STANDARD_IS_SHOW_COMPLETED_TASK = "WIDGET_STANDARD_IS_SHOW_COMPLETED_TASK"
    private const val LANGUAGE_CODE = "LANGUAGE_CODE"
    private const val IS_CHANGE_LANGUAGE = "IS_CHANGE_LANGUAGE"
    private const val IS_FIRST_SCREEN_LANGUAGE = "IS_FIRST_SCREEN_LANGUAGE"
    private const val IS_FIRST_SCREEN_PERMISSION = "IS_FIRST_SCREEN_PERMISSION"
    private const val IS_FIRST_SCREEN_APP = "IS_FIRST_SCREEN_APP"

    fun init(context: Context) {
        if (!AppPrefs::prefs.isInitialized) {
            synchronized(LOCK) {
                prefs = PreferenceHelper.newEncryptPrefs(context, "todo_list_prefs")
            }
        }
    }

    fun isInitialized() = AppPrefs::prefs.isInitialized
    var isFirstApp: Boolean
        set(value) {
            prefs.edit { it.putBoolean(IS_FIRST_APP, value) }
        }
        get() {
            return prefs[IS_FIRST_APP, true] ?: true
        }

    fun saveArrangeTaskOption(s: String) {
        prefs.edit().putString(ARRANGE_OPTION, s).apply()
    }

    fun getArrangeTaskOption(): String? {
        return prefs.getString(ARRANGE_OPTION, null)
    }

    fun saveCompleteTone(s: Boolean) {
        prefs.edit().putBoolean(RING_TONE, s).apply()
    }
    fun getCompleteTone(): Boolean {
        return prefs.getBoolean(RING_TONE, true)
    }

    fun saveOptionDay(s: String) {
        prefs.edit().putString(OPTION_DAY, s).apply()
    }

    fun getOptionDay(): String? {
        return prefs.getString(OPTION_DAY, DAY_MONTH_YEAR)
    }

    fun saveEnableAddTaskFromNotification(bool: Boolean) {
        prefs.edit().putBoolean(ENABLE_ADD_TASK_FROM_NOTIFICATION, bool).apply()
    }

    fun getEnableAddTaskFromNotification(): Boolean {
        return prefs.getBoolean(ENABLE_ADD_TASK_FROM_NOTIFICATION, true)
    }

    fun saveEnableTodoReminder(bool: Boolean) {
        prefs.edit().putBoolean(ENABLE_TODO_REMINDER, bool).apply()
    }

    fun getEnableTodoReminder(): Boolean {
        return prefs.getBoolean(ENABLE_TODO_REMINDER, true)
    }


    fun saveTimeFormat(s: Int) {
        prefs.edit().putInt(TIME_FORMAT, s).apply()
    }

    fun getTimeFormat(): Int {
        return prefs.getInt(TIME_FORMAT, TimeFormatValue.FORMAT_24)
    }
    var isFirstLanguage: Boolean
        set(value) {
            prefs.edit { it.putBoolean(IS_FIRST_SCREEN_LANGUAGE, value) }
        }
        get() {
            return prefs[IS_FIRST_SCREEN_LANGUAGE, true] ?: true
        }
    var languageCode: String
        set(value) {
            prefs[LANGUAGE_CODE] = value
        }
        get() {
            return prefs[LANGUAGE_CODE, "en"] ?: "en"
        }
    @SuppressLint("CommitPrefEdits")
    fun saveIsChangeLanguage(value:Boolean){
        prefs.edit().putBoolean(IS_CHANGE_LANGUAGE,value).apply()
    }
    fun getIsFirstPermission():Boolean{
        return prefs.getBoolean(IS_FIRST_SCREEN_PERMISSION,true)
    }

    @SuppressLint("CommitPrefEdits")
    fun saveIsFirstPermission(){
        prefs.edit().putBoolean(IS_FIRST_SCREEN_PERMISSION,false).apply()
    }

    var scopeTime: Int
        set(value) {
            prefs[WIDGET_STANDARD_SCOPE_TIME] = value
        }
        get() {
            return prefs[WIDGET_STANDARD_SCOPE_TIME, ALL] ?: ALL
        }

    var scopeCategory: Long
        set(value) {
            prefs[WIDGET_STANDARD_SCOPE_CATEGORY] = value
        }
        get() {
            return prefs[WIDGET_STANDARD_SCOPE_CATEGORY, NO_CATEGORY_ID] ?: NO_CATEGORY_ID
        }

    var showCompletedTask: Boolean
        set(value) {
            prefs[WIDGET_STANDARD_IS_SHOW_COMPLETED_TASK] = value
        }
        get() {
            return prefs[WIDGET_STANDARD_IS_SHOW_COMPLETED_TASK, true] ?: true
        }

    var isFirstMainScrApp: Boolean
        set(value) {
            prefs[IS_FIRST_SCREEN_APP] = value
        }
        get() {
            return prefs[IS_FIRST_SCREEN_APP, false] ?: false
        }
}