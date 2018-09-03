package com.kc.twitterclientapp

import android.content.Context
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.IDs
import twitter4j.TwitterException
import twitter4j.User

class TwitterTask(private val context:Context) {
    interface UpdateListener{
        fun update(count: Int)
    }

    private var rootJob: Job? = null

    fun cancelAll(){
        rootJob?.cancel()
        rootJob = null
    }

    fun setRootJob(){
        rootJob = Job()
    }

    fun getParticipants(): List<User>{
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
                    return@launch
                }
            } catch (e: CancellationException){
                //cancel
                idsList.clear()
                return@launch
            } catch (e: TwitterException){
                e.printStackTrace()
            }
        }
        return extractParticipants(idsList)
    }

    private fun extractParticipants(idsList: List<Long>): List<User>{
        val twitter = TwitterUtils.getTwitter(context)
        val participants = mutableListOf<User>()

        if (idsList.isEmpty()){
            return participants
        }

        launch(UI, parent = rootJob) {
            try {
                //IDリストをぶん回しUserオブジェクトを取得
                val max = Math.ceil(idsList.size / 100 * 1.0).toInt() + 1
                val mlist = mutableListOf<Long>()
                for (user_i: Int in 0 until max){
                    var counter = 0
                    mlist.clear()
                    while (true){
                        if (user_i * 100 + counter < idsList.size){
                            mlist.add(idsList[user_i * 100 + counter])
                        } else break

                        counter++
                        if (counter >= 100) break
                    }

                    val userResponseList = async { twitter.lookupUsers(*mlist.toLongArray()) }.await()
                    for (user in userResponseList){
                        if (StringMatcher.getCircleSpace(user.name) != ""){
                            participants.add(user)
                        }
                    }

                    if (context is UpdateListener){
                        context.update(participants.size)
                    }
                }
            } catch (e: CancellationException){
                participants.clear()
                return@launch
            } catch (e: TwitterException){
                e.printStackTrace()
            }
        }
        return participants
    }
}