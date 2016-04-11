package it.mahd.taxidriver.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mahd.taxidriver.Main;
import it.mahd.taxidriver.R;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.Encrypt;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 4/6/16.
 */
public class TaxiSearch extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private EditText Serial_etxt;
    private TextInputLayout Serial_input;
    private FloatingActionButton Search_btn, Add_btn;
    private TextView Mark_txt, Model_txt, Serial_txt, Places_txt, Luggages_txt;
    private String idTaxi, color, mark, model, serial, places, luggages;

    public TaxiSearch() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.taxi_search, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Serial_input = (TextInputLayout) rootView.findViewById(R.id.Serial_input);
        Serial_etxt = (EditText) rootView.findViewById(R.id.Serial_etxt);
        Search_btn = (FloatingActionButton) rootView.findViewById(R.id.Search_btn);
        Add_btn = (FloatingActionButton) rootView.findViewById(R.id.Add_btn);
        Mark_txt = (TextView) rootView.findViewById(R.id.Mark_txt);
        Model_txt = (TextView) rootView.findViewById(R.id.Model_txt);
        Serial_txt = (TextView) rootView.findViewById(R.id.Serial_txt);
        Places_txt = (TextView) rootView.findViewById(R.id.Places_txt);
        Luggages_txt = (TextView) rootView.findViewById(R.id.Luggages_txt);
        Search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    searchtForm();
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void searchtForm() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_serial, Serial_etxt.getText().toString()));
        JSONObject json = sr.getJSON(conf.url_searchTaxi, params);
        if(json != null){
            try{
                if (json.getBoolean(conf.res)) {
                    Encrypt algo = new Encrypt();
                    int keyVirtual = Integer.parseInt(json.getString(conf.tag_key));
                    String newKey = algo.key(keyVirtual);

                    idTaxi = json.getString(conf.tag_id);
                    color = algo.enc2dec(json.getString(conf.tag_color), newKey);
                    mark = algo.enc2dec(json.getString(conf.tag_mark), newKey);
                    model = algo.enc2dec(json.getString(conf.tag_model), newKey);
                    serial = algo.enc2dec(json.getString(conf.tag_serial), newKey);
                    places = algo.enc2dec(json.getString(conf.tag_places), newKey) + " Places";
                    luggages = algo.enc2dec(json.getString(conf.tag_luggages), newKey) + " Kg Luggages";
                    Mark_txt.setTextColor(Color.parseColor(color));
                    Mark_txt.setText(mark);
                    Model_txt.setTextColor(Color.parseColor(color));
                    Model_txt.setText(model);
                    Serial_txt.setTextColor(Color.parseColor(color));
                    Serial_txt.setText(serial);
                    Places_txt.setTextColor(Color.parseColor(color));
                    Places_txt.setText(places);
                    Luggages_txt.setTextColor(Color.parseColor(color));
                    Luggages_txt.setText(luggages);
                    Add_btn.setVisibility(View.VISIBLE);
                    Add_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (conf.NetworkIsAvailable(getActivity())) {
                                submitForm();
                            } else {
                                Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Mark_txt.setText("");
                    Model_txt.setText("");
                    Serial_txt.setText("");
                    Places_txt.setText("");
                    Luggages_txt.setText("");
                    Add_btn.setVisibility(View.GONE);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    private void submitForm() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        params.add(new BasicNameValuePair(conf.tag_id, idTaxi));
        JSONObject json = sr.getJSON(conf.url_addTaxiToDriver, params);
        if(json != null){
            try{
                Toast.makeText(getActivity(), json.getString(conf.response), Toast.LENGTH_SHORT).show();
                if (json.getBoolean(conf.res)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Taxi());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.taxi));
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }
}
