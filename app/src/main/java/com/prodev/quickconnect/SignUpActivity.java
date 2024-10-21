package com.prodev.quickconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUpActivity extends AppCompatActivity {

    /*
    add google sign up for new user - completed
    add twitter sign up for new user
    add email and password sign in for new user
     */

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Retrieve the original padding from the XML
        View mainView = findViewById(R.id.main);
        int originalPaddingLeft = mainView.getPaddingLeft();
        int originalPaddingTop = mainView.getPaddingTop();
        int originalPaddingRight = mainView.getPaddingRight();
        int originalPaddingBottom = mainView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    originalPaddingLeft + systemBars.left,
                    originalPaddingTop + systemBars.top,
                    originalPaddingRight + systemBars.right,
                    originalPaddingBottom + systemBars.bottom
            );
            return insets;
        });

        // xml into object
        MaterialButton signWithGoogleBtn = findViewById(R.id.signUpWithGoogle);

        // sign in with google
        // step1
        firebaseAuth = FirebaseAuth.getInstance();

        // step2
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // step 3
        googleSignInClient = GoogleSignIn.getClient(this, gso); // above gso object of googleSignInOptions to googleSignIn


        signWithGoogleBtn.setOnClickListener( v -> {
            signUpWithGoogle();
        });


        // go to login activity
        TextView logInBtn = findViewById(R.id.LogInBtn);
        logInBtn.setOnClickListener( v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    // Step 4 of google sign in
    private void signUpWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // override default methods

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // handle error
                e.printStackTrace();
            }
        }
    }

    // step 5 for google sign in
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign-in successful
                // Update UI with the signed-in user's information
                // FirebaseUser user = firebaseAuth.getCurrentUser();
                // You can navigate to another activity here
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();

                // and redirect to main activity
                Intent intent = new Intent(this, VideoCallingActivity.class);
                startActivity(intent);
            } else {
                // If sign-in fails, display a message to the user.
                Toast.makeText(this, "Sign up Failed", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Try again some error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

}