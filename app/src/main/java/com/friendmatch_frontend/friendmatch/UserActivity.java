package com.friendmatch_frontend.friendmatch;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class UserActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    ProgressDialog pDialog;
    NestedScrollView contentUser;
    CollapsingToolbarLayout toolbarLayout;
    View commonHobbyView, allHobbyView, allFriendsView;
    TextView commonHobbyHeading, allHobbyHeading, allFriendsHeading;
    int friendID;   // user ID of the clicked friend/user
    String friendName, friendGender;
    int count = 0;  // count of the number of async tasks completed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // getting initial user data from calling activity
        Bundle bundle = getIntent().getExtras();
        friendID = bundle.getInt("FRIEND_ID");
        friendName = bundle.getString("FRIEND_NAME");
        friendGender = bundle.getString("FRIEND_GENDER");

        contentUser = (NestedScrollView) findViewById(R.id.contentUser);

        // setting up the section headings
        commonHobbyView = findViewById(R.id.userCommonHobbies);
        allHobbyView = findViewById(R.id.userAllHobbies);
        allFriendsView = findViewById(R.id.userAllFriends);
        commonHobbyHeading = (TextView) commonHobbyView.findViewById(R.id.hobbySectionHeading);
        allHobbyHeading = (TextView) allHobbyView.findViewById(R.id.hobbySectionHeading);
        allFriendsHeading = (TextView) allFriendsView.findViewById(R.id.friendsSectionHeading);

        commonHobbyHeading.setText(R.string.mutual_hobby_section_heading);
        allHobbyHeading.setText(R.string.all_hobby_section_heading);
        allFriendsHeading.setText(R.string.all_friends_section_heading);

        // setting up the collapsing toolbar
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(friendName);
        ImageView userProfileImage = (ImageView) findViewById(R.id.userProfileImage);
        if (friendGender.equals("Male")) {
            userProfileImage.setImageResource(R.drawable.male);
        } else {
            userProfileImage.setImageResource(R.drawable.female);
        }
        Bitmap bitmap = ((BitmapDrawable) userProfileImage.getDrawable()).getBitmap();
        if (bitmap != null) {
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    toolbarLayout.setContentScrimColor(palette.getMutedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
                    toolbarLayout.setStatusBarScrimColor(palette.getMutedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));
                    toolbarLayout.setExpandedTitleColor(palette.getDarkMutedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryText)));
                }
            });
        }

        // initialize progress dialog
        pDialog = new ProgressDialog(this);
        String firstName = friendName.substring(0, friendName.indexOf(" "));
        pDialog.setMessage(getString(R.string.user_profile_prog_dialog_msg1) + " " + firstName +
                getString(R.string.user_profile_prog_dialog_msg2));
        pDialog.setCancelable(false);

        showProgressDialog();

        // fetch all the data
        getUserInfo();
        getCommonHobbies();
        getAllHobbies();
        getAllFriends();

        FloatingActionButton addFriendFAB = (FloatingActionButton) findViewById(R.id.addFriendFAB);
        addFriendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void getUserInfo() {

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/user/" + friendID + "/info";

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
                        count++;
                        try {
                            int code = response.getInt("code");
                            Log.d(TAG, "Code: " + code);

                            if (code == 200) {

                                // UI references
                                TextView infoGender = (TextView) findViewById(R.id.infoGender);
                                TextView infoAge = (TextView) findViewById(R.id.infoAge);
                                TextView infoEmail = (TextView) findViewById(R.id.infoEmail);
                                TextView infoPhone = (TextView) findViewById(R.id.infoPhone);
                                TextView infoLocation = (TextView) findViewById(R.id.infoLocation);
                                TextView infoCity = (TextView) findViewById(R.id.infoCity);

                                JSONObject info = (response.getJSONObject("message")).getJSONObject("info");

                                // set data
                                infoGender.setText(friendGender);
                                infoAge.setText(info.getString("age"));
                                infoEmail.setText(info.getString("user_email"));
                                Linkify.addLinks(infoEmail, Linkify.EMAIL_ADDRESSES);
                                infoPhone.setText(info.getString("phone_number"));
                                Linkify.addLinks(infoPhone, Linkify.PHONE_NUMBERS);
                                infoLocation.setText(info.getString("location"));
                                Linkify.addLinks(infoLocation, Linkify.MAP_ADDRESSES);
                                infoCity.setText(info.getString("city"));

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                count++;
                hideProgressDialog();
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    public void getCommonHobbies() {

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/user/common/hobby/" + friendID;

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
                        count++;
                        try {
                            int code = response.getInt("code");
                            Log.d(TAG, "Code: " + code);

                            LinearLayout hobbyLayout = (LinearLayout) commonHobbyView.findViewById(R.id.hobbyLayout);
                            TextView hobbyError = (TextView) commonHobbyView.findViewById(R.id.hobbyError);

                            if (code == 200) {

                                hobbyLayout.setVisibility(View.VISIBLE);
                                hobbyError.setVisibility(View.GONE);

                                JSONArray hobbyJSONArray = (response.getJSONObject("message")).getJSONArray("common_hobby");
                                ArrayList<Hobby> hobbyArrayList = new ArrayList<>();

                                for (int i = 0; i < hobbyJSONArray.length(); i++) {
                                    JSONObject hobby = hobbyJSONArray.getJSONObject(i);
                                    Hobby h = new Hobby(hobby.getInt("hobby_id"), hobby.getString("hobby_name"), R.drawable.hobby);
                                    hobbyArrayList.add(h);
                                }

                                if (hobbyArrayList.isEmpty()) {
                                    hobbyError.setVisibility(View.VISIBLE);
                                    hobbyError.setText(R.string.common_hobby_empty_error);
                                }

                                ExpandableHeightGridView hobbyGrid =
                                        (ExpandableHeightGridView) commonHobbyView.findViewById(R.id.hobbyGrid);
                                HobbyGridAdapter hobbyGridAdapter = new HobbyGridAdapter(getApplicationContext(),
                                        hobbyArrayList);
                                hobbyGrid.setAdapter(hobbyGridAdapter);
                                hobbyGrid.setExpanded(true);
                                hobbyGrid.setEmptyView(hobbyError);

                                hobbyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    }
                                });

                            } else {
                                hobbyLayout.setVisibility(View.GONE);
                                hobbyError.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                count++;
                hideProgressDialog();
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    public void getAllHobbies() {

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/user/" + friendID + "/hobby";

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
                        count++;
                        try {
                            int code = response.getInt("code");
                            Log.d(TAG, "Code: " + code);

                            LinearLayout hobbyLayout = (LinearLayout) allHobbyView.findViewById(R.id.hobbyLayout);
                            TextView hobbyError = (TextView) allHobbyView.findViewById(R.id.hobbyError);

                            if (code == 200) {

                                hobbyLayout.setVisibility(View.VISIBLE);
                                hobbyError.setVisibility(View.GONE);

                                JSONArray hobbyJSONArray = (response.getJSONObject("message")).getJSONArray("hobby");
                                ArrayList<Hobby> hobbyArrayList = new ArrayList<>();

                                for (int i = 0; i < hobbyJSONArray.length(); i++) {
                                    JSONObject hobby = hobbyJSONArray.getJSONObject(i);
                                    Hobby h = new Hobby(hobby.getInt("hobby_id"), hobby.getString("hobby_name"), R.drawable.hobby);
                                    hobbyArrayList.add(h);
                                }

                                if (hobbyArrayList.isEmpty()) {
                                    hobbyError.setVisibility(View.VISIBLE);
                                    hobbyError.setText(R.string.hobby_empty_error);
                                }

                                ExpandableHeightGridView hobbyGrid =
                                        (ExpandableHeightGridView) allHobbyView.findViewById(R.id.hobbyGrid);
                                HobbyGridAdapter hobbyGridAdapter = new HobbyGridAdapter(getApplicationContext(),
                                        hobbyArrayList);
                                hobbyGrid.setAdapter(hobbyGridAdapter);
                                hobbyGrid.setExpanded(true);
                                hobbyGrid.setEmptyView(hobbyError);

                                hobbyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    }
                                });

                            } else {
                                hobbyLayout.setVisibility(View.GONE);
                                hobbyError.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                count++;
                hideProgressDialog();
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    public void getAllFriends() {

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/user/" + friendID + "/friends";

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
                        count++;
                        try {
                            int code = response.getInt("code");
                            Log.d(TAG, "Code: " + code);

                            LinearLayout friendLayout = (LinearLayout) allFriendsView.findViewById(R.id.friendLayout);
                            TextView friendError = (TextView) allFriendsView.findViewById(R.id.friendError);

                            if (code == 200) {

                                friendLayout.setVisibility(View.VISIBLE);
                                friendError.setVisibility(View.GONE);

                                JSONArray friendJSONArray = (response.getJSONObject("message")).getJSONArray("friends");
                                ArrayList<User> friendArrayList = new ArrayList<>();

                                for (int i = 0; i < friendJSONArray.length(); i++) {
                                    JSONObject friend = friendJSONArray.getJSONObject(i);
                                    User user = new User(friend.getInt("friend_id"), friend.getString("user_name"),
                                            friend.getString("gender"));
                                    friendArrayList.add(user);
                                }

                                if (friendArrayList.isEmpty()) {
                                    friendError.setVisibility(View.VISIBLE);
                                    friendError.setText(R.string.friend_empty_error);
                                }

                                ExpandableHeightGridView friendGrid =
                                        (ExpandableHeightGridView) allFriendsView.findViewById(R.id.friendGrid);
                                FriendGridAdapter friendGridAdapter = new FriendGridAdapter(getApplicationContext(),
                                        friendArrayList);
                                friendGrid.setAdapter(friendGridAdapter);
                                friendGrid.setExpanded(true);
                                friendGrid.setEmptyView(friendError);

                            } else {
                                friendLayout.setVisibility(View.GONE);
                                friendError.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                count++;
                hideProgressDialog();
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    private void showProgressDialog() {
        if (!pDialog.isShowing()) {
            contentUser.setVisibility(View.GONE);
            pDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing() && count == 4) {
            contentUser.setVisibility(View.VISIBLE);
            pDialog.dismiss();
        }
    }

}
