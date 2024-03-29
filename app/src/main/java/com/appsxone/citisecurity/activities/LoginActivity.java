package com.appsxone.citisecurity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsxone.citisecurity.BuildConfig;
import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.database.Database;
import com.appsxone.citisecurity.location_service.GoogleService;
import com.appsxone.citisecurity.models.OfflineLocationModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.DateFunctions;
import com.appsxone.citisecurity.utils.GPSTracker;
import com.appsxone.citisecurity.utils.InternetConnection;
import com.appsxone.citisecurity.utils.Permissons;
import com.appsxone.citisecurity.utils.SharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements
        ApiCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button btnLogin;
    String fcmToken;
    Database database;
    TextView tvVersionName;
    ApiCallback apiCallback;
    EditText edtEmail, edtPassword;
    public int REQUEST_CHECK_SETTING = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPref.init(this);
        apiCallback = LoginActivity.this;
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvVersionName = findViewById(R.id.tvVersionName);
        database = new Database(this);
        database.createDatabase();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            fcmToken = instanceIdResult.getToken();
        });

        tvVersionName.setText("Version No: " + BuildConfig.VERSION_NAME);
        edtEmail.setText(SharedPref.read("email", ""));
        edtPassword.setText(SharedPref.read("pass", ""));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Permissons.Check_FINE_LOCATION(LoginActivity.this)) {
                    String email = edtEmail.getText().toString().trim();
                    String password = edtPassword.getText().toString().trim();
                    if (email.isEmpty()) {
                        edtEmail.setError("Email Required");
                        edtEmail.requestFocus();
                    } else if (password.isEmpty()) {
                        edtPassword.setError("Password Required");
                        edtPassword.requestFocus();
                    } else {
                        Login(email, password);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please enable permission", Toast.LENGTH_SHORT).show();
                    checkPermission();
                }
            }
        });

        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            String lat = String.valueOf(gpsTracker.getLatitude());
            String lng = String.valueOf(gpsTracker.getLongitude());
            String Uid = null;

            if (InternetConnection.isNetworkConnected(this)) {
                try {
                    JSONObject jsonObject = new JSONObject(SharedPref.read("login_responce", ""));
                    Uid = jsonObject.getString("UserID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (SharedPref.read("login", "").equals("true")) {
                    //updateLocation(lat, lng, "Loggedin");
                } else {
                    //updateLocation(lat, lng, "Loggedout");
                }

                if (database.getAllLocations() != null) {
                    //updateOfflineLocation(Uid, getMacAddr());
                }

            } else {
                String lat_lng_date_time = lat + "{|}" + lng + "{|}" + DateFunctions.getCompleteDate();
                //database.saveLocation(new OfflineLocationModel(0, Uid, getMacAddr(), lat_lng_date_time));
            }
        }
    }

    public void updateOfflineLocation(String userId, String udid) {
        ArrayList<OfflineLocationModel> offlineLocationModelArrayList = database.getAllLocations();
        String[] locationArray = new String[offlineLocationModelArrayList.size()];
        for (int i = 0; i < locationArray.length; i++) {
            locationArray[i] = offlineLocationModelArrayList.get(i).lat_lng_date_time;
        }
        RequestParams requestParams = new RequestParams();
        requestParams.put("UserId", userId);
        requestParams.put("UDID", udid);
        requestParams.put("OfflineLocRecord", locationArray);
        ApiManager apiManager = new ApiManager(this, "post", Const.UPDATE_OFFLINE_LOCATION, requestParams, apiCallback);
        apiManager.loadURL(0);
    }

    private void Login(String email, String password) {
        if (InternetConnection.isNetworkConnected(LoginActivity.this)) {
            RequestParams requestParams = new RequestParams();
            requestParams.put("UserName", email);
            requestParams.put("Password", password);
            requestParams.put("AppVersion", com.appsxone.citisecurity.BuildConfig.VERSION_NAME);
            requestParams.put("FCMTokenId", fcmToken);
            ApiManager apiManager = new ApiManager(LoginActivity.this, "post", Const.LOGIN_SERVICE, requestParams, apiCallback);
            apiManager.loadURL(1);
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.LOGIN_SERVICE)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    SharedPref.write("login_responce", apiResponce);
                    SharedPref.write("email", edtEmail.getText().toString().trim());
                    SharedPref.write("pass", edtPassword.getText().toString().trim());
                    SharedPref.write("login", "true");
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (apiName.equals(Const.UPDATE_LOCATION)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Message")) {

                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (apiName.equals(Const.UPDATE_OFFLINE_LOCATION)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    database.deleteData();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkUserExistance() {
        if (SharedPref.read("login", "").equals("true")) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (isMyServiceRunning(GoogleService.class)) {

                } else {
                    if (Permissons.Check_FINE_LOCATION(LoginActivity.this)) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                            startForegroundService(new Intent(getApplicationContext(), GoogleService.class));
                            registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
                        } else {
                            startService(new Intent(getApplicationContext(), GoogleService.class));
                            registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enable permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String Uid = null;
            if (Permissons.Check_FINE_LOCATION(LoginActivity.this)) {
                String latitude = intent.getStringExtra("latutide");
                String longitude = intent.getStringExtra("longitude");
                if (latitude.equals("no")) {
                    enableLocationPopup();
                } else {
                    if (InternetConnection.isNetworkConnected(LoginActivity.this)) {
                        try {
                            JSONObject jsonObject = new JSONObject(SharedPref.read("login_responce", ""));
                            Uid = jsonObject.getString("UserID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (Uid != null) {
                            if (SharedPref.read("login", "").equals("true")) {
                                //updateLocation(latitude, longitude, "Loggedin");
                            }
                        } else {
                            //updateLocation(latitude, longitude, "Loggedout");
                        }

                        if (database.getAllLocations() != null) {
                            //updateOfflineLocation(Uid, getMacAddr());
                        }
                    } else {
                        String lat_lng_date_time = latitude + "{|}" + longitude + "{|}" + DateFunctions.getCompleteDate();
                        //database.saveLocation(new OfflineLocationModel(0, Uid, getMacAddr(), lat_lng_date_time));
                    }
                }
            } else {
                checkPermission();
                Toast.makeText(context, "Please enable permission", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (states.isNetworkLocationPresent() && states.isGpsPresent() && states.isLocationPresent()) {
            GPSTracker gpsTracker = new GPSTracker(this);
            if (gpsTracker.canGetLocation()) {
                String lat = String.valueOf(gpsTracker.getLatitude());
                String lng = String.valueOf(gpsTracker.getLongitude());
                updateLocation(lat, lng, "Loggedout");
            }
        } else {
            enableLocationPopup();
        }
    }

    GoogleApiClient googleApiClient;

    public void enableLocationPopup() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(30000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTING);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    private void updateLocation(String latitude, String longitude, String comment) {
        if (Permissons.Check_FINE_LOCATION(LoginActivity.this)) {
            SharedPref.init(this);
            String userId = null;
            String res = SharedPref.read("login_responce", "");
            try {
                JSONObject jsonObject = new JSONObject(res);
                userId = jsonObject.getString("UserID");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestParams requestParams = new RequestParams();
            requestParams.put("UDID", getMacAddr());
            requestParams.put("UserId", userId);
            requestParams.put("Latitude", latitude);
            requestParams.put("Longitude", longitude);
            requestParams.put("Comment", comment);
            ApiManager apiManager = new ApiManager(LoginActivity.this, "post", Const.UPDATE_LOCATION, requestParams, apiCallback);
            apiManager.loadURL(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        checkUserExistance();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}