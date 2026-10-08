package com.siarheikrupenich.trailermobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siarheikrupenich.trailermobile.viewmodel.state.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class EventStreamViewModel<Event : UiEvent, State, Result>(
    initialState: State
) : ViewModel() {

    protected val parentJob = Job()

    private val _state = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _state.asStateFlow()

    private val eventChannel = Channel<Event>(capacity = Channel.BUFFERED)

    private val stateMutex = Mutex()

    private val eventTransformerRegistry: MutableMap<Class<out Event>, suspend (Event) -> Result> =
        hashMapOf()

    private val commandTransformerRegistry: MutableMap<Class<out Event>, suspend (Event) -> Unit> =
        hashMapOf()

    init {
        startProcessing()
    }

    private fun startProcessing() {
        viewModelScope.launch(Dispatchers.Default + parentJob) {
            for (event in eventChannel) {
                try {
                    eventTransformerRegistry[event::class.java]?.invoke(event)?.let { result ->
                        sendResult(result)
                    }
                    commandTransformerRegistry[event::class.java]?.invoke(event)
                } catch (e: Exception) {
                    handleProcessingError(e)
                }
            }
        }
    }

    fun nextEvent(event: Event) {
        eventChannel.trySend(event)
    }

    suspend fun sendResult(result: Result) {
        stateMutex.withLock {
            val newState = reduce(lastState(), result)
            _state.emit(newState)
        }
    }

    fun lastState(): State = _state.value

    protected open fun handleProcessingError(throwable: Throwable) {
        // Hook for subclasses to handle processing errors
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> query(event: Class<T>, transformer: suspend (T) -> Result) {
        eventTransformerRegistry[event] = transformer as suspend (Event) -> Result
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> command(event: Class<out T>, transformer: suspend (T) -> Unit) {
        commandTransformerRegistry[event] = transformer as suspend (Event) -> Unit
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    protected abstract suspend fun reduce(previousState: State, result: Result): State
}
