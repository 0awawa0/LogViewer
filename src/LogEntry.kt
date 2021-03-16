package ru.awawa

const val COLOR_LOG_TITLE = "#BABABA"
const val COLOR_DEBUG = "#2FBA38"
const val COLOR_INFO = "#2887BA"
const val COLOR_WARN = "#E4C84F"
const val COLOR_ERROR = "#FF5370"
const val COLOR_ASSERT = "#FF162C"

data class LogEntry(
    val row: String,
    val text: String,
    val color: String
)