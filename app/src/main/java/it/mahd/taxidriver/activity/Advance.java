package it.mahd.taxidriver.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
 * Created by salem on 13/04/16.
 */
public class Advance extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private SwipeRefreshLayout Refresh_swipe;
    private ListView lv;
    ArrayList<AdvanceDB> advanceDBList;
    JSONArray loads = null;

    public Advance() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.advance, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Refresh_swipe = (SwipeRefreshLayout) v.findViewById(R.id.refresh_swipe);
        lv = (ListView) v.findViewById(R.id.listAdvance);
        Refresh_swipe.setOnRefreshListener(this);
        Refresh_swipe.post(new Runnable() {
                               public void run() {
                                   Refresh_swipe.setRefreshing(true);
                                   getAllBookAdvance();
                               }
                           }
        );
        return v;
    }

    public void onRefresh() {
        getAllBookAdvance();
    }

    private void getAllBookAdvance() {
        /*if(conf.NetworkIsAvailable(getActivity())){
            Refresh_swipe.setRefreshing(true);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
            advanceDBList = new ArrayList<>();
            JSONObject json = sr.getJSON(conf.url_getAllReclamation, params);
            if(json != null){
                try{
                    if(json.getBoolean("res")) {
                        loads = json.getJSONArray("data");
                        for (int i = 0; i < loads.length(); i++) {
                            JSONObject c = loads.getJSONObject(i);
                            String id = c.getString(conf.tag_id);
                            String subject = c.getString(conf.tag_subject);
                            String date = c.getString(conf.tag_date);
                            Boolean status = c.getBoolean(conf.tag_status);
                            AdvanceDB adv = new AdvanceDB(id, subject, date, status);
                            advanceDBList.add(adv);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            AdvanceAdapterList adapter = new AdvanceAdapterList(getActivity(), advanceDBList, Advance.this);
            lv.setAdapter(adapter);
            Refresh_swipe.setRefreshing(false);
        }else{
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }*/
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
