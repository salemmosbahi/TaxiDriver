package it.mahd.taxidriver.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by salem on 2/22/16.
 */
public class Controllers {
    //public static final String url = "http://10.0.2.2:4004";
    public static final String url = "http://192.168.1.3:4004";
    public static final String url_addReclamation = url + "/addReclamation";
    public static final String url_getAllReclamation = url + "/getAllReclamation";
    public static final String url_addMessage = url + "/addMessage";
    public static final String url_getMessage = url + "/getAllMessage";
    public static final String url_addBook = url + "/addBook";
    public static final String url_login = url + "/login";
    public static final String url_profile = url + "/profile";
    public static final String url_logout = url + "/logout";
    public static final String url_getAllCountry = url + "/getAllCountry";
    public static final String url_getAllCity = url + "/getAllCity";
    public static final String url_signup = url + "/signup";
    public static final String url_getTaxiDriving = url + "/getTaxiDriving";
    public static final String url_searchTaxi = url + "/searchTaxi";
    public static final String url_addTaxiToDriver = url + "/addTaxiToDriver";


    public static final String app = "AppTaxiDriver";
    public static final String res = "res";
    public static final String response = "response";

    public static final String tag_key = "key";
    public static final String tag_id = "_id";
    public static final String tag_tokenDriver = "tokenDriver";
    public static final String tag_tokenClient = "tokenClient";
    public static final String tag_token = "token";
    public static final String tag_username = "username";
    public static final String tag_name = "name";
    public static final String tag_fname = "fname";
    public static final String tag_lname = "lname";
    public static final String tag_picture = "picture";
    public static final String tag_email = "email";
    public static final String tag_password = "password";
    public static final String tag_gender = "gender";
    public static final String tag_dateN = "dateN";
    public static final String tag_country = "country";
    public static final String tag_city = "city";
    public static final String tag_phone = "phone";
    public static final String tag_pt = "pt";
    public static final String tag_ptt = "ptt";
    public static final String tag_subject = "subject";
    public static final String tag_message = "message";
    public static final String tag_date = "date";
    public static final String tag_status = "status";
    public static final String tag_sender = "sender";
    public static final String tag_latitude = "latitude";
    public static final String tag_longitude = "longitude";
    public static final String tag_repeat = "repeat";
    public static final String tag_mon = "mon";
    public static final String tag_tue = "tue";
    public static final String tag_wed = "wed";
    public static final String tag_thu = "thu";
    public static final String tag_fri = "fri";
    public static final String tag_sat = "sat";
    public static final String tag_sun = "sun";
    public static final String tag_description = "description";
    public static final String tag_working = "working";
    public static final String tag_originLatitude = "originLatitude";
    public static final String tag_originLongitude = "originLongitude";
    public static final String tag_desLatitude = "desLatitude";
    public static final String tag_desLongitude = "desLongitude";
    public static final String tag_validRoute = "validRoute";
    public static final String tag_pcourse = "pcourse";
    public static final String tag_ptake = "ptake";
    public static final String tag_preturn = "preturn";
    public static final String tag_mark = "mark";
    public static final String tag_model = "model";
    public static final String tag_serial = "serial";
    public static final String tag_places = "places";
    public static final String tag_luggages = "luggages";
    public static final String tag_color = "color";

    public static final String tag_socket = "socket";
    public static final String io_searchTaxi = "searchTaxi";
    public static final String io_preBook = "preBook";
    public static final String io_validBook = "validBook";
    public static final String io_postBook = "postBook";
    public static final String io_drawRoute = "drawRoute";
    public static final String io_validRoute = "validRoute";
    public static final String io_endCourse = "endCourse";

    public Controllers() {}

    public boolean NetworkIsAvailable(Context cx) {
        ConnectivityManager manager = (ConnectivityManager) cx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
