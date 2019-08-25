package org.udg.pds.todoandroid.rest;

import org.udg.pds.todoandroid.entity.Client;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Producte;
import org.udg.pds.todoandroid.entity.Reserva;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.Perruquer;
import org.udg.pds.todoandroid.entity.UserLogin;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;


public interface TodoApi {
  @POST("/perruquers/login")
  Call<Perruquer> login(@Body UserLogin login);

  @GET("/perruquers/check")
  Call<String> check();



  @POST("/tasks")
  Call<IdObject> addTask(@Body Task task);

  @GET("/tasks")
  Call<List<Task>> getTasks();

  @GET("/tasks/{id}")
  Call<Task> getTask(@Path("id") String id);




  @POST("/clients")
  Call<IdObject> addClient(@Body Client client);

  @GET("/clients")
  Call<List<Client>> getClients();

  @GET("/clients/{id}")
  Call<Client> getClient(@Path("id") String id);

  @POST("/clients/{id}/productes")
  Call<ResponseBody> addProductes(@Body List<Long> productes, @Path("id") String id);

  @GET("/clients/")
  Call<List<Client>> listClientsDates(@Query("data1") Date data1, @Query("data2") Date data2);




  @POST("/reserves")
  Call<IdObject> addReseva(@Body Reserva reserva);

  @GET("/reserves")
  Call<List<Reserva>> getReserves();

  @GET("/reserves/{id}")
  Call<Reserva> getReserva(@Path("id") String id);

  @DELETE("/reserves/{id}")
  Call<ResponseBody> deleteReserva(@Path("id") String id);




  /*@POST("/productes")
  Call<IdObject> addProducte(@Body Producte producte);*/

  @GET("/productes")
  Call<List<Producte>> getProductes();

  @GET("/reserves/{id}")
  Call<Producte> getProducte(@Path("id") String id);

  @DELETE("/reserves/{id}")
  Call<ResponseBody> deleteProducte(@Path("id") String id);

}

