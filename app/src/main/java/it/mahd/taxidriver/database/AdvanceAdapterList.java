package it.mahd.taxidriver.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mahd.taxidriver.R;
import it.mahd.taxidriver.activity.Advance;
import it.mahd.taxidriver.activity.AdvanceProfile;
import it.mahd.taxidriver.activity.Box;
import it.mahd.taxidriver.activity.ReclamationChat;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 13/04/16.
 */
public class AdvanceAdapterList extends BaseAdapter {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<AdvanceDB> data;
    Fragment fragment;

    public AdvanceAdapterList(Context contxt, List<AdvanceDB> data, Fragment fragment) {
        this.contxt = contxt;
        this.data = data;
        this.fragment = fragment;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return data.indexOf(getItem(position)); }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        AdvanceHolder holder = new AdvanceHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.reclamation_list, null);
            holder.Name_txt = (TextView) v.findViewById(R.id.subject_txt);
            holder.Date_txt = (TextView) v.findViewById(R.id.date_txt);
            holder.Row_relative = (RelativeLayout) v.findViewById(R.id.row_rl);
            v.setTag(holder);
        } else {
            holder = (AdvanceHolder) v.getTag();
        }
        holder.Name_txt.setText(data.get(position).getName());
        holder.Date_txt.setText(data.get(position).getDate());

        holder.Row_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                Fragment fr = new AdvanceProfile();
                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString(conf.tag_id, data.get(position).getId());
                if (Box.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "Box");
                } else if (Advance.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "Advance");
                }
                fr.setArguments(args);
                ft.replace(R.id.container_body, fr);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return v;
    }

    class AdvanceHolder {
        TextView Name_txt;
        TextView Date_txt;
        RelativeLayout Row_relative;
    }
}
