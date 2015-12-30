package com.aswifter.material.net;

import android.support.annotation.NonNull;

import com.aswifter.material.book.Book;

import retrofit.Call;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by zlcd on 2015/12/29.
 */
public interface BookServerApi {


    @GET("v2/book/{id}")
    Call<Book> findBookById(@Path("id") @NonNull Integer   id);

    @GET("v2/book/{id}/annotations")
    Call<String>  findAllAnnotationsByBook(@Path("id") @NonNull Integer   id,@Query("format") String format);

    @GET("v2/book/series/{id}/books")
    Call<String>  findBooksByBook(@Path("id") @NonNull Integer   id);


}
