package com.example.myappdevproject.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myappdevproject.R;

import java.util.List;

public class PersonListAdapter extends ArrayAdapter<Person> {

    private Context context;
    private int layoutResource;
    private List<Person> personList;
    private PersonClickListener personClickListener;

    public interface PersonClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public PersonListAdapter(Context context, List<Person> personList) {
        super(context, 0, personList);
        this.context = context;
        this.personList = personList;
        layoutResource = R.layout.item_person;
    }

    public void setPersonClickListener(PersonClickListener listener) {
        this.personClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_person, parent, false);
        }

        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtAge = convertView.findViewById(R.id.txtAge);
        TextView txtEmail = convertView.findViewById(R.id.txtEmail);
        ImageButton btnEdit = convertView.findViewById(R.id.btnEdit);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        Person person = personList.get(position);
        txtName.setText(person.getName());
        txtAge.setText(String.valueOf(person.getAge()));
        txtEmail.setText(person.getEmail());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personClickListener != null) {
                    personClickListener.onEditClick(position);
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personClickListener != null) {
                    personClickListener.onDeleteClick(position);
                }
            }
        });

        return convertView;
    }
}