package io.phoenyx.sail;

import java.util.List;

import io.phoenyx.sail.models.QuoteResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SailService {

    @GET("/qod.json?category=inspire")
    Call<QuoteResponse> getQOD();
}