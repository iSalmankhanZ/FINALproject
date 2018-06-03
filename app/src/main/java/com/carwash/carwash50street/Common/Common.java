package com.carwash.carwash50street.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.carwash.carwash50street.Model.User;
import com.carwash.carwash50street.Remote.APIService;
import com.carwash.carwash50street.Remote.GoogleRetrofitClient;
import com.carwash.carwash50street.Remote.IGoogleService;
import com.carwash.carwash50street.Remote.RetrofitClient;

public class Common {
    public static User currentUser;

    public static String PHONE_TEXT = "userPhone";

    private static final String BASE_URL="https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";

    public static final String INTENT_SERVICE_ID = "ServiceId";

    public static String topicName="News";

    public static final String USER_KEY = "User";

    public static final String PWD_KEY = "Password";

    public static final String DELETE = "Delete";


    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info!=null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }


    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }


    public static IGoogleService getGoogleMapAPI()
    {
        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static  String convertCodeToStatus(String code) {
        if(code.equals("0"))
            return "Booking Placed";
        else if(code.equals("1"))
            return "On Our way";
        else
            return "Service Done";
    }
}
