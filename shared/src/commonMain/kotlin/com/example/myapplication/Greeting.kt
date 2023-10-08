package com.example.myapplication

class Greeting() {
    private val platform: Platform = getPlatform()

    fun greet(listWifi: List<Any?>): String {
        return "Wifi list sorted by intensity: ${listWifi.toString()}"
    }
}