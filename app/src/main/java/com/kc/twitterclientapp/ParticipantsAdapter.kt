package com.kc.twitterclientapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import twitter4j.User

class ParticipantsAdapter(context: Context): ArrayAdapter<User>(context, android.R.layout.simple_list_item_1) {
    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val cv = convertView?: inflater.inflate(R.layout.adapter_item, null).also {
            it.tag = ItemViewHolder(it)
        }
        val holder = cv.tag as ItemViewHolder
        val item = getItem(position)

        holder.name.text = item.name
        holder.screenName.text = "@" + item.screenName
        holder.circleSpace.text = StringMatcher.getCircleSpace(item.name)
        holder.icon.setOnClickListener{
            val profileUrl = "twitter://user?screen_name=" + item.screenName
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(profileUrl))
            try {
                it.context.startActivity(intent)
            } catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }

        Glide.with(context).load(item.profileImageURL).into(holder.icon)

        return cv
    }

    private class ItemViewHolder(view: View){
        val name: TextView = view.findViewById(R.id.user_name)
        val screenName: TextView = view.findViewById(R.id.screen_name)
        val circleSpace: TextView = view.findViewById(R.id.circle_space)
        val icon: ImageView = view.findViewById(R.id.profile_icon)
    }
}