package robot.speechtotext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {
    EditText schoolName;
    EditText groupName;
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        schoolName = (EditText) findViewById(R.id.schoolName);
        groupName = (EditText) findViewById(R.id.groupName);
        submitBtn = (Button) findViewById(R.id.submitbtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameValue = schoolName.getText().toString();
                String groupValue = groupName.getText().toString();
                Intent intent = new Intent(login.this, MainActivity.class);

                intent.putExtra("SCHOOLNAME", nameValue);
                intent.putExtra("GROUPNAME", groupValue);
                startActivity(intent);
            }
        });
    }
}