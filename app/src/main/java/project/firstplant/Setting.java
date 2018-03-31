package project.firstplant;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import project.firstplant.data.StaticConfig;

public class Setting extends Fragment {

    private DatabaseReference myRef, userDB, Ref;
    private Button select, edit_id;
    private TextView Tset, Tfarm;
    public static String idc;
    List<String> stringlist;
    ArrayAdapter<String> adapter;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);
        edit_id = (Button) view.findViewById(R.id.edit_id);
        select = (Button) view.findViewById(R.id.select);
        Tset = (TextView) view.findViewById(R.id.Tset);
        Tfarm = (TextView) view.findViewById(R.id.Tfarm);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        getActivity().setTitle(R.string.Settings);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Tset.setText(getResources().getString(R.string.Current_ID) + " : " + prefs.getString("IDC", ""));
        Tfarm.setText(getResources().getString(R.string.Current_Farm) + " : " + prefs.getString("Farm", ""));
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("name");
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                mBuilder.setTitle(R.string.Current_Farm);
                myRef = FirebaseDatabase.getInstance().getReference("users").child(username);
                final Spinner spinner = (Spinner) mView.findViewById(R.id.spinner);
                stringlist = new ArrayList<>();
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stringlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                myRef.addValueEventListener(new ValueEventListener() {
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
                        myRef = FirebaseDatabase.getInstance().getReference("users").child(username);
                        if (!spinner.getSelectedItem().toString().equals(R.string.ChooseFarm)) {
                            myRef.addValueEventListener(new ValueEventListener() {
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
            }
        });

        edit_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder eBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_spinner_edit, null);
                eBuilder.setTitle(R.string.Farmname);
                myRef = FirebaseDatabase.getInstance().getReference("users").child(username);
                final Spinner spinner_edit = (Spinner) mView.findViewById(R.id.spinner_edit);
                final EditText editID = (EditText) mView.findViewById(R.id.editID);
                stringlist = new ArrayList<>();
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stringlist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_edit.setAdapter(adapter);
                myRef.addValueEventListener(new ValueEventListener() {
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

                eBuilder.setPositiveButton(R.string.Change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String value = spinner_edit.getSelectedItem().toString();
                        myRef.child(value).setValue(editID.getText().toString());
                        if (value.equals(prefs.getString("Farm", ""))) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("IDC", editID.getText().toString());
                            editor.commit();
                            Tset.setText(getResources().getString(R.string.Current_ID) + " : " + editID.getText().toString());
                        }
                        Ref = FirebaseDatabase.getInstance().getReference(editID.getText().toString());
                        Ref.child("AutoFertilization").child("Alert").setValue("Disable");
                        Ref.child("AutoFertilization").child("Alert2").setValue("Disable");
                        Ref.child("AutoFertilization").child("Notification").setValue("Disable");
                        Ref.child("AutoFertilization").child("Secret").setValue("0");
                        Ref.child("AutoFertilization").child("Status").setValue("Disable");
                        Ref.child("AutoFertilization").child("Volume").setValue(0);
                        Ref.child("AutoIrrigation").child("Notification").setValue("Disable");
                        Ref.child("AutoIrrigation").child("Status").setValue("Disable");
                        Ref.child("AutoIrrigation").child("Warning").setValue(0);
                        Ref.child("Fertilization").child("Status").setValue("Disable");
                        Ref.child("Fertilization").child("Notification").setValue("Disable");
                        Ref.child("Fertilization").child("Volume").setValue(0);
                        Ref.child("Irrigation").child("Time").setValue(0);
                        Ref.child("Irrigation").child("Status").setValue("Disable");
                        Ref.child("Irrigation").child("Notification").setValue("Disable");
                        Ref.child("Weather").child("Temperature").setValue(0);
                        Ref.child("Weather").child("Humidity").setValue(0);
                        Ref.child("Weather").child("Heatindex").setValue(0);
                        Ref.child("Weather").child("Raindrop").setValue(0);
                        Ref.child("Weather").child("Sunlight").setValue(0);
                    }

                });

                eBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                eBuilder.setView(mView);
                AlertDialog dialog = eBuilder.create();
                dialog.show();
            }

        });
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                View dView = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog, null);
                final EditText farmname = (EditText) dView.findViewById(R.id.farmname);
                final EditText controller = (EditText) dView.findViewById(R.id.controller);
                myRef = FirebaseDatabase.getInstance().getReference("users").child(username);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.AddFarm);
                builder.setView(dView);
                builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        myRef.child(farmname.getText().toString()).setValue(controller.getText().toString());
                        Ref = FirebaseDatabase.getInstance().getReference(controller.getText().toString());
                        Ref.child("AutoFertilization").child("Alert").setValue("Disable");
                        Ref.child("AutoFertilization").child("Alert2").setValue("Disable");
                        Ref.child("AutoFertilization").child("Notification").setValue("Disable");
                        Ref.child("AutoFertilization").child("Secret").setValue("0");
                        Ref.child("AutoFertilization").child("Status").setValue("Disable");
                        Ref.child("AutoFertilization").child("Volume").setValue(0);
                        Ref.child("AutoIrrigation").child("Notification").setValue("Disable");
                        Ref.child("AutoIrrigation").child("Status").setValue("Disable");
                        Ref.child("AutoIrrigation").child("Warning").setValue(0);
                        Ref.child("Fertilization").child("Status").setValue("Disable");
                        Ref.child("Fertilization").child("Notification").setValue("Disable");
                        Ref.child("Fertilization").child("Volume").setValue(0);
                        Ref.child("Irrigation").child("Time").setValue(0);
                        Ref.child("Irrigation").child("Status").setValue("Disable");
                        Ref.child("Irrigation").child("Notification").setValue("Disable");
                        Ref.child("Weather").child("Temperature").setValue(0);
                        Ref.child("Weather").child("Humidity").setValue(0);
                        Ref.child("Weather").child("Heatindex").setValue(0);
                        Ref.child("Weather").child("Raindrop").setValue(0);
                        Ref.child("Weather").child("Sunlight").setValue(0);
                    }
                });
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }
        });
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_select);
        item.setVisible(false);
    }
}

