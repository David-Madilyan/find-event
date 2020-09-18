package com.madcryk.findevent.models

data class PosterModel(
    var uuid : String = "",
    var dateStart : String  = "",
    var address : String = "",
    var title : String = "",
    var description: String = "",
    var userUid : String = "",
    var userName : String = "",
    var userPhone : String = "",
    var isPhone : Boolean = false,
    var latitude : Double = 0.0,
    var longitude : Double = 0.0,

    var imageLeft : String = "",
    var imageCenter : String = "",
    var imageRight : String = ""

) {
}