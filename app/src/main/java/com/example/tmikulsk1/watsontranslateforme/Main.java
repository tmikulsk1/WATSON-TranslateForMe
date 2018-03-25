package com.example.tmikulsk1.watsontranslateforme;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * IMPORTANT: THIS IS ONLY A STUDY OF JSON/HTTP DATA RETRIEVAL
 * IS NOT FOR COMMERCIAL PURPOSES OR RELATED
 */

public class Main extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<AvailableLanguages>> {

    //JSON URL RETRIEVAL
    private static final String WATSON_AVAILABLE_LANGUAGES_URL = "https://watson-api-explorer.mybluemix.net/language-translator/api/v2/identifiable_languages";
    private String WATSON_TEXT_TO_TRANSLATE;

    // ADAPTERS TO SPINNER - AVAILABLE LANGUAGES
    private AvaliableLanguagesAdapter mAdapterIN;
    private AvaliableLanguagesAdapter mAdapterOUT;

    //IF 1: FIRST LOADER INIT, IF !1: LOADER RESET
    // 0 ONLY IF THE APP STARTS WITHOUT INTERNET CONNECTION
    private int CHECK_NUMBER_LOADER = 0;

    //INIT UI COMPONENTS
    private Spinner languageIn;
    private Spinner languageOut;
    private EditText inputText;
    private EditText outputText;
    private RelativeLayout splash_screen;
    private TextView internet_warning;
    Button translate;

    //JSON DATA COMPLEMENT
    private String translateFrom;
    private String translateTo;
    private String finalTranslation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INIT UI COMPONENTS
        inputText = findViewById(R.id.input_text);
        outputText = findViewById(R.id.output_text);
        languageIn = findViewById(R.id.language_in);
        languageOut = findViewById(R.id.language_out);
        splash_screen = findViewById(R.id.splash_screen);
        internet_warning = findViewById(R.id.splash_warning);
        translate = findViewById(R.id.translate);

        //INIT ADAPTERS - LANGUAGE 'FROM'
        mAdapterIN = new AvaliableLanguagesAdapter(this, new ArrayList<AvailableLanguages>());
        languageIn.setAdapter(mAdapterIN);

        //INIT ADAPTERS - LANGUAGE 'TO'
        mAdapterOUT = new AvaliableLanguagesAdapter(this, new ArrayList<AvailableLanguages>());
        languageOut.setAdapter(mAdapterOUT);

        //RETRIEVE AVAILABLE LANGUAGES
        loadFirstLoader();

        splash_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // IF IS NOT CONNECTED, AFTER A TAP IN THE UI, TRY TO INITIATE THE FIRST LOADER
                if (CHECK_NUMBER_LOADER == 0) {

                    loadFirstLoader();

                }

            }
        });

        languageIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //STORE USER CHOICE LANGUAGE
                AvailableLanguages currentLanguageIN = mAdapterIN.getItem(i);
                translateFrom = currentLanguageIN.getName();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        languageOut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //STORE USER CHOICE LANGUAGE
                AvailableLanguages currentLanguageOUT = mAdapterOUT.getItem(i);
                translateTo = currentLanguageOUT.getName();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CREATE THE SECOND LOADER - TRANSLATION LOADER
                LoaderManager.LoaderCallbacks<String> loaderCallbacks = new LoaderManager.LoaderCallbacks<String>() {
                    @Override
                    public Loader<String> onCreateLoader(int i, Bundle bundle) {

                        //AFTER VERIFIES THE CONNECTION, START LOADER CREATION
                        splash_screen.setVisibility(View.INVISIBLE);
                        CHECK_NUMBER_LOADER += 1;
                        String IN = inputText.getText().toString();
                        WATSON_TEXT_TO_TRANSLATE = null;
                        WATSON_TEXT_TO_TRANSLATE = "https://watson-api-explorer.mybluemix.net/language-translator/api/v2/translate?text=" + IN + "&source=" + translateFrom + "&target=" + translateTo;

                        return new TranslationLoader(getApplicationContext(), WATSON_TEXT_TO_TRANSLATE);

                    }

                    @Override
                    public void onLoadFinished(Loader<String> loader, String s) {

                        //AFTER FINISHED THE LOADER SHOW THE RESULTS
                        outputText.setText("");
                        finalTranslation = null;
                        finalTranslation = s;
                        outputText.setText(finalTranslation);
                        splash_screen.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onLoaderReset(Loader<String> loader) {

                        //CLEAR COMPONENTS
                        finalTranslation = null;
                        outputText.setText("");

                    }

                };

                //AFTER CREATE THE LOADER OR INITIATE, VERIFIES IF IS/ISN'T THE FIRST CONNECTION AND IF IT'S CONNECTED
                if (CHECK_NUMBER_LOADER != 1) {

                    if (isConnected()) {

                        getLoaderManager().restartLoader(2, null, loaderCallbacks);

                    } else {

                        //IF NO CONNECTION, SPLASH WARNING APPEARS
                        splashWarning();
                        CHECK_NUMBER_LOADER = 2;

                    }

                } else {

                    getLoaderManager().initLoader(2, null, loaderCallbacks);
                }

            }
        });

    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {

        //CREATE FIRST LOADER
        return new AvailableLanguagesLoader(this, WATSON_AVAILABLE_LANGUAGES_URL);

    }

    @Override
    public void onLoadFinished(Loader<List<AvailableLanguages>> loader, List<AvailableLanguages> availableLanguages) {


        //AFTER RETRIEVING DATA, SHOW OPTIONS IN THE SPINNER
        mAdapterIN.addAll(availableLanguages);
        mAdapterOUT.addAll(availableLanguages);

        //WAIT - LIKE A SPLASH SCREEN
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            //DO NOTHING
        }

        //MAIN UI APPEARS
        splash_screen.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<List<AvailableLanguages>> loader) {

        //CLEAR
        mAdapterOUT.clear();
        mAdapterIN.clear();
    }

    /**
     * Function to initialize the first loader,
     * which gets the available languages and
     * shows at the Spinner Object
     * <p>
     * Only starts if the user is connected!
     */
    public void loadFirstLoader() {

        if (isConnected()) {

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, this);

            CHECK_NUMBER_LOADER = 1;


        } else {

            splashWarning();

        }

    }

    /**
     * Every time that the system verifies
     * that there's no connection, the
     * splash warning appears with a info text.
     */
    public void splashWarning() {

        splash_screen.setVisibility(View.VISIBLE);
        internet_warning.setText("You are not connected! \nPlease, verify your connection \n and tap to try again.");

    }

    /**
     * Function to verify the connection status.
     *
     * @return TRUE if is connect or FALSE if is not.
     */
    public Boolean isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {

            return true;

        } else {

            return false;

        }
    }
}
