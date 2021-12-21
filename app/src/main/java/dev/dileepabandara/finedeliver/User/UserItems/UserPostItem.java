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

package dev.dileepabandara.finedeliver.User.UserItems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import dev.dileepabandara.finedeliver.HelperClasses.SpinnerItemAdapter;
import dev.dileepabandara.finedeliver.HelperClasses.UserDetailsHelperClass;
import dev.dileepabandara.finedeliver.HelperClasses.UserPostItemHelperClass;
import dev.dileepabandara.finedeliver.MapActivity.MapActivity;
import dev.dileepabandara.finedeliver.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class UserPostItem extends AppCompatActivity {

    /*  ------------------------  Process  ------------------------
     *   01 - Item Category (SpinnerItemAdapter)
     *   02 - Item Image (imgUploadItem)
     *   03 - Order Name (txtOrderName)
     *   04 - Item Description (txtPostItemDescription)
     *   05 - Item Package Size (txtPostItemHeight + txtPostItemWidth + txtItemSizeUnit)
     *   06 - Item Package Weight (txtPostItemWeight + txtItemWeightUnit)
     *   07 - Item Due Date and Time (DateTimePicker - userPostItemDueDateAndTime)
     *   08 - Item Origin and Destination locations (Switch to Map activity)
     *           - Origin Location(txtPostItemOL)
     *           - Destination Location(txtPostItemDL)
     *   09 - Button Cancel (btnCancelItem)
     *   10 - Button Post (btnPostItem)
     *
     * */

    SpinnerItemAdapter SpinnerItemAdapter;
    ImageButton imgUploadItem, btnSelectItemLocation;
    TextView txtItemSizeUnit, txtItemWeightUnit, userPostItemDueDateAndTime;
    TextInputLayout txtPostItemHeight, txtPostItemWidth, txtPostItemWeight, txtOrderName, txtPostItemDescription, txtPostItemOL, txtPostItemDL;
    Button btnPostItem, btnCancelItem;

    //  ----------------- Image -----------------
    Uri croppedImageURI;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth fAuth;
    String downloadImageURL;

    private final static int REQUEST_CODE_FOR_MAP = 156; //Code for map
    private static final String TAG = "CheckPlayServices";
    private static final int ERROR_DIALOG_REQUEST = 9001;  //Check play services
    String orderID = UUID.randomUUID().toString();

    //  --------------------------------------- Spinner ---------------------------------------
    Spinner spinnerItemCategory;
    String[] names = {"Food", "Electronic", "Computer", "Phone", "Papers", "Other"};
    int[] images = {R.drawable.ic_round_r_fastfood_24, R.drawable.ic_round_r_electrical_services_24, R.drawable.ic_round_r_computer_24,
            R.drawable.ic_round_r_phone_iphone_24, R.drawable.ic_round_r_library_books_24, R.drawable.ic_roundr_r_build_24};

    //  Firebase data save variables
    String itemCategory, orderName, itemDescription, itemPackageSize, itemPackageWeight,
            itemDueDatAndTime, itemOriginLocation, itemDestinationLocation, itemImageUri;

    //  --------------------------------------- onCreate ---------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_item);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //  ----------------- Widgets -----------------
        spinnerItemCategory = findViewById(R.id.spinnerItemCategory);
        imgUploadItem = findViewById(R.id.imgUploadItem);

        txtPostItemHeight = findViewById(R.id.txtPostItemHeight);
        txtPostItemWidth = findViewById(R.id.txtPostItemWidth);
        txtPostItemWeight = findViewById(R.id.txtPostItemWeight);
        txtOrderName = findViewById(R.id.txtOrderName);
        txtPostItemDescription = findViewById(R.id.txtPostItemDescription);

        txtItemSizeUnit = findViewById(R.id.txtItemSizeUnit);
        txtItemWeightUnit = findViewById(R.id.txtItemWeightUnit);

        btnSelectItemLocation = findViewById(R.id.btnSelectItemLocation);
        userPostItemDueDateAndTime = findViewById(R.id.userPostItemDueDateAndTime);

        btnPostItem = findViewById(R.id.btnPostItem);
        btnCancelItem = findViewById(R.id.btnCancelItem);

        txtPostItemOL = findViewById(R.id.txtPostItemOL);
        txtPostItemDL = findViewById(R.id.txtPostItemDL);

        //  1 --------------------------------------- Spinner - Select item category name ---------------------------------------
        SpinnerItemAdapter = new SpinnerItemAdapter(UserPostItem.this, names, images);
        spinnerItemCategory.setAdapter(SpinnerItemAdapter);
        spinnerItemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(UserPostItem.this, names[i], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //  --------------------------------------- Firebase ---------------------------------------
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("Orders Images");   //Firebase storage location which saved order image
        fAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference("Orders");

        //  2 --------------------------------------- Image ---------------------------------------
        imgUploadItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraPermission();
            }
        });

        //  --------------------------------------- Check the play service enable or disable for google map ---------------------------------------
        if (isServicesActive()) {
            init();
        }

        //  --------------------------------------- Post Item ---------------------------------------
        btnPostItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOrderValidate();
            }
        });

        //  --------------------------------------- Post Item ---------------------------------------
        btnCancelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    //  --------------------------------------- Grant camera runtime permission ---------------------------------------
    private void getCameraPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String CAMERA_PERMISSION = Manifest.permission.CAMERA;
        String[] permissions = {CAMERA_PERMISSION};

        if (ContextCompat.checkSelfPermission(UserPostItem.this, CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserPostItem.this, permissions, 10);
        } else {
            cropImageActivity(croppedImageURI);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, "onRequestPermissionsResult", Toast.LENGTH_SHORT).show();
        if (requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cropImageActivity(croppedImageURI);
        } else {
            Toast.makeText(this, "Cancelled! Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }


    //  ---------------------------------------------------- Image ----------------------------------------------------

    private void cropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }


    //  ---------------------------------------------------- Switch Map or Camera ----------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  -------------- Select Map --------------
        if (requestCode == REQUEST_CODE_FOR_MAP && resultCode == Activity.RESULT_OK) {
            if (data != null)

                try {
                    String[] origin = data.getStringArrayExtra("origin");
                    String[] destination = data.getStringArrayExtra("destination");
                    txtPostItemOL.getEditText().setText(Arrays.toString(origin));
                    txtPostItemDL.getEditText().setText(Arrays.toString(destination));

                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


        }


        //  -------------- Select Image by Camera --------------
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imgUploadItem.setImageURI(resultUri);
                croppedImageURI = resultUri;
                Toast.makeText(this, "Cropped Successfully!", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "" + croppedImageURI, Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Failed to Crop" + result.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //  ---------------------------------------------------- Unit Size ----------------------------------------------------
    public void goSelectUnitSize(View view) {

        String[] unitS = {"mm", "cm", "m"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a unit");
        builder.setItems(unitS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtItemSizeUnit.setText(unitS[which]);
            }
        });
        builder.show();

    }

    //  ---------------------------------------------------- Unit Weight ----------------------------------------------------
    public void goSelectUnitWeight(View view) {

        String[] unitW = {"mg", "g", "kg"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a unit");
        builder.setItems(unitW, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtItemWeightUnit.setText(unitW[which]);
            }
        });
        builder.show();

    }

    //  ---------------------------------------------------- Due Date and Time ----------------------------------------------------
    public void goSelectDueDateAndTime(View view) {

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            String simpleDateFormat = "On " + year + "-" + (month + 1) + "-" + dayOfMonth
                                    + " at " + hourOfDay + ":" + minute + "H";

                            userPostItemDueDateAndTime.setText(simpleDateFormat);
                        } catch (Exception e) {
                            Toast.makeText(UserPostItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                new TimePickerDialog(UserPostItem.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(UserPostItem.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }


    //  ---------------------------------------------------- Map ----------------------------------------------------
    //  Check the play service enable or disable for google map
    public boolean isServicesActive() {

        Log.d(TAG, "isServicesActive: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UserPostItem.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesActive: google services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesActive: an error occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UserPostItem.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot create Map", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //  Start map activity
    private void init() {

        btnSelectItemLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPostItem.this, MapActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_MAP);
            }
        });

    }


    //  ---------------------------------------------------- Upload post details to firebase ----------------------------------------------------
    private void uploadPostDetails() {
        if (croppedImageURI != null) {
            final ProgressDialog progressDialog = new ProgressDialog(UserPostItem.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //  Image saved firebase location path
            //StorageReference filepath = storageReference.child(croppedImageURI.getLastPathSegment() + orderID);
            StorageReference filepath = storageReference.child(orderID);
            final UploadTask uploadTask = filepath.putFile(croppedImageURI);

            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTasks = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            downloadImageURL = filepath.getDownloadUrl().toString();

                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {

                                UserPostItemHelperClass userPostItemHelperClass = new UserPostItemHelperClass(
                                        itemCategory, orderName, itemDescription, itemPackageSize,
                                        itemPackageWeight, itemDueDatAndTime, itemOriginLocation,
                                        itemDestinationLocation, downloadImageURL, userID, orderID);


                                //  --------------------------------------- Save data to Orders Tree ---------------------------------------
                                firebaseDatabase.getInstance().getReference("Orders").child(userID).child(orderID)
                                        .setValue(userPostItemHelperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        //  --------------------------------------- Save data to OrdersInDue Tree ---------------------------------------
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("Order ID", orderID);
                                        firebaseDatabase.getInstance().getReference("OrdersInDue").child(userPostItemHelperClass.getPostItemDueDatAndTime()).child(userID).child(orderID)
                                                .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                //  --------------------------------------- Save data to Users Tree ---------------------------------------
                                                firebaseDatabase.getInstance().getReference("Users").child(userID).child("UserOrders").child(orderID)
                                                        .setValue(userPostItemHelperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(UserPostItem.this, "Order Posted successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(UserPostItem.this, UserGuideToPostItem.class));
                                                        finishAffinity();
                                                    }
                                                });

                                            }
                                        });

                                    }
                                });

                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UserPostItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            });

        } else {
            Toast.makeText(this, "Cannot Upload", Toast.LENGTH_SHORT).show();
        }
    }

    //  ---------------------------------------------------- Valid Details for Button Confirm ----------------------------------------------------
    private void validDetails() {

        try {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = firebaseDatabase.getInstance().getReference("Users").child(userID);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    UserDetailsHelperClass userDetailsHelperClass = snapshot.getValue(UserDetailsHelperClass.class);
                    String userName = userDetailsHelperClass.getUserName();
                    String userPhone = userDetailsHelperClass.getUserPhone();
                    String userEmail = userDetailsHelperClass.getUserEmail();

                    btnPostItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            itemCategory = spinnerItemCategory.getSelectedItem().toString();
                            orderName = txtOrderName.getEditText().getText().toString();
                            itemDescription = txtPostItemDescription.getEditText().getText().toString();
                            itemPackageSize = txtPostItemHeight.getEditText().getText().toString() + "x" + txtPostItemWidth.getEditText().getText().toString() + " " + txtItemSizeUnit.getText();
                            itemPackageWeight = txtPostItemWeight.getEditText().getText().toString() + " " + txtItemWeightUnit.getText();
                            itemDueDatAndTime = userPostItemDueDateAndTime.getText().toString();
                            itemOriginLocation = txtPostItemOL.getEditText().getText().toString();
                            itemDestinationLocation = txtPostItemDL.getEditText().getText().toString();
                            itemImageUri = downloadImageURL;


                            UserPostItemHelperClass userPostItemHelperClass = new UserPostItemHelperClass(
                                    itemCategory, orderName, itemDescription, itemPackageSize,
                                    itemPackageWeight, itemDueDatAndTime, itemOriginLocation,
                                    itemDestinationLocation, downloadImageURL, userID, orderID);

                            userPostItemHelperClass.setPostItemCategory(itemCategory);
                            userPostItemHelperClass.setPostOrderName(orderName);
                            userPostItemHelperClass.setPostItemDescription(itemDescription);
                            userPostItemHelperClass.setPostItemPackageSize(itemPackageSize);
                            userPostItemHelperClass.setPostItemPackageWeight(itemPackageWeight);
                            userPostItemHelperClass.setPostItemDueDatAndTime(itemDueDatAndTime);
                            userPostItemHelperClass.setPostItemOriginLocation(itemOriginLocation);
                            userPostItemHelperClass.setPostItemDestinationLocation(itemDestinationLocation);
                            userPostItemHelperClass.setItemImageUri(downloadImageURL);
                            userPostItemHelperClass.setUserID(userID);
                            userPostItemHelperClass.setOrderID(orderID);

                            String dialogMessage = "Your Order"
                                    + "\n\nItem Category = " + itemCategory
                                    + "\n\nItem Description = " + itemDescription
                                    + "\n\nItem Order Name = " + orderName
                                    + "\n\nItem Package Size = " + itemPackageSize
                                    + "\n\nItem Package Weight = " + itemPackageWeight
                                    + "\n\nItem Due Date and Time = " + itemDueDatAndTime
                                    + "\n\nItem Origin Location = " + itemOriginLocation
                                    + "\n\nItem Destination Location = " + itemDestinationLocation;

                            AlertDialog.Builder builder = new AlertDialog.Builder(UserPostItem.this);
                            builder.setTitle("Please Confirm Order");
                            builder.setMessage(dialogMessage);
                            builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadPostDetails();
                                }
                            });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //  ---------------------------------------------------- Validations ----------------------------------------------------

    private void isOrderValidate() {

        String orderNameV = txtOrderName.getEditText().getText().toString();
        String itemDescriptionV = txtPostItemDescription.getEditText().getText().toString();
        String itemPackageSizeV = txtPostItemHeight.getEditText().getText().toString() + "x" + txtPostItemWidth.getEditText().getText().toString() + " " + txtItemSizeUnit.getText();
        String itemPackageWeightV = txtPostItemWeight.getEditText().getText().toString() + " " + txtItemWeightUnit.getText();
        String itemDueDatAndTimeV = userPostItemDueDateAndTime.getText().toString();
        String itemOriginLocationV = txtPostItemOL.getEditText().getText().toString();
        String itemDestinationLocationV = txtPostItemDL.getEditText().getText().toString();

        if (TextUtils.isEmpty(orderNameV)) {
            Toast.makeText(this, "Please Select Order Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(itemDescriptionV)) {
            Toast.makeText(this, "Please Select Item Description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(itemPackageSizeV)) {
            Toast.makeText(this, "Please Select Package Size", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(itemPackageWeightV)) {
            Toast.makeText(this, "Please Select Package Weight", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(itemDueDatAndTimeV)) {
            Toast.makeText(this, "Please Select Due Date and Time", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(itemOriginLocationV)) {
            Toast.makeText(this, "Please Select Origin Location", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(itemDestinationLocationV)) {
            Toast.makeText(this, "Please Select Destination Location", Toast.LENGTH_SHORT).show();
        } else if (imgUploadItem == null) {
            Toast.makeText(this, "Please Select Item Image", Toast.LENGTH_SHORT).show();
        } else {
            validDetails();
        }

    }

}