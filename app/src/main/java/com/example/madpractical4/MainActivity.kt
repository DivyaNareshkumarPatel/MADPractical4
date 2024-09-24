package com.example.madpractical4

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val textClock = findViewById<TextClock>(R.id.textClock)
            textClock.format12Hour = "hh:mm:ss a MMM d, yyyy"
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val timePicker = findViewById<TimePicker>(R.id.reminderTime)
        timePicker.visibility = View.GONE
        timePicker.hour = getHour()
        timePicker.minute = getMinute()
        val createAlarmBtn = findViewById<Button>(R.id.createAlarmBtn)
        createAlarmBtn.setOnClickListener{
            timePicker.visibility = View.VISIBLE
        }
        val okbtn = findViewById<TextView>(R.id.okbtn)
        val cardView = findViewById<com.google.android.material.card.MaterialCardView>(R.id.materialCardView2)
        val textClock2 = findViewById<TextClock>(R.id.textClock2)
        cardView.visibility = View.GONE
        okbtn.setOnClickListener{
            val hour = timePicker.hour
            val minute = timePicker.minute
            setAlarm(getMillis(hour, minute), "Start")
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }
            val formattedTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(calendar.time)
            textClock2.text = formattedTime
            cardView.visibility = View.VISIBLE
            timePicker.visibility = View.GONE
        }
        val cancelbtn = findViewById<TextView>(R.id.cancelbtn)
        cancelbtn.setOnClickListener{
            timePicker.visibility = View.GONE
        }
        val cancelAlarmBtn = findViewById<Button>(R.id.cancelAlarm)
        cancelAlarmBtn.setOnClickListener {
            cardView.visibility = View.GONE
            setAlarm(0, "Stop")
        }
    }
    fun getCurrentDateTime(): String{
        val cal = Calendar.getInstance()
        val df : DateFormat = SimpleDateFormat("MMM, dd, yyyy, hh:mm:ss a")
        return df.format(cal.time)
    }
    fun getMillis(hour:Int, min:Int):Long{
        val setCalendar = Calendar.getInstance()
        setCalendar.set(Calendar.HOUR_OF_DAY, hour)
        setCalendar.set(Calendar.MINUTE, min)
        setCalendar.set(Calendar.SECOND, 0)
        if (setCalendar.timeInMillis <= System.currentTimeMillis()) {
            setCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return setCalendar.timeInMillis
    }
    fun getHour():Int{
        val cal = Calendar.getInstance()
        cal.time = Date()
        return cal[Calendar.HOUR_OF_DAY]
    }
    fun getMinute():Int{
        val cal = Calendar.getInstance()
        cal.time = Date()
        return cal[Calendar.HOUR_OF_DAY]
    }
    fun showTimerDialog(){
        val cldr : Calendar = Calendar.getInstance()
        val hour : Int = cldr.get(Calendar.HOUR_OF_DAY)
        val minute : Int = cldr.get(Calendar.MINUTE)
        val picker = TimePickerDialog(
            this,
            {tp, sHour, sMinute -> sendDialogDataToActivity(sHour, sMinute)},
            hour,
            minute,
            false
        )
        picker.show()
    }
    private fun sendDialogDataToActivity(hour:Int, minute:Int){
        val alarmCalender = Calendar.getInstance()
        val year: Int = alarmCalender.get(Calendar.YEAR)
        val month: Int = alarmCalender.get(Calendar.MONTH)
        val day: Int = alarmCalender.get(Calendar.DATE)
        alarmCalender.set(year, month, day, hour, minute, 0)
        val textAlarmTime = findViewById<TextClock>(R.id.textClock)
        textAlarmTime.text = SimpleDateFormat("hh:mm ss a").format(alarmCalender.time)
        setAlarm(alarmCalender.timeInMillis, "Start")
    }
    fun setAlarm(millisTime: Long, str:String){
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        intent.putExtra("Service1", str)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 234324234, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if(str == "Start"){
            if(alarmManager.canScheduleExactAlarms()){
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    millisTime,
                    pendingIntent
                )
            }
        }
        else if(str == "Stop"){
            alarmManager.cancel(pendingIntent)
            sendBroadcast(intent)
        }
    }
}