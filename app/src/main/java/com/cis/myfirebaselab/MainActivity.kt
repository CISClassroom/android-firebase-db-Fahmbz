package com.cis.myfirebaselab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var googleClient:GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        signin.setOnClickListener { (SignIn())}
        signout.setOnClickListener { (SignOut()) }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this,gso)
        auth = FirebaseAuth.getInstance()
    }
    private fun SignIn(){
        var signInIntet = googleClient.signInIntent
        startActivityForResult(signInIntet,10)
    }
    private fun SignOut() {
        auth.signOut()
        googleClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
        }
    }
    private fun updateUI(user: FirebaseUser?){
        if (user == null){
            tvstatus.text = "Signed Out"
        }else{
            tvstatus.text = "Sign In as" + user.email
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                FirebaseAuth(account!!)
            }catch (e:ApiException){
                updateUI(null)
            }
        }
    }
    private fun FirebaseAuth(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener (this){task
                if (task.isSuccessfull){
                    val user = auth.currentUser
                    updateUI(user)
                }else{
                    updateUI(null)
                }
            }
    }
}
