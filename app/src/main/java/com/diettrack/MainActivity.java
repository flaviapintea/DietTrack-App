package com.diettrack;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // enables Stetho debug bridge used for tables inspection
        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        // opens Database
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Count rows in food
        int numberRows = db.count("food");

        if(numberRows < 1){
            // Runs DBSetupInsert class
            DBSetupInsert setupInsert = new DBSetupInsert(this);
            setupInsert.insertAllCategories();
            setupInsert.insertAllFood();
        }

        // Checks if there is any user in the user table
        // Count rows in user table
        numberRows = db.count("users");

        //Closes database
        db.close();

        if(numberRows < 1){
            // Sign up Activity
            Intent i = new Intent(MainActivity.this, SignUp.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent(MainActivity.this, FragmentActivityNew.class);
            startActivity(i);

        }

    }

}
