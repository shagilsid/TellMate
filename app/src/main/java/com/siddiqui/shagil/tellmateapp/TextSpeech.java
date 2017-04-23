package com.siddiqui.shagil.tellmateapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class TextSpeech extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    private EditText editText;
    private ImageButton mic;
    private TextView countText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_speech);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tts=new TextToSpeech(this, this);
        editText=(EditText)findViewById(R.id.multiAutoCompleteTextView);
        editText.addTextChangedListener(TextEditorWatcher);
        countText=(TextView)findViewById(R.id.countWords);

        mic=(ImageButton) findViewById(R.id.micImageView);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });



    }


    private final TextWatcher TextEditorWatcher=new TextWatcher() {
        private static final int MAX_WORDS=50;
        boolean cancel=false;
        View focusView=null;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            int wordsLength = countWords(s.toString());// words.length;
            // count == 0 means a new word is going to start
            if (count == 0 && wordsLength >= MAX_WORDS) {
                setCharLimit(editText, editText.getText().length());
                editText.setError("Words Limit 50");
                cancel=true;
                focusView=editText;
            } else {
                removeFilter(editText);
            }
            if (cancel){
            focusView.requestFocus();
            }

            countText.setText(String.valueOf(50-wordsLength));
        }


        @Override
        public void afterTextChanged(Editable s) {}
    };

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private InputFilter filter;

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[] { filter });
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

    public boolean onCreateOptionsMenu(Menu  menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent=new Intent(TextSpeech.this,Settings.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onInit(int status) {
        if (status==TextToSpeech.SUCCESS){
            int result=tts.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                speak();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    public void speak() {
        String text= editText.getText().toString();
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        if (tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void SpeechToText(View view) {
        Intent intent=new Intent(TextSpeech.this,SpeechText.class);
        startActivity(intent);
    }
}
