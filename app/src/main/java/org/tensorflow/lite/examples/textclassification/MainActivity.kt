package org.tensorflow.lite.examples.textclassification

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.examples.textclassification.client.Result
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient

class MainActivity : AppCompatActivity() {

    private var client: TextClassificationClient? = null
    private var resultTextView: TextView? = null
    private var inputEditText: EditText? = null
    private var handler: Handler? = null
    private var scrollView: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tfe_tc_activity_main)
        resultTextView = findViewById(R.id.result_text_view)
        inputEditText = findViewById(R.id.input_text)
        scrollView = findViewById(R.id.scroll_view)

        client = TextClassificationClient(applicationContext)
        handler = Looper.myLooper()?.let { Handler(it) }

        findViewById<Button>(R.id.button).setOnClickListener {
            if (inputEditText!!.text.isNotEmpty()) classify(inputEditText?.text.toString())
            else Toast.makeText(this, "Please enter you words", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        handler?.post { client?.load() }
    }

    override fun onStop() {
        super.onStop()
        handler?.post { client?.unload() }
    }

    private fun classify(text: String) {
        handler?.post {
            client?.classify(text)?.let { showResult(text, it) }
        }
    }

    private fun showResult(inputText: String, results: List<Result>) {
        runOnUiThread {
            var textToShow = "Input -> $inputText\nOutput -> "

            when {
                results.last().confidence.equals(0.6294476f) -> {
                    textToShow += "Can't define"
                }
                results.first().confidence > results.last().confidence -> {
                    textToShow += results.first().title + " Value -> " + results.first().confidence
                }
                results.first().confidence < results.last().confidence -> {
                    textToShow += results.last().title + " Value -> " + results.last().confidence
                }
            }

            textToShow += "\n------------------------------------\n"
            resultTextView?.append(textToShow)
            inputEditText?.text?.clear()
            scrollView?.post { scrollView?.fullScroll(View.FOCUS_DOWN) }
        }
    }
}