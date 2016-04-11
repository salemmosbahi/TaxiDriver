package it.mahd.taxidriver.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by salem on 2/18/16.
 */
public class ReclamationAdd extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private TextInputLayout Subject_input, Msg_input;
    private EditText Subject_etxt, Msg_etxt;
    private Button Send_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reclamation_add, container, false);

        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);
        Subject_input = (TextInputLayout) rootView.findViewById(R.id.input_subject);
        Subject_etxt = (EditText) rootView.findViewById(R.id.subject_etxt);
        Msg_input = (TextInputLayout) rootView.findViewById(R.id.input_msg);
        Msg_etxt = (EditText) rootView.findViewById(R.id.msg_etxt);
        Send_btn = (Button) rootView.findViewById(R.id.send_btn);
        Send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    submit();
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    private void submit() {
        if (!validateSubject()) { return; }
        if (!validateMsg()) { return; }

        Encrypt algo = new Encrypt();
        int x = algo.keyVirtual();
        String key = algo.key(x);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        params.add(new BasicNameValuePair(conf.tag_username, algo.dec2enc(pref.getString(conf.tag_fname, "") + " " + pref.getString(conf.tag_lname, ""), key)));
        params.add(new BasicNameValuePair(conf.tag_subject, algo.dec2enc(Subject_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_message, algo.dec2enc(Msg_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_key, x + ""));
        JSONObject json = sr.getJSON(conf.url_addReclamation, params);
        if(json != null){
            try{
                String jsonstr = json.getString("response");
                Toast.makeText(getActivity(), jsonstr, Toast.LENGTH_LONG).show();
                if(json.getBoolean("res")){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Reclamation());
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.reclamation));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getActivity(),"App server is unavailable!",Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateSubject() {
        if(Subject_etxt.getText().toString().trim().isEmpty()) {
            Subject_input.setError(getString(R.string.subject_err));
            requestFocus(Subject_etxt);
            return false;
        } else {
            Subject_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateMsg() {
        if(Msg_etxt.getText().toString().trim().isEmpty()) {
            Msg_input.setError(getString(R.string.message_err));
            requestFocus(Msg_etxt);
            return false;
        } else {
            Msg_input.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new Reclamation());
        ft.addToBackStack(null);
        ft.commit();
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.reclamation));
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
