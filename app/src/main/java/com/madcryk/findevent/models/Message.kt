package com.madcryk.findevent.models

data class Message (
    var username : String = "",
    var userUid : String = "",
    var text : String = "",
    var timeCreated: Long = 0
)
{}