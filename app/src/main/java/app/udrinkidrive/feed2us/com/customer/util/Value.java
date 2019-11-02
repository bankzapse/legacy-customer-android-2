package app.udrinkidrive.feed2us.com.customer.util;

/**
 * Created by E on 12/13/2014.
 */
public class Value {

//    public static String HOST =  "http://dev2.tripapi.udrinkbackend.com/api/";
//    public static String HOST =  "https://trip.api.udrinkidev.com/api/";//DEV
//    public static String HOST =  "https://www.udrinkidrive.co.th/udrink_api_facebook/api/v1/";
      public static String HOST =  "https://trip.api.udrinkbackend.com/api/";
    public static String HOST_verify_otp =  "https://n3t2ltumwi.execute-api.ap-southeast-1.amazonaws.com/prod/";

    public static String FOURSQUARE_CLIENT_ID = "JB1B32YEWKVGHERD0X3GW13ZTU0FS5VEJXBVLVK04AQNQKM0";
    public static String FOURSQUARE_CLIENT_SECRET = "X2LFCFB3DW2NQ0JYHQ2TEB5VNZ14DUC4A2YCWGWAZUUJ2MVS";

    public static String URL_MAIN_DATA= HOST + "customer/card-and-reward";
    public static String URL_REGISTER = HOST + "v2/customer/register";
    public static String URL_FACEBOOK_CONNECT= HOST + "customer/facebook_connect";
    public static String URL_FORGOT = HOST + "customer/forgot-password";
    public static String URL_CHANGEPASS = HOST + "customer/change-password";
    public static String URL_UPDATE = HOST + "v2/customer/edit-profile";
    public static String URL_UPLOAD = HOST + "customer/upload";
    public static String URL_REFERRAL_DATA = HOST + "customer/referral-and-credit";
    public static String URL_REFERRAL_EDIT = HOST + "customer/edit-referralcode";
    public static String URL_SERVICE_HISTORY = HOST + "customer/history";
    public static String URL_DRIVER_AVAILABLE = HOST + "driver/available";
    public static String URL_PROMOTION_CHECK = HOST + "promotion/check";
    public static String URL_SERVICE_ADD = HOST + "trip/add";
    public static String URL_DRIVER_VIEW = HOST + "trip/get-state";
    public static String URL_SERVICE_CANCEL = HOST + "trip/cancel-by-customer";
    public static String URL_REWARD = HOST + "reward/lists";
    public static String URL_REWARD_REDEEM = HOST + "reward/redeem";
    public static String URL_UPDATE_INBOX_VIEW = HOST + "reward/customer-reward-view";
    public static String URL_DETAIL = HOST + "customer/profile";

    public static String IS_LOGIN = "is_login";
    public static String SERVICE = "service";

    public static String USERNAME = "username";

    public static String FIRSTNAME = "firstname";
    public static String LASTNAME = "lastname";
    public static String EMAIL = "email";
    public static String GENDER = "gender";
    public static String MOBILENO = "mobileno";
    public static String API_KEY = "api_key";
    public static String TRIP_ID = "tripId";
    public static String CARD = "add_credit";

    public static String IMAGEURI = "image_uri";
    public static String IMAGEURL = "image_url";

    public static String GCMID = "gcmId";

    public static String PLAYERID = "playerid";

}
