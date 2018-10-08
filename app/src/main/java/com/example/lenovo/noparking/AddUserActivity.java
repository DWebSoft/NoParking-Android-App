package com.example.lenovo.noparking;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddUserActivity extends AppCompatActivity {
    Toolbar toolbar;
    AppDatabase db;
    Button saveBtn;
    Vehicle vehicle;
    EditText owner_name, vehicle_class, model, fuel_type, engine_number, registration_number, mobile_number, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("Add User");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddUserActivity.this, "Back Clicked", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "populus-database")
//                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build();


        owner_name = (EditText)findViewById(R.id.owner_name);
        registration_number = (EditText)findViewById(R.id.registration_number);
        vehicle_class = (EditText)findViewById(R.id.vehicle_class);
        model = (EditText)findViewById(R.id.model);
        fuel_type = (EditText)findViewById(R.id.fuel_type);
        engine_number = (EditText)findViewById(R.id.engine_number);
        mobile_number = (EditText)findViewById(R.id.mobile_number);
        email = (EditText)findViewById(R.id.email);
        saveBtn = (Button)findViewById(R.id.saveBtn);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all values and store in room db
                new DatabaseAsync().execute();
            }
        });

    }

    private class DatabaseAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Perform pre-adding operation here.
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Let's add some dummy data to the database.
            Vehicle vehicle = new Vehicle();
            vehicle.setOwnerName(owner_name.getText().toString());
            vehicle.setEngineNumber(engine_number.getText().toString());
            vehicle.setFuelType(fuel_type.getText().toString());
            vehicle.setModel(model.getText().toString());
            vehicle.setVehicleClass(vehicle_class.getText().toString());
            vehicle.setRegistration_number(registration_number.getText().toString());
            vehicle.setMobileNumber(mobile_number.getText().toString());
            vehicle.setEmail(email.getText().toString());
            //Now access all the methods defined in DaoAccess with sampleDatabase object
            long vehicleid = db.daoAccess().insertOnlySingleRecord(vehicle);
            //Log.i("CODE", vehicleid+"");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(AddUserActivity.this, "Parking Details Saved.", Toast.LENGTH_SHORT).show();
            owner_name.setText("");
            engine_number.setText("");
            fuel_type.setText("");
            model.setText("");
            vehicle_class.setText("");
            registration_number.setText("");
            mobile_number.setText("");
            email.setText("");
            //To after addition operation here.
        }
    }






}
