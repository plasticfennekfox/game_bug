package com.example.game_bug

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.icu.text.ListFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Collections

internal class StoredData {
    private val RECORDS_FILE = "RECORDS_FILE"
    private val RECORDS_KEY = "RECORDS_DATA"
    fun saveNewRecord(context: Context, item: RecordItem?) {
        val gson = Gson()
        val prefs = context.getSharedPreferences(RECORDS_FILE, MODE_PRIVATE)
        val editor = prefs.edit()
        var records: ArrayList<RecordItem?>? = null
        val data = prefs.getString(RECORDS_KEY, null)
        if (data != null) {
            val collectionType: Type? = object : TypeToken<ArrayList<RecordItem?>?>() {}.type
            records = gson.fromJson(data, collectionType)
        }
        if (records == null) {
            records = ArrayList()
        }
        records.add(item)
        Collections.sort(records, object : Comparator<RecordItem?> {
            override fun compare(o1: RecordItem?, o2: RecordItem?): Int {
                if (o1 == null || o2 == null) {
                    return 0 // Здесь можно выбрать желаемое значение, когда один из объектов равен null
                }
                return o2.KillCount.compareTo(o1.KillCount)
            }
        })

        val json: String = gson.toJson(records)
        editor.putString(RECORDS_KEY, json)
        editor.apply()
    }

    fun getAllRecords(context: Context): ArrayList<RecordItem?> {
        val gson = Gson()
        val prefs = context.getSharedPreferences(RECORDS_FILE, MODE_PRIVATE)
        var records: ArrayList<RecordItem?>? = null
        val data = prefs.getString(RECORDS_KEY, null)
        if (data != null) {
            val collectionType: Type = object : TypeToken<ArrayList<RecordItem?>?>() {}.getType()
            records = gson.fromJson(data, collectionType)
        }
        if (records == null) {
            records = ArrayList()
        }
        return records
    }

    fun deleteAllRecords(context: Context) {
        val prefs = context.getSharedPreferences(RECORDS_FILE, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(RECORDS_KEY, null)
        editor.apply()
    }
}