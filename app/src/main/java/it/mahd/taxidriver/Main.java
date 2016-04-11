package it.mahd.taxidriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import it.mahd.taxidriver.activity.Book;
import it.mahd.taxidriver.activity.Taxi;
import it.mahd.taxidriver.activity.Home;
import it.mahd.taxidriver.activity.Login;
import it.mahd.taxidriver.activity.Profile;
import it.mahd.taxidriver.activity.Reclamation;
import it.mahd.taxidriver.activity.Settings;
import it.mahd.taxidriver.model.FragmentDrawer;
import it.mahd.taxidriver.util.Controllers;

public class Main extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    //Socket socket = SocketIO.getInstance();

    public Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pref = getSharedPreferences(conf.app, MODE_PRIVATE);
        //socket.connect();

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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if(id == R.id.action_notify){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }*/
        if (id == R.id.action_settings) {
            displayView(5);
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
                fragment = new Taxi();
                title = getString(R.string.taxi);
                break;
            case 3:
                fragment = new Reclamation();
                title = getString(R.string.reclamation);
                break;
            case 4:
                fragment = new Profile();
                title = getString(R.string.profile);
                break;
            case 5:
                fragment = new Settings();
                title = getString(R.string.settings);
                break;
            default:
                break;
        }

        if (fragment != null) {
            if(pref.getString(conf.tag_token, "").equals("")){
                if(title.equals(getString(R.string.home)) ||title.equals(getString(R.string.settings))) {
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
            }else{
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
