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

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private Set<String> wordSet;
    private Map<String, List<String>> lettersToWord;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        wordSet=new HashSet<>();
        lettersToWord=new HashMap<>();
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            String sortedWord=sortLetters(word);
            if(!lettersToWord.containsKey(sortedWord)){
                lettersToWord.put(sortedWord, new ArrayList<String>());
            }
            lettersToWord.get(sortedWord).add(word);
            wordSet.add(word);
        }
    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<>();
        String compareWord=sortLetters(targetWord);
        for(String word: wordSet){
            if(compareWord.equals(sortLetters(word)));
            result.add(word);
        }
        return result;
    }

    private String sortLetters(String s){
        char[] a=s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<>();
        for(char c='a'; c<='z'; c++){
            char[] arr=(word+c).toCharArray();
            Arrays.sort(arr);
            String s=new String(arr);
            if (lettersToWord.containsKey(s)) {
                for(String anagrams: lettersToWord.get(s)){
                    result.add(anagrams);
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        return "ceab";
    }
}
