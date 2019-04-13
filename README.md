# Ring圆环进度
![github](https://github.com/zhongruiAndroid/Ring/blob/master/app/src/main/res/drawable/ring.gif "github")  

| 属性                    | 类型      | 说明                                                                             |
|-------------------------|-----------|----------------------------------------------------------------------------------|
| neiYuanColor            | color     | 内圆颜色,默认透明                                                                         |
| ringRadius              | dimension | 圆环半径                                                                         |
| ringWidth               | dimension | 圆环宽度                                                                         |
| ringColor               | color     | 圆环颜色                                                                         |
| ringProgressColor       | color     | 圆环进度颜色                                                                     |
| startAngle              | integer   | 开始角度,默认为-90(12点钟方向),0度是3点钟方向,90度是6点钟方向                    |
| isClockwise             | boolean   | 是否顺时针,默认true                                                              |
| progress                | float   | 当前进度                                                                         |
| maxProgress             | float   | 总进度,默认100                                                                   |
| disableAngle            | integer   | 不绘制的角度(圆环缺损角度)                                                       |
| isRound                 | boolean   | 圆环进度是否为圆角,默认true                                                      |
| useAnimation            | boolean   | 是否设置动画,默认true(setProgress方法执行动画时获取progress建议设置监听事件获取) |
| duration                | integer   | 动画执行时间,单位:毫秒,默认1000毫秒                                              |
| isDecimal               | boolean   | 进度百分比数值是否有小数点,默认true                                              |
| decimalPointLength      | integer   | 小数点后几位                                                                     |
| isShowPercentText       | boolean   | 是否显示百分比,默认true                                                          |
| textColor               | color     | 文字颜色                                                                         |
| textSize                | dimension | 文字大小,默认17sp                                                                |


#### 进度监听
```java
CircleProgress circleprogress = (CircleProgress) findViewById(R.id.circleprogress);
circleProgress.setOnCircleProgressInter(new CircleProgress.OnCircleProgressInter() {
    @Override
    public void progress(float scaleProgress, float progress, float max) {
        //总进度max,当前进度:progress,动画执行进度:scaleProgress
    }
});
```

```xml
<com.github.ring.CircleProgress
    android:id="@+id/circleprogress"
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:textSize="22sp"
    app:decimalPointLength="1"
    app:isShowPercentText="true"
    app:progress="120"
    app:maxProgress="200"
    app:ringProgressColor="@color/blue_00"
    app:disableAngle="0"
    app:duration="1000"
    app:isClockwise="true"
    app:isDecimal="true"
    app:isRound="true"
    app:useAnimation="true"
    app:ringColor="@color/top_color1"
    app:neiYuanColor="@color/transparent"
    app:ringRadius="90dp"
    app:ringWidth="10dp"
    app:textColor="@color/blue_00"
    />
```

#### 设置过度颜色
```java
circleprogress.post(new Runnable() {
    @Override
    public void run() {
        LinearGradient linearGradient = new LinearGradient(0,0,
                circleprogress.getWidth(),circleprogress.getHeight(),
                circleprogress.getRingProgressColor(), ContextCompat.getColor(MainActivity.this,R.color.green),
                Shader.TileMode.MIRROR);
        circleprogress.setProgressShader(linearGradient);
    }
});
```  
### 如果本库对您有帮助,还希望支付宝扫一扫下面二维码,你我同时免费获取奖励金(非常感谢 Y(^-^)Y)
![github](https://github.com/zhongruiAndroid/SomeImage/blob/master/image/small_ali.jpg?raw=true "github")  

[ ![Download](https://api.bintray.com/packages/zhongrui/mylibrary/CircleProgress/images/download.svg) ](https://bintray.com/zhongrui/mylibrary/CircleProgress/_latestVersion)<--版本号  

```gradle
compile 'com.github:CircleProgress:版本号看上面'
```
