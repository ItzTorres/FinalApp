package com.igt.finalapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.igt.finalapp.R
import com.igt.finalapp.models.TotalMessagesEvent
import com.igt.finalapp.toast
import com.igt.finalapp.utils.CircleTransform
import com.igt.finalapp.utils.RxBus
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_info.view.*
import java.util.EventListener
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass.
 */
class InfoFragment : Fragment() {

    private lateinit var _view: View
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser : FirebaseUser
    private val compositeDisposable by lazy { CompositeDisposable() }

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef : CollectionReference
    private var chatSuscription:ListenerRegistration?=null
    private lateinit var infoBusListener: Disposable



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_info, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpCurrentUserInfoUI()

        //Total Messages Firebase Style
        //subscribeToTotalMessagesFirebaseStyle()

        //Total Messages Event Bus + Reactive Style
        subscribeToTotalMessagesEventBusReactiveStyle()

        return _view
    }

    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpCurrentUserInfoUI(){
        _view.textViewInfoEmail.text = currentUser.email
        _view.textViewInfoName.text = currentUser.displayName?.let { currentUser.displayName } ?: run{getString(R.string.info_no_name)}
        currentUser.photoUrl?.let {
            Log.i(currentUser.photoUrl.toString(),"photo")
            Picasso.get().load(currentUser.photoUrl).resize(300, 300)
                .centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        } ?: run {
            Log.i(currentUser.photoUrl.toString(),"photo")
            Glide.with(this).load(R.drawable.ic_person).apply(RequestOptions().override(300, 300))
                .centerCrop().circleCrop().into(_view.imageViewInfoAvatar)
            //Picasso.get().load(R.drawable.ic_person).into(_view.imageViewInfoAvatar)
            //Picasso.get().load(R.drawable.ic_person).resize(300,300).into(_view.imageViewInfoAvatar)
        }
    }

    private fun subscribeToTotalMessagesFirebaseStyle(){
        chatSuscription = chatDBRef.addSnapshotListener(object: EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
            override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast("Exception")
                    return
                }
                querySnapshot?.let { _view.textViewInfoTotalMessages.text = "${it.size()}" }

            }
        })
    }

    private fun subscribeToTotalMessagesEventBusReactiveStyle(){
        /*
        compositeDisposable.add(
            RxBus.listen(TotalMessagesEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
                .subscribe({
                    _view.textViewInfoTotalMessages.text = "${it.total}"


                },{
                    it.printStackTrace()

                },{

                })
        )

         */
        infoBusListener = RxBus.listen(TotalMessagesEvent::class.java).subscribe({
            _view.textViewInfoTotalMessages.text = "${it.total}"
        })

    }

    override fun onDestroyView() {
        infoBusListener.dispose()
        chatSuscription?.remove()
        super.onDestroyView()
    }
}
