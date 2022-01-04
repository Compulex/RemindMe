package com.example.remindme.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.remindme.entities.AudioReminder

@Dao
interface AudioReminderDao {
    @Query("select * from recording")
    fun getAllRecReminders(): List<AudioReminder>

    @Insert(onConflict = REPLACE)
    fun insertReminder(audioReminder: AudioReminder)

    @Update(onConflict = REPLACE)
    fun updateReminder(audioReminder: AudioReminder)

    @Delete
    fun deleteReminder(audioReminder: AudioReminder)
}