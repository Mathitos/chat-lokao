package dev.mathitos.chatlokao

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dev.mathitos.chatlokao.message.MessageEntity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mMessagesDatabaseReference: DatabaseReference

    var username: String = "Anonymous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init Firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mMessagesDatabaseReference = mFirebaseDatabase.reference.child("messages")

        mMessagesDatabaseReference

        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    fun sendMessage() {
        val text = messageEditText.text.toString()
        val message = MessageEntity(username, text, null)

        mMessagesDatabaseReference.push().setValue(message)

        messageEditText.text.clear()
    }
}
