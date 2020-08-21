package com.richard.halame.Fragments;

import com.richard.halame.Notification.MyResponse;
import com.richard.halame.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorisation:key=AAAA8f_ueT8:APA91bGJvvifFjJaFt9edFVmVruzDnfwuH32gZOTUONOwbQrvGoihpVF-RDp-3xr7Q5X8_C54tSLdHRVxRoyTmv5NMa2aIMui5490ycl-iJjkuz05hgsi3aaF5qREl33guN6a9mBFQxu"

            }
    )

    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);
}
