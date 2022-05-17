package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        remove_all = findViewById(R.id.remove_all);
        selected_items = findViewById(R.id.selected_items);
        recyclerView = findViewById(R.id.recycler_view);
        appTitle = findViewById(R.id.app_title);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this,selected_items,appTitle,remove_all);
        contacts = new ArrayList<>();
        setContacts();
        recyclerViewAdapter.setContacts(contacts);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setContacts() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, 2);
        }
        try {
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
        } catch (SecurityException e) {
            Toast.makeText(MainActivity.this, "Please grant necessary permissions to work with us", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 1);
        }
    }
}