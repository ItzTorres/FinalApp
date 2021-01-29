package com.igt.finalapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.igt.finalapp.R
import com.igt.finalapp.adapters.ChatAdapter
import com.igt.finalapp.models.Message
import com.igt.finalapp.models.TotalMessagesEvent
import com.igt.finalapp.toast
import com.igt.finalapp.utils.RxBus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private lateinit var _view:View
    private lateinit var adapter: ChatAdapter
    private val messageList: ArrayList<Message> = ArrayList()

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var currentUser:FirebaseUser

    private val store:FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription:ListenerRegistration?= null


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpRecyclerView()
        setUpChatBtn()
        suscribeToChatMessage()

        return _view
    }

    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")
    }
    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpRecyclerView(){
        adapter = ChatAdapter(messageList,currentUser.uid)
        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = LinearLayoutManager(context)
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter
    }
    private fun setUpChatBtn(){
        _view.buttonSend.setOnClickListener{
            val msj:String = _view.editTextMessage.text.toString()
            if(msj.isNotEmpty()){
                val photo = currentUser.photoUrl?.let {
                    currentUser.photoUrl.toString()
                } ?: run{""}
                val message = Message(currentUser.uid,msj,photo, Date())
                //Guardamos el mensaje en firebase
                saveMessage(message)

                _view.editTextMessage.setText("")
            }
        }
    }

    private fun saveMessage(message: Message){
        val newMessage = HashMap<String,Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageURL"] = message.profileImageURL
        newMessage["sentAt"] = message.sentAt

        chatDBRef.add(newMessage).addOnCompleteListener {
            activity!!.toast("Messasge added")
        }
            .addOnFailureListener(){
                activity!!.toast("Message error, try again!")
            }
    }

    private fun suscribeToChatMessage(){
        chatSubscription = chatDBRef.orderBy("sentAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener(object:EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast("Exception")
                    return
                }
                snapshot?.let {
                    messageList.clear()
                    val messages = it.toObjects(Message::class.java)
                    messageList.addAll(messages.asReversed())
                    adapter.notifyDataSetChanged()
                    _view.recyclerView.smoothScrollToPosition(messageList.size)
                    RxBus.publish(TotalMessagesEvent(messageList.size))
                }
            }

        })
    }

    override fun onDestroyView() {

        chatSubscription?.remove()
        super.onDestroyView()
    }

}
