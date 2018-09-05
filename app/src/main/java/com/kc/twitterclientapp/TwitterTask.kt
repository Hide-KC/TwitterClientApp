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
        fun initAccounts(count: Int)
        fun update(count: Int)
        fun complete(participants: List<User>?)
    }

    private var rootJob: Job? = null
    private val listener: TwitterTaskListener?

    init {
        listener = if (context is TwitterTaskListener){
            context
        } else {
            null
        }
    }

    fun cancelAll(){
        Log.d(javaClass.simpleName, "cancel")
        if (rootJob == null) Log.d(javaClass.simpleName, "rootJob is null!")
        rootJob?.cancel()
        rootJob = null
    }

    fun setRootJob(){
        rootJob = Job()
    }

    fun getParticipants(){
        val twitter = TwitterUtils.getTwitter(context)
        val idsList = mutableListOf<Long>()
        var cursor: Long = -1L

        launch(UI, parent = rootJob) {
            try {
                var ids: IDs
                do {
                    ids = async{ twitter.getFriendsIDs(twitter.id, cursor) }.await()
                    for (id: Long in ids.iDs){
                        idsList.add(id)
                    }
                    cursor = ids.nextCursor
                } while (ids.hasNext())

                if (idsList.size <= 0){
                    //取得IDsが0個だった場合return
                    listener?.complete(null)
                    return@launch
                } else {
                    //参加者抽出
                    listener?.initAccounts(idsList.size)

                    val participants = mutableListOf<User>()
                    try {
                        //IDリストをぶん回しUserオブジェクトを取得
                        val max = Math.ceil(idsList.size / 100.0).toInt()
                        for (user_i: Int in 0 until max){
                            val tmpList = if ((user_i + 1) * 100 > idsList.size){
                                idsList.subList(user_i * 100, idsList.size)
                            } else {
                                idsList.subList(user_i * 100, (user_i + 1) * 100)
                            }

                            listener?.update(user_i * 100)

                            val userResponseList = async { twitter.lookupUsers(*tmpList.toLongArray()) }.await()
                            for (user in userResponseList){
                                if (StringMatcher.getCircleSpace(user.name) != ""){
                                    participants.add(user)
                                }
                            }
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