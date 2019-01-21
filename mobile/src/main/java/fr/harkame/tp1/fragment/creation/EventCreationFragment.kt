package fr.harkame.tp1.fragment.creation

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.Wearable
import fr.harkame.tp1.R
import fr.harkame.tp1.db.contract.EventType
import fr.harkame.tp1.db.helper.EventDBHelper
import fr.harkame.tp1.db.model.Event
import fr.harkame.tp1.db.util.DateUtils
import fr.harkame.tp1.receiver.NotificationReceiver
import fr.harkame.tp1.service.notification.NotificationAction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class EventCreationFragment : Fragment() {
    companion object {
        private const val TAG = "EventCreationFragment"
    }

    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var buttonType: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_creation, parent, false)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventDBHelper = EventDBHelper(context!!)

        val currentDateTime = DateTime.now()

        val textView = view.findViewById<TextView>(R.id.eventCreationName)

        textView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_NEXT) {
                view.findViewById<ImageView>(R.id.eventCreationNameError).visibility = View.INVISIBLE
                false
            } else {
                true
            }
        }

        val eventCreationDateButton = view.findViewById<Button>(R.id.eventCreationDate)

        val eventCreationTimeButton = view.findViewById<Button>(R.id.eventCreationTime)

        eventCreationDateButton.text = DateUtils.dateTimeToString(currentDateTime)

        if(currentDateTime.minuteOfHour < 10)
            eventCreationTimeButton.text = "${currentDateTime.hourOfDay}:0${currentDateTime.minuteOfHour}"
        else
            eventCreationTimeButton.text = "${currentDateTime.hourOfDay}:${currentDateTime.minuteOfHour}"

        buttonType = view.findViewById(R.id.eventCreationType)

        buttonType.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Event type")

            builder.setItems(EventType.eventTypes) { _, which ->
                buttonType.text = EventType.getTypeFromID(which)
            }

            val dialog = builder.create()
            dialog.show()
        }

        eventCreationDateButton.setOnClickListener {
            val now = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this.context!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val realMonth = month + 1

                eventCreationDateButton.text = "$dayOfMonth/$realMonth/$year"
            },
                    now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        eventCreationTimeButton.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this.context, TimePickerDialog.OnTimeSetListener(function = { _, hourOfDay, minuteOfHour ->

                if(minuteOfHour < 10)
                    eventCreationTimeButton.text = "$hourOfDay:0$minuteOfHour"
                else
                    eventCreationTimeButton.text = "$hourOfDay:0$minuteOfHour"

            }), currentDateTime.hourOfDay, currentDateTime.minuteOfHour, false)

            timePickerDialog.show()
        }

        val eventCreationValidate = view.findViewById<Button>(R.id.eventCreationValidate)

        eventCreationValidate.setOnClickListener {

            var validEvent = true

            val event = Event()

            event.name = view.findViewById<EditText>(R.id.eventCreationName).text.toString()

            if (event.name.isEmpty()) {
                Toast.makeText(this.context,
                        "Invalid title", Toast.LENGTH_LONG).show()

                view.findViewById<ImageView>(R.id.eventCreationNameError).visibility = View.VISIBLE

                validEvent = false
            }

            val eventDate = view.findViewById<Button>(R.id.eventCreationDate).text.toString()

            val eventTime = view.findViewById<Button>(R.id.eventCreationTime).text.toString()

            event.type = buttonType.text.toString()

            event.description = view.findViewById<EditText>(R.id.eventCreationDescription).text.toString()

            if (validEvent) {
                val formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                event.date = formatter.parseDateTime("$eventDate $eventTime")

                event.dateText = "$eventDate $eventTime"

                event.notification = true

                eventDBHelper.insertEvent(event)

                val notifyIntent = Intent(
                        this.activity!!.baseContext
                        , NotificationReceiver::class.java).apply {

                    putExtra("event", event)
                }

                val pendingIntent = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.date.millis, pendingIntent)

                Toast.makeText(context,
                        "Event created", Toast.LENGTH_LONG).show()
            }
        }
    }
}
