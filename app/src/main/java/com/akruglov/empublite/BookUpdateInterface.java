package com.akruglov.empublite;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by akruglov on 02.03.17.
 */

public interface BookUpdateInterface {
    @GET("/misc/empublite-update.json")
    Call<BookUpdateInfo> update();
}
