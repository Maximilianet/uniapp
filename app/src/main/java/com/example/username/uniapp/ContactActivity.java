package com.example.username.uniapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.IllegalFormatCodePointException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener{
    Button sendMessage, addAttachment;
    EditText your_name, your_email, your_message;
    Uri URI = null;
    private static final int PICK_FROM_GALLERY = 101;
    int columnIndex;
    String attachmentFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        your_name = (EditText) findViewById(R.id.your_name);
        your_email = (EditText) findViewById(R.id.your_email);
        your_message = (EditText) findViewById(R.id.your_message);

        sendMessage = findViewById(R.id.send_errors);
        addAttachment = findViewById(R.id.add_attachment);

        sendMessage.setOnClickListener(this);
        addAttachment.setOnClickListener(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

        @Override
        public void onClick(View v) {
            if (v == addAttachment) {
                openGallery();
            }

            if (v == sendMessage) {
                try {

                    String name = your_name.getText().toString();
                    String email = your_email.getText().toString();
                    String message = your_message.getText().toString();

                    boolean onError = false;
                    if (TextUtils.isEmpty(name)) {
                        onError = true;
                        your_name.setError("Введите своё имя");
                        your_name.requestFocus();
                        return;
                    }

                    if (!isValidEmail(email)) {
                        onError = true;
                        your_email.setError("Неверная почта");
                    }

                    if (TextUtils.isEmpty(message)) {
                        onError = true;
                        your_message.setError("Введите своё сообщение");
                        your_message.requestFocus();
                        return;
                    }

                    if (!onError) {
                        Intent sendEmail = new Intent(android.content.Intent.ACTION_SEND);

                        sendEmail.setType("plain/text");
                        sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"morozovnet85@gmail.com"});
                        sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,
                                "Имя:" + name + '\n' + "Почта:" + email + '\n' + "Сообщение:" + '\n' + message);

                        if (URI != null) {
                            sendEmail.putExtra(Intent.EXTRA_STREAM, URI);
                        }

                        startActivity(Intent.createChooser(sendEmail, "Отправка сообщения"));
                    }
                } catch (Throwable t){
                    Toast.makeText(this,t.toString(), Toast.LENGTH_LONG);
                }
            }
        }

    //Возращение на основную страницу
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //Правильно ли введен email
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            attachmentFile = cursor.getString(columnIndex);
            Log.e("Attachment Path:", attachmentFile);
            URI = Uri.parse("file://" + attachmentFile);
            cursor.close();
        }
    }

    public void openGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(
                Intent.createChooser(intent, "Complete action using"),
                PICK_FROM_GALLERY);

    }
}
