package fr.harkame.tp1.fragment.creation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.ImageViewCompat
import android.util.Log
import android.view.KeyEvent
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
import android.widget.ImageView
import android.widget.Toast
import fr.harkame.tp1.db.model.EventModel
import org.joda.time.format.DateTimeFormat
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.widget.TextView




class EventCreationFragment : Fragment()
{
    companion object
    {
        private const val TAG = "EventCreationFragment"
    }

    private lateinit var eventDBHelper : EventDBHelper

    private lateinit var buttonType : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_creation, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventDBHelper = EventDBHelper(this.context!!)

        val textView = view.findViewById<TextView>(R.id.eventCreationName)

        textView.setOnEditorActionListener { v, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_NEXT){
                view.findViewById<ImageView>(R.id.eventCreationNameError).visibility = View.INVISIBLE
                false
            } else {
                true
            }
        }

        val eventCreationDateButton = view.findViewById<Button>(R.id.eventCreationDate)

        eventCreationDateButton.text = DateUtils.dateTimeToString(DateTime.now())

        buttonType = view.findViewById(R.id.eventCreationType)

        buttonType.setOnClickListener {

            val builder = AlertDialog.Builder(this.context!!)
            builder.setTitle("Event type")

            builder.setItems(EventType.eventTypes) { dialog, which ->
                buttonType.text = EventType.getTypeFromID(which)
            }

            val dialog = builder.create()
            dialog.show()
        }

        eventCreationDateButton.setOnClickListener {
            val now = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this.context, DatePickerDialog.OnDateSetListener {_, year, month, dayOfMonth ->
                val realMonth = month + 1

                eventCreationDateButton.text = "$dayOfMonth/$realMonth/$year"
            },
            now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        val eventCreationValidate = view.findViewById<Button>(R.id.eventCreationValidate)

        eventCreationValidate.setOnClickListener {

            var validEvent = true

            val eventNameTextview = view.findViewById<EditText>(R.id.eventCreationName)

            val eventName = eventNameTextview.text.toString()

            if(eventName.isEmpty()) {
                Toast.makeText(this.context,
                        "Invalid title", Toast.LENGTH_LONG).show()

                view.findViewById<ImageView>(R.id.eventCreationNameError).visibility = View.VISIBLE

                validEvent = false
            }

            val eventDate = view.findViewById<Button>(R.id.eventCreationDate).text.toString()

            val eventType = buttonType.text.toString()

            var eventDescriptionTextView = view.findViewById<EditText>(R.id.eventCreationDescription)

            val eventDescription = eventDescriptionTextView.text.toString()

            if(validEvent) {
                val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
                val dateTime = formatter.parseDateTime(eventDate).withHourOfDay(23)

                eventDBHelper.insertEvent(EventModel(eventName, dateTime, DateUtils.dateTimeToString(dateTime), eventType, eventDescription, false))

                eventNameTextview.setText("")
                eventDescriptionTextView.setText("")

                Toast.makeText(this.context,
                        "Event created", Toast.LENGTH_LONG).show()
            }
        }
    }
}
