package fr.harkame.tp1.activity.creation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import fr.harkame.tp1.activity.home.HomeActivity
import fr.harkame.tp1.db.contract.EventType
import fr.harkame.tp1.db.util.DateUtils
import fr.harkame.tp1.db.helper.EventDBHelper
import fr.harkame.tp1.db.model.EventModel
import fr.tp1.harkame.tp1.R
import kotlinx.android.synthetic.main.activity_event_creation.*
import org.joda.time.DateTime
import java.util.*
import org.joda.time.format.DateTimeFormat

class EventCreationActivity : AppCompatActivity() {

    private lateinit var eventDBHelper : EventDBHelper

    private lateinit var buttonType : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_creation)

        eventDBHelper = EventDBHelper(this)

        val eventCreationDateButton = findViewById<Button>(R.id.eventCreationDate);

        eventCreationDateButton.text = DateUtils.dateTimeToString(DateTime.now())

        buttonType = findViewById(R.id.eventCreationType)


        buttonType.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose an animal")

            builder.setItems(EventType.eventTypes) { dialog, which ->
                buttonType.text = EventType.getTypeFromID(which)
            }

            val dialog = builder.create()
            dialog.show()
        }

        eventCreationDateButton.setOnClickListener {
            val now = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {_, year, month, dayOfMonth ->
                eventCreationDateButton.text = "$dayOfMonth/$month/$year"
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

            val eventType = buttonType.text.toString()

            val eventDescription = findViewById<EditText>(R.id.eventCreationDescription).text.toString()

            if(validEvent) {

                val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
                val dateTime = formatter.parseDateTime(eventDate).withHourOfDay(23)

                eventDBHelper.insertEvent(EventModel(eventName, dateTime, eventType, eventDescription, false))

                val intent = Intent(this, HomeActivity::class.java).apply {

                }
                startActivity(intent)
            }
        }
    }
}
