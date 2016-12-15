package ru.kopte3.phonevalidator;

import android.content.Context;
import android.content.SharedPreferences;

public final class CountryStorage {
    private static CountryStorage INSTANCE;
    private SharedPreferences preferences;

    private CountryStorage(Context context) {
        this.preferences = context.getSharedPreferences("CountryStorage", 0);
    }

    public static synchronized CountryStorage getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CountryStorage(context.getApplicationContext());
        }

        return INSTANCE;
    }

    public Country getCurrentCountry() {
        String country = this.preferences.getString("Country", Country.RU.name());
        return Country.valueOf(country);
    }

    public void setCurrentCountry(Country country) {
        this.preferences
                .edit()
                .putString("Country", country.name())
                .apply();
    }
}
