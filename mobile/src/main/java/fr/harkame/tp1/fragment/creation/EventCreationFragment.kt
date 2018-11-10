package fr.harkame.tp1.fragment.creation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Button
import fr.harkame.tp1.R
import fr.harkame.tp1.db.contract.EventType
import fr.harkame.tp1.db.util.DateUtils
import fr.harkame.tp1.db.helper.EventDBHelper
import org.joda.time.DateTime
import java.util.*
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import fr.harkame.tp1.db.model.EventModel
import org.joda.time.format.DateTimeFormat


class EventCreationFragment : Fragment() {

    companion object {
        private const val TAG = "EventCreationFragment"
    }

    private lateinit var eventDBHelper : EventDBHelper

    private lateinit var buttonType : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")


    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the xml file for the fragment
        return inflater.inflate(R.layout.fragment_event_creation, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventDBHelper = EventDBHelper(this.context!!)

        val eventCreationDateButton = view.findViewById<Button>(R.id.eventCreationDate)

        eventCreationDateButton.text = DateUtils.dateTimeToString(DateTime.now())

        buttonType = view.findViewById(R.id.eventCreationType)


        buttonType.setOnClickListener {

            val builder = AlertDialog.Builder(this.context!!)
            builder.setTitle("Types")

            builder.setItems(EventType.eventTypes) { dialog, which ->
                buttonType.text = EventType.getTypeFromID(which)
            }

            val dialog = builder.create()
            dialog.show()
        }

        eventCreationDateButton.setOnClickListener {
            val now = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this.context, DatePickerDialog.OnDateSetListener {_, year, month, dayOfMonth ->
                eventCreationDateButton.text = "$dayOfMonth/$month/$year"
            },
                    now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        val eventCreationValidate = view.findViewById<Button>(R.id.eventCreationValidate)

        eventCreationValidate.setOnClickListener {

            var validEvent = true

            var eventNameTextview = view.findViewById<EditText>(R.id.eventCreationName)

            val eventName = eventNameTextview.text.toString()

            if(eventName.isEmpty()) {
                Toast.makeText(this.context,
                        "Titre invalide", Toast.LENGTH_LONG).show()

                validEvent = false
            }

            val eventDate = view.findViewById<Button>(R.id.eventCreationDate).text.toString()

            val eventType = buttonType.text.toString()

            var eventDescriptionTextView = view.findViewById<EditText>(R.id.eventCreationDescription)

            val eventDescription = eventDescriptionTextView.text.toString()

            if(validEvent) {

                val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
                val dateTime = formatter.parseDateTime(eventDate).withHourOfDay(23)

                eventDBHelper.insertEvent(EventModel(eventName, dateTime, eventType, eventDescription, false))

                eventNameTextview.setText("")
                eventDescriptionTextView.setText("")

                Toast.makeText(this.context,
                        "Event created", Toast.LENGTH_LONG).show()
            }
        }
    }
}
