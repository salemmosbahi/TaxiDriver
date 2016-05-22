package it.mahd.taxidriver.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import it.mahd.taxidriver.database.AdvanceAdapterList;
import it.mahd.taxidriver.database.AdvanceDB;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 21/05/16.
 */
public class Box extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private ListView lv;
    ArrayList<AdvanceDB> advanceDBList;
    JSONArray loads = null;

    public Box() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.advance, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.box));

        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);
        lv = (ListView) v.findViewById(R.id.listAdvance);

        getBookAdvanceWaiting();

        return v;
    }

    private void getBookAdvanceWaiting() {
        if(conf.NetworkIsAvailable(getActivity())){
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            advanceDBList = new ArrayList<>();
            JSONObject json = sr.getJSON(conf.url_getAdvanceWaiting, params);
            if(json != null){
                try{
                    if(json.getBoolean("res")) {
                        loads = json.getJSONArray("data");
                        for (int i = 0; i < loads.length(); i++) {
                            JSONObject c = loads.getJSONObject(i);
                            String id = c.getString(conf.tag_id);
                            String name = c.getString(conf.tag_username);
                            String date = c.getString(conf.tag_date);
                            AdvanceDB adv = new AdvanceDB(id, name, date);
                            advanceDBList.add(adv);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            AdvanceAdapterList adapter = new AdvanceAdapterList(getActivity(), advanceDBList, Box.this);
            lv.setAdapter(adapter);
        }else{
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
