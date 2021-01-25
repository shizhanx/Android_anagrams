/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.engedu.anagrams

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class AnagramsActivity : AppCompatActivity() {
    private lateinit var dictionary: AnagramDictionary
    private var currentWord: String? = null
    private var anagrams: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anagrams)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val assetManager = assets
        try {
            val inputStream = assetManager.open("words.txt")
            dictionary = AnagramDictionary(InputStreamReader(inputStream))
        } catch (e: IOException) {
            val toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG)
            toast.show()
        }
        // Set up the EditText box to process the content of the box when the user hits 'enter'
        val editText = findViewById<View>(R.id.editText) as EditText
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editText.imeOptions = EditorInfo.IME_ACTION_GO
        editText.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_NULL && event != null && event.action == KeyEvent.ACTION_DOWN) {
                processWord(editText)
                handled = true
            }
            handled
        }
    }

    private fun processWord(editText: EditText) {
        val resultView = findViewById<View>(R.id.resultView) as TextView
        var word = editText.text.toString().trim { it <= ' ' }.toLowerCase()
        if (word.isEmpty()) {
            return
        }
        var color = "#cc0029"
        if (dictionary.isGoodWord(word, currentWord) && anagrams.contains(word)) {
            anagrams.remove(word)
            color = "#00aa29"
        } else {
            word = "X $word"
        }
        resultView.append(Html.fromHtml(String.format("<font color=%s>%s</font><BR>", color, word)))
        editText.setText("")
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_anagrams, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    fun defaultAction(view: View?): Boolean {
        val gameStatus = findViewById<View>(R.id.gameStatusView) as TextView
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        val editText = findViewById<View>(R.id.editText) as EditText
        val resultView = findViewById<View>(R.id.resultView) as TextView
        if (currentWord == null) {
            currentWord = dictionary.pickGoodStarterWord()
            anagrams = dictionary.getAnagramsWithOneMoreLetter(currentWord!!)
            Log.d("sb", anagrams.toString())
            gameStatus.text = Html.fromHtml(String.format(START_MESSAGE, currentWord!!.toUpperCase(), currentWord))
            fab.setImageResource(android.R.drawable.ic_menu_help)
            fab.hide()
            resultView.text = ""
            editText.setText("")
            editText.isEnabled = true
            editText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            editText.setText(currentWord)
            editText.isEnabled = false
            fab.setImageResource(android.R.drawable.ic_media_play)
            currentWord = null
            resultView.append(TextUtils.join("\n", anagrams))
            gameStatus.append(" Hit 'Play' to start again")
        }
        return true
    }

    companion object {
        const val START_MESSAGE = "Find as many words as possible that can be formed by adding one letter to <big>%s</big> (but that do not contain the substring %s)."
    }
}