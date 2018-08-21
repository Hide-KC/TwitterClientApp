package com.kc.twitterclientapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testList = listOf(
                "KC@技術書典5え06",
                "test@技術書典(あ01)",
                "技術書典A11",
                "技術書展あ01",
                "技術書典５",
                "技術書典５A01"
        )

        val builder = StringBuilder()
        for (testcase in testList){
            val space = StringMatcher.getCircleSpace(testcase)
            if (space != ""){
                builder.append(space).append("\n")
            }
        }

        spaces.text = builder.toString()
    }
}
