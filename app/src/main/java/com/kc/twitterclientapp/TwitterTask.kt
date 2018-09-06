package com.kc.twitterclientapp

import android.content.Context
import android.util.Log
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.IDs
import twitter4j.TwitterException
import twitter4j.User

class TwitterTask(private val context:Context) {
    interface TwitterTaskListener{
        fun setInitCount(count: Int)
        fun update(count: Int)
        fun complete(participants: List<User>?)
    }

    private var rootJob: Job? = null
    private val listener: TwitterTaskListener? =
            when (context) {
                is TwitterTaskListener -> context
                else -> null
            }

    fun cancelAll(){
        Log.d(javaClass.simpleName, "cancel")
        if (rootJob == null) Log.d(javaClass.simpleName, "rootJob is null!")
        rootJob?.cancel()
        rootJob = null
    }

    fun createRootJob(){
        rootJob = Job()
    }

    fun getParticipants(){
        val twitter = TwitterUtils.getTwitter(context)
        val idsList = mutableListOf<LongArray>()
        var cursor: Long = -1L

        launch(UI, parent = rootJob) {
            try {
                var ids: IDs
                do {
                    ids = async{ twitter.getFriendsIDs(twitter.id, cursor) }.await()
                    val max = Math.ceil(ids.iDs.size / 100.0).toInt()
                    for (count in 0 until max){
                        idsList.add(ids.iDs.sliceArray(count*100 until (count+1)*100))
                    }
                    cursor = ids.nextCursor
                } while (ids.hasNext())

                if (idsList.size <= 0){
                    //取得IDsが0個だった場合return
                    listener?.complete(null)
                    return@launch
                } else {
                    //参加者抽出
                    listener?.setInitCount(idsList.size * 100)

                    val participants = mutableListOf<User>()
                    try {
                        //IDリストをぶん回しUserオブジェクトを取得
                        var cnt = 1
                        for (array in idsList){
                            listener?.update(cnt * 100)
                            val userResponseList = async { twitter.lookupUsers(*array) }.await()
                            for (user in userResponseList){
                                if (StringMatcher.getCircleSpace(user.name) != ""){
                                    participants.add(user)
                                }
                            }
                            cnt++
                        }

                        listener?.complete(participants)
                    } catch (e: CancellationException){
                        participants.clear()
                        idsList.clear()
                        return@launch
                    } finally {
                        participants.clear()
                        idsList.clear()
                    }
                }
            } catch (e: CancellationException){
                //cancel
                idsList.clear()
                return@launch
            } catch (e: TwitterException){
                e.printStackTrace()
            }
        }
    }
}