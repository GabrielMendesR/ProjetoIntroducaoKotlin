package com.example.projetotrabalhofinal.db

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.projetotrabalhofinal.notification.EventNotificationReceiver


class EventDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "agenda.db"
        const val DATABASE_VERSION = 1

        const val TABLE_EVENTS = "events"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_EVENT_TIME = "event_time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = """
            CREATE TABLE $TABLE_EVENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_EVENT_TIME INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        onCreate(db)
    }

    fun insertEvent(title: String, description: String, eventTime: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_EVENT_TIME, eventTime)
        }
        return db.insert(TABLE_EVENTS, null, values)
    }

    fun deleteEvent(context: Context, id: Long): Int {
        val intent = Intent(context, EventNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(it)
            it.cancel()
        }

        val db = writableDatabase
        return db.delete(
            TABLE_EVENTS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun getUpcomingEvents(currentTime: Long): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_EVENTS,
            null,
            "$COLUMN_EVENT_TIME >= ?",
            arrayOf(currentTime.toString()),
            null,
            null,
            "$COLUMN_EVENT_TIME ASC"
        )
    }

    fun getPastEvents(currentTime: Long): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_EVENTS,
            null,
            "$COLUMN_EVENT_TIME < ?",
            arrayOf(currentTime.toString()),
            null,
            null,
            "$COLUMN_EVENT_TIME DESC"
        )
    }

    fun cursorToEventList(cursor: Cursor): List<Event> {
        val events = mutableListOf<Event>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val eventTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EVENT_TIME))
                events.add(Event(id, title, description, eventTime))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return events
    }
}

data class Event(
    val id: Long,
    val title: String,
    val description: String,
    val eventTime: Long
)