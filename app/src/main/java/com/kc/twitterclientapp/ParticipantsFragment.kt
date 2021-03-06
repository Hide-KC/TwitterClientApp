package com.kc.twitterclientapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import twitter4j.User

class ParticipantsFragment: Fragment() {
    private lateinit var participants: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_participants, container, false)
        participants = view.findViewById(R.id.participant_list)
        return view
    }

    fun setAdapter(users: List<User>){
        if (context != null){
            val adapter = if (participants.adapter != null){
                (participants.adapter as ParticipantsAdapter).also { it.clear() }
            } else {
                ParticipantsAdapter(context!!)
            }

            for (user in users){
                adapter.add(user)
            }
            participants.adapter = adapter
        }
    }

    fun clear(){
        if (participants.adapter != null){
            (participants.adapter as ParticipantsAdapter).clear()
        }
    }

    companion object{
        fun newInstance(target: Fragment?): ParticipantsFragment{
            val fragment = ParticipantsFragment()
            return fragment
        }
    }
}