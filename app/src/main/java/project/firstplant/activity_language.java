package project.firstplant;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Locale;

public class activity_language extends Fragment {
    private ImageView th, eng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_language, container, false);
        getActivity().setTitle(R.string.Language);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        th = (ImageView) view.findViewById(R.id.thai);
        eng = (ImageView) view.findViewById(R.id.eng);
        final Locale current = getResources().getConfiguration().locale;
        final Configuration config = new Configuration();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = prefs.edit();
        th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.locale = new Locale("th");
                getResources().updateConfiguration(config, null);
                if (current.equals(Locale.ENGLISH)) {
                    editor.putString("Lan", config.toString());
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                } else ;
            }
        });

        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.locale = Locale.ENGLISH;
                getResources().updateConfiguration(config, null);
                if (current.equals(new Locale("th"))) {
                    editor.putString("Lan", config.toString());
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                } else ;
            }
        });

        return view;

    }
}
