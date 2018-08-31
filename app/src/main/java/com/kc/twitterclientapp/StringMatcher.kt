package com.kc.twitterclientapp

import java.text.Normalizer
import java.util.regex.Pattern

object StringMatcher {
    private val EVENT_PATTERN = "書[典展]"
    private val SPACE_PATTERN = ".*([a-zA-ZＡ-Ｚあ-んア-ン]).?([0-9０-９][0-9０-９])"

    fun getCircleSpace(name: String): String{
        if (!hasEventPattern(name)) return ""

        val pattern = Pattern.compile(SPACE_PATTERN)
        val matcher = pattern.matcher(name)

        return if (matcher.find()){
            Normalizer.normalize(matcher.group(1)+matcher.group(2), Normalizer.Form.NFKC)
        } else {
            ""
        }
    }

    private fun hasEventPattern(name: String): Boolean{
        val pattern = Pattern.compile(EVENT_PATTERN)
        val matcher = pattern.matcher(name)

        return matcher.find()
    }
}