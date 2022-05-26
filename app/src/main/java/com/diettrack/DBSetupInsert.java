package com.diettrack;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

public class DBSetupInsert {

    private final Context context;

    // Public Class
    public DBSetupInsert(Context ctx) {
        this.context = ctx;
    }

    // Setup Insert To Categories
    // To insert into category table
    public void setupInsertToCategories(String values) {
        try {
            DBAdapter db = new DBAdapter(context);
            db.open();
            db.insert("categories",
                    "_id, category_name, category_parent_id, category_icon, category_note",
                    values);
            db.close();
        } catch (SQLiteException e) {
             Toast.makeText(context, "Error; Could not insert categories.", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertAllCategories() {
        setupInsertToCategories("NULL, 'Grains', '0', '', NULL");
        setupInsertToCategories("NULL, 'Bread', '1', '', NULL");
        setupInsertToCategories("NULL, 'Cereals', '1', '', NULL");
        setupInsertToCategories("NULL, 'Frozen bread and rolls', '1', '', NULL");
        setupInsertToCategories("NULL, 'Crispbread', '1', '', NULL");

        // Parent id: 6
        setupInsertToCategories("NULL, 'Dessert and baking', '0', '', NULL");
        setupInsertToCategories("NULL, 'Baking', '6', '', NULL");
        setupInsertToCategories("NULL, 'Biscuit', '6', '', NULL");


        setupInsertToCategories("NULL, 'Drinks', '0', '', NULL");
        setupInsertToCategories("NULL, 'Soda', '9', '', NULL");


        setupInsertToCategories("NULL, 'Fruit and vegetables', '0', '', NULL");
        setupInsertToCategories("NULL, 'Frozen fruits and vegetables', '11', '', NULL");
        setupInsertToCategories("NULL, 'Fruit', '11', '', NULL");
        setupInsertToCategories("NULL, 'Vegetables', '11', '', NULL");
        setupInsertToCategories("NULL, 'Canned fruits and vegetables', '11', '', NULL");


        setupInsertToCategories("NULL, 'Health', '0', '', NULL");
        setupInsertToCategories("NULL, 'Meal substitutes', '16', '', NULL");
        setupInsertToCategories("NULL, 'Protein bars', '16', '', NULL");
        setupInsertToCategories("NULL, 'Protein powder', '16', '', NULL");


        setupInsertToCategories("NULL, 'Meat, chicken and fish', '0', '', NULL");
        setupInsertToCategories("NULL, 'Meat', '20', '', NULL");
        setupInsertToCategories("NULL, 'Chicken', '20', '', NULL");
        setupInsertToCategories("NULL, 'Seafood', '20', '', NULL");


        setupInsertToCategories("NULL, 'Dairy and eggs', '0', '', NULL");
        setupInsertToCategories("NULL, 'Eggs', '24', '', NULL");
        setupInsertToCategories("NULL, 'Cream and sour cream', '24', '', NULL");
        setupInsertToCategories("NULL, 'Yogurt', '24', '', NULL");


        setupInsertToCategories("NULL, 'Prepared', '0', '', NULL");
        setupInsertToCategories("NULL, 'Ready dishes', '28', '', NULL");
        setupInsertToCategories("NULL, 'Pizza', '28', '', NULL");
        setupInsertToCategories("NULL, 'Noodle', '28', '', NULL");
        setupInsertToCategories("NULL, 'Pasta', '28', '', NULL");
        setupInsertToCategories("NULL, 'Rice', '28', '', NULL");
        setupInsertToCategories("NULL, 'Taco', '28', '', NULL");


        setupInsertToCategories("NULL, 'Cheese', '0', '', NULL");
        setupInsertToCategories("NULL, 'Cream cheese', '35', '', NULL");
        setupInsertToCategories("NULL, 'Cottage cheese', '35', '', NULL");
        setupInsertToCategories("NULL, 'Mozzarella cheese', '35', '', NULL");


        setupInsertToCategories("NULL, 'On bread', '0', '', NULL");
        setupInsertToCategories("NULL, 'Sweet spreads', '37', '', NULL");
        setupInsertToCategories("NULL, 'Jam', '37', '', NULL");


        setupInsertToCategories("NULL, 'Snacks', '0', '', NULL");
        setupInsertToCategories("NULL, 'Nuts', '42', '', NULL");
        setupInsertToCategories("NULL, 'Potato chips', '42', '', NULL");
        setupInsertToCategories("NULL, 'Chocolate', '42', '', NULL");
    }


    // Setup Insert To Food
    // To insert food into food table
    public void setupInsertToFood(String values) {

        try {
            DBAdapter db = new DBAdapter(context);
            db.open();
            db.insert("food",
                    "_id, food_name, food_manufactor_name, food_serving_size_gram, food_serving_size_gram_mesurment, food_serving_size_pcs, food_serving_size_pcs_mesurment, food_energy, food_proteins, food_carbohydrates, food_fat, food_energy_calculated, food_proteins_calculated, food_carbohydrates_calculated, food_fat_calculated, food_user_id, food_barcode, food_category_id, food_thumb, food_image_a, food_image_b, food_image_c, food_notes",
                    values);
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(context, "Error; Could not insert food.", Toast.LENGTH_SHORT).show();
        }

    }

    // Insert all food into food database
    public void insertAllFood() {
        setupInsertToFood("NULL, 'Cottage Cheese', 'Delaco', '250', 'gram', '1', 'box', '96', '13', '1.5', '4.3', '240', '33', '4', '11', NULL, NULL, '35', 'Delaco_cottage_cheese_thumb.jpg', 'Delaco_cottage_cheese_a.jpg', NULL, NULL, NULL");

    }
}
