package com.xdjbx.bench.domain.datamodel

class TriggerTask (val triggerRules: List<TriggerRule>, val evalMs: Long) {

    fun evaluate(): Boolean {
        return triggerRules.all{it.evaluate()}
    }
}