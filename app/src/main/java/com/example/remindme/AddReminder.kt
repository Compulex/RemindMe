package com.example.remindme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.remindme.entities.AudioReminder
import com.example.remindme.entities.TextReminder
import com.example.remindme.viewmodels.RecordingsListViewModel
import com.example.remindme.viewmodels.TextListViewModel
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class AddReminder : AppCompatActivity() {
    private lateinit var outputFile : String
    private var now = Date()
    private var mediaRecorder : MediaRecorder? = null
    private var isPlaying : Boolean = false
    //private var onRepeat : Boolean = false
    private var isARecording : Boolean = false
    private lateinit var recData : AudioReminder
    private lateinit var recVM : RecordingsListViewModel
    private lateinit var txtData : TextReminder
    private lateinit var txtVM : TextListViewModel
    private var reminderType = ""
    private lateinit var recordBtn : ImageButton
    private lateinit var name : EditText
    private lateinit var descText : EditText
    private lateinit var amt : EditText
    private lateinit var timeSpin : Spinner
    //private var repeatToggle : ToggleButton

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        //check if user enabled recording - requesting permissions
        if(checkPermissions()){
            //permissions were not granted so request them
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        //initialize view models
        recVM = RecordingsListViewModel(this.application)

        txtVM = TextListViewModel(this.application)

        //all views initialized
        recordBtn = findViewById(R.id.record_btn)
        name = findViewById(R.id.name_et)
        descText = findViewById(R.id.reminder_desc)
        amt = findViewById(R.id.amount_et)
        //repeatToggle = findViewById(R.id.repeat_toggle)
        timeSpin = findViewById(R.id.time_spinner)

        //add list to spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.time,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //apply adapter to spinner
            timeSpin.adapter = adapter
        }//spinner - time

        //RECORD REMINDER
        recordBtn.setOnClickListener {
            //if button is pressed clear text
            descText.editableText.clear()

            if(!isPlaying)
                playAudio(it)
            else{//stopped recording
                stopAudio(it)
                //set alarm for reminder when done recording
                isARecording = true
            }
        }//onclick - record

        //TEXT REMINDER
        descText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                if(isPlaying)
                    stopAudio(recordBtn)

                //clear text to name the recording
                name.editableText.clear()

                isARecording = false
            }
        }//onFocus - text

        setReminder()
    }//onCreate

    private fun checkPermissions() : Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
    }//checkPermissions

    private fun playAudio(it : View){
        setMediaRecorder()
        try{
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isPlaying = true
            it.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.rec_start_red))
            Toast.makeText(applicationContext, "Recording started!", Toast.LENGTH_SHORT).show()
        }
        catch (e: IllegalStateException){
            e.printStackTrace()
        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }//playAudio

    private fun stopAudio(it: View){
        mediaRecorder?.stop()
        mediaRecorder?.release()
        isPlaying = false
        //change color for stop
        it.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.rec_stop_grey))
        Toast.makeText(applicationContext, "Recording stopped!", Toast.LENGTH_SHORT).show()
    }//stopAudio

    private fun setMediaRecorder(){
        outputFile = "${getExternalFilesDir(null)?.absolutePath}/recording_${now.time}.3gp"

        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder?.setOutputFile(outputFile)
    }//setMediaRecorder

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setReminder(){
        val setBtn = findViewById<Button>(R.id.set_btn)
        //set all inputs
        setBtn.setOnClickListener {
            val amtNum = amt.text.toString().toInt()

            val date = LocalDate.now()
            val time = LocalTime.now()
            val dateStr = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
            val timeStr = time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))

            //ADD to reminder objects
            if(isARecording){
                Log.i("FILE", outputFile)

                //add recording to database
                recData = AudioReminder(outputFile, name.text.toString(), amtNum,
                    timeSpin.selectedItem.toString(), dateStr, timeStr)

                recVM.insertReminder(recData)

                reminderType = "audio"
            }
            else{
                Log.i("DESCRIPTION ", descText.text.toString())

                //add text to database
                txtData = TextReminder(descText.text.toString(), amtNum,
                    timeSpin.selectedItem.toString(), dateStr, timeStr)

                txtVM.insertReminder(txtData)

                reminderType = "text"
            }

            Toast.makeText(applicationContext,"Reminder Set!",Toast.LENGTH_SHORT).show()

            //intent - go to home page to see added reminders
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("reminder_added", reminderType)
            startActivity(mainIntent)
        }//onclick - setBtn

        //start over
        val resetBtn = findViewById<Button>(R.id.reset_btn)
        resetBtn.setOnClickListener {
            if(outputFile != "" && File(outputFile).delete())
                Toast.makeText(applicationContext,"Reminder NOT Set!",Toast.LENGTH_SHORT).show()
            //clear inputs (start over)
            if(isPlaying)
                stopAudio(recordBtn)

            descText.isEnabled = true
            descText.editableText.clear()
            name.isEnabled = true
            name.editableText.clear()
            amt.editableText.clear()
            //repeatToggle.isChecked = false
        }//onclick - resetBtn

    }//setReminder

}//end class