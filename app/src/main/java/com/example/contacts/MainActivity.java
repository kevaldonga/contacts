package com.example.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    List<Contact> contacts;
    Map<String, Object> map;
    ImageButton remove_all, shut_down;
    TextView selected_items, appTitle;
    Intent intent;
    boolean isOPRunning = false;
    boolean registered = false;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    SharedPreferences sharedPreferences;

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
        if(isOPRunning){
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            isOPRunning = true;
            setContacts();
        } else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED && grantResults[2] == PackageManager.PERMISSION_DENIED){
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
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        sharedPreferences = getSharedPreferences("username", MODE_PRIVATE);
        map = new HashMap<>();
        db = FirebaseFirestore.getInstance();
        intent = getIntent();
        ask_permissions();
        shut_down.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Register.class));
        });
    }

    private void uploadData() {
        contactsToMap();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db.collection("users")
                .document(sharedPreferences.getString(sharedPreferences.getString("email", ""), "Anonymous"))
                .set(map)
                .addOnFailureListener(e -> Log.i("firestore", e.toString()))
                .addOnSuccessListener(unused -> Log.i("firestore", "data uploaded successfully"));
    }

    private void contactsToMap() {
        String name, phoneNo;
        for (int i = 0; i < contacts.size(); i++) {
            name = contacts.get(i).getName();
            phoneNo = contacts.get(i).getPhone_no();
            map.put(name, phoneNo);
        }
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
        intent = getIntent();
        String info = intent.getStringExtra("data");
        if (info.equals("login")) {
            registered = false;
            downloadData();
            return;
        }
        else {
            registered = true;
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
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, selected_items, appTitle, remove_all);
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        removeDuplicate();
        if (registered) {
            uploadData();
        }
        recyclerViewAdapter.setContacts(contacts);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void downloadData() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        map = new HashMap<>();
        sharedPreferences = getSharedPreferences("username", MODE_PRIVATE);
        String username = sharedPreferences.getString(user.getEmail(), "Anonymous");
        db.collection("users").document(username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.i("firestore", "getting data was successful");
                        map = documentSnapshot.getData();
                        Log.i("firestore", map.toString());
                        Log.i("firestore", username);
                        mapToContacts();
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, selected_items, appTitle, remove_all);
                        Collections.sort(contacts, new Comparator<Contact>() {
                            @Override
                            public int compare(Contact o1, Contact o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                        recyclerViewAdapter.setContacts(contacts);
                        recyclerView.setAdapter(recyclerViewAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("firestore", e.toString());
                    }
                });
    }

    private void mapToContacts() {
        contacts = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            contacts.add(new Contact(entry.getKey(), entry.getValue().toString()));
        }
        Log.i("firestore", "converting map to contacts were successful " + contacts.size());
    }

    private void removeDuplicate() {
        String oldName, oldNum, newName, newNum;
        int dpc = 0;
        for (int i = 0; i < contacts.size(); i++) {
            if (i == contacts.size() - 1) {
                break;
            }
            oldName = contacts.get(i).getName();
            oldNum = contacts.get(i).getPhone_no();
            i++;
            newName = contacts.get(i).getName();
            newNum = contacts.get(i).getPhone_no();
            i--;
            if (oldName.equals(newName) && oldNum.equals(newNum)) {
                contacts.remove(i);
                Log.i("contacts_details", "removing duplicate contact - " + oldName);
                dpc++;
            }
        }
        Log.i("contacts_details", "total duplicate contacts - " + dpc);
    }
}