package com.example.facebook


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


import com.example.facebook.databinding.ActivityMain2Binding
import com.facebook.internal.FacebookDialogFragment.Companion.TAG
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider



class MainActivity2 : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    lateinit var googleApiClient:GoogleApiClient
    lateinit var auth: FirebaseAuth
    private val TAG = "MainActivity2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main2)

        var binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((resources.getString(R.string.default_web_client_id))) //you can also use R.string.default_web_client_id
            .requestEmail()
            .build()
        googleApiClient = (GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            ?.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            ?.build() ?: binding.signInButton.setOnClickListener {

            binding.signInButton.setOnClickListener {
                val intent: Intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                startActivityForResult(intent, 21)

            }

        }) as GoogleApiClient


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 21) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            handleSignInResult(result)
        }
    }


    private fun handleSignInResult(result: GoogleSignInResult?) {

        val idToken = result?.signInAccount?.idToken

        when {
            idToken != null -> {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = auth.currentUser
                            Log.e(TAG, "handleSignInResult: "+user?.displayName )
                            Log.e(TAG, "handleSignInResult: "+user?.email )
                            Log.e(TAG, "handleSignInResult: "+user?.photoUrl )
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            //updateUI(null)
                        }
                    }
            }
            else -> {
                // Shouldn't happen.
                Log.d(TAG, "No ID token!")
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {


    }
}


