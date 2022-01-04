package com.example.remindme.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.remindme.entities.TextReminder

@Dao
interface TextReminderDao {
    @Query("select * from text")
    fun getAllTxtReminders(): List<TextReminder>

    @Insert(onConflict = REPLACE)
    fun insertReminder(textReminder: TextReminder)

    @Update(onConflict = REPLACE)
    fun updateReminder(textReminder: TextReminder)

    @Delete
    fun deleteReminder(textReminder: TextReminder)
}