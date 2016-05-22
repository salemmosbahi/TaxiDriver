package it.mahd.taxidriver.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mahd.taxidriver.Main;
import it.mahd.taxidriver.R;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 21/05/16.
 */
public class AdvanceProfile extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private TextView UsernameClient_txt, Status_txt, Date_txt, Description_txt, UsernameDriver_txt;
    private Button Accept_btn;
    private JSONArray dataJsonArray = null;
    private String idAdvance, usernameClient, date, description, usernameDriver, latitude, longitude;
    private boolean status;
    private String activity;
    private MapView mMapView;
    private GoogleMap googleMap;
    private CameraPosition cameraPosition;

    public AdvanceProfile() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.advance_profile, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.advance));

        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);
        idAdvance = getArguments().getString(conf.tag_id);
        activity = getArguments().getString(conf.tag_activity);

        UsernameClient_txt = (TextView) v.findViewById(R.id.UsernameClient_txt);
        Status_txt = (TextView) v.findViewById(R.id.Status_txt);
        Date_txt = (TextView) v.findViewById(R.id.Date_txt);
        Description_txt = (TextView) v.findViewById(R.id.Description_txt);
        UsernameDriver_txt = (TextView) v.findViewById(R.id.UsernameDriver_txt);
        Accept_btn = (Button) v.findViewById(R.id.Accept_btn);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (conf.NetworkIsAvailable(getActivity())) {
            getAdvanceById();
        } else {
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }

        if (latitude.equals("")) {
            googleMap.clear();
            mMapView.setVisibility(View.GONE);
        } else {
            double x = Double.valueOf(latitude);
            double y = Double.valueOf(longitude);
            MarkerOptions mark = new MarkerOptions().position(new LatLng(x, y)).title(usernameClient);
            mark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            googleMap.addMarker(mark);
            cameraPosition = new CameraPosition.Builder().target(new LatLng(x, y)).zoom(10).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        UsernameClient_txt.setText(usernameClient);
        String st = (status) ? "Complete" : "padding";
        Status_txt.setText(st);
        Date_txt.setText(date);
        Description_txt.setText(description);
        if (usernameDriver.equals("null")) {
            UsernameDriver_txt.setVisibility(View.GONE);
        } else {
            UsernameDriver_txt.setText(usernameDriver);
        }

        if (!status) {
            Accept_btn.setVisibility(View.VISIBLE);
        } else {
            Accept_btn.setVisibility(View.GONE);
        }

        Accept_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addAcceptFunc();
            }
        });

        return v;
    }

    private void addAcceptFunc() {
        List<NameValuePair> paramx = new ArrayList<NameValuePair>();
        paramx.add(new BasicNameValuePair(conf.tag_id, idAdvance));
        paramx.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        paramx.add(new BasicNameValuePair(conf.tag_username, pref.getString(conf.tag_fname, "") + " " + pref.getString(conf.tag_lname, "")));
        JSONObject jsonx = sr.getJSON(conf.url_acceptDemand, paramx);
        if (jsonx != null) {
            try {
                Toast.makeText(getActivity(), jsonx.getString(conf.response), Toast.LENGTH_SHORT).show();
                if (jsonx.getBoolean(conf.res)) {
                    Accept_btn.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAdvanceById() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_id, idAdvance));
        JSONObject json = sr.getJSON(conf.url_getAdvanceById, params);
        if (json != null) {
            try {
                if(json.getBoolean(conf.res)) {
                    dataJsonArray = json.getJSONArray("data");
                    JSONObject c = dataJsonArray.getJSONObject(0);
                    usernameClient = c.getString(conf.tag_username);
                    latitude = c.getString(conf.tag_latitude);
                    longitude = c.getString(conf.tag_longitude);
                    date = c.getString(conf.tag_dateBook);
                    description = c.getString(conf.tag_description);
                    status = c.getBoolean(conf.tag_status);
                    usernameDriver = c.getString(conf.tag_nameDriver);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private void goFragment(String str) {
        if (str.equals("Box")) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.container_body, new Box());
            ft.commit();
        } else if (str.equals("Advance")) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.container_body, new Advance());
            ft.commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activity.equals("Box")) {
            goFragment("Box");
        } else if (activity.equals("Advance")) {
            goFragment("Advance");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
