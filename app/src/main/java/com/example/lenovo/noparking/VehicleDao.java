package com.example.lenovo.noparking;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface VehicleDao {

    // Adds a person to the database
    @Insert
    void insertAll(Vehicle... vehicles);

    // Removes a person from the database
    @Delete
    void delete(Vehicle vehicle);

    // Gets all people in the database
    @Query("SELECT * FROM vehicle")
    List<Vehicle> getAllPeople();

    // Gets all people in the database with a favorite color
    @Query("SELECT * FROM vehicle WHERE registration_number LIKE :registrationNumber")
//    List<Vehicle> getAllPeopleWithFavoriteColor(String color);
    Vehicle findVehicleByNumber(String registrationNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOnlySingleRecord(Vehicle vehicle);

}
