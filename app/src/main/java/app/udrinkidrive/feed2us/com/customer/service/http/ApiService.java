package app.udrinkidrive.feed2us.com.customer.service.http;

import com.google.gson.JsonElement;

import java.util.ArrayList;

import app.udrinkidrive.feed2us.com.customer.dao.AddCreditCardDao;
import app.udrinkidrive.feed2us.com.customer.dao.AddFavLocationDao;
import app.udrinkidrive.feed2us.com.customer.dao.AddServiceDao;
import app.udrinkidrive.feed2us.com.customer.dao.ChangeCreditCardActiveDao;
import app.udrinkidrive.feed2us.com.customer.dao.CheckCreditCardDao;
import app.udrinkidrive.feed2us.com.customer.dao.CheckServerDao;
import app.udrinkidrive.feed2us.com.customer.dao.CreditCardCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.DelCreditCardDao;
import app.udrinkidrive.feed2us.com.customer.dao.DelFavLocationDao;
import app.udrinkidrive.feed2us.com.customer.dao.DriverCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.EvaluateDao;
import app.udrinkidrive.feed2us.com.customer.dao.FavLocationCollectionDao;
import app.udrinkidrive.feed2us.com.customer.dao.HaveServiceDao;
import app.udrinkidrive.feed2us.com.customer.dao.MainDataDao;
import app.udrinkidrive.feed2us.com.customer.dao.PromoDao;
import app.udrinkidrive.feed2us.com.customer.dao.UserCollectionDao;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by TL3 on 2/14/16 AD.
 */
public interface ApiService {

    @GET("service/alive")
    Call<CheckServerDao> checkServerStatus();

    @FormUrlEncoded
    @POST("customer/login")
    Call<UserCollectionDao> login(@Field("username") String username, @Field("password") String password, @Field("gcm_id") String gcm_id);

    @FormUrlEncoded
    @POST("mock-login")
    Call<UserCollectionDao> loginMockLogin(@Field("username") String username, @Field("password") String password, @Field("gcm_id") String gcm_id);

    @FormUrlEncoded
    @POST("verify-otp")
    Call<UserCollectionDao> VerifyOTP(@Field("mobileNo") String mobileNo, @Field("token") String token, @Field("otp") String otp);

    @FormUrlEncoded
    @POST("customer/login-facebook")
    Call<UserCollectionDao> facebookLogin(@Field("facebook_id") String facebookId, @Field("facebook_token") String facebookToken, @Field("gcm_id") String gcm_id, @Field("username") String username);

    @FormUrlEncoded
    @POST("mock-login-facebook")
    Call<UserCollectionDao> facebookMockLogin(@Field("facebook_id") String facebookId, @Field("facebook_token") String facebookToken, @Field("gcm_id") String gcm_id, @Field("username") String username);

    @FormUrlEncoded
    @POST("customer/has-ongoing")
    Call<HaveServiceDao> checkHaveService(@Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("customer/card-and-reward")
    Call<MainDataDao> getMainData(@Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("v2/trip/nearby-drivers")
    Call<DriverCollectionDao> getDriverAvailable(@Field("latlng") String latlng);

    @FormUrlEncoded
    @POST("promotion/check")
    Call<PromoDao> checkPromo(@Field("code") String code, @Field("api_token") String api_token, @Field("pickup_time") String pickup_time, @Field("latlng") String latlng, @Field("imei") String imei);

    @FormUrlEncoded
    @POST("v2/trip/add")
    Call<AddServiceDao> addService(@Field("player_id") String player_id,
                                   @Field("token_id") String token_id,
                                   @Field("token_omise") String token_omise,
                                   @Field("codename") String codename,
                                   @Field("sex_id") Integer sex_id,
                                   @Field("phone") String phone,
                                   @Field("address_lat") String address_lat,
                                   @Field("address_auto") String address_auto,
                                   @Field("source") String source,
                                   @Field("source_lat") String source_lat,
                                   @Field("destination") String destination,
                                   @Field("destination_lat") String destination_lat,
                                   @Field("pickup_time") String pickup_time,
                                   @Field("estimate_finish_time") Integer estimate_finish_time,
                                   @Field("latlng") String latlng,
                                   @Field("location_name") String location_name,
                                   @Field("waypoint_order") String waypoint_order,
                                   @Field("distance") Double distance,
                                   @Field("price") Integer price,
                                   @Field("gear") String gear,
                                   @Field("remark") String remark,
                                   @Field("data_source") Integer data_source,
                                   @Field("nonce") String nonce,
                                   @Field("customer_note") String customer_note,
                                   @Field("promotion_code") String promotion_code,
                                   @Field("payment_type") String payment_type,
                                   @Field("imei") String imei,
                                   @Field("net_fee") Integer net_fee);

    @FormUrlEncoded
    @POST("customer/fav-locations")
    Call<FavLocationCollectionDao> getFavLocation(@Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("customer/add-fav-locations")
    Call<AddFavLocationDao> addFavLocation(@Field("name") String name, @Field("formatted_address") String formatted_address, @Field("lat") String lat, @Field("lng") String lng, @Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("customer/delete-fav-locations")
    Call<DelFavLocationDao> delFavLocation(@Field("lat") String lat, @Field("lng") String lng, @Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("v2/trip/rating-driver")
    Call<EvaluateDao> evaluateDriver(@Field("tripId") String tripId,@Field("apiToken") String api_key, @Field("score") Integer rating, @Field("comment") String complaint);

    @FormUrlEncoded
    @POST("v2/customer/cards")
    Call<CreditCardCollectionDao> getCardList(@Field("api_token") String api_token);

    @FormUrlEncoded
    @POST("v2/customer/add-card")
    Call<AddCreditCardDao> addCard(
            @Field("api_token") String apiToken,
            @Field("card_name") String cardName,
            @Field("card_number") String cardNumber,
            @Field("card_expiration_month") String expireMonth,
            @Field("card_expiration_year") String expireYear,
            @Field("security_code") String securityCode
    );

    @FormUrlEncoded
    @POST("v2/customer/delete-card")
    Call<DelCreditCardDao> deleteCard(
            @Field("api_token") String api_token,
            @Field("card_id") String card_id
    );

    @FormUrlEncoded
    @POST("v2/customer/set-default-card")
    Call<ChangeCreditCardActiveDao> changeCardActive(
            @Field("api_token") String api_token,
            @Field("card_id") String card_id);

    @FormUrlEncoded
    @POST("v2/customer/check-card-available")
    Call<CheckCreditCardDao> checkCreditCard(
            @Field("omise_card_id") String card_index,
            @Field("api_token") String api_key
    );

    @FormUrlEncoded
    @POST("v2/trip/price-distance")
    Call<JsonElement> GetDistance(
            @Field("tokenId") String tokenId,
            @Field("destination") String destination,
            @Field("origin") String origin,
            @Field("waypoints") ArrayList<String> waypoints,
            @Field("registerSource") String registerSource
    );

    @GET("https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyB9jSBgTnEmfCR0veX2sEx4wFEs-rzXzcc&alternatives=true")
    Call<JsonElement> loadGoogleDirections(@Query("origin") String origin, @Query("destination") String destination, @Query("waypoints") String waypoint);

    @GET("https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyB9jSBgTnEmfCR0veX2sEx4wFEs-rzXzcc&avoid=highways|tolls&alternatives=true")
    Call<JsonElement> loadGoogleDirectionsWithAvoid(@Query("origin") String origin, @Query("destination") String destination, @Query("waypoints") String waypoint);
}
