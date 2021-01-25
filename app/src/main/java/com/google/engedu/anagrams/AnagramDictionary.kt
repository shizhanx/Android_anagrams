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

import android.util.Log
import java.io.BufferedReader
import java.io.Reader
import java.util.*
import kotlin.random.Random

class AnagramDictionary(reader: Reader) {
    private val wordList: MutableList<String>
    private val wordSet: MutableSet<String>
    private val lettersToWord: MutableMap<String, MutableList<String>?>
    private val sizeToWords: MutableMap<Int, MutableList<String>>
    private var wordLength: Int

    fun isGoodWord(word: String, base: String?): Boolean {
        return wordSet.contains(word) && !word.contains(base!!)
    }

    fun getAnagrams(targetWord: String): List<String> {
        val result = ArrayList<String>()
        val compareWord = sortLetters(targetWord)
        for (word in wordSet) {
            if (compareWord == sortLetters(word)) {
                result.add(word)
            }
        }
        return result
    }

    private fun sortLetters(s: String): String {
        val a = s.toCharArray()
        a.sort()
        return String(a)
    }

    fun getAnagramsWithOneMoreLetter(word: String): MutableList<String> {
        val result = mutableListOf<String>()
        var c = 'a'
        while (c <= 'z') {
            val s: String = sortLetters(word + c)
            if (lettersToWord.containsKey(s)) {
                for (anagram in lettersToWord[s]!!) {
                    if (!anagram.contains(word)) {
                        result.add(anagram)
                    }
                }
            }
            c++
        }
        return result
    }

    fun pickGoodStarterWord(): String {
        val list: List<String> = sizeToWords[wordLength]!!
        wordLength = (wordLength + 1).coerceAtMost(MAX_WORD_LENGTH)
        val size = list.size
        val start = Random.nextInt(size)
        var i = start
        var j = start
        while (true) {
            if (i >= 0) {
                val word = list[i]
                if (getAnagramsWithOneMoreLetter(word).size >= MIN_NUM_ANAGRAMS) {
                    return word
                }
            }
            if (j < size) {
                val word = list[j]
                if (getAnagramsWithOneMoreLetter(word).size >= MIN_NUM_ANAGRAMS) {
                    return word
                }
            }
            i--
            j++
        }
    }

    companion object {
        private const val MIN_NUM_ANAGRAMS = 5
        private const val DEFAULT_WORD_LENGTH = 3
        private const val MAX_WORD_LENGTH = 7
    }

    init {
        val `in` = BufferedReader(reader)
        wordList = mutableListOf()
        wordSet = mutableSetOf()
        lettersToWord = mutableMapOf()
        sizeToWords = mutableMapOf()
        var line: String?
        while (`in`.readLine().also { line = it } != null) {
            val word = line!!.trim { it <= ' ' }
            val sortedWord = sortLetters(word)
            val size = word.length
            if (!lettersToWord.containsKey(sortedWord)) {
                lettersToWord[sortedWord] = mutableListOf()
            }
            if (!sizeToWords.containsKey(size)) {
                sizeToWords[size] = mutableListOf()
            }
            lettersToWord[sortedWord]!!.add(word)
            sizeToWords[size]!!.add(word)
            wordList.add(word)
            wordSet.add(word)
        }
        wordLength = DEFAULT_WORD_LENGTH
    }
}