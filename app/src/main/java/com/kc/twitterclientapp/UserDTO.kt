package com.kc.twitterclientapp

import twitter4j.User

data class UserDTO(
        val id: Long,
        val name: String,
        val screen_name: String,
        val icon_url: String
){
    constructor(user: User) : this(
            id = user.id,
            name = user.name,
            screen_name = user.screenName,
            icon_url = user.profileImageURL
    )
}