package ru.kopte3.phonevalidator;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Settings extends Activity {

    private static final String LOD_TAG = "SettingsActivity";
    private RadioGroup mRadioGroup;
    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mRadioGroup = (RadioGroup) findViewById(R.id.countries);
        mSaveButton = (Button) findViewById(R.id.saveButton);

        for (final Country city : Country.values()) {
            RadioButton button = new RadioButton(this);
            button.setText(city.name());
            mRadioGroup.addView(button);
            if (city.name().equals(CountryStorage.getInstance(getApplicationContext()).getCurrentCountry().name())) {
                button.setChecked(true);
            }
        }
    }

    public void onClickSave(View view) {
        int checked = mRadioGroup.getCheckedRadioButtonId();
        String countryName = ((RadioButton) findViewById(checked)).getText().toString();
        Country country = Country.valueOf(countryName);
        CountryStorage.getInstance(getApplicationContext()).setCurrentCountry(country);
        Log.d(LOD_TAG, "Close settings");
        finish();
    }
}
