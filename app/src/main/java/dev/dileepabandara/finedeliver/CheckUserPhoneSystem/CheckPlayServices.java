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

package dev.dileepabandara.finedeliver.CheckUserPhoneSystem;

import android.content.Context;

public abstract class CheckPlayServices extends Context {

    /*private static final String TAG = "CheckPlayServices";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public boolean isServicesActive(){

        Log.d(TAG, "isServicesActive: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CheckPlayServices.this);

        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesActive: google services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesActive: an error occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CheckPlayServices.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else{
            Toast.makeText(this, "Cannot create Map", Toast.LENGTH_SHORT).show();
        }

        return false;
    }*/

}
