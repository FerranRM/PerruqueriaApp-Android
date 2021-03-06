package org.udg.pds.todoandroid.rest;

import org.udg.pds.todoandroid.entity.Client;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Producte;
import org.udg.pds.todoandroid.entity.Reserva;
import org.udg.pds.todoandroid.entity.ServeiPrestat;
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

import java.util.List;


public interface TodoApi {
  @POST("/perruquers/login")
  Call<Perruquer> login(@Body UserLogin login);

  @GET("/perruquers/check")
  Call<String> check();

  @POST("/perruquers/logout")
  Call<String> logout();

  @GET("/perruquers/me")
  Call<Perruquer> getIdPerruquer();

  @GET("/perruquers")
  Call<List<Perruquer>> listAllPerruquers();




  @POST("/tasks")
  Call<IdObject> addTask(@Body Task task);

  @GET("/tasks")
  Call<List<Task>> getTasks();

  @GET("/tasks/{id}")
  Call<Task> getTask(@Path("id") String id);




  @POST("/clients")
  Call<IdObject> addClient(@Body Client client);

  @GET("/clients/{id}")
  Call<Client> getClient(@Path("id") String id);

  @POST("/clients/{id}/productes")
  Call<ResponseBody> addProductes(@Body List<Long> productes, @Path("id") String id);

  @DELETE("/clients/{id}")
  Call<ResponseBody> deleteClient(@Path("id") String id);

  @GET("/clients")
  Call<List<Client>> listAllClients(@Query("data1") String data1, @Query("data2") String data2);

  @GET("/clients")
  Call<List<Client>> listAllClients(@Query("data1") String data1, @Query("data2") String data2, @Query("idPerruquer") Long idPerruquer);
//POSTMAN: http://localhost:8080/clients?data1=2018-07-01T17:36:00&data2=2018-08-20T17:36:00




  @POST("/reserves")
  Call<IdObject> addReseva(@Body Reserva reserva);

  /*@GET("/reserves")
  Call<List<ActivitatReserva>> listAllReserves();*/

  @GET("/reserves")
  Call<List<Reserva>> listAllReserves(@Query("data1") String data1, @Query("data2") String data2);

  @GET("/reserves/{id}")
  Call<Reserva> getReserva(@Path("id") String id);

  @DELETE("/reserves/{id}")
  Call<ResponseBody> deleteReserva(@Path("id") String id);




  @POST("/productes")
  Call<IdObject> addProducte(@Body Producte producte);

  @GET("/productes")
  Call<List<Producte>> getProductes();

  @GET("/productes/{id}")
  Call<Producte> getProducte(@Path("id") String id);

  @DELETE("/productes/{id}")
  Call<ResponseBody> deleteProducte(@Path("id") String id);




  @POST("/serveiPrestat")
  Call<IdObject> addServeiPrestat(@Body ServeiPrestat serveiPrestat);

  @GET("/serveiPrestat")
  Call<List<ServeiPrestat>> getServeisPrestats();

  @GET("/serveiPrestat/{id}")
  Call<ServeiPrestat> getServeiPrestat(@Path("id") String id);

  @DELETE("/serveiPrestat/{id}")
  Call<ResponseBody> deleteServeiPrestat(@Path("id") String id);

}

