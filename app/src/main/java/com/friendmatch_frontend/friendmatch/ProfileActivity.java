package com.friendmatch_frontend.friendmatch;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import static com.friendmatch_frontend.friendmatch.AppController.LOCAL_IP_ADDRESS;

public class ProfileActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName() ;
    ProgressDialog pDialog;
    LinearLayout activity_profile;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        activity_profile = (LinearLayout) findViewById(R.id.activity_profile);

        // initialize progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.profile_progress_dialog_message));
        pDialog.setCancelable(false);

        getUserProfile();

    }

    /**
     * Represents an asynchronous login/registration task used to fetch profile data
     */
    public void getUserProfile() {

        showpDialog();

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/user/profile";

        // handle cookies
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()),
                CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlString, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            int code = response.getInt("code");
                            Log.d(TAG, "Code (message): " + code);
                            if (code == 200) {
                                JSONArray response_array = response.getJSONArray("message");
                                updateUI(response_array);
                            } else {
                                hidepDialog();
                                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            hidepDialog();
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                hidepDialog();
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void updateUI(JSONArray response) throws JSONException {
        JSONObject infoObj, friendsObj, hobbyObj;

        infoObj = response.getJSONObject(0);
        friendsObj = response.getJSONObject(1);
        hobbyObj = response.getJSONObject(3);

        updateInfo(infoObj);
        updateHobby(hobbyObj);
        updateFriends(friendsObj);

        Log.d(TAG, "Updated UI");
        hidepDialog();
    }

    public void updateInfo(JSONObject infoObj) throws JSONException {
        int code = infoObj.getInt("code");
        Log.d(TAG, "Code (info): " + code);

        LinearLayout infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        TextView infoError = (TextView) findViewById(R.id.infoError);

        if (code == 200) {
            infoLayout.setVisibility(View.VISIBLE);
            infoError.setVisibility(View.GONE);

            // UI references
            ImageView infoImage = (ImageView) findViewById(R.id.infoImage);
            TextView infoName = (TextView) findViewById(R.id.infoName);
            TextView infoGender = (TextView) findViewById(R.id.infoGender);
            TextView infoAge = (TextView) findViewById(R.id.infoAge);
            TextView infoEmail = (TextView) findViewById(R.id.infoEmail);
            TextView infoPhone = (TextView) findViewById(R.id.infoPhone);
            TextView infoLocation = (TextView) findViewById(R.id.infoLocation);
            TextView infoCity = (TextView) findViewById(R.id.infoCity);

            // extract data
            JSONObject info = infoObj.getJSONObject("info");
            userID = info.getInt("id");
            String gender;
            if (info.getString("gender").equals("M")) {
                gender = "Male";
                setProfilePicture(infoImage, 0);
            } else {
                gender = "Female";
                setProfilePicture(infoImage, 1);
            }

            // set data
            infoName.setText(info.getString("user_name"));
            infoGender.setText(gender);
            infoAge.setText(info.getString("age"));
            infoEmail.setText(info.getString("user_email"));
            infoPhone.setText(info.getString("phone_number"));
            infoLocation.setText(info.getString("location"));
            infoCity.setText(info.getString("city"));

        } else {
            infoLayout.setVisibility(View.GONE);
            infoError.setVisibility(View.VISIBLE);
        }
    }

    public void updateHobby(JSONObject hobbyObj) throws JSONException {
        int code = hobbyObj.getInt("code");
        Log.d(TAG, "Code (hobby): " + code);

        LinearLayout hobbyLayout = (LinearLayout) findViewById(R.id.hobbyLayout);
        CardView hobbyError = (CardView) findViewById(R.id.hobbyError);

        if (code == 200) {
            hobbyLayout.setVisibility(View.VISIBLE);
            hobbyError.setVisibility(View.GONE);

            // UI references


            // extract data
            ArrayList<Hobby> hobbyArrayList = new ArrayList<>();

            JSONArray hobbyJSONArray = hobbyObj.getJSONArray("hobby");
            for (int i = 0; i < hobbyJSONArray.length(); i++) {
                Hobby h = new Hobby(hobbyJSONArray.get(i).toString(), R.drawable.hobby);
                hobbyArrayList.add(h);
            }

            // set data

        } else {
            hobbyLayout.setVisibility(View.GONE);
            hobbyError.setVisibility(View.VISIBLE);
        }
    }

    public void updateFriends(JSONObject friendsObj) throws JSONException {
        int code = friendsObj.getInt("code");
        Log.d(TAG, "Code (friends): " + code);

    }

    public void setProfilePicture(ImageView imageView, int gender) {
        // 0 for male, 1 for female
        Bitmap iconBitmap;
        if (gender == 0) {
            iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.man);
        } else {
            iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.woman);
        }

        RoundedBitmapDrawable roundDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), iconBitmap);
        roundDrawable.setCornerRadius(Math.max(iconBitmap.getWidth(), iconBitmap.getHeight()) / 2.0f);
        imageView.setImageDrawable(roundDrawable);   // round profile picture
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            activity_profile.setVisibility(View.GONE);
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            activity_profile.setVisibility(View.VISIBLE);
            pDialog.dismiss();
    }

}
