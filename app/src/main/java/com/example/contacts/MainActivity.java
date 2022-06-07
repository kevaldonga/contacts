package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Contact> contacts;
    ImageButton remove_all, shut_down;
    TextView selected_items, appTitle;

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 202) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            setContacts();
        } else {
            Toast.makeText(this, "Please allow permissions to work with us !!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        remove_all = findViewById(R.id.remove_all);
        selected_items = findViewById(R.id.selected_items);
        recyclerView = findViewById(R.id.recycler_view);
        appTitle = findViewById(R.id.app_title);
        shut_down = findViewById(R.id.log_out);
        ask_permissions();
        shut_down.setOnClickListener(v ->{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),Register.class));
        });
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
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        removeDuplicate();
        recyclerViewAdapter.setContacts(contacts);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void removeDuplicate() {
        String oldName,oldNum,newName,newNum;
        int dpc = 0;
        for(int i = 0; i < contacts.size(); i++){
            if(i == contacts.size() - 1){
                break;
            }
            oldName = contacts.get(i).getName();
            oldNum = contacts.get(i).getPhone_no();
            i++;
            newName = contacts.get(i).getName();
            newNum = contacts.get(i).getPhone_no();
            i--;
            if(oldName.equals(newName) && oldNum.equals(newNum)){
                contacts.remove(i);
                Log.i("contacts_details", "removing duplicate contact - " + oldName);
                dpc++;
            }
        }
        Log.i("contacts_details", "total duplicate contacts - " + dpc);
    }
}