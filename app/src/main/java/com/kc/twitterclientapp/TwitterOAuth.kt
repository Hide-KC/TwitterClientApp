package com.kc.twitterclientapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken

class TwitterOAuth(private val context: Context) {
    private val twitter: Twitter = TwitterUtils.getTwitter(context)
    private val callbackUrl = context.getString(R.string.callback_url)
    private lateinit var requestToken: RequestToken

    //認証開始。コルーチンで処理してみる。
    fun startAuthorize(){
        launch(UI) {
            val requestToken: String? = async {
                try {
                    requestToken = twitter.getOAuthRequestToken(callbackUrl)
                    return@async requestToken.authenticationURL
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: TwitterException) {
                    e.printStackTrace()
                }
                return@async null
            }.await()

            if (requestToken != null){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(requestToken))
                context.startActivity(intent)
            } else {
                Log.d(javaClass.simpleName, "Authorize失敗")
            }
        }
    }

    //ConfirmOAuthActivity#onNewIntentからコール
    fun oAuthApproval(context: Context, intent: Intent?){
        if (intent == null || intent.data == null || !intent.data.toString().startsWith(callbackUrl)){
            return
        }

        launch(UI) {
            val accessToken: AccessToken? = async {
                try {
                    return@async twitter.getOAuthAccessToken(requestToken, "oauth_verifier")
                } catch (e: TwitterException) {
                    e.printStackTrace()
                }
                return@async null
            }.await()

            if (accessToken != null) {
                //認証成功。AccessTokenを保存して終了。
                TwitterUtils.storeMyAccount(context, accessToken)
                Log.d("Comiketter", "認証成功！")
                Toast.makeText(context, context.getString(R.string.accesstoken_success), Toast.LENGTH_SHORT).show()
            } else {
                Log.d("Comiketter", "認証失敗。")
                Toast.makeText(context, context.getString(R.string.accesstoken_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
}