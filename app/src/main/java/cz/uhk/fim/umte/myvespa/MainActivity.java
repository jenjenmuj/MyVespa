package cz.uhk.fim.umte.myvespa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button viewMap, save;
    private EditText name_in, vin_in, ccm_in;

    private String vin, name, ccm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewMap = findViewById(R.id.btn_load_map);
        save = findViewById(R.id.btn_save_vespa);
        name_in = findViewById(R.id.pt_my_vespa);
        vin_in = findViewById(R.id.pt_vin);
        ccm_in = findViewById(R.id.pt_ccm);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String savedName = prefs.getString("Name", "Name");
        name_in.setText("" + savedName);

        String savedVin = prefs.getString("VIN", "VIN");
        vin_in.setText("" + savedVin);

        String savedCcm = prefs.getString("ccm", "125");
        ccm_in.setText("" + savedCcm);


        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_in.getText().toString();
                vin = vin_in.getText().toString();
                ccm = ccm_in.getText().toString();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("Name", name);
                editor.putString("VIN", vin);
                editor.putString("ccm", ccm);

                editor.apply();

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void openMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
