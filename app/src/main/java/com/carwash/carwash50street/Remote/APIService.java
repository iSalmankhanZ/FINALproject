package com.carwash.carwash50street.Remote;

import com.carwash.carwash50street.Model.MyResponse;
import com.carwash.carwash50street.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_6_FZBE:APA91bGigEqGp_BAMzJMDns22UGfG5yeHON3OoI9APBZYjYpQrzmFv4kbeQ902ApLj7T-PnPLkMJG1HpzKswqt8wqQBg-C4HEXFpyd0GsrU6eEe1f-s3l1SCaOyGQHowX1NotLuoTA_H"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
