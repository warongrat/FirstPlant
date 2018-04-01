package project.firstplant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import project.firstplant.Service.ServiceUtils;
import project.firstplant.data.FriendDB;
import project.firstplant.data.GroupDB;
import project.firstplant.data.StaticConfig;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int notification_id;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private DatabaseReference userDB, Ref;
    private String username;
    private TextView Tset, Tfarm;
    private FragmentManager fragmentManager;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("name");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);

        notification_id = (int) System.currentTimeMillis();

        Intent notification_intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notification_intent, 0);

        builder.setSmallIcon(R.drawable.icon_small)
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.Welcome))
                .setContentText("First App First Plant")
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(Notification.PRIORITY_MAX);

        notificationManager.notify(notification_id, builder.build());
        notificationManager.cancel(notification_id);
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Name", username);
                editor.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (prefs.getString("First", "").equals("")) {
            fragment = new get_start();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.mainFrame, fragment).commit();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            setTitle("FirstPlant");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int id = item.getItemId();
        if (id == R.id.action_exit) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Confirm Exit..!!!");
            alertDialogBuilder.setMessage("Are you sure,You want to exit ?");
            alertDialogBuilder.setIcon(R.drawable.question);
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                    System.exit(0);
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        if (id == R.id.action_chat) {
            startActivity(new Intent(MainActivity.this, MainChat.class));
            return true;
        }
        /*if (id == R.id.action_about) {
            fragment = new About();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.mainFrame, fragment).commit();
            return true;
        }*/
        if (id == R.id.action_lan) {
            fragment = new activity_language();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.mainFrame, fragment).commit();
            return true;
        }
        if (id == R.id.action_signout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Sure..!!!");
            alertDialogBuilder.setMessage("Are you sure,You want to sign out ?");
            alertDialogBuilder.setIcon(R.drawable.question);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.commit();
                    FirebaseAuth.getInstance().signOut();
                    FriendDB.getInstance(getApplicationContext()).dropDB();
                    GroupDB.getInstance(getApplicationContext()).dropDB();
                    ServiceUtils.stopServiceFriendChat(getApplicationContext(), true);
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        if (id == R.id.action_select) {
            View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            View view = getLayoutInflater().inflate(R.layout.activity_setting, null);
            final List<String> stringlist;
            final ArrayAdapter<String> adapter, adapter_type;
            final Spinner spinner = (Spinner) mView.findViewById(R.id.spinner);
            Tset = (TextView) view.findViewById(R.id.Tset);
            Tfarm = (TextView) view.findViewById(R.id.Tfarm);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setTitle(R.string.Farmname);

            stringlist = new ArrayList<>();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringlist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            Ref = FirebaseDatabase.getInstance().getReference("users").child(prefs.getString("Name", ""));
            Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    stringlist.clear();
                    stringlist.add(getString(R.string.ChooseFarm));
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey().toString();
                        stringlist.add(key);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    Ref = FirebaseDatabase.getInstance().getReference("users").child(username);
                    if (!spinner.getSelectedItem().toString().equals(R.string.ChooseFarm)) {
                        Ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String value = dataSnapshot.child(spinner.getSelectedItem().toString()).getValue(String.class);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("Farm", spinner.getSelectedItem().toString());
                                editor.putString("IDC", value);
                                editor.commit();
                                Tset.setText(getResources().getString(R.string.Current_ID) + " : " + prefs.getString("IDC", ""));
                                Tfarm.setText(getResources().getString(R.string.Current_Farm) + " : " + prefs.getString("Farm", ""));
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            });

            mBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_weather) {
            fragment = new weatherFragment();
        } else if (id == R.id.nav_water) {
            fragment = new IrrigationFragment();
        } else if (id == R.id.nav_Wauto) {
            fragment = new WautoFragment();
        } else if (id == R.id.nav_fertilizer) {
            fragment = new FertilizationFragment();
        } else if (id == R.id.nav_time) {
            fragment = new SetTimeFragment();
        } else if (id == R.id.setting) {
            fragment = new Setting();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.mainFrame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}