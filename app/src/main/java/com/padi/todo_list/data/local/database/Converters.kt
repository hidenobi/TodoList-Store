package com.padi.todo_list.data.local.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun restoreListString(strList: String): ArrayList<String>? {
        return Gson().fromJson(strList, object : TypeToken<ArrayList<String?>?>() {}.type)
    }

    @TypeConverter
    fun saveListString(list: List<String>): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap):ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return  outputStream.toByteArray()
    }


    @TypeConverter
    fun toBitmap(byteArray: ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(byteArray,0, byteArray.size)
    }
}