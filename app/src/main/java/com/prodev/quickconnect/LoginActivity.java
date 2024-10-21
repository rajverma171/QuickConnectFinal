package com.prodev.quickconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    // global variable
    FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


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

        // ui backend
        final boolean[] passwordToggle = {true};
        EditText passwordET = findViewById(R.id.passwordET);
        ImageView password_toggle = findViewById(R.id.passwordToggle);



        password_toggle.setOnClickListener( v ->  {
            if ( passwordToggle[0] == false) {
//                passwordToggle[0] = true;
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password_toggle.setImageResource(R.drawable.hidepassword);
            } else {
//                passwordToggle[0] = false;
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password_toggle.setImageResource(R.drawable.showpassword);
            }

            passwordToggle[0] = !passwordToggle[0];

            // Move cursor to the end of the text after toggling
            passwordET.setSelection(passwordET.getText().length());
        });

        TextView signUp = findViewById(R.id.SignUp);
        MaterialButton continueWithGoogle = findViewById(R.id.continueWithGoogle);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);

        });






        mAuth = FirebaseAuth.getInstance();


        // Get references to your views
//        EditText phoneEditText = findViewById(R.id.numberEditText); // Change ID if needed
//        Spinner countrySpinner = findViewById(R.id.my_spinner);
//        MaterialButton nextButton = findViewById(R.id.nextBtn); // Change ID if needed



//
//        nextButton.setOnClickListener(v -> {
//            String phoneNumber = phoneEditText.getText().toString().trim();
//            // Optionally get the country code from spinner
//            // String countryCode = countrySpinner.getSelectedItem().toString();
//            if (phoneNumber.isEmpty()) {
//                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
//            } else {
//                phoneNumber = "+91" + phoneNumber;
//                // Optionally prepend country code
//                verifyPhoneNumber(phoneNumber);
//            }
//        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Client ID from Firebase Console
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        continueWithGoogle.setOnClickListener(v -> {
            signInWithGoogle();
        });
    }

    private void verifyPhoneNumber(String phoneNumber) {
        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity for callback binding
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                // Automatically verified, sign in the user
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                // Handle error
                                Toast.makeText(LoginActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .build());
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to the next screen
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // Start next activity (e.g., MainActivity)
                    } else {
                        // Sign in failed, display a message
                        Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(LoginActivity.this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();
                        // Start next activity (e.g., MainActivity)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Google Sign-In Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign-In failed, update UI appropriately
                Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
