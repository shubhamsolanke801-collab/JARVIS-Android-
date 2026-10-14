package com.shubham.jarvis;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.content.Context;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private static final int SPEECH_REQUEST = 41;
    private static final int CAMERA_PERMISSION = 42;
    private TextToSpeech tts;
    private TextView status;
    private boolean torchOn = false;
    private CameraManager cameraManager;
    private String cameraId;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this, this);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try { for (String id : cameraManager.getCameraIdList()) { if (cameraManager.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) != null && Boolean.TRUE.equals(cameraManager.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE))) { cameraId = id; break; } } } catch (Exception ignored) {}

        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(28, 36, 28, 24); root.setBackgroundColor(0xFF07111F);
        TextView title = new TextView(this); title.setText("J.A.R.V.I.S."); title.setTextColor(0xFF35C7F4); title.setTextSize(34); title.setGravity(Gravity.CENTER); title.setLetterSpacing(0.12f);
        TextView subtitle = new TextView(this); subtitle.setText("PERSONAL ANDROID ASSISTANT"); subtitle.setTextColor(0xFF9EB6C9); subtitle.setTextSize(11); subtitle.setGravity(Gravity.CENTER); subtitle.setPadding(0, 6, 0, 22);
        status = new TextView(this); status.setText("Systems online. Tap LISTEN and give a command."); status.setTextColor(0xFFEAF6FF); status.setTextSize(16); status.setGravity(Gravity.CENTER); status.setPadding(12, 20, 12, 24);
        root.addView(title); root.addView(subtitle); root.addView(status, new LinearLayout.LayoutParams(-1, 0, 1));
        addButton(root, "🎙  LISTEN", v -> listen());
        addButton(root, "TIME & BATTERY", v -> showDeviceInfo());
        addButton(root, "OPEN YOUTUBE", v -> openUrl("https://www.youtube.com"));
        addButton(root, "OPEN CHROME / GOOGLE", v -> openUrl("https://www.google.com"));
        addButton(root, "FLASHLIGHT ON / OFF", v -> toggleTorch());
        addButton(root, "PHONE SETTINGS", v -> { try { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); } catch (Exception e) { say("I could not open settings."); } });
        TextView footer = new TextView(this); footer.setText("Starter build • Commands are processed on your phone"); footer.setTextColor(0xFF7890A4); footer.setTextSize(10); footer.setGravity(Gravity.CENTER); footer.setPadding(0, 18, 0, 0); root.addView(footer);
        ScrollView scroll = new ScrollView(this); scroll.addView(root); setContentView(scroll);
    }

    private void addButton(LinearLayout root, String label, View.OnClickListener click) {
        Button b = new Button(this); b.setText(label); b.setTextColor(0xFFEAF6FF); b.setAllCaps(false); b.setTextSize(14); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF142B40));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.setMargins(0, 5, 0, 5); root.addView(b, lp); b.setOnClickListener(click);
    }
    private void listen() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()); intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "JARVIS is listening...");
        try { startActivityForResult(intent, SPEECH_REQUEST); } catch (ActivityNotFoundException e) { say("Speech recognition is not available on this phone."); }
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK && data != null) { ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); if (results != null && !results.isEmpty()) process(results.get(0)); }
    }
    private void process(String command) {
        String c = command.toLowerCase(Locale.ROOT); status.setText("You: " + command);
        if (c.contains("time") || c.contains("battery")) showDeviceInfo();
        else if (c.contains("youtube")) openUrl("https://www.youtube.com");
        else if (c.contains("google") || c.contains("search") || c.contains("chrome")) { String q = c.replace("search", "").replace("google", "").trim(); if (q.isEmpty() || q.equals("chrome")) openUrl("https://www.google.com"); else openUrl("https://www.google.com/search?q=" + android.net.Uri.encode(q)); }
        else if (c.contains("flashlight") || c.contains("torch")) toggleTorch();
        else if (c.contains("setting")) startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        else if (c.contains("hello") || c.contains("hi jarvis")) say("Hello. At your service.");
        else if (c.contains("who are you")) say("I am JARVIS, your Android assistant starter.");
        else say("I heard: " + command + ". Try time, battery, YouTube, Google, flashlight, or settings.");
    }
    private void showDeviceInfo() {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE); int battery = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()); String msg = "Time: " + time + "\nBattery: " + battery + "%"; status.setText(msg); say("The time is " + time + ". Battery is " + battery + " percent.");
    }
    private void openUrl(String url) { try { startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))); } catch (Exception e) { say("I could not open that link."); } }
    private void toggleTorch() {
        if (cameraId == null) { say("This phone does not expose a flashlight to the app."); return; }
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) { requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION); return; }
        try { torchOn = !torchOn; cameraManager.setTorchMode(cameraId, torchOn); say(torchOn ? "Flashlight on." : "Flashlight off."); } catch (CameraAccessException | SecurityException e) { say("I could not control the flashlight."); }
    }
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { super.onRequestPermissionsResult(requestCode, permissions, grantResults); if (requestCode == CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) toggleTorch(); else if (requestCode == CAMERA_PERMISSION) say("Camera permission is needed for the flashlight."); }
    private void say(String text) { status.setText(text); if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_speech"); }
    @Override public void onInit(int result) { if (result == TextToSpeech.SUCCESS && tts != null) tts.setLanguage(Locale.getDefault()); }
    @Override protected void onDestroy() { if (tts != null) { tts.stop(); tts.shutdown(); } super.onDestroy(); }
}
