package com.realityexpander.stateflowvsflowvsshareflowvslivedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    // Will work with Java (others will not)
    private val _liveData = MutableLiveData(0)
    val liveData: LiveData<Int> = _liveData

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow: StateFlow<Int> = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<Int>(0)
    val sharedFlow = _sharedFlow.asSharedFlow()

    var sharedFlowValue = 0


    fun triggerLiveData() {
        _liveData.value = liveData.value?.plus(1)
    }

    fun triggerStateFlow() {
        _stateFlow.value++
    }

    fun triggerSharedFlow() {
        viewModelScope.launch {
            // Note: no flow builder needed.
//            _sharedFlow.emit(sharedFlow.replayCache.last() + 1)
            _sharedFlow.emit(sharedFlowValue + 1)
        }
    }

    fun triggerFlow(): Flow<Int> {
        return flow {
            repeat(5) {
                emit(it)
                delay(500)
            }
        }
    }



}