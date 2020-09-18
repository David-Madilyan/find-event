package com.madcryk.findevent.models

data class EventModel(
    var uuid : String = "",
    var title: String = "",
    var body : String = "",
    var time : String = "",
    var timeCreate : String = "",
    var latitude : Double = 0.0,
    var longitude : Double = 0.0,
    var address: String = "",
    var userUuid : String = "",
    var userName : String = "",
    var userPhone : String = "",
    var maxPersons : Int = 0,
    var count : Int = 0,
    var isPhone : Boolean = false
) {
}