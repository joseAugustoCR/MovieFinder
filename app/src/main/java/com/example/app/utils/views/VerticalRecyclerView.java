package com.example.app.utils.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class VerticalRecyclerView extends RecyclerView {

    private GestureDetector mGestureDetector;

    public VerticalRecyclerView(Context context) {
        super(context);
        init();
    }

    public VerticalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);

        mGestureDetector = new GestureDetector(getContext(), new HorizontalScrollDetector());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (mGestureDetector.onTouchEvent(e)) {
            return false;
        }
        return super.onInterceptTouchEvent(e);
    }

    public class HorizontalScrollDetector extends
            GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) < Math.abs(distanceX);
        }

    }}
