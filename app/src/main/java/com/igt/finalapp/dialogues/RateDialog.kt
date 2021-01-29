package com.igt.finalapp.dialogues

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.igt.finalapp.R
import com.igt.finalapp.models.NewRateEvent
import com.igt.finalapp.models.Rate
import com.igt.finalapp.toast
import com.igt.finalapp.utils.RxBus
import kotlinx.android.synthetic.main.dialog_rate.view.*
import java.util.*

class RateDialog : DialogFragment(){

    private val mAuth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var currentUser: FirebaseUser

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setUpCurrentUser()

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_rate, null)

        return AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.dialog_tittle))
            .setView(view)
            .setPositiveButton(getString(R.string.dialog_ok)){ _, _ ->
                activity!!.toast("Pressed Ok")
                val textRate = view.editTextRateFeedback.text.toString()
                if(textRate.isNotEmpty()){
                    val imgURL = currentUser.photoUrl?.toString() ?: kotlin.run { "" }
                    val rate = Rate(currentUser.uid,textRate, view.ratingBarFeedback.rating, Date(), imgURL )
                    RxBus.publish(NewRateEvent(rate))
                }else{
                    activity!!.toast("Please let us a comment!")
                }

            }
            .setNegativeButton(getString(R.string.dialog_cancel)){ _, _ ->
                activity!!.toast("Pressed Cancel")
            }
            .create()
    }

}