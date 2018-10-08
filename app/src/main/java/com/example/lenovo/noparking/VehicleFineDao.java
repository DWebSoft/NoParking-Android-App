package com.example.lenovo.noparking;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface VehicleFineDao {
    // Adds a person to the database
//    @Insert
//    void insertAll(VehicleFine... fines);

    // Removes a person from the database
//    @Delete
//    void delete(VehicleFine fine);

    // Gets all people in the database
//    @Query("SELECT * FROM vehiclefine")
//    List<Vehicle> getAllPeople();

    // Gets all people in the database with a favorite color
   // @Query("SELECT * FROM vehiclefine WHERE registration_number LIKE :registrationNumber")
//    List<Vehicle> getAllPeopleWithFavoriteColor(String color);
    //Vehicle findVehicleByNumber(String registrationNumber);

    @Query("SELECT * FROM vehiclefine WHERE vehicleId = :vehicleId ORDER BY vehicleFineId desc LIMIT 1")
//    List<Vehicle> getAllPeopleWithFavoriteColor(String color);
    VehicleFine findFineByVehicleId(int vehicleId);

    @Insert
    void insertOnlySingleRecord(VehicleFine vehicleFine);

    @Query("UPDATE vehiclefine SET fine=:fine WHERE vehicleId = :id")
    void update(Float fine, int id);
}
