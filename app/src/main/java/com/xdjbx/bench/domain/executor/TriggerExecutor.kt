package com.xdjbx.bench.domain.executor

import com.xdjbx.bench.domain.datamodel.TriggerTask

class TriggerExecutor (var triggerTaskList: MutableList<TriggerTask>) {

    fun evaluate(): Boolean {
        return triggerTaskList.all{it.evaluate()}
    }

}