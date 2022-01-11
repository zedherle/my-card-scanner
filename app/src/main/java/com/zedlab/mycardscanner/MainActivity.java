package com.zedlab.mycardscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.camerakit.CameraKitView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button button;
    private CameraKitView cameraKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraKitView = findViewById(R.id.camera);
        button = findViewById(R.id.button_first);

        button.setOnClickListener(v -> cameraKitView.captureImage((cameraKitView, bytes) -> {
            cameraKitView.onStart();
            Bitmap result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            processImage(result);
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    void processImage(Bitmap bitmap) {

        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        recognizer.process(image).addOnSuccessListener(text -> {
            String ccPattern = "((?:(?:\\d{4}[- ]){3}\\d{4}|\\d{16}))(?![\\d])";
            String expiryPattern = "(?:0[1-9]|1[0-2])/[0-9]{2}";

            String cardNumber = extractCardInfo(ccPattern, text.getText());
            String expiry = extractCardInfo(expiryPattern, text.getText());

            if (cardNumber != null && expiry != null)
                Toast.makeText(getApplicationContext(), "Here is Card Number : " + cardNumber + " Expiry : " + expiry, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Cound not find ", Toast.LENGTH_LONG).show();

        }).addOnFailureListener(e -> Log.d("app", "" + e));

    }

    String extractCardInfo(String patternExtract, String text) {
        String found = null;
        Pattern pattern = Pattern.compile(patternExtract);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find())
            found = matcher.group(0);

        return found;
    }

}
