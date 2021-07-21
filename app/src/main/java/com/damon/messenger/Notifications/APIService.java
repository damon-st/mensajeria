package com.damon.messenger.Notifications;

import com.damon.messenger.Notifications.MyResponse;
import com.damon.messenger.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAYWkjvQA:APA91bES7oqEn5cHYddsWKnCMHThA9AZDgu20JJHCZXGa673bDZw6NLAuftOoKgIv6t5Jszg9ZBs4b52eGwFo0TPYykNfvnSl3vH1Sv7vTAgdOhE0eQJzOXlZMwS4CYfVC3W4Ua64tdg"
            }
    )


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
