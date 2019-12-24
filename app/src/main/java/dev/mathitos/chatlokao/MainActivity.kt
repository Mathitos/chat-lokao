package dev.mathitos.chatlokao

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import dev.mathitos.chatlokao.message.MessageEntity
import dev.mathitos.chatlokao.ui.MessageAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var messagesDatabaseReference: DatabaseReference

    lateinit var messageListAdapter: MessageAdapter

    var username: String = "Anonymous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance()
        messagesDatabaseReference = firebaseDatabase.reference.child("messages")

        sendButton.setOnClickListener {
            sendMessage()
        }

        messagesDatabaseReference.addChildEventListener( object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val message: MessageEntity? = dataSnapshot.getValue(MessageEntity::class.java)
                if(message != null) {
                    messageListAdapter.messages.add(message)
                    messageListAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })

        messageListAdapter = MessageAdapter(this@MainActivity,  mutableListOf())

        messageList.adapter = messageListAdapter
    }

    private fun sendMessage() {
        val text = messageEditText.text.toString()
        val message = MessageEntity(username, text, null)

        messagesDatabaseReference.push().setValue(message)

        messageEditText.text.clear()
    }

    private fun showToaster ( text: String){
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
    }
}
