package com.friendmatch_frontend.friendmatch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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


public class FriendSuggestionFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    ProgressDialog pDialog;
    LinearLayout suggestionLayout;

    public FriendSuggestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // initialize progress dialog
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getString(R.string.suggestion_progress_dialog_message));
        pDialog.setCancelable(false);

        getFriendSuggestion(container);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_suggestion, container, false);
    }

    public void getFriendSuggestion(final ViewGroup container) {

        showpDialog();

        final CardView friendError = (CardView) container.findViewById(R.id.friendError);
        suggestionLayout = (LinearLayout) container.findViewById(R.id.suggestionLayout);

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/user/suggest/friends";

        // handle cookies
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getContext()),
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
                            Log.d(TAG, "Code: " + code);

                            if (code == 200) {
                                JSONArray friendJSONArray = (response.getJSONObject("message")).getJSONArray("suggestions");
                                ArrayList<User> friendArrayList = new ArrayList<>();

                                for (int i = 0; i < friendJSONArray.length(); i++) {
                                    JSONObject friend = friendJSONArray.getJSONObject(i);
                                    User user = new User(friend.getInt("id"), friend.getString("user_name"),
                                            friend.getString("gender"));
                                    friendArrayList.add(user);
                                }

                                ExpandableHeightGridView friendGrid =
                                        (ExpandableHeightGridView) container.findViewById(R.id.friendGrid);
                                FriendGridAdapter friendGridAdapter =
                                        new FriendGridAdapter(getContext(), friendArrayList);
                                friendGrid.setAdapter(friendGridAdapter);
                                friendGrid.setExpanded(true);

                                hidepDialog();

                            } else {
                                hidepDialog();
                                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            hidepDialog();
                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                hidepDialog();
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

}
