package it.mahd.taxidriver.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mahd.taxidriver.Main;
import it.mahd.taxidriver.R;
import it.mahd.taxidriver.util.Calculator;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.Encrypt;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 2/13/16.
 */
public class Profile extends Fragment {
    SharedPreferences pref;
    ServerRequest sr = new ServerRequest();
    Controllers conf = new Controllers();
    Encrypt algo = new Encrypt();

    private TextView Username_txt, City_txt, Age_txt, Email_txt, Phone_txt;
    private ImageView Picture_iv;
    private RatingBar Point_rb;
    private Button Logout_btn;

    private String fname, lname, gender, dateN, country, city, email, phone, picture;
    private Float point, pointTotal;

    public Profile() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile, container, false);

        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Username_txt = (TextView) rootView.findViewById(R.id.username_txt);
        Username_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });


        Age_txt = (TextView) rootView.findViewById(R.id.age_txt);
        Age_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        City_txt = (TextView) rootView.findViewById(R.id.city_txt);
        City_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        Email_txt = (TextView) rootView.findViewById(R.id.email_txt);
        Email_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        Phone_txt = (TextView) rootView.findViewById(R.id.phone_txt);
        Phone_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        Point_rb = (RatingBar) rootView.findViewById(R.id.pt_rb);
        LayerDrawable stars = (LayerDrawable) Point_rb.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);

        Picture_iv = (ImageView) rootView.findViewById(R.id.picture_iv);

        Logout_btn = (Button) rootView.findViewById(R.id.logout_btn);
        Logout_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    logoutFunct();
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(conf.NetworkIsAvailable(getActivity())){
            findUser();
        }else{
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    public void findUser() {
        Encrypt algo = new Encrypt();
        int x = algo.keyVirtual();
        String key = algo.key(x);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("app", algo.dec2enc(conf.app, key)));
        params.add(new BasicNameValuePair(conf.tag_key, x + ""));
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        JSONObject json = sr.getJSON(conf.url_profile, params);
        if(json != null){
            try{
                if(json.getBoolean("res")) {
                    int keyVirtual = Integer.parseInt(json.getString(conf.tag_key));
                    String newKey = algo.key(keyVirtual);
                    fname = algo.enc2dec(json.getString(conf.tag_fname), newKey);
                    lname = algo.enc2dec(json.getString(conf.tag_lname), newKey);
                    gender = algo.enc2dec(json.getString(conf.tag_gender), newKey);
                    dateN = algo.enc2dec(json.getString(conf.tag_dateN), newKey);
                    country = algo.enc2dec(json.getString(conf.tag_country), newKey);
                    city = algo.enc2dec(json.getString(conf.tag_city), newKey);
                    email = algo.enc2dec(json.getString(conf.tag_email), newKey);
                    phone = algo.enc2dec(json.getString(conf.tag_phone), newKey);
                    point = Float.parseFloat(algo.enc2dec(json.getString(conf.tag_pt), newKey));
                    pointTotal = Float.parseFloat(algo.enc2dec(json.getString(conf.tag_ptt), newKey));
                    picture = json.getString(conf.tag_picture);
                    Username_txt.setText(fname + " " + lname);
                    int[] tab = new Calculator().getAge(dateN);
                    Age_txt.setText(tab[0] + "years, " + tab[1] + "month, " + tab[2] + "day");
                    City_txt.setText(gender + " from " + country + ", lives in " + city);
                    Email_txt.setText(email);
                    Phone_txt.setText(phone);
                    Point_rb.setRating((5 * point) / pointTotal);
                    byte[] imageAsBytes = Base64.decode(picture.getBytes(), Base64.DEFAULT);
                    Picture_iv.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Logout_btn.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    public void logoutFunct() {
        Encrypt algo = new Encrypt();
        int x = algo.keyVirtual();
        String key = algo.key(x);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("app", algo.dec2enc(conf.app, key)));
        params.add(new BasicNameValuePair(conf.tag_key, x + ""));
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        JSONObject json = sr.getJSON(conf.url_logout, params);
        if(json != null){
            try{
                if(json.getBoolean("res")){
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(conf.tag_token, "");
                    edit.putString(conf.tag_fname, "");
                    edit.putString(conf.tag_lname, "");
                    edit.putString(conf.tag_picture, "");
                    edit.commit();

                    RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.nav_header_container);
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View vi = inflater.inflate(R.layout.toolnav_drawer, null);
                    TextView tv = (TextView) vi.findViewById(R.id.usernameTool_txt);
                    tv.setText("");
                    rl.addView(vi);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Home());
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.home));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new Home());
        ft.addToBackStack(null);
        ft.commit();
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.home));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
