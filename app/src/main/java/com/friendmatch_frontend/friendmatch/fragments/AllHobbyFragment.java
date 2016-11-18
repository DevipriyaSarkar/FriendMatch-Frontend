package com.friendmatch_frontend.friendmatch.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.friendmatch_frontend.friendmatch.R;
import com.friendmatch_frontend.friendmatch.adapters.HobbyGridAdapter;
import com.friendmatch_frontend.friendmatch.application.AppController;
import com.friendmatch_frontend.friendmatch.models.Hobby;
import com.friendmatch_frontend.friendmatch.utilities.ExpandableHeightGridView;
import com.friendmatch_frontend.friendmatch.utilities.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import static com.friendmatch_frontend.friendmatch.application.AppController.LOCAL_IP_ADDRESS;


public class AllHobbyFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    ProgressDialog pDialog;
    int hobbyImageID = R.drawable.hobby;

    public AllHobbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // initialize progress dialog
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getString(R.string.hobby_progress_dialog_message));
        pDialog.setCancelable(false);

        getAllHobbies(container);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_hobby, container, false);
    }

    public void getAllHobbies(final ViewGroup container) {

        showProgressDialog();

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/all/hobby";

        // handle cookies
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getContext()),
                CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        // final View allHobbyView = container.findViewById(R.id.allHobbyView);

        final LinearLayout hobbyLayout = (LinearLayout) container.findViewById(R.id.hobbyLayout);
        final TextView hobbyError = (TextView) container.findViewById(R.id.hobbyError);
        // TextView hobbySectionHeading = (TextView) container.findViewById(R.id.hobbySectionHeading);
        // hobbySectionHeading.setText(R.string.all_hobbies_heading);

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
                                JSONArray hobbyJSONArray = (response.getJSONObject("message")).getJSONArray("hobby");
                                final ArrayList<Hobby> hobbyArrayList = new ArrayList<>();

                                for (int i = 0; i < hobbyJSONArray.length(); i++) {
                                    JSONObject hObj = hobbyJSONArray.getJSONObject(i);
                                    Hobby h = new Hobby(hObj.getInt("hobby_id"), hObj.getString("hobby_name"), hobbyImageID,
                                            hObj.getBoolean("is_user_hobby"));
                                    hobbyArrayList.add(h);
                                }

                                if (hobbyArrayList.isEmpty()) {
                                    hobbyError.setVisibility(View.VISIBLE);
                                    hobbyError.setText(R.string.hobby_empty_error);
                                }

                                ExpandableHeightGridView hobbyGrid = (ExpandableHeightGridView)
                                        container.findViewById(R.id.hobbyGrid);
                                HobbyGridAdapter hobbyGridAdapter = new HobbyGridAdapter(container.getContext(), hobbyArrayList);
                                hobbyGrid.setAdapter(hobbyGridAdapter);
                                hobbyGrid.setExpanded(true);
                                hobbyGrid.setEmptyView(hobbyError);

                                hobbyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                        // ask if they wanna attend that event
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                                        alertDialogBuilder.setTitle(R.string.add_hobby_dialog_title);
                                        alertDialogBuilder.setMessage(R.string.add_hobby_dialog_message);
                                        alertDialogBuilder.setPositiveButton(R.string.dialog_positive_button,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        if (hobbyArrayList.get(i).isHobby())
                                                            deleteHobby(hobbyArrayList.get(i));
                                                        else
                                                            addHobby(hobbyArrayList.get(i));
                                                    }
                                                });
                                        alertDialogBuilder.setNegativeButton(R.string.dialog_negative_button,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        // do nothing
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });

                                hideProgressDialog();

                            } else {
                                hobbyLayout.setVisibility(View.GONE);
                                hobbyError.setVisibility(View.VISIBLE);
                                hideProgressDialog();
                                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            hideProgressDialog();
                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                hideProgressDialog();
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void addHobby(Hobby hobby) {
        pDialog.setMessage(getString(R.string.add_hobby_progress_dialog_message));
        showProgressDialog();

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/user/add/hobby/" + hobby.getHobbyID();

        // handle cookies
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getContext()),
                CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlString, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());

                        try {
                            String message = response.getString("message");
                            Log.d(TAG, "Message: " + message);
                            int code = response.getInt("code");
                            Log.d(TAG, "Code: " + code);

                            if (code == 200) {
                                hideProgressDialog();
                                Toast.makeText(getContext(), R.string.add_hobby_success, Toast.LENGTH_SHORT).show();
                            } else {
                                hideProgressDialog();
                                Toast.makeText(getContext(), R.string.add_hobby_error, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            hideProgressDialog();
                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                hideProgressDialog();
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    private void deleteHobby(Hobby hobby) {
        pDialog.setMessage(getString(R.string.remove_hobby_progress_dialog_message));
        showProgressDialog();

        String urlString = "http://" + LOCAL_IP_ADDRESS + ":5000/user/delete/hobby/" + hobby.getHobbyID();

        // handle cookies
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getContext()),
                CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlString, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());

                        try {
                            String message = response.getString("message");
                            Log.d(TAG, "Message: " + message);
                            int code = response.getInt("code");
                            Log.d(TAG, "Code: " + code);

                            if (code == 200) {
                                hideProgressDialog();
                                Toast.makeText(getContext(), R.string.remove_hobby_success, Toast.LENGTH_SHORT).show();
                            } else {
                                hideProgressDialog();
                                Toast.makeText(getContext(), R.string.remove_hobby_error, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON Error: " + e.getMessage());
                            hideProgressDialog();
                            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error in " + TAG + " : " + error.getMessage());
                hideProgressDialog();
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    private void showProgressDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

}