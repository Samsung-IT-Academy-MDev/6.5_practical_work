package ru.samsung.itschool.firebaseauth

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.samsung.itschool.firebaseauth.databinding.ActivityMainBinding
import java.io.InputStream
import java.net.URL

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "FirebaseAuth"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth


        binding.btRegister.setOnClickListener({
             if (binding.etEmail.text.toString().isEmpty() or
                binding.etPassword.text.toString().isEmpty()
            )
                return@setOnClickListener
            auth.createUserWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful)
                        toast("Register success")
                    else
                        toast("Register failed")
                }
        })

        binding.btLogin.setOnClickListener({
            if (binding.etEmail.text.toString().isEmpty() or
                binding.etPassword.text.toString().isEmpty()
            )
                return@setOnClickListener
            auth.signInWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful)
                        toast("Sign in success as " + auth.currentUser?.email)
                    else
                        toast("Sign iт fail")
                }
        })

        binding.btLogout.setOnClickListener({
            if (auth !=null) {
                auth?.signOut();
                toast("Sign out")
            }
        })

        binding.btInfo.setOnClickListener({
            val user = auth.currentUser
            user?.let {
                binding.tvName.text = user.displayName
                binding.tvEmail.text = user.email
                val emailVerified = user.isEmailVerified
                val uid = user.uid
            }
        })


    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null)
            toast("User already sign in")
    }

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}