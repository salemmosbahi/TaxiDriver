package it.mahd.taxidriver.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.mahd.taxidriver.Main;
import it.mahd.taxidriver.R;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 2/13/16.
 */
public class Reclamation extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private SwipeRefreshLayout Refresh_swipe;
    ArrayList<HashMap<String, String>> ReclamationList;
    JSONArray loads = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reclamation, container, false);

        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);
        Refresh_swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_swipe);
        Refresh_swipe.setOnRefreshListener(this);
        Refresh_swipe.post(new Runnable() {
                               public void run() {
                                   Refresh_swipe.setRefreshing(true);
                                   getAllReclamation();
                               }
                           }
        );

        FloatingActionButton AddReclamation_btn = (FloatingActionButton) rootView.findViewById(R.id.add_btn);
        AddReclamation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container_body, new ReclamationAdd());
                ft.addToBackStack(null);
                ft.commit();
                ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.reclamation_new));
            }
        });
        return rootView;
    }

    public void onRefresh() {
        getAllReclamation();
    }

    private void getAllReclamation() {
        if(conf.NetworkIsAvailable(getActivity())){
            Refresh_swipe.setRefreshing(true);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
            ReclamationList = new ArrayList<HashMap<String, String>>();
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
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(conf.tag_id, id);
                            map.put(conf.tag_subject, subject);
                            map.put(conf.tag_date, date);
                            map.put(conf.tag_status, (status) ? "Now" : "");
                            ReclamationList.add(map);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ListAdapter adapter = new SimpleAdapter(getActivity(), ReclamationList, R.layout.reclamation_list,
                    new String[] { conf.tag_subject, conf.tag_status, conf.tag_date, conf.tag_id },
                    new int[] { R.id.subject_txt, R.id.status_txt, R.id.date_txt, R.id.idRec });
            setListAdapter(adapter);
            Refresh_swipe.setRefreshing(false);
        }else{
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    public void onListItemClick(ListView l, View view, int position, long id){
        ViewGroup viewg = (ViewGroup)view;
        TextView tv = (TextView) viewg.findViewById(R.id.idRec);
        Fragment fr = new ReclamationChat();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString(conf.tag_id, tv.getText().toString());
        fr.setArguments(args);
        ft.replace(R.id.container_body, fr);
        ft.addToBackStack(null);
        ft.commit();
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.reclamation));
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
