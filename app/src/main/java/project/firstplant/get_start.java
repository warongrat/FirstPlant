package project.firstplant;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.firstplant.data.StaticConfig;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.TourGuide;

public class get_start extends Fragment {
    public TourGuide mTutorialHandler, mTutorialHandler2;
    private Button get_start;
    private DatabaseReference myRef, userDB, Ref;
    private TextView Tset, Tfarm;
    private String username;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_get_start, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        get_start = (Button) view.findViewById(R.id.get_start);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("name");
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
        Overlay overlay = new Overlay()
                // Note: disable click has no effect when setOnClickListener is used, this is here for demo purpose
                // if setOnClickListener is not used, disableClick() will take effect
                .disableClick(true)
                .disableClickThroughHole(false)
                .setStyle(Overlay.Style.ROUNDED_RECTANGLE)
                .setRoundedCornerRadius(8);

        mTutorialHandler = TourGuide.init(getActivity()).with(TourGuide.Technique.CLICK)
                .setPointer(new Pointer())
                .setOverlay(overlay)
                .playOn(get_start);

        get_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //mTutorialHandler.cleanUp();
                View dView = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog, null);
                View sView = getActivity().getLayoutInflater().inflate(R.layout.activity_setting, null);
                final EditText farmname = (EditText) dView.findViewById(R.id.farmname);
                final EditText controller = (EditText) dView.findViewById(R.id.controller);
                Tset = (TextView) sView.findViewById(R.id.Tset);
                Tfarm = (TextView) sView.findViewById(R.id.Tfarm);
                myRef = FirebaseDatabase.getInstance().getReference("users").child(username);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setView(dView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mTutorialHandler.cleanUp();
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

                        String value = controller.getText().toString();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("First", "1");
                        editor.putString("IDC", value);
                        editor.putString("Farm", farmname.getText().toString());
                        editor.commit();
                        Tset.setText(getResources().getString(R.string.Current_ID) + " : " + prefs.getString("IDC", ""));
                        Tfarm.setText("Farm name: "+ prefs.getString("Farm", ""));
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }
        });

        return view;

    }
}
