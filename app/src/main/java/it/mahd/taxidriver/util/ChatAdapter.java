package it.mahd.taxidriver.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.mahd.taxidriver.R;

/**
 * Created by salem on 2/21/16.
 */
public class ChatAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    ArrayList chatMessageList;

    public ChatAdapter(Activity activity, ArrayList list) {
        chatMessageList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.reclamation_msgview, null);

        TextView msg = (TextView) vi.findViewById(R.id.message_txt);
        msg.setText(message.Message);
        TextView date = (TextView) vi.findViewById(R.id.date_txt);
        date.setText(message.Date);
        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi.findViewById(R.id.bubble_layout_parent);

        if (message.IsMe) {// if message is me then align to right
            //layout.setBackgroundResource(R.drawable.bubble2);
            parent_layout.setGravity(Gravity.RIGHT);
        }else {// If not me then align to left
            //layout.setBackgroundResource(R.drawable.bubble1);
            parent_layout.setGravity(Gravity.LEFT);
        }
        //msg.setTextColor(Color.BLACK);
        return vi;
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }
}
