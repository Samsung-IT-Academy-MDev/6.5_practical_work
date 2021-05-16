package ru.samsung.itschool.googleauthapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.samsung.itschool.googleauthapplication.databinding.ActivityMainBinding
import java.io.InputStream
import java.net.URL


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        val account:GoogleSignInAccount  = GoogleSignIn.getLastSignedInAccount(this)!!;
        binding.activityButtonSignIn.setOnClickListener({
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, Companion.RC_AUTH_CODE)
        })
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("onConnectionFailed", p0.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Companion.RC_AUTH_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acct = completedTask.getResult(ApiException::class.java)
            if (acct != null) {
                val materialAlertDialogBuilder: MaterialAlertDialogBuilder =
                    MaterialAlertDialogBuilder(this)
                val customAlertDialogView: View = LayoutInflater.from(this)
                    .inflate(R.layout.login_dialog, null, false)
                customAlertDialogView.findViewById<TextView>(R.id.tv_person_name).text=acct.displayName
                customAlertDialogView.findViewById<TextView>(R.id.tv_person_given_name).text=acct.givenName
                customAlertDialogView.findViewById<TextView>(R.id.tv_person_family_name).text=acct.familyName
                customAlertDialogView.findViewById<TextView>(R.id.tv_person_email).text=acct.email
                customAlertDialogView.findViewById<TextView>(R.id.tv_person_id).text=acct.id
                if (acct.photoUrl != null) {
                    Thread(){
                        kotlin.run {
                            val inp: InputStream = URL(acct.photoUrl.toString()).openStream()
                            val bmp = BitmapFactory.decodeStream(inp)
                            customAlertDialogView.findViewById<ImageView>(R.id.iv_person_photo).setImageBitmap(bmp)
                        }
                    }.start()
                }

                materialAlertDialogBuilder.setView(customAlertDialogView)
                    .setTitle("Profile")
//                    .setMessage("Show your basic details")
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                Log.d(TAG, "signInResult:success")
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    companion object {
        const val RC_AUTH_CODE = 1
        const val TAG = "GoogleAuthApplication"
    }
}