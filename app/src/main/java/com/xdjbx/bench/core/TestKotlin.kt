package com.xdjbx.bench.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TestKotlin {

    fun getData(): Flow<Int> = flow {
        for (i in 1..5) {
            delay(100)
            emit(i)
        }
    }
}