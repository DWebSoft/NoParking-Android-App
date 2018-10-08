package com.example.lenovo.noparking;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class VehicleFine {
    @PrimaryKey(autoGenerate = true)
    int vehicleFineId;
    int vehicleId;
    String fine;
    String dueDate;
    String createdOn;

    public int getVehicleFineId() {
        return vehicleFineId;
    }

    public void setVehicleFineId(int vehicleFineId) {
        this.vehicleFineId = vehicleFineId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
