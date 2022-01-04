package com.example.remindme.entities

import androidx.room.*

@Entity(tableName="Recording")
class AudioReminder(
    @ColumnInfo(name="audioFile") var audioFile: String,
    @ColumnInfo(name="title") var title: String,
    @ColumnInfo(name="amountTime") var amountTime: Int,
    @ColumnInfo(name="timeLength") var timeLength: String,
    @ColumnInfo(name="date") var date: String,
    @ColumnInfo(name="time") var time: String)
{
    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}