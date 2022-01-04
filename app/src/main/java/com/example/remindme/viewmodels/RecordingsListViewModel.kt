package com.example.remindme.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindme.database.ReminderDatabase
import com.example.remindme.entities.AudioReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordingsListViewModel(application: Application) : AndroidViewModel(application) {
    private var allReminders: MutableList<AudioReminder> = mutableListOf()
    private var mAllReminders: MutableLiveData<MutableList<AudioReminder>> = MutableLiveData()
    private val database: ReminderDatabase = ReminderDatabase.getInstance(this.getApplication())

    init {
        viewModelScope.launch (Dispatchers.IO){
            allReminders = database.audioReminderDao().getAllRecReminders().toMutableList()
            mAllReminders.postValue(allReminders)
        }
    }

    fun getReminders() : MutableLiveData<MutableList<AudioReminder>>{
        return mAllReminders
    }//getReminders

    fun insertReminder(audioReminder: AudioReminder){
        viewModelScope.launch (Dispatchers.IO){
            database.audioReminderDao().insertReminder(audioReminder)
            allReminders = database.audioReminderDao().getAllRecReminders().toMutableList()
            mAllReminders.postValue(allReminders)
        }
    }//insertReminder

    fun updateReminder(audioReminder: AudioReminder){
        viewModelScope.launch (Dispatchers.IO){
            database.audioReminderDao().updateReminder(audioReminder)
            allReminders = database.audioReminderDao().getAllRecReminders().toMutableList()
            mAllReminders.postValue(allReminders)
        }
    }//updateReminder

    fun deleteReminder(audioReminder: AudioReminder){
        viewModelScope.launch (Dispatchers.IO){
            database.audioReminderDao().deleteReminder(audioReminder)
            allReminders = database.audioReminderDao().getAllRecReminders().toMutableList()
            mAllReminders.postValue(allReminders)
        }
    }//deleteReminder
}