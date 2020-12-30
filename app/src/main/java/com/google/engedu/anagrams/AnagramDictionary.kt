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

import java.io.BufferedReader
import java.io.Reader
import java.util.*

class AnagramDictionary(reader: Reader?) {
    private val random = Random()
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
        Arrays.sort(a)
        return String(a)
    }

    fun getAnagramsWithOneMoreLetter(word: String): List<String> {
        val result = ArrayList<String>()
        var c = 'a'
        while (c <= 'z') {
            val arr = (word + c).toCharArray()
            Arrays.sort(arr)
            val s = String(arr)
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
        val list: List<String>? = sizeToWords[wordLength]
        wordLength = Math.min(MAX_WORD_LENGTH, wordLength + 1)
        val size = list!!.size
        val start = random.nextInt(size)
        var i = start
        var j = start
        while (true) {
            if (i >= 0) {
                val word = list[i]
                if (getAnagramsWithOneMoreLetter(word).size >= MIN_NUM_ANAGRAMS) {
                    return list[i]
                }
            }
            if (j < size) {
                val word = list[j]
                if (getAnagramsWithOneMoreLetter(word).size >= MIN_NUM_ANAGRAMS) {
                    return list[j]
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
        wordList = ArrayList()
        wordSet = HashSet()
        lettersToWord = HashMap()
        sizeToWords = HashMap()
        var line: String
        while (`in`.readLine().also { line = it } != null) {
            val word = line.trim { it <= ' ' }
            val sortedWord = sortLetters(word)
            val size = word.length
            if (!lettersToWord.containsKey(sortedWord)) {
                lettersToWord[sortedWord] = LinkedList()
            }
            if (!sizeToWords.containsKey(size)) {
                sizeToWords[size] = LinkedList()
            }
            lettersToWord[sortedWord]!!.add(word)
            sizeToWords[size]!!.add(word)
            wordList.add(word)
            wordSet.add(word)
        }
        wordLength = DEFAULT_WORD_LENGTH
    }
}