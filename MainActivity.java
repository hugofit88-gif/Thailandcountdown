package com.hugofit.thailandcountdown;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView countdown;

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            countdown.setText(formatRemaining());
            handler.postDelayed(this, 1000);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48, 48, 48, 48);
        root.setBackgroundColor(Color.rgb(5, 7, 10));

        TextView title = new TextView(this);
        title.setText("THAILAND");
        title.setTextColor(Color.WHITE);
        title.setTextSize(42);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.12f);

        TextView subtitle = new TextView(this);
        subtitle.setText("3 DECEMBER 2026");
        subtitle.setTextColor(Color.rgb(150, 160, 170));
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 14, 0, 32);

        countdown = new TextView(this);
        countdown.setTextColor(Color.WHITE);
        countdown.setTextSize(28);
        countdown.setGravity(Gravity.CENTER);
        countdown.setPadding(0, 0, 0, 42);

        Button setWallpaper = new Button(this);
        setWallpaper.setText("SET LIVE WALLPAPER");
        setWallpaper.setOnClickListener(v -> openWallpaperPreview());

        root.addView(title, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(subtitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(countdown, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(setWallpaper, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(root);
    }

    private void openWallpaperPreview() {
        try {
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, CountdownWallpaperService.class));
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
        }
    }

    private String formatRemaining() {
        long diff = targetMillis() - System.currentTimeMillis();
        if (diff <= 0) return "YOU MADE IT 🇹🇭";
        long totalSeconds = diff / 1000;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%d DAYS\n%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    static long targetMillis() {
        Calendar target = Calendar.getInstance();
        target.set(Calendar.YEAR, 2026);
        target.set(Calendar.MONTH, Calendar.DECEMBER);
        target.set(Calendar.DAY_OF_MONTH, 3);
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        return target.getTimeInMillis();
    }

    @Override protected void onResume() { super.onResume(); handler.post(ticker); }
    @Override protected void onPause() { super.onPause(); handler.removeCallbacks(ticker); }
}
