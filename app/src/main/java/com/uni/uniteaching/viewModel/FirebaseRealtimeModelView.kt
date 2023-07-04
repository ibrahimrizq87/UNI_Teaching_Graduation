package com.uni.uniteaching.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uni.uniteaching.classes.Hall
import com.uni.uniteaching.data.FirebaseRealtimeRepo
import com.uni.uniteaching.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseRealtimeModelView @Inject constructor(
   private val repository: FirebaseRealtimeRepo
): ViewModel() {


    private val _startGeneratingCode= MutableStateFlow<Resource<String>?>(null)
    val startGeneratingCode=_startGeneratingCode.asStateFlow()

    fun startGeneratingCode (hall:Hall)= viewModelScope.launch {
        _startGeneratingCode.value= Resource.Loading
        repository.startGeneratingCode(hall){
            _startGeneratingCode.value=it
        }
    }
}


