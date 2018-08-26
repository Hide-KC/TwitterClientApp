package com.kc.twitterclientapp

import android.content.Context
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import twitter4j.IDs
import twitter4j.TwitterException

class TwitterLinkTask(private val context:Context, private val rootJob: Job?) {
    interface UIUpdateListener{
        fun update(count: Int)
    }

    fun cancel(){
        rootJob?.cancel()
    }

    fun getFollow(): List<UserDTO>{
        val follows = mutableListOf<UserDTO>()
        val twitter = TwitterUtils.getTwitter(context)
        var ids: IDs
        val idsList = mutableListOf<Long>()
        var cursor: Long = -1L

        launch(UI, parent = rootJob) {
            try {
                val mySelf = twitter.verifyCredentials()
                do {
                    ids = async{ twitter.getFriendsIDs(mySelf.id, cursor) }.await()
                    for (id: Long in ids.iDs){
                        idsList.add(id)
                    }
                    cursor = ids.nextCursor
                } while (ids.hasNext())

                if (idsList.size <= 0){
                    //取得IDsが0個だった場合return
                    return@launch
                }

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
                            val userDTO = UserDTO(user)
                            follows.add(userDTO)
                        }
                    }

                    if (context is UIUpdateListener){
                        context.update(follows.size)
                    }
                }
            } catch (e: CancellationException){
                //cancel
                follows.clear()
                return@launch
            } catch (e: TwitterException){
                e.printStackTrace()
            }
        }
        return follows
    }
}