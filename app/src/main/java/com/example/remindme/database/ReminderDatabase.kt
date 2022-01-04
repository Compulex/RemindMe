package com.example.remindme.database

import android.content.Context
import androidx.room.*
import com.example.remindme.entities.TextReminder
import com.example.remindme.dao.AudioReminderDao
import com.example.remindme.dao.TextReminderDao
import com.example.remindme.entities.AudioReminder

@Database(entities = [AudioReminder::class, TextReminder::class], version = 5, exportSchema  = false)
abstract class ReminderDatabase : RoomDatabase(){
    abstract fun audioReminderDao(): AudioReminderDao

    abstract fun textReminderDao(): TextReminderDao

    companion object{
        //singleton
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ReminderDatabase {
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                ReminderDatabase::class.java,
                "reminder.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }
    }
}