package com.infosysit.rainforest;

import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MovableFloatingActionButton extends FloatingActionButton implements View.OnTouchListener {

    private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.

    private float downRawX, downRawY;
    private float dX, dY;

    public MovableFloatingActionButton(Context context) {
        super(context);
        init();
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent){

        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            Log.d("EVENNTACTION","ACTION DOWN");
            downRawX = motionEvent.getRawX();
            downRawY = motionEvent.getRawY();
            Log.d("EVENNTACTION","ACTION DOWN: "+downRawX+" "+downRawY);
            dX = view.getX() - downRawX;
            Log.d("DragEventCalled","X: "+dX);
            dY = view.getY() - downRawY;
            Log.d("DragEventCalled","Y: "+dY);
            return true; // Consumed

        }
        else if (action == MotionEvent.ACTION_MOVE) {
            Log.d("EVENNTACTION","ACTION MOVE");

            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();

            View viewParent = (View)view.getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = motionEvent.getRawX() + dX;
            newX = Math.max(0, newX); // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(parentWidth - viewWidth, newX); // Don't allow the FAB past the right hand side of the parent

            float newY = motionEvent.getRawY() + dY;
            newY = Math.max(0, newY); // Don't allow the FAB past the top of the parent
            newY = Math.min(parentHeight - viewHeight, newY); // Don't allow the FAB past the bottom of the parent
            Log.d("EVENNTACTION","ACTION MOVE "+newX+" "+newY);
            view.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();

            return true; // Consumed

        }
        else if (action == MotionEvent.ACTION_UP) {
            Log.d("EVENNTACTION","ACTION UP");

            float upRawX = motionEvent.getRawX();
            float upRawY = motionEvent.getRawY();

            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;

            Log.d("EVENNTACTION","ACTION UP "+upRawX+" "+upRawY);


            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                return performClick();
            }
            else { // A drag
                return true; // Consumed
            }

        }
        else {
            return super.onTouchEvent(motionEvent);
        }

    }


}

//class dropListener implements View.OnDragListener {
//
//    View draggedView;
//    TextView dropped;
//
//    @Override
//    public boolean onDrag(View v, DragEvent event) {
//        Log.d("DragEventCalled","Event: "+event.getAction());
//        switch (event.getAction()) {
//            case DragEvent.ACTION_DRAG_STARTED:
//                draggedView = (View) event.getLocalState();
//                dropped = (TextView) draggedView;
//                draggedView.setVisibility(View.INVISIBLE);
//                break;
//            case DragEvent.ACTION_DRAG_ENTERED:
//                break;
//            case DragEvent.ACTION_DRAG_EXITED:
//                break;
//            case DragEvent.ACTION_DROP:
//
//                TextView dropTarget = (TextView) v;
//                dropTarget.setText("DRop here to close this");
//                break;
//            case DragEvent.ACTION_DRAG_ENDED:
//                break;
//            default:
//                break;
//        }
//        return true;
//    }
//
//}

