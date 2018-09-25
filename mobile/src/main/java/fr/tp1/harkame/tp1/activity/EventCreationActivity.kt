package fr.tp1.harkame.tp1.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import fr.tp1.harkame.tp1.db.helper.EventDBHelper
import fr.tp1.harkame.tp1.EventModel
import fr.tp1.harkame.tp1.R
import kotlinx.android.synthetic.main.activity_event_creation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.jvm.java

class EventCreationActivity : AppCompatActivity() {

    private lateinit var eventDBHelper : EventDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_creation)

        eventDBHelper = EventDBHelper(this)

        val eventCreationDateButton = findViewById<Button>(R.id.eventCreationDate);

        eventCreationDateButton.text =  LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString()

        eventCreationDateButton.setOnClickListener {
            val now = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {_, year, month, dayOfMonth ->
                eventCreationDateButton.text = LocalDate.of(year, month + 1, dayOfMonth).toString()
            },
                    now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        eventCreationValidate.setOnClickListener {

            var validEvent = true

            val eventName = findViewById<EditText>(R.id.eventCreationName).text.toString()

            if(eventName.isEmpty()) {
                Toast.makeText(applicationContext,
                        "Titre invalide", Toast.LENGTH_LONG).show()

                validEvent = false
            }

            val eventDate = findViewById<Button>(R.id.eventCreationDate).text.toString()

            val eventType = findViewById<EditText>(R.id.eventCreationType).text.toString()

            if(eventType.isEmpty()) {
                Toast.makeText(applicationContext,
                        "Type invalide", Toast.LENGTH_LONG).show()

                validEvent = false
            }

            val eventDescription = findViewById<EditText>(R.id.eventCreationDescription).text.toString()

            if(validEvent) {

                val localDate = LocalDate.parse(eventDate, DateTimeFormatter.ISO_LOCAL_DATE)

                eventDBHelper.insertEvent(EventModel(eventName, localDate, eventType, eventDescription))

                val intent = Intent(this, HomeActivity::class.java).apply {

                }
                startActivity(intent)
            }
        }
    }
}
