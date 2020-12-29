package net.snatchdreams.spotifyclone.other

open class Event<out T>(private val data: T) {
    var hasBeanHandle = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeanHandle) {
            return null
        } else {
            hasBeanHandle = true
            return data
        }
    }

    fun peekContent() = data
}