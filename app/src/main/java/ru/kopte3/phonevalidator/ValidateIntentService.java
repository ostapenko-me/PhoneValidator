package ru.kopte3.phonevalidator;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ValidateIntentService extends IntentService {

    public static final String ACTION_CHECK_PHONE = "ru.kopte3.phonevalidator.action.CHECK_PHONE";

    public static final String EXTRA_PHONE_NUMBER = "ru.kopte3.phonevalidator.extra.PHONE_NUMBER";
    public static final String EXTRA_COUNTRY_CODE = "ru.kopte3.phonevalidator.extra.COUNTRY_CODE";

    public static final String MASHAPE_URL = "https://neutrinoapi-phone-validate.p.mashape.com/phone-validate";
    public static final String X_MASHAPE_KEY_HEADER_NAME = "X-Mashape-Key";
    public static final String X_MASHAPE_KEY_HEADER_VALUE = "2ndV4pZBnSmsh0AmmnTmk3R9GmBgp1HhTUrjsncQwIJ2Il9tcG";
    private static final String LOG_TAG = "ValidateIntentService";


    public ValidateIntentService() {
        super("ValidateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.d(LOG_TAG, String.format("New intent with action %s", action));
            if (ACTION_CHECK_PHONE.equals(action)) {
                final String phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
                final String countryCode = intent.getStringExtra(EXTRA_COUNTRY_CODE);
                Log.d(LOG_TAG, String.format("New validation request: %s (%s)", phoneNumber, countryCode));

                try {
                    handleCheckPhoneNumber(phoneNumber, countryCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleCheckPhoneNumber(String phoneNumber, String countryCode) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("country-code", countryCode)
                .add("number", phoneNumber)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(MASHAPE_URL)
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader(X_MASHAPE_KEY_HEADER_NAME, X_MASHAPE_KEY_HEADER_VALUE)
                .build();

        Log.d(LOG_TAG, "Executing request");

        Response response = client.newCall(request).execute();
        final String responseBody = response.body().string();

        Log.d(LOG_TAG, String.format("Request success, response body = %s", responseBody));


        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Main.ACTION_CHECK_PHONE_RESULT);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(Main.EXTRA_VALIDATION_RESULT, responseBody);
        sendBroadcast(broadcastIntent);
    }
}
