package com.wolfpack.ajays.slider;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


public class Slider extends View {
    public static final int CIRCLE_RADIUS_LARGE = 124;
    public static final int CIRCLE_RADIUS_MEDIUM = 100;
    public static final int CIRCLE_RADIUS_SMALL = 72;
    public static final String CIRCLE_COLOUR = "#103f72af";

    private int mLargeRadius;
    private int mMediumRadius;
    private int mSmallRadius;
    private Paint mPaint;
    private Paint mBitmapAlphaPaint;
    private Bitmap mCurrentBitmap;
    private int mCurrentBitmapStartX;
    private int mCurrentBitmapEndX;
    private int mNextBitmapIndex;
    private Bitmap mNextBitmap;
    private int mNextBitmapStartX;
    private int mNextBitampEndX;
    private int mBitmapStartY;
    private int mBitmapEndY;
    private int currentBitmapAlpha = 255;

    private boolean leftSlide = true;
    private int mBitmapSlideOffset;

    private Rect mCurrentBitampRect;
    private Rect mNextBitmapRect;

    private int[] bitmaps;
    private Resources resources;
    private int maxSlideOffsetValue;

    private Path mPath;
    private boolean sliding;
    private int width;
    private int height;
    private int cx;
    private int cy;
    private int mCircleColour;

    public Slider(Context context) {
        super(context);
        init(context);
    }

    public Slider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Slider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        resources = context.getResources();
        mLargeRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS_LARGE, resources.getDisplayMetrics());
        mMediumRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS_MEDIUM, resources.getDisplayMetrics());
        mSmallRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS_SMALL, resources.getDisplayMetrics());
        mCircleColour = Color.parseColor(CIRCLE_COLOUR);
        bitmaps = new int[0];
        mPath = new Path();
        mCurrentBitampRect = new Rect(0, 0, 0, 0);
        mNextBitmapRect = new Rect(0, 0, 0, 0);
        mPaint = new Paint();
        mBitmapAlphaPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mCircleColour);
        mPaint.setStyle(Paint.Style.FILL);
    }


    public void setImageResource(int[] imageResourceIds) {
        bitmaps = imageResourceIds;
        if (bitmaps.length > 0) {
            mCurrentBitmap = getBitmapFromId(bitmaps[0]);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        cx = width / 2;
        cy = height / 2;
        mPaint.setColor(mCircleColour);
        canvas.drawCircle(cx, cy, mLargeRadius, mPaint);
        canvas.drawCircle(cx, cy, mMediumRadius, mPaint);
        canvas.drawCircle(cx, cy, mSmallRadius, mPaint);
        //clip bitmap drawing to smaller circle
        mPath.addCircle(cx, cy, mMediumRadius, Path.Direction.CCW);
        canvas.clipPath(mPath);
        int padding = mLargeRadius - mSmallRadius;
        mCurrentBitmapStartX = padding;
        mCurrentBitmapEndX = 2 * mSmallRadius + padding;
        mNextBitmapStartX = mCurrentBitmapEndX;
        mNextBitampEndX = mCurrentBitmapEndX + (2 * mSmallRadius);
        mBitmapStartY = padding;
        mBitmapEndY = 2 * mSmallRadius + padding;
        maxSlideOffsetValue = 2 * mSmallRadius;
        //drawing bitmaps
        if (sliding) {
            if (leftSlide) {
                //current bitmap will disappear
                mBitmapAlphaPaint.setAlpha(255 - currentBitmapAlpha);
                mCurrentBitampRect.set(mCurrentBitmapStartX - mBitmapSlideOffset, mBitmapStartY, mCurrentBitmapEndX - mBitmapSlideOffset, mBitmapEndY);
                if (mCurrentBitmap != null)
                    canvas.drawBitmap(mCurrentBitmap, null, mCurrentBitampRect, mBitmapAlphaPaint);
                if (mNextBitmap != null) {
                    //next bitmap will appear
                    mBitmapAlphaPaint.setAlpha(currentBitmapAlpha);
                    mNextBitmapRect.set(mNextBitmapStartX - mBitmapSlideOffset, mBitmapStartY, mNextBitampEndX - mBitmapSlideOffset, mBitmapEndY);
                    canvas.drawBitmap(mNextBitmap, null, mNextBitmapRect, mBitmapAlphaPaint);
                }
            } else {
                //current bitmap will disappear
                mBitmapAlphaPaint.setAlpha(255 - currentBitmapAlpha);
                mCurrentBitampRect.set(mCurrentBitmapStartX + mBitmapSlideOffset, mBitmapStartY, mCurrentBitmapEndX + mBitmapSlideOffset, mBitmapEndY);
                if (mCurrentBitmap != null)
                    canvas.drawBitmap(mCurrentBitmap, null, mCurrentBitampRect, mBitmapAlphaPaint);
                if (mNextBitmap != null) {
                    //next bitmap will appear
                    mBitmapAlphaPaint.setAlpha(currentBitmapAlpha);
                    mNextBitmapRect.set((padding - 2 * mSmallRadius) + mBitmapSlideOffset, mBitmapStartY, padding + mBitmapSlideOffset, mBitmapEndY);
                    canvas.drawBitmap(mNextBitmap, null, mNextBitmapRect, mBitmapAlphaPaint);
                }
            }
        } else {
            mBitmapAlphaPaint.setAlpha(255);
            //current bitmap
            mCurrentBitampRect.set(mCurrentBitmapStartX, mBitmapStartY, mCurrentBitmapEndX, mBitmapEndY);
            if (mCurrentBitmap != null)
                canvas.drawBitmap(mCurrentBitmap, null, mCurrentBitampRect, mBitmapAlphaPaint);
        }
    }

    private void startValueAnimator() {
        ValueAnimator slideOffsetValueAnimator = ValueAnimator.ofInt(0, maxSlideOffsetValue);
        slideOffsetValueAnimator.setDuration(500);
        slideOffsetValueAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        slideOffsetValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mBitmapSlideOffset = (int) valueAnimator.getAnimatedValue();
                updateBitmapAlpha(mBitmapSlideOffset);
                invalidate();
            }
        });
        slideOffsetValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                sliding = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCurrentBitmap = mNextBitmap;
                mBitmapSlideOffset = 0;
                sliding = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                sliding = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        slideOffsetValueAnimator.start();
    }

    private void updateBitmapAlpha(int mBitmapSlideOffset) {
        //As offset goes higher,alpha goes higher
        currentBitmapAlpha = (255 * mBitmapSlideOffset) / maxSlideOffsetValue;
    }

    private void checkAndUpdateNextBitmap() {
        if (mNextBitmapIndex >= 0 && mNextBitmapIndex < bitmaps.length) {
            mNextBitmap = getBitmapFromId(bitmaps[mNextBitmapIndex]);
        } else {
            mNextBitmap = null;
        }
    }

    public void slideLeft() {
        //check for possibility
        if (!sliding) {
            if (mNextBitmapIndex < bitmaps.length - 1) {
                leftSlide = true;
                mNextBitmapIndex++;
                checkAndUpdateNextBitmap();
                if (mNextBitmap != null) {
                    startValueAnimator();
                }
            } else {
                if (slideListener != null) {
                    slideListener.onReachedLast();
                }
            }
        }
    }

    public void slideRight() {
        //check for possibility
        if (!sliding) {
            if (mNextBitmapIndex > 0) {
                leftSlide = false;
                mNextBitmapIndex--;
                checkAndUpdateNextBitmap();
                if (mNextBitmap != null) {
                    startValueAnimator();
                }
            } else {
                if (slideListener != null) {
                    slideListener.onReachedFirst();
                }
            }
        }
    }

    private Bitmap getBitmapFromId(int id) {
        return BitmapFactory.decodeResource(resources, id);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = getPaddingStart() + getPaddingEnd() + mLargeRadius * 2;
        int w = resolveSizeAndState(minWidth, widthMeasureSpec, 0);
        int minHeight = getPaddingBottom() + getPaddingTop() + mLargeRadius * 2;
        int h = resolveSizeAndState(minHeight, heightMeasureSpec, 0);
        setMeasuredDimension(w, h);
    }

    public void setColor(int color) {
        mCircleColour = Color.argb(10, Color.red(color), Color.green(color), Color.blue(color));
    }

    public SlideListener slideListener;

    public void setSlideListener(SlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public interface SlideListener {
        void onReachedFirst();

        void onReachedLast();
    }
}