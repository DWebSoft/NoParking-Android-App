package com.example.lenovo.noparking;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PERMISSION_CAMERA = 3232;
    Button btnCamera;
    ImageView mImageView;
    Button btnUpload;
    byte[] encoded;
    ArrayList<String> primaryPlates, secondaryPlates;
    TextView plate_name, owner_name, model, fuel_type, vehicle_class, engine_number;
    AppDatabase db;
    Button btnSaveFine;
    String vehicleId = "";
    EditText fineTxt;
    TextView fine;
    ProgressDialog progressDialog;
    EditText dueDate;
    TextView dueDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mImageView = (ImageView) findViewById(R.id.image_view);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        plate_name = (TextView)findViewById(R.id.plate_name);
        owner_name = (TextView)findViewById(R.id.owner_name);
        model = (TextView)findViewById(R.id.model);
        vehicle_class = (TextView)findViewById(R.id.vehicle_class);
        fuel_type = (TextView)findViewById(R.id.fuel_type);
        engine_number = (TextView)findViewById(R.id.engine_number);
        btnSaveFine = (Button)findViewById(R.id.btnSaveFine);
        fineTxt = (EditText)findViewById(R.id.fineTxt);
        fine = (TextView)findViewById(R.id.fine);
        dueDate = (EditText)findViewById(R.id.dueDate);
        dueDateText = (TextView)findViewById(R.id.dueDateText);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "populus-database")
//                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .fallbackToDestructiveMigration()
                .build();


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ) {

                    ActivityCompat.requestPermissions(HomeActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_CAMERA);

                    // Permission is not granted
                } else {
                    dispatchTakePictureIntent();
                }
            }
            });

        btnSaveFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vehicleId != null){
                    new addFine().execute();
//                    db.daoAccess().update(Float.parseFloat(fineTxt.getText().toString()), Integer.parseInt(vehicleId));
                    Toast.makeText(HomeActivity.this, "Fine Saved Successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new uploadImage().execute(encoded);
            }
        });

        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                String date = year + "/" + ( monthOfYear+1 ) + "/" + dayOfMonth;
                                dueDate.setText(date);
                            }
                        },
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            encodeTobase64(imageBitmap);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            imageBitmap.recycle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    ActivityCompat.requestPermissions(HomeActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_CAMERA);

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public byte[] encodeTobase64(Bitmap image) {

        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        encoded = Base64.encode(b, Base64.DEFAULT);
        return encoded;
    }

    public class uploadImage extends AsyncTask<byte[], Void, String> {
        String json_content = "";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //inicia diálogo de progresso, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(HomeActivity.this, "", "Uploading Image...", true, false);
        }


        @Override
        protected String doInBackground(byte[]... params) {

            try {
                String secret_key = "sk_e6d85487dbb80d17b39c0ab3";
                URL url = new URL("https://api.openalpr.com/v2/recognize_bytes?recognize_vehicle=1&country=in&secret_key=" + secret_key);
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection) con;
                http.setRequestMethod("POST"); // PUT is another valid option
                http.setFixedLengthStreamingMode(encoded.length);
                http.setDoOutput(true);

                // Send our Base64 content over the stream
                try (OutputStream os = http.getOutputStream()) {
                    os.write(encoded);
                }

                int status_code = http.getResponseCode();
                if (status_code == 200) {
                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            http.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        json_content += inputLine;
                    in.close();

                    System.out.println(json_content);
                } else {
                    System.out.println("Got non-200 response: " + status_code);
                }


            } catch (MalformedURLException e) {
                System.out.println("Bad URL");
            } catch (IOException e) {
                System.out.println("Failed to open connection");
            }

            return json_content;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
//            Log.d("", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("results");
                primaryPlates = new ArrayList<>();
                secondaryPlates = new ArrayList<>();

                for(int i = 0; i < results.length(); i++){
                    JSONObject jsonObject1 = results.getJSONObject(i);
                    if(jsonObject1 != null && jsonObject1.has("plate")) {
                        String numberPlate = jsonObject1.getString("plate");
                        primaryPlates.add(numberPlate);
                        JSONArray candidates = jsonObject1.getJSONArray("candidates");
                        for(int j = 0; j < candidates.length(); j++){
                            JSONObject jsonObject2 = candidates.getJSONObject(j);
                            String plate = jsonObject2.getString("plate");
                            secondaryPlates.add(plate);
                        }
                    }


                }

                operate(primaryPlates, secondaryPlates);

            } catch (JSONException e) {
                e.printStackTrace();
            }


//            TextView txt = (TextView) findViewById(R.id.output);
//            txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void operate(ArrayList<String> arrayPrimary, ArrayList<String> arraySecondary){
        if(arrayPrimary != null && arrayPrimary.size() > 0){
            plate_name.setText(arrayPrimary.get(0));
            //check if this plate exists in database
            new DatabaseAsync().execute(arrayPrimary.get(0));

        } else {

        }
    }

    private class DatabaseAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Perform pre-adding operation here.
        }

        @Override
        protected Void doInBackground(String... plate) {
            //Let's add some dummy data to the database.

            final Vehicle vehicle = db.daoAccess().findVehicleByNumber(plate[0]);
            if(vehicle != null) {
//            Log.d("", String.valueOf(vehicle));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        owner_name.setText(vehicle.getOwnerName());
                        vehicle_class.setText(vehicle.getVehicleClass());
                        model.setText(vehicle.getModel());
                        fuel_type.setText(vehicle.fuelType);
                        engine_number.setText(vehicle.getEngineNumber());
                        plate_name.setText(vehicle.getRegistration_number());
                        vehicleId = String.valueOf(vehicle.getVehicleid());
                    }
                });
            } else {
//                Toast.makeText(HomeActivity.this, run"Could not find vehicle.", Toast.LENGTH_SHORT).show();
            }
            //Now access all the methods defined in DaoAccess with sampleDatabase object
//            db.daoAccess().insertOnlySingleRecord(vehicle);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(vehicleId.isEmpty()){
                Toast.makeText(HomeActivity.this, "Could not find vehicle.", Toast.LENGTH_SHORT).show();
            }
            //To after addition operation here.
        }
    }


    //Update fine
    private class addFine extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... amount) {
            VehicleFine vehicleFineObj = new VehicleFine();
            vehicleFineObj.setFine(fineTxt.getText().toString());
            vehicleFineObj.setDueDate(dueDate.getText().toString());
            vehicleFineObj.setCreatedOn( Calendar.getInstance().getTime().toString() );
            vehicleFineObj.setVehicleId(Integer.parseInt(vehicleId));
            db.daoFineAccess().insertOnlySingleRecord(vehicleFineObj);
            //db.daoAccess().update(Float.parseFloat(fineTxt.getText().toString()), Integer.parseInt(vehicleId));
           // final VehicleFine vehicleFine = db.daoFineAccess().findFineByVehicleId(Integer.parseInt(vehicleId));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    fine.setText(vehicleFine.getFine());
//                    dueDateText.setText(vehicleFine.getDueDate());
                    fine.setText(fineTxt.getText().toString());
                    dueDateText.setText(dueDate.getText().toString());

                }
            });
            return null;
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(HomeActivity.this, AddUserActivity.class);
//        startActivity(intent);
//        finish();
    }
}
