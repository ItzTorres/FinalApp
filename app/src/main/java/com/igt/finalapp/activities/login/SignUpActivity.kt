package com.igt.finalapp.activities.login


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.igt.finalapp.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.editTextEmail
import kotlinx.android.synthetic.main.activity_sign_up.editTextPassword


class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy{ FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        //FirebaseApp.initializeApp(applicationContext)
        buttonGoLogIn.setOnClickListener{
            goToActivity<LoginActivity>()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        buttonSignUp.setOnClickListener{
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmpassword = editTextConfirmPassword.text.toString()
            if(isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(password,confirmpassword)){
                signUpByEmail(email,password)
            }else{
                toast("Please make sure all the data is correct")
            }
        }

        editTextEmail.validate {
            editTextEmail.error = if(isValidEmail(it)) null else "The email is not valid"
        }

        editTextPassword.validate {
            editTextPassword.error = if(isValidPassword(it)) null else "The password should contain 1 number, 1 lowercase, 1 uppercase, 1 special character and 6 characters length at least"
        }

        editTextConfirmPassword.validate {
            editTextConfirmPassword.error = if (isValidConfirmPassword(editTextPassword.text.toString(),it)) null else "Confirm password doesn't match with Password"
        }

    }

    private fun signUpByEmail(email:String,password:String){

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                    mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this) {
                        toast("An email has been sent you. Please confirm before sign in")
                        goToActivity<LoginActivity> {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                } else { // If sign in fails, display a message to the user.
                    toast("An unexpected error ocurred, please try again")

                }
            }
    }
}
