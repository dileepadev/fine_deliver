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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import dev.dileepabandara.finedeliver.HelperClasses.UniqueUserHelperClass;
import dev.dileepabandara.finedeliver.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    Button btnSignUp;
    TextInputLayout txtSignName, txtSignPhone, txtSignEmail, txtSignPassword, txtSignCPassword;
    String signName, signPhone, signEmail, signPassword, signCPassword;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    //  onCreate --> btnSignUp --> validations --> checkPhoneAvailable --> saveUser --> authUser --> SignUpPhoneVerify

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnSignUp = findViewById(R.id.btnSignUp);
        txtSignName = findViewById(R.id.txtSignName);
        txtSignPhone = findViewById(R.id.txtSignPhone);
        txtSignEmail = findViewById(R.id.txtSignEmail);
        txtSignPassword = findViewById(R.id.txtSignPassword);
        txtSignCPassword = findViewById(R.id.txtSignCPassword);

        databaseReference = firebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signName = txtSignName.getEditText().getText().toString();
                signPhone = txtSignPhone.getEditText().getText().toString();
                signEmail = txtSignEmail.getEditText().getText().toString();
                signPassword = txtSignPassword.getEditText().getText().toString();
                signCPassword = txtSignCPassword.getEditText().getText().toString();

                if (!validateName() | !validatePhone() | !validateEmail() | !validatePassword() | !validateCPassword()) {
                    Toast.makeText(SignUp.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    checkPhoneAvailable();
                }
            }
        });

    }

    //Check phone already taken
    private void checkPhoneAvailable() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserPhones");

        //Get all the values
        signPhone = txtSignPhone.getEditText().getText().toString();
        signEmail = txtSignEmail.getEditText().getText().toString();

        final UniqueUserHelperClass helperClass = new UniqueUserHelperClass(signPhone, signEmail);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(helperClass.getMobile()).exists()) {
                    txtSignPhone.setError("This mobile number already registered\ne.g. 0798765432");
                } else {
                    txtSignPhone.setErrorEnabled(false);
                    saveUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignUp.this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Save user
    private void saveUser() {

        final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("Registration in progress 1\nPlease wait...");
        mDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(signEmail, signPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("Name", signName);
                    hashMap1.put("Phone", signPhone);
                    hashMap1.put("Email", signEmail);
                    hashMap1.put("Password", signPassword);

                    HashMap<String, String> hashMap2 = new HashMap<>();
                    hashMap2.put("Name", signName);
                    hashMap2.put("Phone", signPhone);

                    firebaseDatabase.getInstance().getReference("UserPhones").child(signPhone).setValue(hashMap2);

                    firebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            authUser();
                        }
                    });

                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    txtSignEmail.setError("This email already registered\ne.g. finedeliver@gmail.com");

                }
            }
        });
    }

    //Auth user
    private void authUser() {

        final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("Registration in progress 2\nPlease wait...");
        mDialog.show();

        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("You have registered.\nMake sure to verify the Email");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent i = new Intent(SignUp.this, SignUpPhoneVerify.class);
                            i.putExtra("phoneNumber", signPhone);
                            startActivity(i);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    mDialog.dismiss();
                    ReusableCodeForAll.showAlert(SignUp.this, "Error", task.getException().getMessage());
                }
            }
        });

    }

    //Switch to LogIn
    public void goToLogIn(View view) {
        Intent i = new Intent(SignUp.this, LogIn.class);
        startActivity(i);
        finish();
    }

    //Validate Name
    private Boolean validateName() {
        String val = txtSignName.getEditText().getText().toString();

        if (val.isEmpty()) {
            txtSignName.setError("Name cannot be empty\ne.g. Fine Deliver");
            return false;
        } else if (val.length() > 20) {
            txtSignName.setError("Use 0-20 characters for name\ne.g. Fine Deliver");
            return false;
        } else {
            txtSignName.setError(null);
            txtSignName.setErrorEnabled(false);
            return true;
        }
    }

    //Validate Phone
    private Boolean validatePhone() {
        String val = txtSignPhone.getEditText().getText().toString();

        if (val.isEmpty()) {
            txtSignPhone.setError("Mobile Number cannot be empty\ne.g. 0798765432");
            return false;
        } else if (!(val.length() == 10)) {
            txtSignPhone.setError("Invalid mobile number\ne.g. 0798765432");
            return false;
        } else {
            txtSignPhone.setError(null);
            return true;
        }
    }

    //Validate Email
    private Boolean validateEmail() {
        String val = txtSignEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            txtSignEmail.setError("Email cannot be empty\ne.g. finedeliver@gmail.com");
            return false;
        } else if (!val.matches(emailPattern)) {
            txtSignEmail.setError("Invalid email pattern\ne.g. finedeliver@gmail.com");
            return false;
        } else {
            txtSignEmail.setError(null);
            txtSignEmail.setErrorEnabled(false);
            return true;
        }
    }

    //Validate Password
    private Boolean validatePassword() {
        String val = txtSignPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            txtSignPassword.setError("Password cannot be empty\ne.g. Fine@56#p");
            return false;
        } else if (!val.matches(passwordVal)) {
            txtSignPassword.setError("Password is too weak. Use uppercase, lowercase letters, numbers and special characters\ne.g. Fine@56#p");
            return false;
        } else if (val.length() < 6) {
            txtSignPassword.setError("Use at least 6 characters\ne.g. Fine@56#p");
            return false;
        } else {
            txtSignPassword.setError(null);
            txtSignPassword.setErrorEnabled(false);
            return true;
        }
    }

    //Validate CPassword
    private Boolean validateCPassword() {
        String val1 = txtSignCPassword.getEditText().getText().toString();
        String val2 = txtSignPassword.getEditText().getText().toString();

        if (val1.isEmpty()) {
            txtSignCPassword.setError("Confirm Password cannot be empty");
            return false;
        } else if (!val1.equals(val2)) {
            txtSignCPassword.setError("Password is not match");
            return false;
        } else {
            txtSignCPassword.setError(null);
            txtSignCPassword.setErrorEnabled(false);
            return true;
        }

    }

}