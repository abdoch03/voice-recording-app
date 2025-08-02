package com.example.voice_memo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

public class WaveformView extends View {

    private static final int MAX_AMPLITUDE_HISTORY = 100;
    private final LinkedList<Integer> amplitudes = new LinkedList<>();
    private final Paint linePaint = new Paint();

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint.setColor(Color.CYAN);
        linePaint.setStrokeWidth(6f);
        linePaint.setAntiAlias(true);
    }

    public void addAmplitude(int amplitude) {
        if (amplitudes.size() > MAX_AMPLITUDE_HISTORY) {
            amplitudes.removeFirst();
        }

        // Boost amplitude for better visibility if needed (try x3)
        amplitudes.add(amplitude * 3);
        invalidate(); // redraw
    }


    public void clear() {
        amplitudes.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float space = width / (float) MAX_AMPLITUDE_HISTORY;

        float x = 0;
        for (Integer amp : amplitudes) {
            float normalized = Math.min(amp / 32767f, 1f); // Clamp to [0,1]
            float lineHeight = normalized * height;
            float startY = height / 2 - lineHeight / 2;
            float endY = height / 2 + lineHeight / 2;
            canvas.drawLine(x, startY, x, endY, linePaint);
            x += space;
        }
    }

}
