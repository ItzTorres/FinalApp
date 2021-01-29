package com.igt.finalapp.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.igt.finalapp.*
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private val mAuth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        editTextEmail.validate{
            editTextEmail.error = if (isValidEmail(it)) null else "Email is not valid"
        }

        buttonGoLogIn.setOnClickListener{
            goToActivity<LoginActivity>{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        }

        buttonForgot.setOnClickListener{
            val email = editTextEmail.text.toString()
            if(isValidEmail(email)){
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this){
                    toast("Email has been sent to reset your password")
                    goToActivity<LoginActivity>{
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                }
            } else{
                toast("Please make sure your email password is correct")
            }
        }
    }
}
