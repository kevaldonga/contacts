package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Contact> contacts;
    ImageButton remove_all;
    TextView selected_items, appTitle;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 202) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            setContacts();
        }
        else {
            Toast.makeText(this, "Please allow permissions to work with us !!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ask_permissions();
        remove_all = findViewById(R.id.remove_all);
        selected_items = findViewById(R.id.selected_items);
        recyclerView = findViewById(R.id.recycler_view);
        appTitle = findViewById(R.id.app_title);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ask_permissions() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.CALL_PHONE}, 202);
        onRequestPermissionsResult(202, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.CALL_PHONE}, new int[]{checkSelfPermission(Manifest.permission.READ_CONTACTS)
                , checkSelfPermission(Manifest.permission.WRITE_CONTACTS)
                , checkSelfPermission(Manifest.permission.CALL_PHONE)});
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setContacts() {
        contacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null);
        String name, phone_no;
        int i = 0;
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext() && cursor != null) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phone_no = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (cursor.getPosition() > 1) {
                    cursor.moveToPrevious();
                    String old_name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    cursor.moveToNext();
                    String new_name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    if (old_name.equals(new_name)) {
                        i++;
                        Log.i("contacts_details", "Duplicate of " + old_name + " found");
                        Log.i("contacts_details", i + " Duplicate Contacts found so far");
                        continue;
                    }
                } // Duplicate contact checking
                // removing all spaces from phone no's
                phone_no = phone_no.replaceAll(" ", "");
                contacts.add(new Contact(name, phone_no));
                Log.i("contacts_details", "Contact name --" + name);
                Log.i("contacts_details", "Contact phone no --" + phone_no);
                Log.i("contacts_details", "Current position --" + cursor.getPosition());
            }
        }
        Log.i("contacts_details", "total contacts in your phone is " + cursor.getCount());
        cursor.close();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, selected_items, appTitle, remove_all);
        recyclerViewAdapter.setContacts(contacts);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}