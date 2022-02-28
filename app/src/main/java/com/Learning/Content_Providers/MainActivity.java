package com.Learning.Content_Providers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.Learning.Content_Providers.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ListView contact_names;

    private static final int REQUEST_CODE_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        contact_names = (ListView) findViewById(R.id.contact_names);

        setSupportActionBar(binding.toolbar);
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        /*permission management */
        int hasReadContcatPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        Log.d(TAG, "onCreate: permissions granted is : " + hasReadContcatPermission);

        if (hasReadContcatPermission != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "onCreate: requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }


        binding.fab.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("Range")
            @Override
            public void onClick(View view)
            {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Log.d(TAG, "onClick: starts");

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "onCreate: button pressed but permission is denied");
                    Snackbar.make(view, "Please grant access to contacts", Snackbar.LENGTH_INDEFINITE).setAction("Grant access", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Log.d(TAG, "onClick: starts");
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS))
                            {
                                Log.d(TAG, "snackbar onclick asking permission");
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
                            } else
                            {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                Log.d(TAG, "snackbar onClick: URI->>" + uri.toString());
                                intent.setData(uri);
                                MainActivity.this.startActivity(intent);


                            }

                            Log.d(TAG, "snackbar onClick: ends ");
                        }
                    }).show();
                    return;
                }

                String[] projetion = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projetion, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

                if (cursor != null)
                {

                    List<String> contacts = new ArrayList<String>();
                    while (cursor.moveToNext())
                    {

                        String contact = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                        if (contact != null)
                            contacts.add(contact);
                    }
                    Log.d(TAG, "onClick: contacts list :" + contacts.toString());
                    cursor.close();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.contact_name, R.id.name, contacts);
                    contact_names.setAdapter(adapter);

                }
                Log.d(TAG, "onClick: ends");
            }
        });
        Log.d(TAG, "onCreate: ends");
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//
//
//        Log.d(TAG, "onRequestPermissionsResult: starts");
//        switch (requestCode)
//        {
//            case REQUEST_CODE_READ_CONTACTS:
//            {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
//                } else
//                {
//                    Log.d(TAG, "onRequestPermissionsResult: permission refused");
//
//                }
//                return;
//
//            }
//
//
//        }
//        Log.d(TAG, "onRequestPermissionsResult: ends");
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//      //  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//     //   return NavigationUI.navigateUp(navController, appBarConfiguration)
//       //         || super.onSupportNavigateUp();
//    }
}