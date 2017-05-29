package io.phoenyx.sail;

import io.phoenyx.sail.models.Quote;
import retrofit2.Call;
import retrofit2.http.GET;

interface SailService {

    @GET("api/1.0/?method=getQuote&format=json&lang=en")
    Call<Quote> getQOD();

}