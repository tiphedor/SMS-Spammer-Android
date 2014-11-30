package io.tiph.smsspammer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button btnContactPicker, btnStart, btnStop;
    EditText txtPhoneNb,txtMessage;

    private final int ACTIVITYRESULT_CONTACTPICKER = 1000;

    private Timer spammingTimer;
    private boolean spamming;
    private SmsManager smsManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnContactPicker = (Button) findViewById(R.id.btnContactPicker);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        txtPhoneNb = (EditText) findViewById(R.id.txtPhoneNb);
        txtMessage = (EditText) findViewById(R.id.txtMessage);

        btnContactPicker.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        smsManager = SmsManager.getDefault();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Cursor cursor = null;
            String phone = "";
            try {
                Uri result = data.getData();
                String id = result.getLastPathSegment();
                cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
                int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                if (cursor.moveToFirst()) {
                    phone = cursor.getString(phoneIdx);
                }
            } catch (Exception e) {
                // err
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                txtPhoneNb.setText(phone);
            }
        }
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == btnContactPicker.getId()) {
            Intent contactsIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
            startActivityForResult(contactsIntent, ACTIVITYRESULT_CONTACTPICKER);
        } else if(v.getId() == btnStart.getId()) {
            initTimer();
        } else if(v.getId() == btnStop.getId()) {
            cancelTimer();
        }
    }

    public void cancelTimer() {
        if(spammingTimer != null && spamming) {
            spammingTimer.cancel();
            spammingTimer = null;
            spamming = false;
        }
    }

    public void initTimer() {
        if(spammingTimer == null && !spamming) {
            spamming = true;
            spammingTimer = new Timer();
            spammingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    smsManager.sendTextMessage(txtPhoneNb.getText().toString(), null, txtMessage.getText().toString(), null, null);
                }
            },0, 300);
        }
    }
}
