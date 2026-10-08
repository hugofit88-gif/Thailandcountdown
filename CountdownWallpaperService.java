package com.hugofit.thailandcountdown;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.Locale;

public class CountdownWallpaperService extends WallpaperService {
    @Override public Engine onCreateEngine() { return new CountdownEngine(); }

    private class CountdownEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean visible;
        private final Runnable drawRunner = this::drawFrame;

        @Override public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) drawFrame(); else handler.removeCallbacks(drawRunner);
        }

        @Override public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawFrame();
        }

        @Override public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(drawRunner);
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) drawCountdown(canvas);
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            if (visible) handler.postDelayed(drawRunner, 1000);
        }

        private void drawCountdown(Canvas canvas) {
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            canvas.drawColor(Color.rgb(5, 7, 10));
            paint.setTextAlign(Paint.Align.CENTER);

            paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD));
            paint.setColor(Color.WHITE);
            paint.setTextSize(Math.max(64f, w * 0.14f));
            canvas.drawText("THAILAND", w / 2f, h * 0.35f, paint);

            paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
            paint.setColor(Color.rgb(150, 160, 170));
            paint.setTextSize(Math.max(28f, w * 0.05f));
            canvas.drawText("3 DECEMBER 2026", w / 2f, h * 0.41f, paint);

            long diff = MainActivity.targetMillis() - System.currentTimeMillis();
            if (diff <= 0) {
                paint.setColor(Color.WHITE);
                paint.setTextSize(Math.max(42f, w * 0.09f));
                canvas.drawText("YOU MADE IT 🇹🇭", w / 2f, h * 0.55f, paint);
                return;
            }

            long totalSeconds = diff / 1000;
            long days = totalSeconds / 86400;
            long hours = (totalSeconds % 86400) / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            paint.setColor(Color.WHITE);
            paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD));
            paint.setTextSize(Math.max(60f, w * 0.13f));
            canvas.drawText(String.format(Locale.US, "%d DAYS", days), w / 2f, h * 0.54f, paint);
            paint.setTextSize(Math.max(42f, w * 0.085f));
            canvas.drawText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds), w / 2f, h * 0.62f, paint);
            paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
            paint.setColor(Color.rgb(95, 105, 115));
            paint.setTextSize(Math.max(24f, w * 0.045f));
            canvas.drawText("UNTIL THE MOVE", w / 2f, h * 0.69f, paint);
        }
    }
}
