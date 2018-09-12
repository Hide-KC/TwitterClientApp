package com.kc.twitterclientapp

abstract class Subject<T, E> {
    enum class States{
        WAIT, RUNNING, ERROR
    }

    protected val observers = mutableListOf<T>()
    private var state: States = States.WAIT

    fun attach(observer: T){
        observers.add(observer)
    }

    fun detach(observer: T): Boolean{
        for(i in observers.indices){
            //参照等価
            if (observers[i] == observer){
                observers.removeAt(i)
                return true
            }
        }
        return false
    }

    fun detachAll(){
        observers.clear()
    }

    abstract fun notify(value: E)

    protected fun getState(): States{
        return state
    }

    protected fun setState(state: States) {
        this.state = state
    }
}