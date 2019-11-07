package com.zaidimarvels.voiceapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.haozhang.lib.AnimatedRecordingView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecog;
    AnimatedRecordingView mRecordingView;

    List<Message> messagesList = new ArrayList<>();
    RecyclerView messageRecyclerView;
    MessageAdapter messageAdapter;
    String contactName = "", type = "";
    private static final int REQUEST_CALL = 2;

    public static final int REQUEST_READ_CONTACTS = 79;
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messageRecyclerView = findViewById(R.id.chatRV);

        mRecordingView = (AnimatedRecordingView) findViewById(R.id.recording);
        LinearLayoutManager layoutManager=
                new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,true);
        //layoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(layoutManager);
        /*// start recording animation
        mRecordingView.start();
        // start loading animation
        mRecordingView.loading();
        // start finished animation
        mRecordingView.stop();*/

        //FloatingActionButton fab = findViewById(R.id.fab);
        mRecordingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.RECORD_AUDIO)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    mRecordingView.start();
                    // start loading animation
                    mRecordingView.loading();
                    // Permission has already been granted
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

                    speechRecog.startListening(intent);




                }
            }
        });


        messagesList.clear();
        MakeRecyclerView(messagesList);
        initializeTextToSpeech();
        initializeSpeechRecognizer();
    }



    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {

            speechRecog = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecog.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> result_arr = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(result_arr.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void showMessage(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    private void processResult(String result_message) {
        result_message = result_message.toLowerCase();

//        Handle at least four sample cases
        mRecordingView.stop();
        Message message = new Message("user", result_message);
        messagesList.add(message);
        MakeRecyclerView(messagesList);
//        First: What is your Name?
//        Second: What is the time?
//        Third: Is the earth flat or a sphere?
//        Fourth: Open a browser and open url

        if(result_message.contains("what")){
            if(result_message.contains("your name")){
                speak("My Name is Mobi Assist. Nice to meet you!");
            }
            if (result_message.contains("time")){
                String time_now = DateUtils.formatDateTime(this, new Date().getTime(),DateUtils.FORMAT_SHOW_TIME);
                speak("The time is now: " + time_now);
            }
            if (result_message.contains("love")){
                speak("Love is wonderful.");
            }
        }else if (result_message.contains("love")){
            speak("Love is wonderful.");
        }else if (result_message.contains("shut")){
            speak("You are very rude! Take a chill pill.");
        }else if(result_message.contains("prime minister") && result_message.contains("pakistan")){
            speak("Imran Khan is the prime minister of pakistan. Ohhh, I am sorry, new Pakistan..");
        }else if(result_message.contains("pakistan")){
            speak("Pakistan is our beloved country...");
        }else if(result_message.contains("how") && result_message.contains("are")){
            speak("I'm fine! What about you? ");
        }else if (result_message.contains("hi")){
            speak("Hey, How are you? By the way! i'm fine...");
        }
        else if (result_message.contains("earth")){
            speak("Don't be silly, The earth is a sphere. As are all other planets and celestial bodies");
        } else if (result_message.contains("browser")){
            speak("Opening a browser master.");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(intent);
        } else if(result_message.contains("sms") || result_message.contains("message")){
            setContactName("");
            contactName = removeWord(result_message, "send");
            contactName = removeWord(contactName, "sms");
            contactName = removeWord(contactName, "to");
            contactName = removeWord(contactName, "message");
            setType("sms");
            setContactName(contactName);
            searchContacts(contactName);
        } else if(result_message.contains("call")){
            setContactName("");
            setType("call");
            contactName = removeWord(result_message, "call");
            setContactName(contactName);
            showMessage(contactName);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                searchContacts(getContactName());
            } else {
                requestPermission();
            }


        } else if(result_message.contains("alarm")){
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);

            String regex = "(\\d{2}:\\d{2})";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(result_message);
            if (matcher.find()) {
                int start = matcher.start(); // start index of match
                int end = matcher.end(); // end index of match
                String result = matcher.group(1);
                showMessage(result);

                String temp = result.substring(0,2);
                intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(temp));
                temp = result.substring(result.lastIndexOf(":") + 1);
                intent.putExtra(AlarmClock.EXTRA_MINUTES,Integer.parseInt(temp));
                startActivity(intent);

            }else{
                regex = "(\\d{1}:\\d{2})";
                pattern = Pattern.compile(regex);
                matcher = pattern.matcher(result_message);
                if (matcher.find()) {
                    int start = matcher.start(); // start index of match
                    int end = matcher.end(); // end index of match
                    String result = matcher.group(1);
                    showMessage(result);

                    //int nums = Integer.parseInt(result.split("(?=\\D)")[0]);
                    String temp = result.substring(0,1);
                    intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(temp));
                    temp = result.substring(result.lastIndexOf(":") + 1);
                    intent.putExtra(AlarmClock.EXTRA_MINUTES,Integer.parseInt(temp));
                    startActivity(intent);
                }
            }


        } else if(result_message.contains("map") || result_message.contains("maps")){
            speak("Opening Google maps");
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps"));
            startActivity(intent);
        }else if(result_message.contains("setting") || result_message.contains("settings")){
            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        }else if(result_message.contains("camera")){
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        }else if(result_message.contains("music") || result_message.contains("song")){
            /*Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            speak("Enjoy music...");
            if (mAudioManager == null)
                mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
            mAudioManager.dispatchMediaKeyEvent(event);
        }else{
            speak("I don't understand, What you're saying!");
        }
    }

    private void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    //To get contact name from voice to text

    public static String removeWord(String string, String word)
    {

        // Check if the word is present in string
        // If found, remove it using removeAll()
        if (string.contains(word)) {

            // To cover the case
            // if the word is at the
            // beginning of the string
            // or anywhere in the middle
            String tempWord = word + " ";
            string = string.replaceAll(tempWord, "");

            // To cover the edge case
            // if the word is at the
            // end of the string
            tempWord = " " + word;
            string = string.replaceAll(tempWord, "");
        }

        // Return the resultant string
        return string.trim();
    }

    private void initializeTextToSpeech() {

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0 ){
                    Toast.makeText(MainActivity.this, getString(R.string.tts_no_engines),Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    tts.setLanguage(Locale.US);
                    speak("Hello there, I am Mobi Assist. How may i help you?");
                }
            }
        });
    }

    private void speak(String message) {
        Message message1 = new Message("bot", message);
        messagesList.add(message1);
        MakeRecyclerView(messagesList);
        if(Build.VERSION.SDK_INT >= 21){
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Reinitialize the recognizer and tts engines upon resuming from background such as after openning the browser
        initializeSpeechRecognizer();
        initializeTextToSpeech();
    }*/

    private void MakeRecyclerView(List<Message> messagesList) {
        Collections.sort(messagesList, dateSort);
        //Collections.reverse(messagesList);
        messageAdapter = new MessageAdapter(getApplicationContext(), messagesList);
        messageRecyclerView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
    }
    public static Comparator<Message> dateSort = new Comparator<Message>() {

        public int compare(Message s1, Message s2) {

            Long no1 = (Long) s1.getCurrentTime();
            Long no2 = (Long) s2.getCurrentTime();

            /*For ascending order*/
            return (int) (no2-no1);

            /*For descending order*/
            //rollno2-rollno1;
        }};

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                searchContacts(contactName);
            }/* else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }*/
        }else if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                searchContacts(contactName);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void searchContacts(String contactName) {
        List<Contact> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME)).trim();
                String cnt =cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                // nameList.add(name+" : "+cnt);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //nameList.add(name+" : "+phoneNo);
                        String stringWithoutSpaces = convertToLowerCase(name).replaceAll("\\s+", "");
                        if(stringWithoutSpaces.contains(contactName)){
                            if(getType().equals("call")){
                                MakePhoneCall("tel:"+phoneNo);
                                speak("Calling "+name);
                                break;
                            }else{
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setData(Uri.parse("sms:"+phoneNo));
                                sendIntent.putExtra("sms_body", "");
                                startActivity(sendIntent);
                                speak("Opening App to message "+name);
                                break;
                            }

                        }
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        //return nameList;

    }

    private String convertToLowerCase(String name) {
        return  name.toLowerCase();
    }

    public void MakePhoneCall(String s){
        /*Intent callIntent =new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(s));
        startActivity(callIntent);*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CALL_PHONE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

                }
            } else {

                Intent callIntent =new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(s));
                startActivity(callIntent);
            }
        }else{
            Intent callIntent =new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(s));
            startActivity(callIntent);
        }
    }
}
