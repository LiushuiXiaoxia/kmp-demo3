package com.example.demo_03.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<S, I>(
    initialState: S,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)

    val state: StateFlow<S> = _state.asStateFlow()

    fun onIntent(intent: I) {
        handleIntent(intent)
    }

    protected fun setState(reducer: S.() -> S) {
        _state.update { current -> current.reducer() }
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(block = block)
    }

    fun clear() {
        viewModelScope.coroutineContext.cancel()
    }

    protected abstract fun handleIntent(intent: I)
}
