package fr.harkame.tp1.fragment.details

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.util.Log
import android.widget.Button
import fr.harkame.tp1.R
import fr.harkame.tp1.db.contract.EventType
import fr.harkame.tp1.db.helper.EventDBHelper
import java.util.*
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import fr.harkame.tp1.db.model.EventModel
import org.joda.time.format.DateTimeFormat
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import fr.harkame.tp1.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_event_details.*


class EventDetailsFragment  : Fragment()
{
    companion object
    {
        private const val TAG = "EventDetailsFragment"

        fun newInstance(event : EventModel): EventDetailsFragment {
            val fragment = EventDetailsFragment()
            val args = Bundle()
            args.putSerializable("event", event)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var eventDBHelper : EventDBHelper

    private lateinit var buttonType : Button

    private lateinit var event : EventModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        event = this.arguments!!["event"] as EventModel

        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_details, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        eventDBHelper = EventDBHelper(this.context!!)

        val eventDetailsNameEditText = view.findViewById<EditText>(R.id.eventDetailsName)

        eventDetailsNameEditText.text = Editable.Factory.getInstance().newEditable(event.name)

        eventDetailsNameEditText.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_NEXT){
                view.findViewById<ImageView>(R.id.eventDetailsNameError).visibility = View.INVISIBLE
                false
            } else {
                true
            }
        }

        val eventDetailsDateButton = view.findViewById<Button>(R.id.eventDetailsDate)

        eventDetailsDateButton.text = event.dateText

        buttonType = view.findViewById(R.id.eventDetailsType)

        buttonType.text = event.type

        buttonType.setOnClickListener {

            val builder = AlertDialog.Builder(this.context!!)
            builder.setTitle("Event type")

            builder.setItems(EventType.eventTypes) { _, which ->
                buttonType.text = EventType.getTypeFromID(which)
            }

            val dialog = builder.create()
            dialog.show()
        }

        val eventDetailsDescriptionEditText = view.findViewById<EditText>(R.id.eventDetailsDescription)

        eventDetailsDescriptionEditText.text = Editable.Factory.getInstance().newEditable(event.description)

        eventDetailsDateButton.setOnClickListener {
            val now = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this.context, DatePickerDialog.OnDateSetListener {_, year, month, dayOfMonth ->
                val realMonth = month + 1

                eventDetailsDateButton.text = "$dayOfMonth/$realMonth/$year"
            },
                    now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        val eventDetailsModify = view.findViewById<Button>(R.id.eventDetailsModify)

        eventDetailsModify.setOnClickListener {

            var validEvent = true

            if (eventDetailsNameEditText.text.isEmpty()) {
                Toast.makeText(this.context,
                        "Invalid title", Toast.LENGTH_LONG).show()

                view.findViewById<ImageView>(R.id.eventDetailsNameError).visibility = View.VISIBLE

                validEvent = false
            }

            val eventDate = view.findViewById<Button>(R.id.eventDetailsDate).text.toString()

            if (validEvent) {
                val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
                val dateTime = formatter.parseDateTime(eventDate).withHourOfDay(23)

                event.name = eventDetailsNameEditText.text.toString()
                event.type = eventDetailsType.text.toString()
                event.description = eventDetailsDescriptionEditText.text.toString()
                event.date = dateTime
                event.dateText = eventDate

                eventDBHelper.updateEvent(event)

                Toast.makeText(this.context,
                        "Event modified", Toast.LENGTH_LONG).show()

                (this.activity as MainActivity).popFragment()
            }
        }

        val eventDetailsDeleteButton = view.findViewById<Button>(R.id.eventDetailsDelete)

        eventDetailsDeleteButton.setOnClickListener {
            eventDBHelper.deleteEvent(event)

            Toast.makeText(this.context,
                    "Event deleted", Toast.LENGTH_LONG).show()

            (this.activity as MainActivity).popFragment()
        }
    }
}
