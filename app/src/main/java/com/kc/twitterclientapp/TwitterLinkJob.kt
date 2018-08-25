package com.kc.twitterclientapp

import android.content.Context
import kotlinx.coroutines.experimental.CancellationException
import twitter4j.IDs
import twitter4j.TwitterException

class TwitterLinkJob(private val context:Context) {
    fun getFollow(): List<UserDTO>{
        val follows = mutableListOf<UserDTO>()
        val twitter = TwitterUtils.getTwitter(context)
        var ids: IDs
        val idsList = mutableListOf<Long>()
        var cursor: Long = -1L

        try {
            val mySelf = twitter.verifyCredentials()
            do {
                ids = twitter.getFriendsIDs(mySelf.id, cursor)
                for (id: Long in ids.iDs){
                    idsList.add(id)
                }
                cursor = ids.nextCursor
            } while (ids.hasNext())

            if (idsList.size <= 0){
                //取得IDsが0個だった場合return
                return follows
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

                val userResponseList = twitter.lookupUsers(*mlist.toLongArray())
                for (user in userResponseList){
                    if (StringMatcher.getCircleSpace(user.name) != ""){
                        val userDTO = UserDTO(user)
                        follows.add(userDTO)
                    }
                }
            }
        } catch (e: CancellationException){
            //cancel
            follows.clear()
            return follows
        } catch (e: TwitterException){
            e.printStackTrace()
        }

        return follows
    }
}