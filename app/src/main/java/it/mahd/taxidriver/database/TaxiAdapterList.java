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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.mahd.taxidriver.R;
import it.mahd.taxidriver.activity.TaxiProfile;
import it.mahd.taxidriver.util.Controllers;

/**
 * Created by salem on 4/6/16.
 */
public class TaxiAdapterList extends BaseAdapter implements Filterable {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<TaxiDB> data;
    List<TaxiDB> dataFilter;

    Fragment fragment;
    private ItemFilter mFilter = new ItemFilter();

    public TaxiAdapterList(Context contxt, List<TaxiDB> data, Fragment fragment) {
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

    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TaxiHolder holder = new TaxiHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.taxi_list, null);
            holder.Model_txt = (TextView) v.findViewById(R.id.Model_txt);
            holder.Serial_txt = (TextView) v.findViewById(R.id.Serial_txt);
            holder.Date_txt = (TextView) v.findViewById(R.id.Date_txt);
            holder.Row_relative = (RelativeLayout) v.findViewById(R.id.row_rl);
            v.setTag(holder);
        } else {
            holder = (TaxiHolder) v.getTag();
        }

        holder.Serial_txt.setText(data.get(position).getSerial());
        holder.Date_txt.setText(data.get(position).getDate());
        if (data.get(position).getWorking()) {
            holder.Model_txt.setText("My Taxi " + data.get(position).getModel());
            holder.Model_txt.setTypeface(null, Typeface.BOLD_ITALIC);
            holder.Serial_txt.setTypeface(null, Typeface.BOLD_ITALIC);
            holder.Date_txt.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            holder.Model_txt.setText(data.get(position).getModel());
            holder.Model_txt.setTypeface(null, Typeface.NORMAL);
            holder.Serial_txt.setTypeface(null, Typeface.NORMAL);
            holder.Date_txt.setTypeface(null, Typeface.NORMAL);
        }

        holder.Row_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                Fragment fr = new TaxiProfile();
                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString(conf.tag_id, data.get(position).getIdTaxi());
                args.putString(conf.tag_model, data.get(position).getModel());
                args.putString(conf.tag_serial, data.get(position).getSerial());
                args.putString(conf.tag_places, data.get(position).getPlaces());
                args.putString(conf.tag_luggages, data.get(position).getLuggages());
                args.putString(conf.tag_color, data.get(position).getColor());
                args.putString(conf.tag_date, data.get(position).getDate());
                args.putBoolean(conf.tag_working, data.get(position).getWorking());
                fr.setArguments(args);
                ft.replace(R.id.container_body, fr);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return v;
    }

    static class TaxiHolder {
        TextView Model_txt, Serial_txt, Date_txt;
        RelativeLayout Row_relative;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<TaxiDB> list = data;
            int count = list.size();
            final ArrayList<TaxiDB> nlist = new ArrayList<TaxiDB>(count);

            for (TaxiDB p : data) {
                if (p.getSerial().toLowerCase().contains(filterString))
                    nlist.add(p);
            }
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataFilter = (ArrayList<TaxiDB>) results.values;
            notifyDataSetChanged();
        }
    }
}
