package com.igt.finalapp

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT ) = Toast.makeText(this,message,duration).show()
fun Activity.toast(resourceId: Int, duration: Int = Toast.LENGTH_SHORT ) = Toast.makeText(this,resourceId,duration).show()

fun Activity.snackBar(message: CharSequence, view : View? = findViewById(R.id.container),
                      duration: Int = Snackbar.LENGTH_SHORT, action: String? = null,
                      actionEvt: (v: View) -> Unit = {}){
    if(view != null){
        val snackbar = Snackbar.make(view ,message,duration)
        if (!action.isNullOrEmpty()){
            snackbar.setAction(action,actionEvt)
        }
        snackbar.show()
    }
}

fun ViewGroup.inflate(layoutId: Int) = LayoutInflater.from(context).inflate(layoutId,this,false )!!

//fun ImageView.loadByUrl(url: String) = Picasso.with(context).load(url).into(this)

inline fun <reified T : Activity> Activity.goToActivity(noinline init: Intent.() -> Unit = {}){
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

fun EditText.validate(validation: (String)-> Unit){
    this.addTextChangedListener(object : TextWatcher{
        override fun afterTextChanged(editable: Editable) {
            validation(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}

fun Activity.isValidEmail(email: String):Boolean{
    val pattern =  Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

fun Activity.isValidPassword(password: String):Boolean{
    //Necesita contener --> 1 Num/1 Minuscula/1 Special/Min Caracteres 4
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
    val pattern =  Pattern.compile(passwordPattern)
    return pattern.matcher(password).matches()
}

fun Activity.isValidConfirmPassword(password:String, confirmPassword:String):Boolean{
    return password == confirmPassword
}