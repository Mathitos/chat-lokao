package dev.mathitos.chatlokao

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import dev.mathitos.chatlokao.message.MessageEntity
import dev.mathitos.chatlokao.ui.MessageAdapter
import kotlinx.android.synthetic.main.activity_main.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var messagesDatabaseReference: DatabaseReference
    private var databaseEventListener: ChildEventListener? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    lateinit var messageListAdapter: MessageAdapter

    private var username: String = "Anonymous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        messagesDatabaseReference = firebaseDatabase.reference.child("messages")

        sendButton.setOnClickListener {
            sendMessage()
        }

        addAuthStateListener()

        initMessageAdapter()
    }

    override fun onResume() {
        super.onResume()
        authStateListener?.let { firebaseAuth.addAuthStateListener(it) }
    }

    override fun onPause() {
        super.onPause()
        authStateListener?.let { firebaseAuth.removeAuthStateListener(it) }
    }

    private fun initMessageAdapter() {
        messageListAdapter = MessageAdapter(this@MainActivity,  mutableListOf())
        messageList.adapter = messageListAdapter
    }

    private fun buildDatabaseEventListener(){
       databaseEventListener =  object: ChildEventListener {
           override fun onCancelled(p0: DatabaseError) {
           }

           override fun onChildMoved(p0: DataSnapshot, p1: String?) {
           }

           override fun onChildChanged(p0: DataSnapshot, p1: String?) {
           }

           override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val message: MessageEntity? = dataSnapshot.getValue(MessageEntity::class.java)
                if(message != null) {
                    messageListAdapter.add(message)
                    messageListAdapter.notifyDataSetChanged()
                }
            }

           override fun onChildRemoved(p0: DataSnapshot) {
           }
       }
    }

    private fun addDatabaseEventListener(){
        if(databaseEventListener == null ){
            buildDatabaseEventListener()
            messagesDatabaseReference.addChildEventListener(databaseEventListener!!)
        }
    }

    private fun removeDatabaseEventListener(){
        databaseEventListener?.let { messagesDatabaseReference.removeEventListener(it) }
    }

    private fun addAuthStateListener(){
        authStateListener = FirebaseAuth.AuthStateListener {
            firebaseAuth ->
                if(firebaseAuth.currentUser != null) {
                    username = firebaseAuth.currentUser!!.displayName!!
                    addDatabaseEventListener()
                } else {
                    messageListAdapter.clear()
                    removeDatabaseEventListener()
                    createSignInIntent()
                }
        }
    }

    private fun sendMessage() {
        val text = messageEditText.text.trim().toString()
        if(text.isEmpty()) return

        val message = MessageEntity(username, text, null)

        messagesDatabaseReference.push().setValue(message)

        messageEditText.text.clear()
    }

    private fun showToaster ( text: String){
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if(user != null && user.displayName != null ) {
                    username = user.displayName!!
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                showToaster("deu ruim no login ${response.toString()}")
            }
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
    }

    companion object {

        private const val RC_SIGN_IN = 123
    }
}
