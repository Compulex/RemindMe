package com.example.remindme.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.remindme.database.ReminderDatabase
import com.example.remindme.entities.TextReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextListViewModel(application: Application) : AndroidViewModel(application) {
    private var allReminders: MutableList<TextReminder> = mutableListOf()
    private var mAllReminders: MutableLiveData<MutableList<TextReminder>> = MutableLiveData()
    private val database: ReminderDatabase = ReminderDatabase.getInstance(this.getApplication())

    init{
        viewModelScope.launch (Dispatchers.IO){
            allReminders = database.textReminderDao().getAllTxtReminders().toMutableList()
            mAllReminders.postValue(allReminders)
        }
    }

    fun getReminders() : MutableLiveData<MutableList<TextReminder>>{
        return mAllReminders
    }//getReminders

    fun insertReminder(textReminder: TextReminder){
        viewModelScope.launch (Dispatchers.IO){
            database.textReminderDao().insertReminder(textReminder)
            allReminders = database.textReminderDao().getAllTxtReminders().toMutableList()
            mAllReminders.postValue(allReminders)
        }
    }//insertReminder

    fun updateReminder(textReminder: TextReminder){
        viewModelScope.launch (Dispatchers.IO){
            database.textReminderDao().updateReminder(textReminder)
            allReminders = database.textReminderDao().getAllTxtReminders().toMutableList()
            mAllReminders.postValue(allReminders)
        }
    }//updateReminder

    fun deleteReminder(textReminder: TextReminder){
        viewModelScope.launch (Dispatchers.IO){
            database.textReminderDao().deleteReminder(textReminder)
            allReminders = database.textReminderDao().getAllTxtReminders().toMutableList()
            mAllReminders.postValue(allReminders)
        }
    }//deleteReminder
}