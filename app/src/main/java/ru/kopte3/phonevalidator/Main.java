package ru.kopte3.phonevalidator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

public class Main extends Activity {

    public static final String ACTION_CHECK_PHONE_RESULT = "ru.kopte3.phonevalidator.action.CHECK_PHONE_RESULT";

    public static final String EXTRA_VALIDATION_RESULT = "ru.kopte3.phonevalidator.extra.VALIDATION_RESULT";

    private static final String LOG_TAG = "MainActivity";

    private Gson gson;

    private EditText mPhoneNumberEditText;
    private Button mCheckButton;
    private TextView mResultTextView;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        gson = new Gson();


        mPhoneNumberEditText = (EditText) findViewById(R.id.input);
        mCheckButton = (Button) findViewById(R.id.saveButton);
        mResultTextView = (TextView) findViewById(R.id.resultTextView);

        runBroadcastReceiver();
    }

    private void runBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String responseBody = intent.getStringExtra(EXTRA_VALIDATION_RESULT);
                showValidationResult(responseBody);
            }
        };
        IntentFilter intentFilter = new IntentFilter(ACTION_CHECK_PHONE_RESULT);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void showValidationResult(String responseBody) {
        Log.d(LOG_TAG, String.format("onReceive: responseBody = %s", responseBody));
        ResponseClass response = gson.fromJson(responseBody, ResponseClass.class);

        if (response.valid) {
            mResultTextView.setText(String.format(getString(R.string.number_is_valid), response.location, response.internationalNumber, response.localNumber));
        } else {
            mResultTextView.setText(R.string.not_a_valid);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        CountryStorage storage = CountryStorage.getInstance(getApplicationContext());
        Country country = storage.getCurrentCountry();
        Log.d(LOG_TAG, String.format("Current country is %s", country.name()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    public void openSettings(MenuItem item) {
        Log.d(LOG_TAG, "openSettings");
        Intent intent = new Intent(Main.this, Settings.class);
        startActivity(intent);
    }

    public void onClickCheck(View view) {
        final String phoneNumber = mPhoneNumberEditText.getText().toString();
        CountryStorage storage = CountryStorage.getInstance(getApplicationContext());
        Country country = storage.getCurrentCountry();
        final String countryCode = country.name();

        Log.d(LOG_TAG, "Intent starting");

        Intent intent = new Intent(this, ValidateIntentService.class)
                .setAction(ValidateIntentService.ACTION_CHECK_PHONE)
                .putExtra(ValidateIntentService.EXTRA_COUNTRY_CODE, countryCode)
                .putExtra(ValidateIntentService.EXTRA_PHONE_NUMBER, phoneNumber);
        startService(intent);

        Log.d(LOG_TAG, "Intent started");
    }
}
