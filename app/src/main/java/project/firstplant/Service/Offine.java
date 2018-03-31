package project.firstplant.Service;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by waron on 2/9/2560.
 */

public class Offine extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
