package it.mahd.taxidriver.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.mahd.taxidriver.Main;
import it.mahd.taxidriver.R;
import it.mahd.taxidriver.database.TaxiAdapterList;
import it.mahd.taxidriver.database.TaxiDB;
import it.mahd.taxidriver.util.Calculator;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.Encrypt;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 2/13/16.
 */
public class Taxi extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private EditText Search_etxt;
    private TextInputLayout Search_input;
    private ListView lv;
    private TaxiAdapterList adapter;
    ArrayList<TaxiDB> taxiDBList;
    JSONArray loads = null;

    public Taxi() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.taxi, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.taxi));

        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Search_input = (TextInputLayout) rootView.findViewById(R.id.Search_input);
        Search_etxt = (EditText) rootView.findViewById(R.id.Search_etxt);
        lv = (ListView) rootView.findViewById(R.id.listTaxi);
        lv.setTextFilterEnabled(true);

        FloatingActionButton AddTaxi_btn = (FloatingActionButton) rootView.findViewById(R.id.add_btn);
        AddTaxi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container_body, new TaxiSearch());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        if (conf.NetworkIsAvailable(getActivity())) {
            getTaxi();
        } else {
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }

        Search_etxt.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }
            public void afterTextChanged(Editable s) { }
        });

        return rootView;
    }

    private void getTaxi() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        taxiDBList = new ArrayList<>();
        JSONObject json = sr.getJSON(conf.url_getTaxiDriving, params);
        if(json != null){
            try{
                if(json.getBoolean(conf.res)) {
                    loads = json.getJSONArray("data");
                    JSONObject cx = loads.getJSONObject(0);
                    JSONArray xx = cx.getJSONArray("taxis");
                    if(xx.length() != 0){
                        for (int i = 0; i < xx.length(); i++) {
                            JSONObject c = xx.getJSONObject(i);
                            String idTaxi = c.getString(conf.tag_id);
                            String model = c.getString(conf.tag_model);
                            String serial = c.getString(conf.tag_serial);
                            String places = String.valueOf(c.getInt(conf.tag_places));
                            String luggages = String.valueOf(c.getString(conf.tag_luggages));
                            String date = c.getString(conf.tag_date);
                            Boolean working = c.getBoolean(conf.tag_working);
                            TaxiDB taxi = new TaxiDB(idTaxi, model, serial, places, luggages, date, working);
                            taxiDBList.add(taxi);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter = new TaxiAdapterList(getActivity(), taxiDBList, Taxi.this);
        lv.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new Home());
        ft.addToBackStack(null);
        ft.commit();
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
