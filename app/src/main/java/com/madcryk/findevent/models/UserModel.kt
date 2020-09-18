package com.madcryk.findevent.models

data class UserModel(
    var name : String = "",
    var email : String = "",
    var uid : String = "",
    var city : String = "",
    var latitude : Double = 0.0,
    var longitude : Double = 0.0,
    var age : Int = 0,
    var events : Int = 0,
    var posters : Int = 0,
    var phone: String = ""
) {
    companion object  {
        val instance : UserModel = UserModel()
    }
}