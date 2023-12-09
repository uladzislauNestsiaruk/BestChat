package com.example.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth Auth;
    private EditText emailEditText, passwordEditText, name, repeatPassword;
    private Button SignUp;
    private TextView logInTextView;
    boolean loginIsActive = false;
    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.EmailSignInEditText);
        passwordEditText = findViewById(R.id.PasswordSignInEditText);
        name = findViewById(R.id.NameSignInEditText);
        SignUp = findViewById(R.id.LoginSignInButton);
        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");
        logInTextView = findViewById(R.id.TapToLogInText);
        repeatPassword = findViewById(R.id.PasswordAgainSignInEditText);
        /////////////////////////////////On click
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsMailCorrect(String.valueOf(emailEditText.getText()).trim())) {
                    if (IsPasswordCorrect(String.valueOf(passwordEditText.getText()).trim()))
                        if(String.valueOf(repeatPassword.getText()).trim().equals( String.valueOf(passwordEditText.getText()).trim()) &&  !loginIsActive)
                        loginOrSignUpUser(String.valueOf(emailEditText.getText()).trim(), String.valueOf(passwordEditText.getText()).trim());
                        else {
                            if(!loginIsActive)
                            Toast.makeText(SignInActivity.this, "Password mismatch", Toast.LENGTH_SHORT).show();
                            else
                                loginOrSignUpUser(String.valueOf(emailEditText.getText()).trim(), String.valueOf(passwordEditText.getText()).trim());
                        }
                    else
                        Toast.makeText(SignInActivity.this, "Password must be at least 6 characters \nand include letters and numbers", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(SignInActivity.this, "Invalid mail address", Toast.LENGTH_SHORT).show();
            }
        });
        if(Auth.getCurrentUser() != null)
            startActivity(new Intent(SignInActivity.this, UserListActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = Auth.getCurrentUser();

    }
    //////////////////////////////////////Login or Sing up
    private void loginOrSignUpUser(String email, String password) {
        if(loginIsActive){
            Auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("--------", "signInWithEmail:success");
                                FirebaseUser user = Auth.getCurrentUser();
                                Intent intent = new Intent(SignInActivity.this, ChatActivity.class);
                                intent.putExtra("UserName", String.valueOf(name.getText()).trim());
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("-------", "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                // ...
                            }

                            // ...
                        }
                    });
        }else
        Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("--------", "createUserWithEmail:success");
                            FirebaseUser user = Auth.getCurrentUser();
                            createUser(user);
                            Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                            intent.putExtra("UserName", String.valueOf(name.getText()).trim());
                            startActivity(intent);
                        } else {
                            Log.w("-------", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private boolean IsMailCorrect(String email) {
        if (email.indexOf('@') != -1) {
            if(email.equals("") || email.replace(" ", "").equals(""))
                return false;
            String parts[] = email.split("@");
            int count = 0;
            char chars[] = parts[1].toCharArray();
            for (int i = 0; i < chars.length; i++)
                if (chars[i] == '.')
                    count++;
            return count == 1;
        } else
            return false;
    }
    private boolean IsPasswordCorrect(String password) {
        if (password.length() < 6)
            return false;
        else {
            char chars[] = password.toCharArray();
            int characterCount = 0, numbersCount = 0;
            for (int i = 0; i < chars.length; i++) {
                if (isDigit(chars[i]))
                    numbersCount++;
                else if (isLetter(chars[i]))
                    characterCount++;
            }
            return numbersCount > 0 && characterCount > 0;
        }
    }
    public void LoginMode(View view) {
        if (loginIsActive) {
            loginIsActive = false;
            SignUp.setText("Sign up");
            logInTextView.setText("log in");
            name.setVisibility(View.VISIBLE);
            repeatPassword.setVisibility(View.VISIBLE);
        } else {
            loginIsActive = true;
            SignUp.setText("Log in");
            logInTextView.setText("Sign up");
            name.setVisibility(View.GONE);
            repeatPassword.setVisibility(View.GONE);
        }
    }
    public void createUser(FirebaseUser user){
        User res = new User();
        res.SetId(user.getUid());
        res.SetEmail(user.getEmail());
        res.SetName(String.valueOf(name.getText()).trim());
        usersDatabaseReference.push().setValue(res);
    }

}
