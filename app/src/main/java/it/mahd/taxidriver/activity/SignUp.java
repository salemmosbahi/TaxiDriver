package it.mahd.taxidriver.activity;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.mahd.taxidriver.Main;
import it.mahd.taxidriver.R;
import it.mahd.taxidriver.util.Controllers;
import it.mahd.taxidriver.util.Encrypt;
import it.mahd.taxidriver.util.ServerRequest;

/**
 * Created by salem on 3/13/16.
 */
public class SignUp extends Fragment {
    SharedPreferences pref;
    ServerRequest sr = new ServerRequest();
    Controllers conf = new Controllers();

    private EditText Fname_etxt, Lname_etxt, Email_etxt, Password_etxt, Phone_etxt;
    private TextView DateN_txt;
    private TextInputLayout Fname_input, Lname_input, Email_input, Password_input;
    private Spinner Gender_sp, Country_sp, City_sp;
    private Button Login_btn, SignUp_btn;
    private ImageView Picture_iv;

    private int year, month, day;
    private static final int SELECT_PICTURE = 1;
    private String imagePath;
    private ArrayList<String> CountrysList, CitysList;
    private ArrayAdapter<String> cityAdapter, countryAdapter;
    JSONArray countrys = null, citys = null;

    public SignUp() {}

    private boolean NetworkIsAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                imagePath = selectedImageUri.getPath();
                Picture_iv.setImageURI(selectedImageUri);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup, container, false);

        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);
        Fname_input = (TextInputLayout) rootView.findViewById(R.id.input_fname);
        Fname_etxt = (EditText) rootView.findViewById(R.id.fname_etxt);
        Lname_input = (TextInputLayout) rootView.findViewById(R.id.input_lname);
        Lname_etxt = (EditText) rootView.findViewById(R.id.lname_etxt);
        Gender_sp = (Spinner) rootView.findViewById(R.id.gender_sp);
        DateN_txt = (TextView) rootView.findViewById(R.id.dateN_txt);
        Country_sp = (Spinner) rootView.findViewById(R.id.country_sp);
        City_sp = (Spinner) rootView.findViewById(R.id.city_sp);
        Email_input = (TextInputLayout) rootView.findViewById(R.id.input_email);
        Email_etxt = (EditText) rootView.findViewById(R.id.email_etxt);
        Password_input = (TextInputLayout) rootView.findViewById(R.id.input_password);
        Password_etxt = (EditText) rootView.findViewById(R.id.password_etxt);
        Phone_etxt = (EditText) rootView.findViewById(R.id.phone_etxt);
        Login_btn = (Button) rootView.findViewById(R.id.login_btn);
        SignUp_btn = (Button) rootView.findViewById(R.id.sign_up_btn);
        Picture_iv = (ImageView) rootView.findViewById(R.id.picture_iv);

        Fname_etxt.addTextChangedListener(new MyTextWatcher(Fname_etxt));
        Lname_etxt.addTextChangedListener(new MyTextWatcher(Lname_etxt));
        Email_etxt.addTextChangedListener(new MyTextWatcher(Email_etxt));
        Password_etxt.addTextChangedListener(new MyTextWatcher(Password_etxt));

        CountrysList = new ArrayList<String>();
        CitysList = new ArrayList<String>();
        List<NameValuePair> countryParams = new ArrayList<NameValuePair>();
        JSONObject json = sr.getJSON(conf.url_getAllCountry, countryParams);
        if(json != null){
            try{
                if(json.getBoolean("res")){
                    countrys = json.getJSONArray("data");
                    for (int i=0; i<countrys.length(); i++) {
                        JSONObject c = countrys.getJSONObject(i);
                        CountrysList.add(c.getString(conf.tag_name));
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        countryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,CountrysList);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Country_sp.setAdapter(countryAdapter);

        cityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,CitysList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        City_sp.setAdapter(cityAdapter);

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DateN_txt.setText(new StringBuilder().append(1990).append("-").append(month + 1).append("-").append(day));
        DateN_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, dateSetListener, year, month, day).show();
            }
        });

        Gender_sp.setSelection(1);
        Gender_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Country_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CitysList = new ArrayList<String>();
                List<NameValuePair> cityParams = new ArrayList<NameValuePair>();
                cityParams.add(new BasicNameValuePair(conf.tag_name, Country_sp.getSelectedItem().toString()));
                JSONObject jsonx = sr.getJSON(conf.url_getAllCity, cityParams);
                if (jsonx != null) {
                    try {
                        if (jsonx.getBoolean("res")) {
                            citys = jsonx.getJSONArray("data");
                            for (int i = 0; i < citys.length(); i++) {
                                JSONObject x = citys.getJSONObject(i);
                                CitysList.add(x.getString(conf.tag_name));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                cityAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, CitysList);
                cityAdapter.
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                City_sp.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Picture_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
            }
        });
        SignUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    submitForm();
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginForm();
            }
        });
        return rootView;
    }

    private String getStringPicture() {
        Picture_iv.buildDrawingCache();
        Bitmap bitmap = Picture_iv.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void LoginForm() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new Login());
        ft.commit();
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
    }

    private void submitForm() {
        if (!validateFname()) { return; }
        if (!validateLname()) { return; }
        if (!validateEmail()) { return; }
        if (!validatePassword()) { return; }

        Encrypt algo = new Encrypt();
        int x = algo.keyVirtual();
        String key = algo.key(x);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("app", algo.dec2enc(conf.app, key)));
        params.add(new BasicNameValuePair(conf.tag_fname, algo.dec2enc(Fname_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_lname, algo.dec2enc(Lname_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_gender, algo.dec2enc(Gender_sp.getSelectedItem().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_dateN, algo.dec2enc(DateN_txt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_country, algo.dec2enc(Country_sp.getSelectedItem().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_city, algo.dec2enc(City_sp.getSelectedItem().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_email, algo.dec2enc(Email_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_password, algo.dec2enc(Password_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_phone, algo.dec2enc(Phone_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_picture, getStringPicture()));
        params.add(new BasicNameValuePair(conf.tag_key, x + ""));
        JSONObject json = sr.getJSON(conf.url_signup, params);
        if(json != null){
            try{
                String jsonstr = json.getString("response");
                Toast.makeText(getActivity(),jsonstr,Toast.LENGTH_LONG).show();
                if(json.getBoolean("res")){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Login());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateFname() {
        if(Fname_etxt.getText().toString().trim().isEmpty()) {
            Fname_input.setError(getString(R.string.fname_err));
            requestFocus(Fname_etxt);
            return false;
        } else {
            Fname_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLname() {
        if(Lname_etxt.getText().toString().trim().isEmpty()) {
            Lname_input.setError(getString(R.string.lname_err));
            requestFocus(Lname_etxt);
            return false;
        } else {
            Lname_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = Email_etxt.getText().toString().trim();
        if(email.isEmpty() || !isValidEmail(email)){
            Email_input.setError(getString(R.string.email_err));
            requestFocus(Email_etxt);
            return false;
        }else{
            Email_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if(Password_etxt.getText().toString().trim().isEmpty()) {
            Password_input.setError(getString(R.string.password_err));
            requestFocus(Password_etxt);
            return false;
        } else {
            Password_input.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;
        private MyTextWatcher(View view) { this.view = view; }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.fname_etxt:
                    validateFname();
                    break;
                case R.id.lname_etxt:
                    validateLname();
                    break;
                case R.id.email_etxt:
                    validateEmail();
                    break;
                case R.id.password_etxt:
                    validatePassword();
                    break;
            }
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            DateN_txt.setText(new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
