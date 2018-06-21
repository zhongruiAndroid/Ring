package com.github.ring;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;


/**
 * Created by Administrator on 2018/5/22.
 */

public class CircleProgress extends View {
    public boolean isDebug=true;
    private OnCircleProgressInter onCircleProgressInter;
    public interface OnCircleProgressInter {
        void progress(int progress, int max);
    }
    public void Log(String log) {
        if(BuildConfig.DEBUG&&isDebug){
            Log.i("ClockView===", log);
        }
    }
    private int centerX;
    private int centerY;
    private Paint mPaint;

    //内圆颜色
    private int neiYuanColor;
    //圆环半径
    private int ringRadius;
    //圆环宽度
    private int ringWidth;
    //圆环颜色
    private int ringColor;
    //圆环进度颜色
    private int ringProgressColor;
    //圆环进度过度颜色
    private int ringProgressSecondColor;
    //开始角度
    private int startAngle=-90;
    //是否顺时针
    private boolean isClockwise=true;
    //当前进度
    private int progress=10;
    //总进度
    private int maxProgress=100;

    //用于逻辑计算的总进度(主要使动画效果更平滑)
    private final int viewMax=3600;
    //用于逻辑计算的当前进度(主要使动画效果更平滑)
    private int viewProgress=progress*viewMax/maxProgress;

    //不绘制的度数
    private int disableAngle=0;
    //圆环进度是否为圆角
    private boolean isRound=true;
    //是否设置动画
    private boolean useAnimation =true;
    //动画执行时间
    private int duration =1000;

    //进度百分比
    private double progressPercent;
    //进度百分比数值是否是小数
    private boolean isDecimal=true;
    //小数点后几位
    private int decimalPointLength=1;
    //是否显示百分比
    private boolean isShowPercentText=true;
    //文字颜色
    private int textColor;
    //文字大小
    private int textSize;

    public CircleProgress(Context context) {
        super(context);
        initAttr(null);
    }
    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
    }
    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }
    private void initAttr(AttributeSet attrs) {
        initPaint();
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        neiYuanColor = typedArray.getColor(R.styleable.CircleProgress_neiYuanColor,getTransparentColor());
        ringRadius = (int) typedArray.getDimension(R.styleable.CircleProgress_ringRadius, -1);
        ringWidth = (int) typedArray.getDimension(R.styleable.CircleProgress_ringWidth, 30);
        ringColor = typedArray.getColor(R.styleable.CircleProgress_ringColor, ContextCompat.getColor(getContext(),R.color.top_color2));
        ringProgressColor = typedArray.getColor(R.styleable.CircleProgress_ringProgressColor,ContextCompat.getColor(getContext(),R.color.green1));
        ringProgressSecondColor = typedArray.getColor(R.styleable.CircleProgress_ringProgressSecondColor, ringProgressColor);
        startAngle = typedArray.getInteger(R.styleable.CircleProgress_startAngle, -90);
        isClockwise = typedArray.getBoolean(R.styleable.CircleProgress_isClockwise, true);
        progress = typedArray.getInteger(R.styleable.CircleProgress_progress, 10);
        maxProgress = typedArray.getInteger(R.styleable.CircleProgress_maxProgress, 100);


        viewProgress=progress*viewMax/maxProgress;

        disableAngle = typedArray.getInteger(R.styleable.CircleProgress_disableAngle, 0);
        isRound = typedArray.getBoolean(R.styleable.CircleProgress_isRound, true);
        useAnimation = typedArray.getBoolean(R.styleable.CircleProgress_useAnimation, true);
        duration = typedArray.getInteger(R.styleable.CircleProgress_duration, 1000);
        isDecimal = typedArray.getBoolean(R.styleable.CircleProgress_isDecimal, true);
        decimalPointLength = typedArray.getInteger(R.styleable.CircleProgress_decimalPointLength, 1);
        isShowPercentText = typedArray.getBoolean(R.styleable.CircleProgress_isShowPercentText,true);
        textColor = typedArray.getColor(R.styleable.CircleProgress_textColor,ContextCompat.getColor(getContext(),R.color.green1));
        textSize = (int) typedArray.getDimension(R.styleable.CircleProgress_textSize, getDef_TextSize());
        typedArray.recycle();
//        initData();
    }
    private int getTransparentColor(){
        return ContextCompat.getColor(getContext(),R.color.transparent);
    }

    private int getDef_TextSize(){
        return dip2px(getContext(),17);
    }

    private void initPaint() {
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
    }



    private void initData() {
        neiYuanColor=ContextCompat.getColor(getContext(),R.color.transparent);
        ringRadius =-1;
        ringWidth=30;
        ringColor=ContextCompat.getColor(getContext(),R.color.gray_99);
        ringProgressColor=ContextCompat.getColor(getContext(),R.color.green1);
        ringProgressSecondColor=ContextCompat.getColor(getContext(),R.color.blue_00);
        textSize=dip2px(getContext(),17);
        textColor=ContextCompat.getColor(getContext(),R.color.green1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth =200;
        int mHeight = 200;
        if(getLayoutParams().width== ViewGroup.LayoutParams.WRAP_CONTENT&&getLayoutParams().height== ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,mHeight);
        }else if(getLayoutParams().width== ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,heightSize);
        }else if(getLayoutParams().height== ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(widthSize,mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int WH=Math.min(getWidth()-getPaddingLeft()-getPaddingRight(),getHeight()-getPaddingTop()-getPaddingBottom());
        if(ringRadius<0){
            ringRadius=(WH-ringWidth)/2;
        }
        centerX = getWidth()/2;
        centerY = getHeight()/2;
        //绘制圆环
//        drawRing(canvas);
        drawRing2(canvas);
        //绘制内圆
        drawNeiYuan(canvas);
        //绘制进度圆环
        drawProgressRing(canvas);
        //绘制进度百分比
        if(isShowPercentText){
            drawProgressText(canvas);
        }
    }

    private void drawProgressText(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        progressPercent  = AndroidUtils.chuFa(progress * 100, maxProgress, decimalPointLength);
        String percentStr=progressPercent+"%";
        if(!isDecimal){
            percentStr=((int)progressPercent)+"%";
        }
        Rect rect=new Rect();
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        mPaint.getTextBounds(percentStr,0,percentStr.length(),rect);
        float baseLineHeight = Math.abs(mPaint.getFontMetrics().ascent);
        canvas.drawText(percentStr+"",centerX-rect.width()/2,centerY+baseLineHeight/2,mPaint);

    }

    private void drawNeiYuan(Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(neiYuanColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX,centerY,ringRadius-ringWidth/2,mPaint);
    }

    private void drawProgressRing(Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(ringProgressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setShader(null);

        RectF rectF=new RectF(centerX-ringRadius,centerY-ringRadius,centerX+ringRadius,centerY+ringRadius);
//        mPaint.setShadowLayer(1000,1000,1000,ContextCompat.getColor(getContext(),R.color.blue_00));
        LinearGradient linearGradient = new LinearGradient(0,0,
                getMeasuredWidth(),getMeasuredHeight(),
                ringProgressColor,ringProgressSecondColor,
                Shader.TileMode.MIRROR);
        mPaint.setShader(linearGradient);

        if(isRound){
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        float angle = (float) AndroidUtils.chuFa(viewProgress*getEffectiveDegree(),viewMax,2);
        if(!isClockwise){
            angle=-1*angle;
        }
        canvas.drawArc(rectF,startAngle,angle,false,mPaint);
        mPaint.reset();//如果不reset，setShader导致设置进度无效果
    }

    private void drawRing2(Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setColor(ringColor);
        RectF rectF=new RectF(centerX-ringRadius,centerY-ringRadius,centerX+ringRadius,centerY+ringRadius);
        if(isRound){
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        float angle = getEffectiveDegree();

        if(!isClockwise){
            angle=-1*angle;
        }
        canvas.drawArc(rectF,startAngle,angle,false,mPaint);
    }
    private void drawRing(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setColor(ringColor);
        canvas.drawCircle(centerX,centerY, ringRadius,mPaint);
    }
    /*******************************get*set********************************************/
    public int getEffectiveDegree(){
        return 360-disableAngle;
    }
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        setProgress(progress,useAnimation);
    }

    public int getDisableAngle() {
        return disableAngle;
    }

    public void setDisableAngle(int disableAngle) {
        int beforeDisableAngle=this.disableAngle;
        if(disableAngle>360){
            this.disableAngle=360;
        }else if(disableAngle<0){
            this.disableAngle=0;
        }else{
            this.disableAngle = disableAngle;
        }
        if(useAnimation){
            ValueAnimator valueAnimator =ValueAnimator.ofInt(beforeDisableAngle,disableAngle);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    CircleProgress.this.disableAngle= (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        }else{
            invalidate();
        }
    }

    public int getStartAngle() {
        return startAngle;
    }

    public CircleProgress setStartAngle(int startAngle) {
        this.startAngle = startAngle;
        invalidateCircleProgress();
        return this;
    }

    public void setProgress(int progress, boolean useAnimation) {
        int beforeProgress=viewProgress;
        if(progress>maxProgress){
            this.progress=maxProgress;
        }else if(progress<0){
            this.progress=0;
        }else{
            this.progress = progress;
        }
        viewProgress = progress * viewMax / maxProgress;
        if(useAnimation){
            ValueAnimator valueAnimator =ValueAnimator.ofInt(beforeProgress,viewProgress);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    CircleProgress.this.viewProgress = (int) animation.getAnimatedValue();
                    CircleProgress.this.progress = CircleProgress.this.viewProgress*CircleProgress.this.maxProgress/viewMax;
                    invalidate();
                    setCircleProgress(CircleProgress.this.progress,CircleProgress.this.maxProgress);
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        }else{
            invalidate();
            setCircleProgress(CircleProgress.this.progress,CircleProgress.this.maxProgress);
        }
    }

    public int getNeiYuanColor() {
        return neiYuanColor;
    }

    public CircleProgress setNeiYuanColor(@ColorInt int neiYuanColor) {
        this.neiYuanColor = neiYuanColor;
        invalidateCircleProgress();
        return this;
    }

    public int getRingRadius() {
        return ringRadius;
    }

    public CircleProgress setRingRadius(int ringRadius) {
        this.ringRadius = ringRadius;
        invalidateCircleProgress();
        return this;
    }

    public int getRingWidth() {
        return ringWidth;
    }

    public CircleProgress setRingWidth(int ringWidth) {
        this.ringWidth = ringWidth;
        invalidateCircleProgress();
        return this;
    }

    public int getRingColor() {
        return ringColor;
    }

    public CircleProgress setRingColor(@ColorInt int ringColor) {
        this.ringColor = ringColor;
        invalidateCircleProgress();
        return this;
    }

    public int getRingProgressColor() {
        return ringProgressColor;
    }

    public CircleProgress setRingProgressColor(@ColorInt int ringProgressColor) {
        this.ringProgressColor = ringProgressColor;
        invalidateCircleProgress();
        return this;
    }

    public int getRingProgressSecondColor() {
        return ringProgressSecondColor;
    }

    public CircleProgress setRingProgressSecondColor(@ColorInt int ringProgressSecondColor) {
        this.ringProgressSecondColor = ringProgressSecondColor;
        invalidateCircleProgress();
        return this;
    }


    public boolean isClockwise() {
        return isClockwise;
    }

    public CircleProgress setClockwise(boolean clockwise) {
        isClockwise = clockwise;
        invalidateCircleProgress();
        return this;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public CircleProgress setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidateCircleProgress();
        return this;
    }

    public boolean isRound() {
        return isRound;
    }

    public CircleProgress setRound(boolean round) {
        isRound = round;
        invalidateCircleProgress();
        return this;
    }

    public boolean isUseAnimation() {
        return useAnimation;
    }

    public CircleProgress setUseAnimation(boolean useAnimation) {
        this.useAnimation = useAnimation;
        invalidateCircleProgress();
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public CircleProgress setDuration(int duration) {
        this.duration = duration;
        invalidateCircleProgress();
        return this;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    public CircleProgress setDecimal(boolean decimal) {
        isDecimal = decimal;
        invalidateCircleProgress();
        return this;
    }

    public int getDecimalPointLength() {
        return decimalPointLength;
    }

    public CircleProgress setDecimalPointLength(int decimalPointLength) {
        this.decimalPointLength = decimalPointLength;
        invalidateCircleProgress();
        return this;
    }

    public boolean isShowPercentText() {
        return isShowPercentText;
    }

    public CircleProgress setShowPercentText(boolean showPercentText) {
        isShowPercentText = showPercentText;
        invalidateCircleProgress();
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public CircleProgress setTextColor(@ColorInt int textColor) {
        this.textColor = textColor;
        invalidateCircleProgress();
        return this;
    }

    public int getTextSize() {
        return textSize;
    }

    public CircleProgress setTextSize(int textSize) {
        this.textSize = textSize;
        invalidateCircleProgress();
        return this;
    }

    public OnCircleProgressInter getOnCircleProgressInter() {
        return onCircleProgressInter;
    }
    public CircleProgress setOnCircleProgressInter(OnCircleProgressInter onCircleProgressInter) {
        this.onCircleProgressInter = onCircleProgressInter;
        return this;
    }
    private void setCircleProgress(int progress,int max) {
        if(onCircleProgressInter !=null){
            onCircleProgressInter.progress(progress,max);
        }
    }
    private void invalidateCircleProgress(){
        invalidate();
    }

    private int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    private int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }
}
