package it.mahd.taxidriver;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mahd.taxidriver.activity.Advance;
import it.mahd.taxidriver.activity.Book;
import it.mahd.taxidriver.activity.Taxi;
import it.mahd.taxidriver.activity.Home;
import it.mahd.taxidriver.activity.Login;
import it.mahd.taxidriver.activity.Profile;
import it.mahd.taxidriver.activity.Reclamation;
import it.mahd.taxidriver.activity.Settings;
import it.mahd.taxidriver.model.FragmentDrawer;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.ServerRequest;
import it.mahd.taxidriver.util.SocketIO;

public class Main extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();
    Socket socket = SocketIO.getInstance();

    public Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private static Dialog bookDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pref = getSharedPreferences(conf.app, MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        displayView(0);
        if(!pref.getString(conf.tag_token, "").equals("")){
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.nav_header_container);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.toolnav_drawer, null);
            TextView tv = (TextView) vi.findViewById(R.id.usernameTool_txt);
            tv.setText(pref.getString(conf.tag_fname, "") + " " + pref.getString(conf.tag_lname, ""));
            ImageView im = (ImageView) vi.findViewById(R.id.pictureTool_iv);
            byte[] imageAsBytes = Base64.decode(pref.getString(conf.tag_picture, "").getBytes(), Base64.DEFAULT);
            im.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            rl.addView(vi);

            socket.connect();
            socket.on(conf.io_notify, handleIncomingNotify);//listen in book advance
        }
    }

    private Emitter.Listener handleIncomingNotify = new Emitter.Listener(){
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    final Boolean notify;
                    try {
                        notify = data.getBoolean(conf.tag_notify);
                        if (notify) {
                            bookDialog = new Dialog(Main.this, R.style.FullHeightDialog);
                            bookDialog.setContentView(R.layout.advance_dialog);
                            bookDialog.setCancelable(false);
                            bookDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    bookDialog.dismiss();
                                }}, 2000);  // 2 seconds
                        }
                    } catch (JSONException e) { }
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_notify);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_notify){
            item.setVisible(false);
            displayView(6);
            return true;
        }
        if (id == R.id.action_settings) {
            displayView(7);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new Home();
                title = getString(R.string.home);
                break;
            case 1:
                fragment = new Book();
                title = getString(R.string.now);
                break;
            case 2:
                fragment = new Advance();
                title = getString(R.string.advance);
                break;
            case 3:
                fragment = new Taxi();
                title = getString(R.string.taxi);
                break;
            case 4:
                fragment = new Reclamation();
                title = getString(R.string.reclamation);
                break;
            case 5:
                fragment = new Profile();
                title = getString(R.string.profile);
                break;
            case 6:
                fragment = new Settings();
                title = getString(R.string.notify);
                break;
            case 7:
                fragment = new Settings();
                title = getString(R.string.settings);
                break;
            default:
                break;
        }

        if (fragment != null) {
            if(pref.getString(conf.tag_token, "").equals("")){
                if(title.equals(getString(R.string.home)) || title.equals(getString(R.string.settings))) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(title);
                }else{
                    fragment = new Login();
                    title = getString(R.string.login);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(title);
                }
            } else {
                if (title.equals(getString(R.string.now)) || title.equals(getString(R.string.advance))) {
                    if(conf.NetworkIsAvailable(this)){
                        haveTaxi(title, fragment);
                    }else{
                        Toast.makeText(this, R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(title);
                }
            }
        }
    }

    private void haveTaxi(String str, Fragment frag) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        JSONObject json = sr.getJSON(conf.url_haveTaxi, params);
        if(json != null){
            try{
                if(json.getBoolean(conf.res)) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, frag);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    getSupportActionBar().setTitle(str);
                } else {
                    Toast.makeText(this, "Don't have a taxi working", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }
}
