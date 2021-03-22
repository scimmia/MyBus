//package com.scimmia.mybus;
//
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import main.smart.R;
//import main.smart.bus.bean.BusBean;
//import main.smart.bus.bean.LineBean;
//import main.smart.bus.bean.StationBean;
//import main.smart.bus.bean.StationRegion;
//import main.smart.common.SmartBusApp;
//import main.smart.common.util.CharUtil;
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.Rect;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Toast;
//
///**
// * 画线路图：站点，线，车，文字
// *
// * **/
//public class BusLineGraphView extends View {
//    static final String TAG = "LINE-VIEW";
//    Bitmap mBeginIcon;// 开始站图标
//    private List<BusBean> mBusData;
//    Bitmap mBusIcon;// 车标下行
//    Bitmap mBusIcon1;// 车标下行
//    Bitmap mBusIcon2;// 车标下行
//    Bitmap mBusIcon3;// 车标下行
//    Bitmap mBusIconsx;// 车标上行
//    Bitmap mBusIconsx1;// 车标上行
//    Bitmap mBusIconsx2;// 车标上行
//    Bitmap mBusIconsx3;// 车标上行
//    private LineBean mBusLine;// 线路对象
//    int mBusLineColor;// 线路颜色
//    int mColWidth;// 站点在线路上横向距离
//    int mRowHeight;// 直线图 两行的距离
//    Bitmap mComingBg;
//    Bitmap mEndIcon;// 终点站图标
//    Bitmap mGetOffIcon;
//    private int mGetOffStationIdx;
//    Bitmap mGetOnIcon;
//    private int mGetOnStationIdx;
//    private double screenSize;
//    private int orHeight=0;
//    int mLinkSize;
//    // 站点点击 监听
//    private OnBusStationClickListener mOnStationClickListener;
//    //上车站点图标
//    Bitmap upStationIcon;
//    //上车站点编号
//    int upStation=0;
//
//
//    Bitmap mStationIcon;// 站点标
//    int mTextColor;
//    int mTextSize;
//    //记录每个站点的位置坐标
//    private List<StationRegion> stlist=null;
//
//
//    //---------记录原点移动了多少---------
//    public BusLineGraphView(Context paramContext) {
//        // super(paramContext);
//        this(paramContext, null);
//        Log.d(TAG, "线路图构造函数");
//    }
//
//    public LineBean getBusLine() {
//        return this.mBusLine;
//    }
//
//    public void setBusLine(LineBean paramBusLine) {
//        this.mBusLine = paramBusLine;
//        this.updateBuses();
//        invalidate();
//    }
//
//    public void setHeight(double heiPix){
//        screenSize =heiPix;
//    }
//    public BusLineGraphView(Context paramContext, AttributeSet paramAttributeSet) {
//        super(paramContext, paramAttributeSet);
//        this.mGetOnStationIdx = -1;
//        this.mGetOffStationIdx = -1;
//        Resources localResources = getResources();
//        this.orHeight = localResources.getDimensionPixelSize(R.dimen.busline_graph_row_height);
//        this.mRowHeight=orHeight;
//        this.mLinkSize = localResources.getDimensionPixelSize(R.dimen.busline_graph_link_size);
//        this.mTextSize = localResources.getDimensionPixelSize(R.dimen.busline_graph_node_text_size);
//        this.mTextColor = localResources.getColor(R.color.black_text);
//        this.mBusLineColor = localResources.getColor(R.color.busline_graph_color);
//        this.mBusIcon = BitmapFactory.decodeResource(getResources(),R.drawable.sketch_busicon);
//        this.upStationIcon=BitmapFactory.decodeResource(getResources(),R.drawable.bustransfer_on_bubble);
//        this.mBusIcon1=BitmapFactory.decodeResource(getResources(),R.drawable.sketch_busicon_1);
//        this.mBusIcon2=BitmapFactory.decodeResource(getResources(),R.drawable.sketch_busicon_2);
//        this.mBusIcon3=BitmapFactory.decodeResource(getResources(),R.drawable.sketch_busicon_3);
//        this.mBusIconsx=BitmapFactory.decodeResource(getResources(), R.drawable.sketch_busicon_red);
//        this.mBusIconsx1=BitmapFactory.decodeResource(getResources(), R.drawable.sketch_busicon_green_1);
//        this.mBusIconsx2=BitmapFactory.decodeResource(getResources(), R.drawable.sketch_busicon_green_2);
//        this.mBusIconsx3=BitmapFactory.decodeResource(getResources(), R.drawable.sketch_busicon_green_3);
//        // 灰色圆圈站
//        this.mStationIcon = BitmapFactory.decodeResource(getResources(),R.drawable.staitonlist_station_noline);
//        // 即将有车到达的站
//        this.mComingBg = BitmapFactory.decodeResource(getResources(),R.drawable.staitonlist_station_coming_solid);
//        // 始发站
//        this.mBeginIcon = BitmapFactory.decodeResource(getResources(),R.drawable.sketch_start);
//        // 终点站
//        this.mEndIcon = BitmapFactory.decodeResource(getResources(),R.drawable.sketch_finish);
//        // 上
//        this.mGetOnIcon = BitmapFactory.decodeResource(getResources(),R.drawable.stationlist_on);
//        // 下
//        this.mGetOffIcon = BitmapFactory.decodeResource(getResources(),R.drawable.stationlist_off);
//    }
//    /**
//     * 根据站点数 决定行距
//     * 80
//     * */
//    public void getRowHeight( int stationSize){
//        int row=stationSize/10;
//        if(this.screenSize>5&&row<3){//当作平板处理
//            this.mRowHeight = orHeight*2;
//        }
//    }
//    protected void onDraw(Canvas paramCanvas) {
//        super.onDraw(paramCanvas);
//        stlist=new ArrayList();
//        paintGraph(paramCanvas);
//        paintBuses(paramCanvas);
//
//    }
//
//
//    // 画线
//    public void paintGraph(Canvas paramCanvas) {
//        if (this.mBusLine == null)
//            return;
//        paintLinks(paramCanvas);
//        paintNodes(paramCanvas);
//    }
//
//    public void paintLinks(Canvas paramCanvas) {
//        Log.d(TAG, "线路图画线");
//        int stationNum = this.mBusLine.getStations().size();
//        paramCanvas.save();
//        // 定义画笔 粗细，颜色
//        Paint localPaint = new Paint(1);
//        localPaint.setStrokeWidth(this.mLinkSize);
//        localPaint.setColor(this.mBusLineColor);
//        // 站点总数
//        stationNum = stationNum == 0 ? 31 : stationNum;
//        // 一共多少行站点
//        int rowNum = (-1 + stationNum + 5) / 5;
//        // 原点分别在x轴和y轴偏移多远的距离，然后以偏移后的位置作为坐标原点。
//        // 也就是说原来在（100,100）,然后translate（1，1）新的坐标原点在（101,101）而不是（1,1）
//        paramCanvas.translate(this.mColWidth, this.orHeight);
//        int currRow = 0;
//        do {
//            if (currRow >= rowNum) {
//                paramCanvas.restore();
//                return;
//            }
//            int l = -1 + Math.min(stationNum - currRow * 5, 5);
//            int i1;
//            if (currRow % 2 != 0) {// 奇数行
//                i1 = l * -mColWidth;
//            } else {
//                i1 = l * mColWidth;
//            }
//            paramCanvas.drawLine(0.0F, 0.0F, i1, 0, localPaint);
//            paramCanvas.translate(i1, 0);
//            if (currRow < rowNum - 1) {
//                int i3 = this.mRowHeight;// 画折线
//                paramCanvas.drawLine(0.0F, 0.0F, 0, i3, localPaint);
//                paramCanvas.translate(0, i3);
//            }
//            ++currRow;
//        } while (true);
//    }
//
//    /**
//     * 线路图上画站点
//     * */
//    public void paintNodes(Canvas paramCanvas) {
//        Log.d(TAG, "线路图画站点");
//        int j = 0;
//        int k = 1;
//        int l = 0;
//        float x=this.mColWidth;
//        float y=this.orHeight;
//        int flag = 1;// 站点排列方向 1正向 2 逆向
//        paramCanvas.save();
//        int stationNum = this.mBusLine.getStations().size();
//        int sxx =this.mBusLine.getLineId();//上下行 0：下行 1：上行
//        // 定义画笔
//        Paint localPaint = new Paint(1);
//        localPaint.setStyle(Paint.Style.FILL);
//        localPaint.setTextSize(this.mTextSize);
//        localPaint.setColor(this.mTextColor);
//        j = (int)(this.mColWidth * Math.acos(0.5235987755982988D));
//        ArrayList list = new ArrayList();
//        // -------------------------
//        paramCanvas.translate(this.mColWidth, this.orHeight);
//        if(sxx==0){
//            while (k <= stationNum) {
//                // -------同行---------------
//                if (k==upStation){
//                    mStationIcon=this.upStationIcon;
//                }
//                if ( k % 5 != 0) {
//                    if (k / 5 % 2 != 0){// 奇数行，倒排
//                        flag = -1;
//                        int i2 = -this.mColWidth;
//                        if (k % 5 != 1) {
//                            paramCanvas.translate(i2, l);
//                            x=x+i2;
//                        }
//                        SumStationXY(x,y,k,sxx,mStationIcon);
//                        // 即将到达站点
//                        if ((k != stationNum)&& (list.contains(Integer.valueOf(k)))) {
//                            float f11 = -this.mComingBg.getWidth() / 2;
//                            float f12 = -this.mComingBg.getHeight() / 2;
//                            paramCanvas.drawBitmap(this.mComingBg, f11, f12, null);
//                        }
//                        float f9 = -this.mStationIcon.getWidth() / 2;
//                        float f10 = -this.mStationIcon.getHeight() / 2;
//                        paramCanvas.drawBitmap(this.mStationIcon, f9, f10, null);
//                    } else {// 偶数行，正排
//                        flag = 1;
//                        if (k != 1) {
//                            int i2 = this.mColWidth;
//                            // x轴 左右 ；y轴上下
//                            if (k % 5 != 1) {
//                                paramCanvas.translate(i2, l);
//                                x=x+i2;
//                            }
//                            SumStationXY(x,y,k,sxx,mStationIcon);
//                            float f1 = -this.mStationIcon.getWidth() / 2;
//                            float f2 = -this.mStationIcon.getHeight() / 2;
//                            paramCanvas.drawBitmap(this.mStationIcon, f1, f2, null);
//                        }
//                    }
//                } else {// ---------换行处理-----------
//                    String str = ((StationBean) this.mBusLine.getStations().get(k - 1)).getStationName();//文字
//                    Path localPath = new Path();
//                    localPath.moveTo(0.0F, -this.mRowHeight / 4);
//                    localPath.lineTo(this.mColWidth,(int) (-(this.mColWidth * Math.tan(0.5235987755982988D))));
//                    if (k != stationNum) {
//                        int i2 = flag * this.mColWidth;
//                        // x轴 左右 ；y轴上下
//                        paramCanvas.translate(i2, l);
//                        float f1 = -this.mStationIcon.getWidth() / 2;
//                        float f2 = -this.mStationIcon.getHeight() / 2;
//                        paramCanvas.drawBitmap(this.mStationIcon, f1, f2, null);
//                        paintTextOnPath(paramCanvas, j, str, localPath, localPaint);
//                        paramCanvas.translate(0, mRowHeight);
//                        x=x+i2;
//                        SumStationXY(x,y,k,sxx,mStationIcon);
//                    }else{//最后一个
//                        int i2 = flag * this.mColWidth;
//                        // x轴 左右 ；y轴上下
//                        paramCanvas.translate(i2, l);
//                        x=x+i2;
//                        SumStationXY(x,y,k,sxx,mStationIcon);
//                    }
//                    y=y+mRowHeight;
//                }
//                if(k%5!=0||(k%5==0&&k==stationNum)){
//                    // -----------站点文字------------
//                    String str = ((StationBean) this.mBusLine.getStations().get(k - 1)).getStationName();
//                    Path localPath = new Path();
//                    localPath.moveTo(0.0F, -this.mRowHeight / 4);
//                    localPath.lineTo(this.mColWidth,(int) (-(this.mColWidth * Math.tan(0.5235987755982988D))));
//                    paintTextOnPath(paramCanvas, j, str, localPath, localPaint);
//                }
//                if (this.mGetOffStationIdx == k) {
//                    float f7 = -this.mGetOffIcon.getWidth() / 2;
//                    float f8 = -this.mGetOffIcon.getHeight() / 2;
//                    paramCanvas.drawBitmap(this.mGetOffIcon, f7, f8, null);
//                }
//                if (k == 1) {// 始发站
//                    SumStationXY(x,y,k,sxx,mBeginIcon);
//                    float f5 = -this.mBeginIcon.getWidth() / 2;
//                    float f6 = -this.mBeginIcon.getHeight() / 2;
//                    paramCanvas.drawBitmap(this.mBeginIcon, f5, f6, null);
//
//                } else if(k == stationNum) {// 终点站
//                    float f3 = -this.mEndIcon.getWidth() / 2;
//                    float f4 = -this.mEndIcon.getHeight() / 2;
//                    paramCanvas.drawBitmap(this.mEndIcon, f3, f4, null);
//                }
//                ++k;
//                this.mStationIcon = BitmapFactory.decodeResource(getResources(),R.drawable.staitonlist_station_noline);
//            }
//        }else if(sxx==1){//上行
//            k =stationNum;
//            while (k >0) {
//                if (k==upStation){
//                    mStationIcon=this.upStationIcon;
//                }
//                // -------同行---------------
//                if ((stationNum-k) % 5 != 4) {
//                    if ((stationNum-k) / 5 % 2 != 0){// 奇数行，倒排
//                        flag = -1;
//                        int i2 = -this.mColWidth;
//                        if ((stationNum-k) % 5 != 0) {
//                            paramCanvas.translate(i2, l);
//                            x=x+i2;
//                        }
//                        SumStationXY(x,y,k,sxx,mStationIcon);
//                        // 即将到达站点
//                        if ((k != 1)&& (list.contains(Integer.valueOf(k)))) {
//                            float f11 = -this.mComingBg.getWidth() / 2;
//                            float f12 = -this.mComingBg.getHeight() / 2;
//                            paramCanvas.drawBitmap(this.mComingBg, f11, f12, null);
//                        }
//                        float f9 = -this.mStationIcon.getWidth() / 2;
//                        float f10 = -this.mStationIcon.getHeight() / 2;
//                        paramCanvas.drawBitmap(this.mStationIcon, f9, f10, null);
//                    } else {// 偶数行，正排
//                        flag = 1;
//                        int i2 = this.mColWidth;
//                        if ((stationNum-k) % 5 != 0) {
//                            paramCanvas.translate(i2, l);
//                            x=x+i2;
//                        }
//                        SumStationXY(x,y,k,sxx,mStationIcon);
//                        if (k != 1) {// x轴 左右 ；y轴上下
//                            float f1 = -this.mStationIcon.getWidth() / 2;
//                            float f2 = -this.mStationIcon.getHeight() / 2;
//                            paramCanvas.drawBitmap(this.mStationIcon, f1, f2, null);
//                        }
//                    }
//                } else {// ---------换行处理-----------
//                    String str = ((StationBean) this.mBusLine.getStations().get(k - 1)).getStationName();
//                    Path localPath = new Path();
//                    localPath.moveTo(0.0F, -this.mRowHeight / 4);
//                    localPath.lineTo(this.mColWidth,(int) (-(this.mColWidth * Math.tan(0.5235987755982988D))));
//                    if (k != 1) {
//                        int i2 = flag * this.mColWidth;
//                        // x轴 左右 ；y轴上下
//                        paramCanvas.translate(i2, l);
//                        float f1 = -this.mStationIcon.getWidth() / 2;
//                        float f2 = -this.mStationIcon.getHeight() / 2;
//                        paramCanvas.drawBitmap(this.mStationIcon, f1, f2, null);
//                        x=x+i2;
//                        SumStationXY(x,y,k,sxx,mStationIcon);
//                        // -----------站点文字------------
//                        paintTextOnPath(paramCanvas, j, str, localPath, localPaint);
//                        paramCanvas.translate(0, mRowHeight);
//                    }else{//最后一个
//                        int i2 = flag * this.mColWidth;
//                        // x轴 左右 ；y轴上下
//                        paramCanvas.translate(i2, l);
//                        x=x+i2;
//                        SumStationXY(x,y,k,sxx,mStationIcon);
//                        paintTextOnPath(paramCanvas, j, str, localPath, localPaint);
//                    }
//                    y=y+mRowHeight;
//                }
//                if((stationNum-k)%5!=4||k==stationNum){
//                    // -----------站点文字------------
//                    String str = ((StationBean) this.mBusLine.getStations().get(k - 1)).getStationName();
//                    Path localPath = new Path();
//                    localPath.moveTo(0.0F, -this.mRowHeight / 4);
//                    localPath.lineTo(this.mColWidth,(int) (-(this.mColWidth * Math.tan(0.5235987755982988D))));
//                    paintTextOnPath(paramCanvas, j, str, localPath, localPaint);
//                }
//                if (this.mGetOffStationIdx == k) {
//                    float f7 = -this.mGetOffIcon.getWidth() / 2;
//                    float f8 = -this.mGetOffIcon.getHeight() / 2;
//                    paramCanvas.drawBitmap(this.mGetOffIcon, f7, f8, null);
//                }
//                if (k == stationNum) {// 始发站
//                    SumStationXY(x,y,k,sxx,mBeginIcon);
//                    float f5 = -this.mBeginIcon.getWidth() / 2;
//                    float f6 = -this.mBeginIcon.getHeight() / 2;
//                    paramCanvas.drawBitmap(this.mBeginIcon, f5, f6, null);
//                } else if (k == 1) {// 终点站
//                    float f3 = -this.mEndIcon.getWidth() / 2;
//                    float f4 = -this.mEndIcon.getHeight() / 2;
//                    paramCanvas.drawBitmap(this.mEndIcon, f3, f4, null);
//                }
//                --k;
//                this.mStationIcon = BitmapFactory.decodeResource(getResources(),R.drawable.staitonlist_station_noline);
//            }
//        }
//        paramCanvas.restore();
//    }
//
//    /**
//     * 画车
//     * @param canvas
//     */
//    private void paintBuses(Canvas canvas){
//        canvas.save();
//        int stationNum=this.mBusLine.getStations().size();//站点总数
//        if (mBusData != null && mBusData.size() > 0){
//            Paint paint = new Paint(1);
//            paint.setStyle(android.graphics.Paint.Style.FILL);
//            paint.setTextSize(mTextSize);
//            paint.setColor(mTextColor);
//            Iterator iterator = mBusData.iterator();
//            //-----1.判断上下行----
//            int sxx =this.mBusLine.getLineId();//上下行 0：下行 1：上行
//            int flag=0;
//            if(sxx==0){
//                while (iterator.hasNext()) {
//                    BusBean businfo = (BusBean)iterator.next();
//                    if(businfo.getSxx().equals("0")){
//                        float leftPos = businfo.getLeftStation();
//                        float f1 =Float.parseFloat(businfo.getLeftDistance());
//                        int i = (int)Math.floor(leftPos);
//                        int j =  ((i-1)%5)* mColWidth + mColWidth;//宽度
//                        int k = ((i-1) / 5) * mRowHeight + mRowHeight;//高度
//                        float width;
//                        float height;
//                        if (i  % 5 != 0){
//                            //两站之间
//                            if (i  % 5 != 0)
//                                j = (int)((float)j + f1 * (float)mColWidth);
//                            else
//                                k = (int)((float)k + f1 * (float)mRowHeight);
//                            if (((i-1) / 5) % 2 != 0)
//                                j = getMeasuredWidth() - j;
//                        }else{//转行的边界站点
//                            if (((i-1) / 5) % 2 != 0)
//                                j = getMeasuredWidth() - j;
//                            k = (int)((float)k + f1 * (float)mRowHeight);
//                        }
//                        int bus=selectbus(leftPos,0,stationNum);
//                        if (bus==1){
//                            width = j - mBusIcon1.getWidth() / 2;
//                            height = k - mBusIcon1.getHeight();
//                            canvas.drawBitmap(mBusIcon1, width, height, null);
//                        }else if(bus==2){
//                            width = j - mBusIcon2.getWidth() / 2;
//                            height = k - mBusIcon2.getHeight();
//                            canvas.drawBitmap(mBusIcon2, width, height, null);
//                        }else{
//                            width = j - mBusIcon3.getWidth() / 2;
//                            height = k - mBusIcon3.getHeight();
//                            canvas.drawBitmap(mBusIcon3, width, height, null);
//                        }
//                        String s =businfo.getBusCode();
//                        Rect rect = new Rect();
//                        paint.getTextBounds(s, 0, s.length(), rect);
//                        int l = j - rect.width() / 2;
//                        int i1 = k + rect.height();
//                        canvas.drawText(s, l, i1, paint);
//                        //if(leftPos+1==upStation&&upStation!=0){
//                        //	this.PlayRingTone();
//                        //}
//                    }
//                }
//            }else{//----------------------上行  序号从大到小
//                while (iterator.hasNext()) {
//                    BusBean businfo = (BusBean)iterator.next();
//                    if(businfo.getSxx().equals("1")){
//                        float leftPos = businfo.getLeftStation();
//                        float f1 =Float.parseFloat(businfo.getLeftDistance());
//                        int i = (int)Math.floor(leftPos);
//                        int j =  ((stationNum-i)%5)* mColWidth + mColWidth;//宽度
//                        int k = ((stationNum-i) / 5) * mRowHeight + mRowHeight;//高度
//                        float width;
//                        float height;
//                        if ((stationNum-i)  % 5 != 4){
//                            j = (int)((float)j + f1 * (float)mColWidth);
//                            if ((((stationNum-i)) / 5) % 2 != 0)
//                                j = getMeasuredWidth() - j;
//                        }else{//转行的边界站点
//                            if ((((stationNum-i)) / 5) % 2 != 0)
//                                j = getMeasuredWidth() - j;
//                            k = (int)((float)k + f1 * (float)mRowHeight);
//                        }
//                        int bus=selectbus(leftPos,1,stationNum);
//                        if (bus==1){
//                            width = j - mBusIconsx1.getWidth() / 2;
//                            height = k - mBusIconsx1.getHeight();
//                            canvas.drawBitmap(mBusIconsx1, width, height, null);
//                        }else if(bus==2){
//                            width = j - mBusIconsx2.getWidth() / 2;
//                            height = k - mBusIconsx2.getHeight();
//                            canvas.drawBitmap(mBusIconsx2, width, height, null);
//                        }else{
//                            width = j - mBusIconsx3.getWidth() / 2;
//                            height = k - mBusIconsx3.getHeight();
//                            canvas.drawBitmap(mBusIconsx3, width, height, null);
//                        }
//                        String s =businfo.getBusCode();
//                        Rect rect = new Rect();
//                        paint.getTextBounds(s, 0, s.length(), rect);
//                        int l = j - rect.width() / 2;
//                        int i1 = k + rect.height();
//                        canvas.drawText(s, l, i1, paint);
//                        //if(leftPos-1==upStation&&upStation!=0){
//                        //	this.PlayRingTone();
//                        //}
//                    }
//                }
//            }
//        }
//        canvas.restore();
//    }
//
//    public int selectbus(float pos,int sxx,int stationNum){
//        if (sxx==0){
//            String str=(long)pos+"";
//            if (str.length()==1){
//                str="0"+str;
//            }
//            if (pos%5==0){
//                return 2;
//            }else{
//                String val=str.substring(1, 2);
//                if (val.equals("1")||val.equals("2")||val.equals("3")||val.equals("4")){
//                    return 1;
//                }else{
//                    return 3;
//                }
//            }
//        }else{
//            int k=1;
//            int station=stationNum;
//            for(int i=1;i<=stationNum;i++){
//                if (station==pos){
//                    k=i;
//                    break;
//                }
//                station--;
//            }
//            String str=k+"";
//            if (str.length()==1){
//                str="0"+str;
//            }
//            if (k%5==0){
//                return 2;
//            }else{
//                String val=str.substring(1, 2);
//                if (val.equals("1")||val.equals("2")||val.equals("3")||val.equals("4")){
//                    return 1;
//                }else{
//                    return 3;
//                }
//            }
//        }
//    }
//
//
//
//    /**
//     * 线路图上 文字提示
//     * */
//    public void paintTextOnPath(Canvas paramCanvas, int paramInt,String stationName, Path paramPath, Paint paramPaint) {
//        ArrayList list = CharUtil.staticLayout(paramInt, stationName,paramPaint);
//        if ((list != null) && (list.size() == 1)) {
//            paramCanvas.drawTextOnPath((String) list.get(0), paramPath, 0.0F,5.5F, paramPaint);
//            return;
//        }
//        for (int i = 0;i < list.size(); ++i) {
//            paramCanvas.drawTextOnPath((String) list.get(i), paramPath, 0.0F,28.5F * (i + 1) - (16 * list.size()), paramPaint);
//        }
//    }
//
//
//    protected void onMeasure(int paramInt1, int paramInt2) {
//        super.onMeasure(paramInt1, paramInt2);
//        this.mColWidth = (getMeasuredWidth() / 6);
//        setMeasuredDimension(getMeasuredWidth(), this.mRowHeight* (4 + (-1 + 5 + this.mBusLine.getStations().size()) / 5));
//    }
//
//
//
//    public void setBusLineAndOnOff(LineBean paramBusLine, int paramInt1,int paramInt2) {
//        this.mBusLine = paramBusLine;
//        this.mGetOnStationIdx = paramInt1;
//        this.mGetOffStationIdx = paramInt2;
//        invalidate();
//    }
//
//    public void setGetOnOffStations(int paramInt1, int paramInt2) {
//        this.mGetOnStationIdx = paramInt1;
//        this.mGetOffStationIdx = paramInt2;
//        invalidate();
//    }
//
//    public void setOnBusStationClickListener(
//            OnBusStationClickListener paramOnBusStationClickListener) {
//        this.mOnStationClickListener = paramOnBusStationClickListener;
//    }
//
//    public void updateBuses(List<BusBean> paramList) {
//        this.mBusData = paramList;
//        invalidate();
//    }
//
//    public static abstract interface OnBusStationClickListener {
//        public abstract void onBusStationClick(View paramView, int paramInt1,int paramInt2, int paramInt3);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // TODO Auto-generated method stub
//        float x=event.getX();
//        float y=event.getY();
//        for(int i=0;i<stlist.size();i++){
//            StationRegion sr=stlist.get(i);
//            float maxX=sr.getMaxX();
//            float minX=sr.getMinX();
//            float maxY=sr.getMaxY();
//            float minY=sr.getMinY();
//            if(x>=minX&&x<=maxX&&y>=minY&&y<=maxY){
//                if(sr.getStation()==upStation){
//                    upStation=0;
//                }else{
//                    upStation=sr.getStation();
//                }
//                invalidate();
//                break;
//            }
//        }
//
//        return super.onTouchEvent(event);
//    }
//
//    /**
//     * 计算每个站点的坐标
//     */
//    public void  SumStationXY(float x,float y,int station,int sxx,Bitmap Icon){
//        StationRegion sr=new StationRegion();
//        sr.setX(x);
//        sr.setY(y);
//        sr.setSxx(sxx);
//        float maxX=x+Icon.getWidth()/2;
//        float minX=x-Icon.getWidth()/2;
//        float maxY=y+Icon.getHeight()/2;
//        float minY=y-Icon.getHeight()/2;
//        sr.setMaxX(maxX);
//        sr.setMaxY(maxY);
//        sr.setMinX(minX);
//        sr.setMinY(minY);
//        sr.setStation(station);
//        stlist.add(sr);
//    }
//
//
//    /**
//     * 更新车的位置判断是否报警
//     */
//    private void updateBuses(){
//        int stationNum=this.mBusLine.getStations().size();//站点总数
//        if (mBusData != null && mBusData.size() > 0){
//
//            Iterator iterator = mBusData.iterator();
//            //-----1.判断上下行----
//            int sxx =this.mBusLine.getLineId();//上下行 0：下行 1：上行
//            int flag=0;
//            if(sxx==0){
//                while (iterator.hasNext()) {
//                    BusBean businfo = (BusBean)iterator.next();
//                    if(businfo.getSxx().equals("0")){
//                        float leftPos = businfo.getLeftStation();
//                        if(leftPos+1==upStation&&upStation!=0){
//                            this.PlayRingTone();
//                        }
//                    }
//                }
//            }else{//----------------------上行  序号从大到小
//                while (iterator.hasNext()) {
//                    BusBean businfo = (BusBean)iterator.next();
//                    if(businfo.getSxx().equals("1")){
//                        float leftPos = businfo.getLeftStation();
//                        if(leftPos-1==upStation&&upStation!=0){
//                            this.PlayRingTone();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//
//    /**
//     * 播放系统声音
//     * @param ctx
//     * @param type
//     * @throws InterruptedException
//     */
//    public void PlayRingTone() {
//        for(int i=0;i<3;i++){
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(SmartBusApp.getInstance().getApplicationContext(), notification);
//            r.play();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        upStation=0;
//        invalidate();
//    }
//
//
//
//
//
//}
