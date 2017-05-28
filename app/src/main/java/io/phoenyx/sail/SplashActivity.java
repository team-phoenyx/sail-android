package io.phoenyx.sail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.phoenyx.sail.models.QuoteResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    DBHandler dbHandler;
    String quote;

    SharedPreferences sharedPreferences;
    InputStream inputStream;
    BufferedReader bufferedReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(this);

        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);

        /*
        if (sharedPreferences.getBoolean("firstrun", true)) {
            //ADD QUOTES TO DATABASE
            try {
                inputStream = getAssets().open("quotes.txt");
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = bufferedReader.readLine();
                String currentQuote = "";
                while (line != null) {
                    if (line.equals("%")) {
                        dbHandler.createQuote(currentQuote);
                        currentQuote = "";
                    } else {
                        currentQuote += line;
                    }
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            sharedPreferences.edit().putBoolean("notifyBeforeDiscard", true).commit();
            sharedPreferences.edit().putBoolean("notifyBeforeDelete", true).commit();
            sharedPreferences.edit().putBoolean("firstrun", false).commit();
        }

        quote = dbHandler.getRandomQuote();

        if (quote.contains("        ")) {
            quote = quote.replace("        ", "\n");
        }

        while (quote.length() > 60) {
            quote = dbHandler.getRandomQuote();
        }
        */

        SailService sailService = RetrofitClient.getClient("quotes.rest/").create(SailService.class);
        sailService.getQOD().enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                if (response.isSuccessful()) {
                    quote = response.body().getContents().getQuotes().get(0).getQuote() + "\n" + "    ";
                } else {
                    quote = "Sorry, quotes are currently unavailable";
                }
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                quote = "Sorry, quotes are currently unavailable";
            }
        });

        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startIntent.putExtra("quote", quote);
        startActivity(startIntent);
        finish();
    }
}
