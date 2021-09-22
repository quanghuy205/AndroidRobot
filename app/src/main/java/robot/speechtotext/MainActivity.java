package robot.speechtotext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button Start;
    ImageButton Forward;
    ImageButton Left;
    ImageButton Right;
    ImageButton Back;
    ImageButton Stop;
    ImageButton Send;
    ActionBar actionBar;
    ImageButton speechButton;
    EditText speechText;
    EditText commandText;
    MqttAndroidClient client;
    private  static final int RECOGNIZER_RESULT = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Robot Controller");

        Intent intent = this.getIntent();
        String schoolName = intent.getStringExtra("SCHOOLNAME");
        String groupName = intent.getStringExtra("GROUPNAME");
        Log.d("name", schoolName);


        speechButton = findViewById(R.id.speechbutton);
        speechText = findViewById(R.id.speechtext);
        commandText = findViewById(R.id.commandtext);

        Start = findViewById(R.id.start);
        Forward = findViewById(R.id.btn_fwd);
        Left = findViewById(R.id.btn_left);
        Right = findViewById(R.id.btn_right);
        Back = findViewById(R.id.btn_bwd);
        Stop = findViewById(R.id.btn_stop);
        Send = findViewById(R.id.sendcommandbutton);

        Start.setOnClickListener(mLisstener);
        Forward.setOnClickListener(mLisstener);
        Left.setOnClickListener(mLisstener);
        Right.setOnClickListener(mLisstener);
        Back.setOnClickListener(mLisstener);
        Stop.setOnClickListener(mLisstener);
        speechButton.setOnClickListener(mLisstener);
        Send.setOnClickListener(mLisstener);
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://citlab.myftp.org:1883",
                        clientId);
//        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
//                clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");
                    Toast.makeText(MainActivity.this, "Connected Fail", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
    String payload;
    View.OnClickListener mLisstener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            Start.setTextColor(Color.BLACK);    Start.setSelected(false);
//            Forward.setTextColor(Color.BLACK);  Forward.setSelected(false);
//            Left.setTextColor(Color.BLACK);     Left.setSelected(false);
//            Right.setTextColor(Color.BLACK);    Right.setSelected(false);
//            Back.setTextColor(Color.BLACK);     Back.setSelected(false);
//            Stop.setTextColor(Color.BLACK);     Stop.setSelected(false);
            speechButton.setSelected(false);

            switch (id){
                case R.id.start:
                    speechText.setText("Start");
                    pub("Start");
                    Start.setTextColor(Color.RED);
                    Start.setSelected(true);
                    break;
                case R.id.btn_fwd:
                    speechText.setText("Forward");
                    pub("Forward");
//                    Forward.setTextColor(Color.RED);
//                    Forward.setSelected(true);
                    break;
                case R.id.btn_left:
                    speechText.setText("Left");
                    pub("Left");
//                    Left.setTextColor(Color.RED);
//                    Left.setSelected(true);
                    break;
                case R.id.btn_right:
                    speechText.setText("Right");
                    pub("Right");
//                    Right.setTextColor(Color.RED);
//                    Right.setSelected(true);
                    break;
                case R.id.btn_bwd:
                    speechText.setText("Back");
                    pub("Back");
//                    Back.setTextColor(Color.RED);
//                    Back.setSelected(true);
                    break;
                case R.id.btn_stop:
                    speechText.setText("Stop");
                    pub("Stop");
//                    Stop.setTextColor(Color.RED);
//                    Stop.setSelected(true);
                    break;
                case R.id.speechbutton:
                    Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hi speak something");
                    startActivityForResult(speechIntent,RECOGNIZER_RESULT);
                    speechText.setText("");
                    speechButton.setSelected(true);
                case R.id.sendcommandbutton:
                    speechText.setText(commandText.getText().toString());
                    pub(commandText.getText().toString());
                default:
                    break;
            }
        }
    };

    void pub(String content){
//        String topic = "garden1/sensor1";
        String topic = "robobits/test";
        payload = speechText.getText().toString();
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);

            client.publish(topic, message);

        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            speechText.setText(matches.get(0).toString());
            pub("chào cu");
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

}