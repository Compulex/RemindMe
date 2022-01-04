package com.example.remindme

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.remindme.database.ReminderDatabase
import com.example.remindme.entities.AudioReminder
import com.example.remindme.entities.TextReminder
import com.example.remindme.viewmodels.RecordingsListViewModel
import com.example.remindme.viewmodels.TextListViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class ViewReminder : AppCompatActivity() {
    //views
    private lateinit var playAudio : ImageButton
    private lateinit var editText : EditText
    private lateinit var repeatBtn : Button
    private lateinit var cancelBtn : Button
    private lateinit var extraTime : TableRow
    private lateinit var extraAmt : EditText
    private lateinit var spinner: Spinner
    private lateinit var updateBtn : Button
    //data object
    private lateinit var curRecData : AudioReminder
    private lateinit var recVM : RecordingsListViewModel
    private lateinit var curTxtData : TextReminder
    private lateinit var txtVM : TextListViewModel
    //media
    private val mediaPlayer = MediaPlayer()
    //intent
    private lateinit var mainIntent : Intent


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reminder)

        //all views initialized
        playAudio = findViewById(R.id.play_audio)
        editText = findViewById(R.id.editTextTM)
        repeatBtn = findViewById(R.id.repeat_btn)
        cancelBtn = findViewById(R.id.cancel_btn)
        extraTime = findViewById(R.id.extra_time)
        extraAmt = findViewById(R.id.extra_amt)
        spinner = findViewById(R.id.spinner)
        updateBtn = findViewById(R.id.update_btn)

        //add list to spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.time,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //apply adapter to spinner
            spinner.adapter = adapter
        }//spinner

        //show edit text, spinner, and button to set extra time
        repeatBtn.setOnClickListener {
            extraTime.visibility = View.VISIBLE
            updateBtn.visibility = View.VISIBLE
        }

        //get database
        val db = ReminderDatabase.getInstance(this.application)

        //listview models
        recVM = RecordingsListViewModel(this.application)
        txtVM = TextListViewModel(this.application)

        //intent
        val getId = intent.getStringExtra("view")
        val split = getId?.split(":")
        val type = split?.get(0)
        val id = split?.get(1)?.toLong()

        //intent to Main
        mainIntent = Intent(this, MainActivity::class.java)

        if(type == "audio"){
            val list = db.audioReminderDao().getAllRecReminders()
            curRecData = AudioReminder("","",0,"","","")
            //println("HEERRRRREEE: ${curRecData.id}")
            for(rem in list){
                if(rem.id == id){
                    curRecData = rem
                }
            }
            //if user deletes item from list before alarm notification
            if(curRecData.id == 0L){
                //hide audio view
                playAudio.visibility = View.INVISIBLE
                editText.visibility = View.VISIBLE

                editText.setText("This reminder was deleted")
                repeatBtn.isEnabled = false
            }else{
                //set up media player
                mediaPlayer.setDataSource(curRecData.audioFile)
                mediaPlayer.prepare()

                playAudio.setOnClickListener { playPause() }
            }
        }
        else if(type == "text"){
            val list = db.textReminderDao().getAllTxtReminders()
            curTxtData = TextReminder("",0,"","","")
            //println("HEERRRRREEE: ${curTxtData.id}")
            for(rem in list){
                if(rem.id == id){
                    curTxtData = rem
                }
            }

            //hide audio view
            playAudio.visibility = View.INVISIBLE
            editText.visibility = View.VISIBLE

            if(curTxtData.id == 0L){
                editText.setText("This reminder was deleted")
                repeatBtn.isEnabled = false
            }else{
                editText.setText(curTxtData.description)
            }
        }//type - text

        //UPDATE
        updateBtn.setOnClickListener { update(type!!) }

        //do nothing
        cancelBtn.setOnClickListener { cancel(type!!) }
    }//onCreate

    private fun playPause(){
        //play
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.isLooping = false
        }

        if(!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            Log.i("AUDIO PLAY", "Audio is currently playing")

            //currently playing change image to pause
            playAudio.setImageDrawable(
                ContextCompat.getDrawable(this, android.R.drawable.ic_media_pause))
        }
        else{
            mediaPlayer.pause()
            Log.i("AUDIO PAUSE", "User just paused audio")

            playAudio.setImageDrawable(
                ContextCompat.getDrawable(this, android.R.drawable.ic_media_play))
        }

        mediaPlayer.setOnCompletionListener {
            Log.i("AUDIO FINISHED", "Audio is finished playing")

            playAudio.setImageDrawable(
                ContextCompat.getDrawable(this, android.R.drawable.ic_media_play))
        }
    }//playPause

    @RequiresApi(Build.VERSION_CODES.O)
    private fun update(type : String){
        var notify = ""
        var curId = ""
        var amtNum = 0
        var timeLength = ""

        //time set
        val date = LocalDate.now()
        val time = LocalTime.now()
        val dateStr = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        val timeStr = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))

        if(type == "audio"){
            curRecData.amountTime = extraAmt.text.toString().toInt()
            curRecData.timeLength = spinner.selectedItem.toString()
            curRecData.date = dateStr
            curRecData.time = timeStr

            //update audio reminder
            recVM.updateReminder(curRecData)

            notify = curRecData.title
            curId = "$type:${curRecData.id}"
            amtNum = curRecData.amountTime
            timeLength = curRecData.timeLength
        }
        else if(type == "text"){
            curTxtData.description = editText.text.toString()
            curTxtData.amountTime = extraAmt.text.toString().toInt()
            curTxtData.timeLength = spinner.selectedItem.toString()
            curTxtData.date = dateStr
            curTxtData.time = timeStr

            //update audio reminder
            txtVM.updateReminder(curTxtData)

            notify = curTxtData.description
            curId = "$type:${curTxtData.id}"
            amtNum = curTxtData.amountTime
            timeLength = curTxtData.timeLength
        }

        //set alarm notification
        val alarmNotification = AlarmNotification(this, notify, curId, amtNum, timeLength)
        alarmNotification.createNotificationChannel()
        alarmNotification.scheduleNotification()

        //back to main
        Toast.makeText(applicationContext, "Reminder Set!", Toast.LENGTH_SHORT).show()

        startActivity(mainIntent)
    }//update

    private fun cancel(type : String){
        if(type == "audio"){
            //no alarm set
            curRecData.amountTime = 0
            curRecData.timeLength = ""

            //update object
            recVM.updateReminder(curRecData)
        }
        else if(type == "text"){
            //no alarm set
            curTxtData.amountTime = 0
            curTxtData.timeLength = ""

            //update object
            txtVM.updateReminder(curTxtData)
        }

        startActivity(mainIntent)
    }//cancel
}