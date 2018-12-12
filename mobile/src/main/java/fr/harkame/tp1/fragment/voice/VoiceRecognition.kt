package fr.harkame.tp1.fragment.voice

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import fr.harkame.tp1.R
import fr.harkame.tp1.R.layout.fragment_voice_recognition


class VoiceRecognition: Fragment() {


    companion object {
      private const val TAG = "VoiceRecoFragment"
      private const val res = "Result : "
      private const val REQUEST_SPEECH_RECOGNIZER = 3000
  }

    private lateinit var speakButton: Button
    private lateinit var mText: TextView
    private lateinit var recognizer: SpeechRecognizer
    private var mAnswer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(VoiceRecognition.TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragment_voice_recognition, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        speakButton = view.findViewById<Button>(R.id.speakButton)
        mText = view.findViewById<TextView>(R.id.voiceText)

        speakButton.setOnClickListener { speakButtonClicked(view) }
        recognizer = SpeechRecognizer.createSpeechRecognizer(activity)
        //recognizer.setRecognitionListener(voiceListener())

        verifyRecognitionService()

    }

    fun verifyRecognitionService(){
        var pm: PackageManager = activity?.packageManager!!
        Log.d("PackageManager : ",pm.toString())

        var activities = pm.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0)
        if(activities.size == 0) {
            speakButton.isEnabled = false
            speakButton.text = "Recognizer not present"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            var matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            for(matche in matches!!){
                Log.d("match : ", matche)
                if(matche == "création"){
                    mAnswer = matche
                }
            }
        mText.text = mAnswer

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun speakButtonClicked(view: View){
        Log.d("DEBUG :" , "Button Clicked !")
        startVoiceRecognitionActivity()
    }

    private fun startVoiceRecognitionActivity() {
        val intent  = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test")

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        startActivityForResult(intent, 1234)
    }



    inner class voiceListener :RecognitionListener{
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "OnReadyForSpeach")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d(TAG, "onRmsChanged")
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Log.d(TAG, "onBufferReceived")
        }

        override fun onPartialResults(partialResults: Bundle?) {
            Log.d(TAG, "onPartialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Log.d(TAG, "onEvent " + eventType)
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech")
        }

        override fun onError(error: Int) {
            Log.d(TAG,"error : " + error)

        }

        override fun onResults(results: Bundle?) {
            var str = ""
            Log.d(TAG, "onResults " + results)

            val datas = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if(datas?.size != 0){
                for(data in datas!!){
                    Log.d(TAG, "Result : " + data)
                    str += data
                }
                mAnswer = str
            }
        }
    }
}