package com.example.myappdevproject;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.myappdevproject.data.EditActivity;
import com.example.myappdevproject.data.PersonListAdapter;
import com.example.myappdevproject.data.DatabaseOperations;
import com.example.myappdevproject.data.Person;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PersonListAdapter.PersonClickListener{

    private EditText editName, editAge, editEmail;
    private Button btnAdd;
    private ListView listPersons;
    private PersonListAdapter personListAdapter;
    private DatabaseOperations dbOperations;
    private List<Person> personList;
    private static final int EDIT_ACTIVITY_REQUEST_CODE = 1;
    private boolean dataUpdated = false;
    private LinearLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editEmail = findViewById(R.id.editEmail);
        btnAdd = findViewById(R.id.btnAdd);
        listPersons = findViewById(R.id.listPersons);
        personList = new ArrayList<>();
        personListAdapter = new PersonListAdapter(this, personList);
        personListAdapter.setPersonClickListener(this);
        listPersons.setAdapter(personListAdapter);
        mainLayout = findViewById(R.id.mainLayout);

        dbOperations = new DatabaseOperations(this);
        dbOperations.open();
        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllUsers();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPerson();
            }
        });
        updatePersonList();
        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDarkMode();
                } else {
                    setLightMode();
                }
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = preferences.getBoolean("dark_mode_enabled", false);
        toggleButton.setChecked(isDarkModeEnabled);
        if (isDarkModeEnabled) {
            setDarkMode();
        } else {
            setLightMode();
        }
    }

    private void addPerson() {
        String name = editName.getText().toString().trim();
        String ageText = editAge.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        if (name.isEmpty() || ageText.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageText);

        Person person = new Person();
        person.setName(name);
        person.setAge(age);
        person.setEmail(email);

        dbOperations.insertPerson(person);

        editName.setText("");
        editAge.setText("");
        editEmail.setText("");

        updatePersonList();
    }

    private void updatePersonList() {
        personList.clear();
        personList.addAll(dbOperations.getAllPersons());
        personListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            updatePersonList();
        }
    }
    @Override
    public void onEditClick(int position) {
        Person person = personList.get(position);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("personId", person.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        Person person = personList.get(position);
        dbOperations.deletePerson(person.getId());
        personListAdapter.notifyDataSetChanged();
        personList.remove(position);
        Toast.makeText(MainActivity.this, "Person deleted", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (dataUpdated) {
            updatePersonList();
            dataUpdated = false;
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.hasExtra("dataUpdated")) {
            boolean dataUpdated = intent.getBooleanExtra("dataUpdated", false);
            if (dataUpdated) {
                updatePersonList();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOperations.close();
    }
    private void setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey));
        saveDarkModePreference(true);
    }

    private void setLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        saveDarkModePreference(false);
    }
    @Override
    public void recreate(){
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(getIntent());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    private void deleteAllUsers() {
        dbOperations.deleteAllPersons();
        personList.clear();
        personListAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "All users deleted", Toast.LENGTH_SHORT).show();
    }


    private void saveDarkModePreference(boolean isDarkModeEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dark_mode_enabled", isDarkModeEnabled);
        editor.apply();
    }
}
