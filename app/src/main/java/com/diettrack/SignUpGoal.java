package com.diettrack;

import java.util.Calendar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SignUpGoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_goal);


        // Listener submit
        Button buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signUpGoalSubmit();
            }
        });

        //
        hideErrorHandling();

        mesurmentUsed();

    } // onCreate

    /* signUpGoalSubmit ----------------------------------------------------- */
    public void signUpGoalSubmit(){
        /* Open database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        /* Error */
        ImageView imageViewError = (ImageView)findViewById(R.id.imageViewError);
        TextView textViewErrorMessage = (TextView)findViewById(R.id.textViewErrorMessage);
        String errorMessage = "";


        /* Get target weight */
        EditText editTextTargetWeight = (EditText)findViewById(R.id.editTextTargetWeight);
        String stringTargetWeight = editTextTargetWeight.getText().toString();
        double doubleTargetWeight = 0;
        try{
            doubleTargetWeight = Double.parseDouble(stringTargetWeight);
        }
        catch(NumberFormatException nfe) {
            errorMessage = "Target weight has to be a number.";
        }

        // Spinner IWantTo
        //  Lose weight
        //  Gain weight
        Spinner spinnerIWantTo = (Spinner)findViewById(R.id.spinnerIWantTo);
        int intIWantTo = spinnerIWantTo.getSelectedItemPosition();

        // Spinner spinnerWeeklyGoal
        Spinner spinnerWeeklyGoal = (Spinner)findViewById(R.id.spinnerWeeklyGoal);
        String stringWeeklyGoal = spinnerWeeklyGoal.getSelectedItem().toString();


        // Update fields
        if(errorMessage.isEmpty()){

            long goalID = 1;

            double doubleTargetWeightSQL = db.quoteSmart(doubleTargetWeight);
            db.update("goal", "_id", goalID, "goal_target_weight", doubleTargetWeightSQL);

            int intIWantToSQL = db.quoteSmart(intIWantTo);
            db.update("goal", "_id", goalID, "goal_i_want_to", intIWantToSQL);

            String stringWeeklyGoalSQL = db.quoteSmart(stringWeeklyGoal);
            db.update("goal", "_id", goalID, "goal_weekly_goal", stringWeeklyGoalSQL);

        }


        if(errorMessage.isEmpty()){

            // Get row number one from users
            long rowID = 1;
            String fields[] = new String[] {
                    "_id",
                    "user_dob",
                    "user_gender",
                    "user_height"
            };
            Cursor c = db.select("users", fields, "_id", rowID);
            String stringUserDob = c.getString(1);
            String stringUserGender  = c.getString(2);
            String stringUserHeight = c.getString(3);

            // Get weight actvity level
            rowID = 1;
            String fieldsGoal[] = new String[] {
                    "_id",
                    "goal_current_weight",
                    "goal_activity_level"
            };
            Cursor cGoal = db.select("goal", fieldsGoal, "_id", rowID);
            String stringUserCurrentWeight = cGoal.getString(1);
            String stringUserActivityLevel = cGoal.getString(2);

            // Get weight
            double doubleUserCurrentWeight = 0;
            try{
                doubleUserCurrentWeight = Double.parseDouble(stringUserCurrentWeight);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }


            // Get Age
            String[] items1 = stringUserDob.split("-");
            String stringYear = items1[0];
            String stringMonth = items1[1];
            String stringDay = items1[2];

            int intYear = 0;
            try {
                intYear = Integer.parseInt(stringYear);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            int intMonth = 0;
            try {
                intMonth = Integer.parseInt(stringMonth);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            int intDay = 0;
            try {
                intDay = Integer.parseInt(stringDay);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            String stringUserAge = getAge(intYear, intMonth, intDay);

            int intUserAge = 0;
            try {
                intUserAge = Integer.parseInt(stringUserAge);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            // Height
            double doubleUserHeight = 0;
            try {
                doubleUserHeight = Double.parseDouble(stringUserHeight);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }


            long goalID = 1;


            // BMR
            double goalEnergyBMR = 0;
            if(stringUserGender.startsWith("m")){
                // Male
                // BMR = 88.362 + (13.397 x kg body weight) + (5.003x height in cm) - (5.677 x age)
                goalEnergyBMR = 88.362+(13.397*doubleUserCurrentWeight)+(5.003*doubleUserHeight)-(5.677*intUserAge);



            }
            else{
                // Female
                // BMR = 447.593 + (9.247 x kg body weight) + (3.098 x height in cm) - (4.330 x age)
                goalEnergyBMR = 447.593+(9.247*doubleUserCurrentWeight)+(3.098*doubleUserHeight)-(4.330*intUserAge);

            }
            goalEnergyBMR = Math.round(goalEnergyBMR);
            double energyBmrSQL = db.quoteSmart(goalEnergyBMR);
            db.update("goal", "_id", goalID, "goal_energy_bmr", energyBmrSQL);

            // Proteins, carbs and fat with BMR
            // 20-25 % protein
            // 40-50 % carbs
            // 25-35 % fat
            double proteinsBmr = Math.round(goalEnergyBMR*25/100);
            double carbsBmr = Math.round(goalEnergyBMR*50/100);
            double fatBmr = Math.round(goalEnergyBMR*25/100);

            double proteinsBmrSQL = db.quoteSmart(proteinsBmr);
            double carbsBmrSQL = db.quoteSmart(carbsBmr);
            double fatBmrQL = db.quoteSmart(fatBmr);
            db.update("goal", "_id", goalID, "goal_proteins_bmr", proteinsBmrSQL);
            db.update("goal", "_id", goalID, "goal_carbs_bmr", carbsBmrSQL);
            db.update("goal", "_id", goalID, "goal_fat_bmr", fatBmrQL);


            // Compute daily calorie intake
            double doubleWeeklyGoal = 0;
            try {
                doubleWeeklyGoal = Double.parseDouble(stringWeeklyGoal);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            // 1 kg fat = 7700 kcal
            double kcal = 0;
            double energyDiet = 0;
            kcal = 7700*doubleWeeklyGoal;
            if(intIWantTo == 0){
                // Lose weight
                energyDiet = Math.round((goalEnergyBMR - (kcal/7)) * 1.2);

            }
            else{
                // Gain weight
                energyDiet = Math.round((goalEnergyBMR + (kcal/7)) * 1.2);
            }

            // Update database
            double energyDietSQL = db.quoteSmart(energyDiet);
            db.update("goal", "_id", goalID, "goal_energy_diet", energyDietSQL);

            double proteinsDiet = Math.round(energyDiet*25/100);
            double carbsDiet = Math.round(energyDiet*50/100);
            double fatDiet = Math.round(energyDiet*25/100);

            double proteinsDietSQL = db.quoteSmart(proteinsDiet);
            double carbsDietSQL = db.quoteSmart(carbsDiet);
            double fatDietQL = db.quoteSmart(fatDiet);
            db.update("goal", "_id", goalID, "goal_proteins_diet", proteinsDietSQL);
            db.update("goal", "_id", goalID, "goal_carbs_diet", carbsDietSQL);
            db.update("goal", "_id", goalID, "goal_fat_diet", fatDietQL);


            // Taking in to account activity
            double energyWithActivity = 0;
            if(stringUserActivityLevel.equals("0")) {
                energyWithActivity = goalEnergyBMR * 1.2;
            }
            else if(stringUserActivityLevel.equals("1")) {
                energyWithActivity = goalEnergyBMR * 1.375; // slightly_active
            }
            else if(stringUserActivityLevel.equals("2")) {
                energyWithActivity = goalEnergyBMR*1.55; // moderately_active
            }
            else if(stringUserActivityLevel.equals("3")) {
                energyWithActivity = goalEnergyBMR*1.725; // active_lifestyle
            }
            else if(stringUserActivityLevel.equals("4")) {
                energyWithActivity = goalEnergyBMR * 1.9; // very_active
            }
            energyWithActivity = Math.round(energyWithActivity);
            double energyWithActivitySQL = db.quoteSmart(energyWithActivity);
            db.update("goal", "_id", goalID, "goal_energy_with_activity", energyWithActivitySQL);


            double proteinsWithActivity = Math.round(energyWithActivity*25/100);
            double carbsWithActivity = Math.round(energyWithActivity*50/100);
            double fatWithActivity = Math.round(energyWithActivity*25/100);

            double proteinsWithActivitySQL = db.quoteSmart(proteinsWithActivity);
            double carbsWithActivitySQL = db.quoteSmart(carbsWithActivity);
            double fatWithActivityQL = db.quoteSmart(fatWithActivity);
            db.update("goal", "_id", goalID, "goal_proteins_with_activity", proteinsWithActivitySQL);
            db.update("goal", "_id", goalID, "goal_carbs_with_activity", carbsWithActivitySQL);
            db.update("goal", "_id", goalID, "goal_fat_with_activity", fatWithActivityQL);



            // With_activity_and_diet

            kcal = 0;
            double energyWithActivityAndDiet = 0;
            kcal = 7700*doubleWeeklyGoal;
            if(intIWantTo == 0){
                // Lose weight
                energyWithActivityAndDiet = goalEnergyBMR - (kcal/7);

            }
            else{
                // Gain weight
                energyWithActivityAndDiet = goalEnergyBMR + (kcal/7);
            }

            if(stringUserActivityLevel.equals("0")) {
                energyWithActivityAndDiet= energyWithActivityAndDiet* 1.2;
            }
            else if(stringUserActivityLevel.equals("1")) {
                energyWithActivityAndDiet= energyWithActivityAndDiet* 1.375; // slightly_active
            }
            else if(stringUserActivityLevel.equals("2")) {
                energyWithActivityAndDiet= energyWithActivityAndDiet*1.55; // moderately_active
            }
            else if(stringUserActivityLevel.equals("3")) {
                energyWithActivityAndDiet= energyWithActivityAndDiet*1.725; // active_lifestyle
            }
            else if(stringUserActivityLevel.equals("4")) {
                energyWithActivityAndDiet = energyWithActivityAndDiet* 1.9; // very_active
            }
            energyWithActivityAndDiet = Math.round(energyWithActivityAndDiet);

            // Update database
            double energyWithActivityAndDietSQL = db.quoteSmart(energyWithActivityAndDiet);
            db.update("goal", "_id", goalID, "goal_energy_with_activity_and_diet", energyWithActivityAndDietSQL);


            double proteins = Math.round(energyWithActivityAndDiet*25/100);
            double carbs = Math.round(energyWithActivityAndDiet*50/100);
            double fat = Math.round(energyWithActivityAndDiet*25/100);

            double proteinsSQL = db.quoteSmart(proteins);
            double carbsSQL = db.quoteSmart(carbs);
            double fatSQL = db.quoteSmart(fat);
            db.update("goal", "_id", goalID, "goal_proteins_with_activity_and_diet", proteinsSQL);
            db.update("goal", "_id", goalID, "goal_carbs_with_activity_and_diet", carbsSQL);
            db.update("goal", "_id", goalID, "goal_fat_with_activity_and_diet", fatSQL);

        }



        // Error handling
        if(!(errorMessage.isEmpty())){
            // There is error
            textViewErrorMessage.setText(errorMessage);
            imageViewError.setVisibility(View.VISIBLE);
            textViewErrorMessage.setVisibility(View.VISIBLE);

        }

        // Close db
        db.close();

        // Move to main activity
        if(errorMessage.isEmpty()){
            Intent i = new Intent(SignUpGoal.this, MainActivity.class);
            startActivity(i);
        }
    } // signUpGoalSubmit

    //hideErrorHandling
    public void hideErrorHandling(){
        /* Hide error icon and message */
        ImageView imageViewError = (ImageView)findViewById(R.id.imageViewError);
        imageViewError.setVisibility(View.GONE);

        TextView textViewErrorMessage = (TextView)findViewById(R.id.textViewErrorMessage);
        textViewErrorMessage.setVisibility(View.GONE);

    }

    // mesurmentUsed
    public void mesurmentUsed(){
        // Open database
        DBAdapter db = new DBAdapter(this);
        db.open();

        // Get row number one from users
        long rowID = 1;
        String fields[] = new String[] {
                "_id",
                "user_mesurment"
        };
        Cursor c = db.select("users", fields, "_id", rowID);
        String mesurment;
        mesurment = c.getString(1);


        if(mesurment.startsWith("m")){
            // Metric
        }
        else{
            // Imperial

            // Kg to punds
            TextView textViewTargetMesurmentType = (TextView)findViewById(R.id.textViewTargetMesurmentType);
            textViewTargetMesurmentType.setText("pounds");


            // Kg each week to pounds each week
            TextView textViewKgEachWeek = (TextView)findViewById(R.id.textViewKgEachWeek);
            textViewKgEachWeek.setText("pounds each week");
        }


        // Close database
        db.close();
    }

    // getAge
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = Integer.valueOf(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
