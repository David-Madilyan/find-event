package com.madcryk.findevent.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.madcryk.findevent.R
import com.madcryk.findevent.adapters.MessagesAdapter
import com.madcryk.findevent.models.Message
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.presenters.ChatPresenter
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var uuid : String
    private lateinit var chatPresenter: ChatPresenter
    private lateinit var messagesAdapter: MessagesAdapter
    private var messages : ArrayList<Message> = ArrayList()
    private var user = UserModel.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
    }

    private fun initViews() {
        uuid = intent.getStringExtra("uuid")
        titleChat.text = intent.getStringExtra("title")
        chatPresenter = ChatPresenter(this)

        messagesAdapter = MessagesAdapter(this)

        messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = messagesAdapter
        }

        loadMessages()

        messageEditText.addTextChangedListener( object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length!! > 0){
                    sendMessageButton.visibility = View.VISIBLE
                }else{
                    sendMessageButton.visibility = View.GONE
                }
            }

        })

        sendMessageButton.setOnClickListener {
            if(messageEditText.text.isNotEmpty()){
                val mes = Message()
                mes.text = messageEditText.text.toString()
                mes.userUid = user.uid
                mes.username = user.name
                messageEditText.setText("")
                chatPresenter.pushNewMessage(mes, uuid)
            }
        }
    }

    private fun loadMessages() {
        chatPresenter.messagesLoadListener(uuid)
    }

    fun setNewMessage(message: Message) {
        messagesAdapter.addMessage(message)
        val count =  messagesAdapter.itemCount
        messagesRecyclerView.layoutManager?.scrollToPosition(count - 1)
    }

    fun getUserUid(): String {
       return user.uid
    }
}