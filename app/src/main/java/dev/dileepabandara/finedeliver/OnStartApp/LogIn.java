/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2021
   --------------------------------------
*/

package dev.dileepabandara.finedeliver.OnStartApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import dev.dileepabandara.finedeliver.R;
import dev.dileepabandara.finedeliver.User.UserDashboard;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    Button btnLogIn;
    TextInputLayout txtLogEmail, txtLogPassword1;
    String logEmail, logPassword1;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnLogIn = findViewById(R.id.btnLogIn);
        txtLogEmail = findViewById(R.id.txtLogEmail);
        txtLogPassword1 = findViewById(R.id.txtLogPassword1);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logEmail = txtLogEmail.getEditText().getText().toString();
                logPassword1 = txtLogPassword1.getEditText().getText().toString();

                if (!validateEmail() | !validatePassword()) {
                    Toast.makeText(LogIn.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    userSignIn();

                }

            }
        });

    }

    private void userSignIn() {

        final ProgressDialog mDialog = new ProgressDialog(LogIn.this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setMessage("LogIn in process\nPlease Wait...");
        mDialog.show();

        try {
            firebaseAuth.signInWithEmailAndPassword(logEmail, logPassword1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mDialog.dismiss();
                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                            mDialog.dismiss();
                            Toast.makeText(LogIn.this, "Welcome to FineDelver", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LogIn.this, UserDashboard.class);
                            startActivity(i);
                            finish();
                        } else {
                            ReusableCodeForAll.showAlert(LogIn.this, "Verification Failed!", "Please verify your email before log");

                        }
                    } else {
                        mDialog.dismiss();
                        ReusableCodeForAll.showAlert(LogIn.this, "Error", task.getException().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    //Forgot Password
    public void goForgotPassword(View view) {
        Intent i = new Intent(LogIn.this, ForgotPassword.class);
        startActivity(i);
    }

    //Switch to SignUp
    public void goToSignUp(View view) {
        Intent i = new Intent(LogIn.this, SignUp.class);
        startActivity(i);
        finish();
    }

    //Validate Email
    private Boolean validateEmail() {

        String logEmail = txtLogEmail.getEditText().getText().toString();

        if (logEmail.isEmpty()) {
            txtLogEmail.setHelperText("Email cannot be empty");
            Toast.makeText(this, "Email cannot be empty\"", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            txtLogEmail.setHelperText("");
            return true;
        }
    }

    //Validate Password
    private Boolean validatePassword() {

        String logPassword1 = txtLogPassword1.getEditText().getText().toString();

        if (logPassword1.isEmpty()) {
            txtLogPassword1.setHelperText("Password cannot be empty");
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            txtLogPassword1.setHelperText("");
            return true;
        }
    }
}