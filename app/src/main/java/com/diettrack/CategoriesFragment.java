package com.diettrack;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;



public class CategoriesFragment extends Fragment {


    //Class Variables
    private View mainView;
    private Cursor listCursorCategory;
    private Cursor listCursorFood;

    // Action buttons on toolbar
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    // Holder for buttons on toolbar
    private String currentCategoryId;
    private String currentCategoryName;

    private String currentFoodId;
    private String currentFoodName;

    //Fragment Variables
    // Nessesary for making fragment run

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    // Constructur
    // Nessesary for having Fragment as class
    public CategoriesFragment() {
        // Required empty public constructor
    }


    //Creating Fragment
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // on Activity Created
    // Run methods when started
    // Set toolbar menu items
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set title
        ((FragmentActivityNew)getActivity()).getSupportActionBar().setTitle("Categories");

        // Populate the list of categories
        populateList("0", ""); // Parent

        // Create menu
        setHasOptionsMenu(true);


    }


    //On create view
    // Sets main View variable to the view, so we can change views in fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_categories, container, false);
        return mainView;
    }

    //set main view
    // Changing view method in fragment
    private void setMainView(int id){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    // on Create Options Menu
    // Creating action icon on toolbar
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate menu
        ((FragmentActivityNew)getActivity()).getMenuInflater().inflate(R.menu.menu_categories, menu);

        // Assign menu items to variables
        menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemDelete = menu.findItem(R.id.action_delete);

        // Hide as default
        menuItemEdit.setVisible(false);
        menuItemDelete.setVisible(false);
    }


    // on Options Item Selected
    // Action icon clicked on
    // Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.action_add) {
            createNewCategory();
        }
        else if (id == R.id.action_edit) {
            editCategory();
        }
        else if (id == R.id.action_delete) {
            deleteCategory();
        }
        return super.onOptionsItemSelected(menuItem);
    }

// methods

    // populate List
    public void populateList(String parentID, String parentName){

        // Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Get categories
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        listCursorCategory = db.select("categories", fields, "category_parent_id", parentID, "category_name", "ASC");

        // Createa a array
        ArrayList<String> values = new ArrayList<String>();

        // Convert categories to string
        int categoriesCount = listCursorCategory.getCount();
        for(int x=0;x<categoriesCount;x++){
            values.add(listCursorCategory.getString(listCursorCategory.getColumnIndex("category_name")));

            listCursorCategory.moveToNext();
        }

        // Close cursor
        // categoriesCursor.close();

        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);

        // Set Adapter
        ListView lv = (ListView)getActivity().findViewById(R.id.listViewCategories);
        lv.setAdapter(adapter);

        // OnClick
        if(parentID.equals("0")) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    listItemClicked(arg2);
                }
            });
        }

        // Close db
        db.close();

        if(parentID.equals("0")){

        }
        else{
            // Show edit button
            menuItemEdit.setVisible(true);
            menuItemDelete.setVisible(true);

        }


    } // populateList

    // List item clicked
    public void listItemClicked(int listItemIDClicked){

        // Move cursor to ID clicked
        listCursorCategory.moveToPosition(listItemIDClicked);

        // Get ID and name from cursor
        // Set current name and id
        currentCategoryId = listCursorCategory.getString(0);
        currentCategoryName = listCursorCategory.getString(1);
        String parentID = listCursorCategory.getString(2);

        // Change title
        ((FragmentActivityNew)getActivity()).getSupportActionBar().setTitle(currentCategoryName);


        // Move to sub class
        populateList(currentCategoryId, currentCategoryName);


        // Show food in category
        showFoodInCategory(currentCategoryId, currentCategoryName, parentID);


    } // listItemClicked


    // Create new category
    public void createNewCategory(){
        // Change layout
        int id = R.layout.fragment_categories_add_edit;
        setMainView(id);

        // Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Fill spinner with categories
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0", "category_name", "ASC");

        // Creating array
        int dbCursorCount = dbCursor.getCount();
        String[] arraySpinnerCategories = new String[dbCursorCount+1];

        // This is parent
        arraySpinnerCategories[0] = "-";

        // Convert Cursor to String
        for(int x=1;x<dbCursorCount+1;x++){
            arraySpinnerCategories[x] = dbCursor.getString(1).toString();
            dbCursor.moveToNext();
        }

        // Populate spinner
        Spinner spinnerParent = (Spinner) getActivity().findViewById(R.id.spinnerCategoryParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);



        // SubmitButton listener
        Button buttonHome = (Button)getActivity().findViewById(R.id.buttonCategoriesSubmit);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewCategorySubmitOnClick();
            }
        });

        db.close();

    }
    // Create new category Submit on click
    public void createNewCategorySubmitOnClick() {
        // Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Error?
        int error = 0;

        // Name
        EditText editTextName = (EditText)getActivity().findViewById(R.id.editTextName);
        String stringName = editTextName.getText().toString();
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_SHORT).show();
            error = 1;
        }


        // Parent
        Spinner spinner = (Spinner)getActivity().findViewById(R.id.spinnerCategoryParent);
        String stringSpinnerCategoryParent = spinner.getSelectedItem().toString();
        String parentID;
        if(stringSpinnerCategoryParent.equals("-")){
            parentID = "0";
        }
        else{
            // Find we want to find parent ID from the text
            String stringSpinnerCategoryParentSQL = db.quoteSmart(stringSpinnerCategoryParent);
            String fields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
            Cursor findParentID = db.select("categories", fields, "category_name", stringSpinnerCategoryParentSQL);
            parentID = findParentID.getString(0).toString();


        }

        if(error == 0){
            // Ready variables
            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);

            // Insert into database
            String input = "NULL, " + stringNameSQL + ", " + parentIDSQL;
            db.insert("categories", "_id, category_name, category_parent_id", input);

            // Give feedback
            Toast.makeText(getActivity(), "Category created", Toast.LENGTH_LONG).show();

            // Move user back to correct design
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

        }


        db.close();
    } // createNewCategorySubmitOnClick



    // Edit category
    public void editCategory(){
        // Edit Name = currentName
        // Edit ID   = currentID

        // Change layout
        int id = R.layout.fragment_categories_add_edit;
        setMainView(id);

        // Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Ask for parent ID
        Cursor c;
        String fieldsC[] = new String[] { "category_parent_id" };
        String currentIdSQL = db.quoteSmart(currentCategoryId);
        c = db.select("categories", fieldsC, "_id", currentIdSQL);
        String currentParentID = c.getString(0);
        int intCurrentParentID = 0;
        try {
            intCurrentParentID = Integer.parseInt(currentParentID);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        // Fill name
        EditText editTextName = (EditText) getActivity().findViewById(R.id.editTextName);
        editTextName.setText(currentCategoryName);


        // Fill spinner with categories
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0", "category_name", "ASC");

        // Creating array
        int dbCursorCount = dbCursor.getCount();
        String[] arraySpinnerCategories = new String[dbCursorCount+1];

        // This is parent
        arraySpinnerCategories[0] = "-";

        // Convert Cursor to String
        int correctParentID = 0;
        for(int x=1;x<dbCursorCount+1;x++){
            arraySpinnerCategories[x] = dbCursor.getString(1).toString();

            if(dbCursor.getString(0).toString().equals(currentParentID)){
                correctParentID = x;
            }

            // Move to next
            dbCursor.moveToNext();
        }

        // Populate spinner
        Spinner spinnerParent = (Spinner) getActivity().findViewById(R.id.spinnerCategoryParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);

        // Select corrent spinner item, that is the parent to currentID
        spinnerParent.setSelection(correctParentID);

        db.close();


        // SubmitButton listener
        Button buttonHome = (Button)getActivity().findViewById(R.id.buttonCategoriesSubmit);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCategorySubmitOnClick();
            }
        });

    }

    // Edit category submit on click
    public void editCategorySubmitOnClick(){
        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Error?
        int error = 0;

        // Name
        EditText editTextName = (EditText)getActivity().findViewById(R.id.editTextName);
        String stringName = editTextName.getText().toString();
        if(stringName.equals("")){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_SHORT).show();
            error = 1;
        }


        // Parent
        Spinner spinner = (Spinner)getActivity().findViewById(R.id.spinnerCategoryParent);
        String stringSpinnerCategoryParent = spinner.getSelectedItem().toString();
        String parentID;
        if(stringSpinnerCategoryParent.equals("-")){
            parentID = "0";
        }
        else{
            // Find we want to find parent ID from the text
            String stringSpinnerCategoryParentSQL = db.quoteSmart(stringSpinnerCategoryParent);
            String fields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };
            Cursor findParentID = db.select("categories", fields, "category_name", stringSpinnerCategoryParentSQL);
            parentID = findParentID.getString(0).toString();


        }

        if(error == 0){
            // Current ID to long
            long longCurrentID = Long.parseLong(currentCategoryId);

            // Ready variables
            long currentIDSQL = db.quoteSmart(longCurrentID);
            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);

            // Insert into database
            String input = "NULL, " + stringNameSQL + ", " + parentIDSQL;
            // db.insert("categories", "_id, category_name, category_parent_id", input);
            db.update("categories", "_id", currentIDSQL, "category_name", stringNameSQL);
            db.update("categories", "_id", currentIDSQL, "category_parent_id", parentIDSQL);

            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_LONG).show();

            // Move user back to correct design
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

        }

        /* Close db */
        db.close();
    } // editCategorySubmitOnClick


    // Delete category
    public void deleteCategory(){

        //Change layout
        int id = R.layout.fragment_categories_delete;
        setMainView(id);

        // Submit Button listener
        Button buttonCancel = (Button)getActivity().findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryCancelOnClick();
            }
        });

        Button buttonConfirmDelete = (Button)getActivity().findViewById(R.id.buttonConfirmDelete);
        buttonConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryConfirmOnClick();
            }
        });


    }
    public void deleteCategoryCancelOnClick(){
        // Move user back to correct design
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

    }
    public void deleteCategoryConfirmOnClick(){
        // Delete from SQL

        // Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        // Current ID to long
        long longCurrentID = 0;
        try {
            longCurrentID = Long.parseLong(currentCategoryId);
        }
        catch (NumberFormatException e){
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        // Ready variables
        long currentIDSQL = db.quoteSmart(longCurrentID);

        // Delete
        db.delete("categories", "_id", currentIDSQL);

        // Close db
        db.close();

        // Give message
        Toast.makeText(getActivity(), "Category deleted", Toast.LENGTH_LONG).show();

        // Move user back to correct design
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesFragment(), CategoriesFragment.class.getName()).commit();

    }



    // Show food in category
    public void showFoodInCategory(String categoryId, String categoryName, String categoryParentID){
        if(!(categoryParentID.equals("0"))) {

            // Change layout
            int id = R.layout.fragment_food;
            setMainView(id);

            // Database
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            // Get categories
            String fields[] = new String[] {
                    "_id",
                    "food_name",
                    "food_manufactor_name",
                    "food_description",
                    "food_serving_size_gram",
                    "food_serving_size_gram_mesurment",
                    "food_serving_size_pcs",
                    "food_serving_size_pcs_mesurment",
                    "food_energy_calculated"
            };
            listCursorFood = db.select("food", fields, "food_category_id", categoryId, "food_name", "ASC");


            // Find ListView to populate
            ListView lvItemsFood = (ListView)getActivity().findViewById(R.id.listViewFood);

            // Setup cursor adapter using cursor from last step
            FoodCursorAdapter continentsAdapter = new FoodCursorAdapter(getActivity(), listCursorFood);

            // Attach cursor adapter to the ListView
            lvItemsFood.setAdapter(continentsAdapter); // uses ContinensCursorAdapter


            // OnClick
            lvItemsFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    foodListItemClicked(arg2);
                }
            });


            // Close db
            db.close();

        } //categoryParentID.equals
    } // showFoodInCategory

    // Food list item clicked
    private void foodListItemClicked(int intFoodListItemIndex){
        // We should use
        currentFoodId = listCursorFood.getString(0);
        currentFoodName = listCursorFood.getString(1);


        // Change fragment to FoodView

        // Inialize fragment
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = FoodFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Send variable
        Bundle bundle = new Bundle();
        bundle.putString("currentFoodId", ""+currentFoodId);
        fragment.setArguments(bundle);

        // Need to pass meal number
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();



    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
