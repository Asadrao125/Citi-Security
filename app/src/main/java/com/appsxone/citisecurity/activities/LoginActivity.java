package com.appsxone.citisecurity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.api.ApiCallback;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.location_service.GoogleService;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.SharedPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements ApiCallback {
    ApiCallback apiCallback;
    EditText edtEmail, edtPassword;
    Button btnLogin;
    String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPref.init(this);
        apiCallback = LoginActivity.this;
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            fcmToken = instanceIdResult.getToken();
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

    }

    private void Login(String email, String password) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("UserName", email);
        requestParams.put("Password", password);
        requestParams.put("AppVersion", BuildConfig.VERSION_NAME);
        requestParams.put("FCMTokenId", fcmToken);
        ApiManager apiManager = new ApiManager(LoginActivity.this, "post", Const.LOGIN_SERVICE, requestParams, apiCallback);
        apiManager.loadURL(1);
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        if (apiName.equals(Const.LOGIN_SERVICE)) {
            try {
                JSONObject jsonObject = new JSONObject(apiResponce);
                if (jsonObject.getString("Status").equals("Success")) {
                    SharedPref.write("login_responce", apiResponce);
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
                    Toast.makeText(this, "" + jsonObject.getString("Status"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "" + jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkUserExistance() {
        if (SharedPref.read("login", "").equals("true")) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    startService(new Intent(getApplicationContext(), GoogleService.class));
                    registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String latitude = intent.getStringExtra("latutide");
            String longitude = intent.getStringExtra("longitude");
            if (SharedPref.read("login", "").equals("true")) {
                updateLocation(latitude, longitude, "Loggedin");
            } else {
                updateLocation(latitude, longitude, "Loggedout");
            }
        }
    };

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

    private void updateLocation(String latitude, String longiTude, String comment) {
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
        requestParams.put("Longitude", longiTude);
        requestParams.put("Comment", comment);
        ApiManager apiManager = new ApiManager(LoginActivity.this, "post", Const.UPDATE_LOCATION, requestParams, apiCallback);
        apiManager.loadURL(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        checkUserExistance();
    }
}