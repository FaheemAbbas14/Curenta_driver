package com.curenta.driver.retrofit;



import com.curenta.driver.dto.Posts;
import com.curenta.driver.retrofit.apiDTO.AcceptRideResponse;
import com.curenta.driver.retrofit.apiDTO.CancelRouteResponse;
import com.curenta.driver.retrofit.apiDTO.ChangePasswordResponse;
import com.curenta.driver.retrofit.apiDTO.CheckEmailResponse;
import com.curenta.driver.retrofit.apiDTO.ConfirmDeliveryResponse;
import com.curenta.driver.retrofit.apiDTO.ConfirmOrderResponse;
import com.curenta.driver.retrofit.apiDTO.DriverAPIResponse;
import com.curenta.driver.retrofit.apiDTO.EarningAPIResponse;
import com.curenta.driver.retrofit.apiDTO.GetRouteResponse;
import com.curenta.driver.retrofit.apiDTO.GetRoutesResponse;
import com.curenta.driver.retrofit.apiDTO.OTPResponse;
import com.curenta.driver.retrofit.apiDTO.OrderPickupResponse;
import com.curenta.driver.retrofit.apiDTO.RouteInprogressResponse;
import com.curenta.driver.retrofit.apiDTO.UpdateDRiverLocationRequest;
import com.curenta.driver.retrofit.apiDTO.UpdateDriverStatusResponse;
import com.curenta.driver.retrofit.apiDTO.UpdateLocationResponse;

import java.util.List;


import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IClientAPI {


    @Headers("Content-Type: application/json")
    @POST("api/Driver/LoginDriver")
    Single<DriverAPIResponse> loginAPI(@Body String userString);

    @Multipart
    @POST("api/Driver/Drivers")
    Single<DriverAPIResponse> registerDriver(@Part MultipartBody.Part profileImg,
                                           @Part MultipartBody.Part licenseImg,
                                           @Part MultipartBody.Part carInsurance,
                                           @Part MultipartBody.Part carRegistration,
                                           @Part("DriverFname") RequestBody DriverFname,
                                           @Part("DriverLname") RequestBody DriverLname,
                                           @Part("Email") RequestBody Email,
                                           @Part("Phonenumber") RequestBody Phonenumber,
                                           @Part("Password") RequestBody Password,
                                           @Part("DateofBirth") RequestBody DateofBirth,
                                           @Part("Gender") RequestBody Gender,
                                           @Part("Street") RequestBody Street,
                                           @Part("City") RequestBody City,
                                           @Part("State") RequestBody State,
                                           @Part("Zipcode") RequestBody Zipcode,
                                           @Part("DriverLicenseNumber") RequestBody DriverLicenseNumber,
                                           @Part("VehicleModel") RequestBody VehicleModel,
                                           @Part("Vehiclecolor") RequestBody Vehiclecolor,
                                           @Part("SocialsecurityNumber") RequestBody SocialsecurityNumber,
                                           @Part("Bankname") RequestBody Bankname,
                                           @Part("Accountnumber") RequestBody Accountnumber,
                                           @Part("Routingnumber") RequestBody Routingnumber,
                                           @Part("Longitude") RequestBody Longitude,
                                           @Part("Latitude") RequestBody Latitude,
                                           @Part("UserIdRef") RequestBody UserIdRef,
                                           @Part("FcmToken") RequestBody FcmToken,
                                           @Part("DeviceId") RequestBody DeviceId
    );
    @GET("api/Driver/CheckEmailExists/{email}")
    Single<CheckEmailResponse> CheckEmailExists(@Path("email") String email);

    @Headers("Content-Type: application/json")
    @POST("api/Route/AcceptRoute")
    Single<AcceptRideResponse> acceptRide(@Body String userString);

    @Multipart
    @POST("api/Driver/DriverSelfie")
    Single<DriverAPIResponse> uploadSelfie(@Part MultipartBody.Part DriverSelfiefile,
                                             @Part("DriverId") RequestBody DriverId
    );

    @Headers("Content-Type: application/json")
    @POST("api/Route/getRoute")
    Single<GetRouteResponse> getRoute(@Body String userString);

    @Multipart
    @POST("/api/Route/PickupRoute")
    Single<ConfirmDeliveryResponse> orderPickupWithImage(@Part MultipartBody.Part pickupConfirmationImage,
                                                    @Part("routeId") RequestBody routeId
    );

    @Multipart
    @POST("/api/Route/ConfirmOrder")
    Single<ConfirmOrderResponse> confirmDelivery(@Part MultipartBody.Part[] DriverSelfiefile,
                                                 @Part("routeId") RequestBody RouteId,
                                                 @Part("RouteStepId") RequestBody RouteStepId
    );
    @Headers("Content-Type: application/json")
    @POST("api/Route/PickupRoute")
    Single<OrderPickupResponse> orderPickup(@Body String userString);
    @Headers("Content-Type: application/json")
    @POST("api/Driver/GetDriverEarnings")
    Single<EarningAPIResponse> getDriverEarnings(@Body String userString);
    @Multipart
    @PUT("api/Driver/UpdateDriverProfilePicture")
    Single<DriverAPIResponse> uploadProfilePic(@Part MultipartBody.Part DriverSelfiefile,
                                           @Part("driverId") RequestBody DriverId
    );
    @Headers("Content-Type: application/json")
    @PUT("/api/Driver/UpdateDriverInfo")
    Single<DriverAPIResponse> updateProfile(@Body String userString);

    @Headers("Content-Type: application/json")
    @POST("/api/Driver/ChangePassword")
    Single<ChangePasswordResponse> changePassword(@Body String userString);

    @Headers("Content-Type: application/json")
    @POST("/api/Route/CancelRoute")
    Single<CancelRouteResponse> cancelRoute(@Body String userString);

    @Headers("Content-Type: application/json")
    @POST("/api/Route/CancelRouteOrder")
    Single<CancelRouteResponse> cancelRouteOrder(@Body String userString);


    @Headers("Content-Type: application/json")
    @POST("/api/Driver/SendOTP")
    Single<OTPResponse> getVerificationCode(@Body String userString);

    @Headers("Content-Type: application/json")
    @PUT("/api/Driver/UpdateDriverLocation")
    Single<UpdateLocationResponse> updateDriverLOcation(@Body String userString);

    @GET("api/Route/GetInProgressRoute")
    Single<RouteInprogressResponse> checkInprogressRoute(@Query("DriverId") Integer DriverId,
                                                         @Query("PlayerId") String PlayerId,
                                                         @Query("FcmToken") String FcmToken);
    @Headers("Content-Type: application/json")
    @PUT("/api/Driver/UpdateDriverStatus")
    Single<UpdateDriverStatusResponse> updateDriverStatus(@Body String userString);
    @GET("api/Route/GetRoutes")
    Single<GetRoutesResponse> getRoutes(@Query("RouteId") String RouteId,
                                        @Query("PickupAddress") boolean PickupAddress,
                                        @Query("Order") boolean Order,
                                        @Query("RouteStep") boolean RouteStep);
}
