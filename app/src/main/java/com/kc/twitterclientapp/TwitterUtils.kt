package com.kc.twitterclientapp

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

object TwitterUtils {
    private enum class KeyEnum{
        TOKEN, TOKEN_SECRET
    }

    fun getTwitter(context: Context): Twitter {
        val twitter = TwitterFactory.getSingleton()

        //ConsumerKeyとConsumerSecretを入力
        Log.d(javaClass.simpleName, context.getString(R.string.consumer_key))
        twitter.setOAuthConsumer(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret))

        //AccessTokenを生成
        val accessToken = loadAccessToken(context)
        if (accessToken != null){
            twitter.oAuthAccessToken = accessToken
        } else {
            Log.d(javaClass.simpleName, "AccessToken is not seved")
        }

        //AccessTokenをセットしたTwitterオブジェクトを返す
        return twitter
    }

    fun storeMyAccount(context: Context, accessToken: AccessToken){
        storeAccessToken(context, accessToken.token, accessToken.tokenSecret)
    }

    fun loadAccessToken(context: Context): AccessToken?{
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val token = prefs.getString(KeyEnum.TOKEN.name, null)
        val tokenSecret = prefs.getString(KeyEnum.TOKEN_SECRET.name, null)

        return if (token != null && tokenSecret != null){
            AccessToken(token, tokenSecret)
        } else {
            null
        }
    }

    fun deleteAccessToken(context: Context){
        storeAccessToken(context, null, null)
    }

    private fun storeAccessToken(context: Context, token: String?, tokenSecret: String?){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString(KeyEnum.TOKEN.name, token)
        editor.putString(KeyEnum.TOKEN_SECRET.name, tokenSecret)
        editor.apply()
    }
}