package com.hg.crs.demo07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hg.crs.demo07.common.Event
import com.hg.crs.demo07.common.LocalEventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SharedFlowViewModel : ViewModel() {

   private lateinit var job: Job

   fun startRefresh() {
       job = viewModelScope.launch(Dispatchers.IO) {
           while (true) {
               LocalEventBus.postEvent(Event(System.currentTimeMillis()))
           }
       }
   }

    fun stopRefresh() {
        job.cancel()
    }

}