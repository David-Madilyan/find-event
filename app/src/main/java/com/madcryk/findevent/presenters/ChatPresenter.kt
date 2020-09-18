package com.madcryk.findevent.presenters

import co.metalab.asyncawait.async
import com.google.firebase.database.*
import com.google.firebase.database.core.ServerValues
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.madcryk.findevent.activities.ChatActivity
import com.madcryk.findevent.models.Message
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatPresenter(_view : ChatActivity) {
    private var view = _view
    private var dbRef: DatabaseReference = Firebase.database.reference

    fun messagesLoadListener( uuid : String ){
        async {
            dbRef.child("messages").child(uuid).addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue<Message>() as Message
                    view.setNewMessage(message)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}
            })
        }
    }

    fun pushNewMessage(message: Message, uuid: String){
        async {
            val m : HashMap<String, Any> = hashMapOf()
            m["text"] = message.text
            m["username"] = message.username
            m["userUid"] = message.userUid
            m["timeCreated"] = ServerValue.TIMESTAMP
            dbRef.child("messages").child(uuid).push().setValue(m)
        }
    }
}