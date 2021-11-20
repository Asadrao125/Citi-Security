package com.appsxone.citisecurity.location_service;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import static com.appsxone.citisecurity.activities.LoginActivity.getMacAddr;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.appsxone.citisecurity.R;
import com.appsxone.citisecurity.activities.HomeActivity;
import com.appsxone.citisecurity.activities.LoginActivity;
import com.appsxone.citisecurity.api.ApiManager;
import com.appsxone.citisecurity.database.Database;
import com.appsxone.citisecurity.models.OfflineLocationModel;
import com.appsxone.citisecurity.utils.Const;
import com.appsxone.citisecurity.utils.DateFunctions;
import com.appsxone.citisecurity.utils.GPSTracker;
import com.appsxone.citisecurity.utils.InternetConnection;
import com.appsxone.citisecurity.utils.Permissons;
import com.appsxone.citisecurity.utils.SharedPref;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BackgroundService extends Service {
    double latitude, longitude;
    Database database;
    String userId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        database = new Database(this);
        database.createDatabase();

        SharedPref.init(this);
        String res = SharedPref.read("login_responce", "");
        try {
            JSONObject jsonObject = new JSONObject(res);
            userId = jsonObject.getString("UserID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);

        Intent resultIntent = new Intent(this, HomeActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

        notificationBuilder.setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.logo_login)
                .setContentText(getString(R.string.app_name))
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1222, notification);
        } else {
            startForeground(1222, notification);
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GPSTracker gpsTracker = new GPSTracker(this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        Log.d("service123", "onStartCommand1: " + latitude + "\t" + longitude);
        Thread closeActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(10000);
                        if (gpsTracker.canGetLocation()) {
                            latitude = gpsTracker.getLatitude();
                            longitude = gpsTracker.getLongitude();
                            callToApi(latitude, longitude);
                            Log.d("service123", "onStartCommand2: " + latitude + "\t" + longitude);
                            i = 0;
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enable location", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                }
            }
        });
        closeActivity.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    public void callToApi(double latitude, double longitude) {
        Log.d("debug_ing1", "callToApi: incalltoapi");
        if (InternetConnection.isNetworkConnected2(getApplicationContext())) {
            Log.d("debug_ing1", "run: ");
            updateLocation(latitude + "", longitude + "", "Loggedin");

            ArrayList<OfflineLocationModel> offlineLocationModelArrayList =
                    database.getAllLocations();
            if (offlineLocationModelArrayList != null) {
                updateOfflineLocation(userId, getMacAddr());
            }

        } else {
            Log.d("debug_ing2", "run: ");
            String lat_lng_date_time = latitude + "{|}" + longitude + "{|}" +
                    DateFunctions.getCompleteDate();
            database.saveLocation(new OfflineLocationModel(0, userId,
                    getMacAddr(), lat_lng_date_time));
        }
    }

    private void updateLocation(String latitude, String longitude, String comment) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("UDID", getMacAddr());
        requestParams.put("UserId", userId);
        requestParams.put("Latitude", latitude);
        requestParams.put("Longitude", longitude);
        requestParams.put("Comment", comment);
        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(100000);
        client.post(Const.BASE_URL + Const.UPDATE_LOCATION,
                requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String content = new String(responseBody);
                            Log.d("onSuccessOnline", "Success: " + content);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        try {
                            String content = new String(responseBody);
                            Log.d("onFailureOnline", "Failure: " + content);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
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

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(100000);
        client.post(Const.BASE_URL + Const.UPDATE_OFFLINE_LOCATION,
                requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String content = new String(responseBody);
                            Log.d("onSuccessOffline", "Success: " + content);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        try {
                            String content = new String(responseBody);
                            Log.d("onFailureOffline", "Failure: " + content);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}