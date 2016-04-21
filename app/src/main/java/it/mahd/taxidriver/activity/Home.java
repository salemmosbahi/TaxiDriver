package it.mahd.taxidriver.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mahd.taxidriver.Main;
import it.mahd.taxidriver.R;
import it.mahd.taxidriver.model.FragmentDrawer;
import it.mahd.taxidriver.util.Calculator;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 2/13/16.
 */
public class Home extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    public Home() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Button Now_btn = (Button) rootView.findViewById(R.id.btn_now);
        Now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getString(conf.tag_token, "").equals("")) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Login());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
                } else {
                    if(conf.NetworkIsAvailable(getActivity())){
                        haveTaxi(getString(R.string.now));
                    }else{
                        Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button Advance_btn = (Button) rootView.findViewById(R.id.btn_advance);
        Advance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getString(conf.tag_token, "").equals("")){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Login());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
                }else{
                    if(conf.NetworkIsAvailable(getActivity())){
                        haveTaxi(getString(R.string.advance));
                    }else{
                        Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button Taxi_btn = (Button) rootView.findViewById(R.id.btn_taxi);
        Taxi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getString(conf.tag_token, "").equals("")){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Login());
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
                }else{
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Taxi());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.taxi));
                }
            }
        });

        Button Reclamation_btn = (Button) rootView.findViewById(R.id.btn_reclamation);
        Reclamation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getString(conf.tag_token, "").equals("")){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Login());
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
                }else{
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Reclamation());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.reclamation));
                }
            }
        });

        Button Profile_btn = (Button) rootView.findViewById(R.id.btn_profile);
        Profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getString(conf.tag_token, "").equals("")){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Login());
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
                }else{
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Profile());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.profile));
                }
            }
        });
        return rootView;
    }

    private void haveTaxi(String str) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        JSONObject json = sr.getJSON(conf.url_haveTaxi, params);
        if(json != null){
            try {
                if(json.getBoolean(conf.res)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Book());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(str);
                } else {
                    Toast.makeText(getActivity(), "Don't have a taxi working", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
