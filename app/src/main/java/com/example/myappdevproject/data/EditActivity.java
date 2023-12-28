package com.example.myappdevproject.data;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myappdevproject.MainActivity;
import com.example.myappdevproject.R;

public class EditActivity extends AppCompatActivity {

    private EditText editName, editAge, editEmail;
    private Button btnSave, btnCancel;
    private int personId;
    private DatabaseOperations dbOperations;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppBackground();
        setContentView(R.layout.activity_edit);

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        dbOperations = new DatabaseOperations(this);
        dbOperations.open();

        personId = getIntent().getIntExtra("personId", -1);

        person = dbOperations.getPerson(personId);

        if (person != null) {
            editName.setText(person.getName());
            editAge.setText(String.valueOf(person.getAge()));
            editEmail.setText(person.getEmail());
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePerson();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updatePerson() {
        String name = editName.getText().toString().trim();
        String ageText = editAge.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        if (name.isEmpty() || ageText.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageText);

        person.setName(name);
        person.setAge(age);
        person.setEmail(email);

        dbOperations.updatePerson(person);

        Toast.makeText(this, "Person updated successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        intent.putExtra("dataUpdated", true);
        startActivity(intent);

        finish();
    }
    private void setAppBackground() {
        boolean isDarkMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;

        int backgroundColor = isDarkMode ? Color.parseColor("#FF333333") : Color.parseColor("#FFFFFFFF");
        findViewById(android.R.id.content).setBackgroundColor(backgroundColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOperations.close();
    }
}