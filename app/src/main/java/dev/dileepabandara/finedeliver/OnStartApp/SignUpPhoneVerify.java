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
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import dev.dileepabandara.finedeliver.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpPhoneVerify extends AppCompatActivity {

    Button btnVerify, btnReSendOTP;
    CheckedTextView chkTxtPhone;
    EditText txtOTP;
    TextView txtState;
    ProgressBar progressBar;

    String userEnterMobile;
    FirebaseAuth firebaseAuth;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;  //Resend code if the code send fail
    private String verificationID; //Hold the OTP/Verification code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_phone_verify);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        userEnterMobile = getIntent().getStringExtra("phoneNumber").trim();

        btnVerify = findViewById(R.id.btnVerify);
        btnReSendOTP = findViewById(R.id.btnReSendOTP);
        chkTxtPhone = findViewById(R.id.chkTxtPhone);
        txtOTP = findViewById(R.id.txtOTP);
        txtState = findViewById(R.id.txtState);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        btnReSendOTP.setVisibility(View.INVISIBLE);
        txtState.setVisibility(View.INVISIBLE);

        //Get user mobile number from Prevalent2 class
        String trimUserEnterMobile = userEnterMobile.substring(1, 10);
        String userSignPhone = "+94" + trimUserEnterMobile;
        chkTxtPhone.setText(userSignPhone);
        Toast.makeText(this, "" + userSignPhone, Toast.LENGTH_SHORT).show();

        sendVerificationCode(userSignPhone);

        //Verify button
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = txtOTP.getText().toString().trim();
                btnReSendOTP.setVisibility(View.INVISIBLE);

                if (code.isEmpty() && code.length() < 6) {
                    txtOTP.setError("Enter Valid OTP");
                    txtOTP.requestFocus();
                    return;
                }
                verifyCode(code);

                //chkTxtPhone.setCheckMarkDrawable(R.drawable.ic_baseline_verified_24);
                //progressBar.setVisibility(View.INVISIBLE);
            }
        });

        timer();

        //Resend OTP button
        btnReSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnReSendOTP.setVisibility(View.INVISIBLE);
                resendVerificationCode(userSignPhone, forceResendingToken);
                timer();
            }
        });

    }

    //Send verification code
    private void sendVerificationCode(String phoneNumber) {
        Toast.makeText(this, "sendVerificationCode", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions
                        .newBuilder(FirebaseAuth.getInstance())
                        .setActivity(this)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setCallbacks(mCallBack)
                        .build());
        Toast.makeText(SignUpPhoneVerify.this, "Your OTP send to " + phoneNumber, Toast.LENGTH_LONG).show();
    }

    //Resend verification code
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions
                        .newBuilder(FirebaseAuth.getInstance())
                        .setActivity(this)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setForceResendingToken(token)
                        .setCallbacks(mCallBack)
                        .build());
    }

    //On verification state Changed callbacks
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //If OTP send to own device
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            try {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    txtOTP.setText(code);   //Auto verification
                    verifyCode(code);
                    Toast.makeText(SignUpPhoneVerify.this, "OTP send to your own device", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpPhoneVerify.this, "OTP cannot received. Please check again", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(SignUpPhoneVerify.this, "" + e, Toast.LENGTH_SHORT).show();
            }
        }

        //If verification failed
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(SignUpPhoneVerify.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //If OTP send to other device
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, forceResendingToken);
            verificationID = s;
            forceResendingToken = token;
            Toast.makeText(SignUpPhoneVerify.this, "OTP send to other device", Toast.LENGTH_SHORT).show();
        }
    };

    //Verify code
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        signInWithPhoneAuthCredentials(credential);
        Toast.makeText(SignUpPhoneVerify.this, "Checking OTP", Toast.LENGTH_SHORT).show();
    }

    //SignIn the user by credential
    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(SignUpPhoneVerify.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            notification();
                            Intent intent = new Intent(SignUpPhoneVerify.this, WelcomeScreen.class);
                            startActivity(intent);
                            Toast.makeText(SignUpPhoneVerify.this, "Verification Success", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignUpPhoneVerify.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignUpPhoneVerify.this, "Verification not success. Please try again!", Toast.LENGTH_SHORT).show();
                            }
                            //Reusable code for all
                            ReusableCodeForAll.showAlert(SignUpPhoneVerify.this, "Error", task.getException().getMessage());
                        }
                    }
                });
    }

    //Timer
    private void timer() {
        new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long l) {
                txtState.setVisibility(View.VISIBLE);
                txtState.setText("Resend Code within " + l / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                btnReSendOTP.setVisibility(View.VISIBLE);
                txtState.setVisibility(View.INVISIBLE);
            }
        }.start();
    }


    //Send notification
    private void notification() {
        //Show notification
        String notificationTitle = "Welcome to FineDeliver";
        String notificationMessage = "Hi! Thank you for join with us";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                SignUpPhoneVerify.this
        )
                .setSmallIcon(R.drawable.ic_round_phone_android_24)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        //OnTouch goto activity
        Intent intent = new Intent(SignUpPhoneVerify.this, WelcomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", notificationMessage);

        PendingIntent pendingIntent = PendingIntent.getActivity(SignUpPhoneVerify.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE
        );
        notificationManager.notify(0, builder.build());
    }

}