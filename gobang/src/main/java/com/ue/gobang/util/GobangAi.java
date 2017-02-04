package com.ue.gobang.util;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//算法核心类，算法的主体思想分三个步骤，
//第一步：根据双方的当前的形势循环地假设性的分别给自己和对方下一子（在某个范围内下子），并判断此棋子能带来的形势上的变化，如能不能冲4，能不能形成我方或敌方双3等，
//第二步：根据上一步结果，组合每一步棋子所带来的所有结果（如某一步棋子可能形成我方1个活3，1个冲4（我叫它半活4）等），包括敌方和我方的。
//第三步：根据用户给的规则对上一步结果进行排序，并选子（有进攻形、防守形规则）
public class GobangAi {
    private static final String TAG="GobangAi";
    // 四个方向，横- 、纵| 、正斜/ 、反斜\
    private static final int HENG = 0;
    private static final int ZHONG = 1;
    private static final int ZHENG_XIE = 2;
    private static final int FAN_XIE = 3;
    //往前往后
    private static final boolean FORWARD = true;
    private static final boolean BACKWARD = false;
    //限定电脑计算范围，如果整个棋盘计算，性能太差，目前是根据所有已下的棋子的边界值加RANGE_STEP值形成，目前为1
    private int RANGE_STEP = 1;
    //棋盘最大横坐标和纵标
    protected int maxX;
    protected int maxY;

    public GobangAi(int maxX, int maxY){
        this.maxX=maxX;
        this.maxY=maxY;
    }

    public void setRangeStep(int rangeStep){
        this.RANGE_STEP=rangeStep;
    }

    //标示分析结果当前点位是两头通（ALIVE）还是只有一头通（HALF_ALIVE），封死的棋子分析过程自动屏蔽，不作为待选棋子
    private static final int ALIVE = 1;
    private static final int HALF_ALIVE = 0;

    //计算范围，太大的范围会有性能问题
    private class calculateRange{
        int xStart,yStart,xStop,yStop;
        private calculateRange(int xStart, int yStart, int xStop, int yStop) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xStop = xStop;
            this.yStop = yStop;
        }
    }

    calculateRange currentRange = new calculateRange(0, 0, 0, 0);
    private void initRange(ArrayList<Point> comuters, ArrayList<Point> humans){
        currentRange.xStart = humans.get(0).x-RANGE_STEP;
        currentRange.yStart = humans.get(0).y-RANGE_STEP;
        currentRange.xStop = humans.get(0).x+RANGE_STEP;
        currentRange.yStop = humans.get(0).y+RANGE_STEP;
        for (Point point : humans) {
            if(point.x-RANGE_STEP<currentRange.xStart){
                currentRange.xStart = point.x-RANGE_STEP;
            }else if(point.x+RANGE_STEP>currentRange.xStop){
                currentRange.xStop = point.x+RANGE_STEP;
            }
            if(point.y-RANGE_STEP<currentRange.yStart){
                currentRange.yStart = point.y-RANGE_STEP;
            }else if(point.y+RANGE_STEP>currentRange.yStop){
                currentRange.yStop = point.y+RANGE_STEP;
            }
        }
        for (Point point : comuters) {
            if(point.x-RANGE_STEP<currentRange.xStart){
                currentRange.xStart = point.x-RANGE_STEP;
            }else if(point.x+RANGE_STEP>currentRange.xStop){
                currentRange.xStop = point.x+RANGE_STEP;
            }
            if(point.y-RANGE_STEP<currentRange.yStart){
                currentRange.yStart = point.y-RANGE_STEP;
            }else if(point.y+RANGE_STEP>currentRange.yStop){
                currentRange.yStop = point.y+RANGE_STEP;
            }
        }

        //如果范围扩大后超过了棋盘，则等于棋盘
        currentRange.xStart=currentRange.xStart<0?0:currentRange.xStart;
        currentRange.yStart=currentRange.yStart<0?0:currentRange.yStart;
        currentRange.xStop=currentRange.xStop>=maxX?maxX-1:currentRange.xStop;
        currentRange.yStop=currentRange.yStop>=maxY?maxY-1:currentRange.yStop;
    }

    // 分析当前形式的入口方法，分析总共分三个步骤，第三步骤可由子类干预以作难度控制
    public Point doAnalysis(ArrayList<Point> comuters, ArrayList<Point> humans, ArrayList<Point> allFreePoints) {
        if(humans.size()==1){//第一步
            return getFirstPoint(humans);
        }

        //初始化计算范围
        initRange(comuters, humans);

        //清除以前的结果
        initAnalysisResults();
        // 开始分析，扫描所有空白点，形成第一次分析结果
        Point bestPoint = doFirstAnalysis(comuters, humans,allFreePoints);
        if(bestPoint!=null){
            //System.out.println("这个棋子最重要，只能下这个棋子");
            return bestPoint;
        }
        // 分析第一次结果，找到自己的最佳点位
        bestPoint = doComputerSencondAnalysis(computerFirstResults,computerSencodResults);
        if(bestPoint!=null){
            //System.out.println("快要赢了，就下这个棋子");
            return bestPoint;
        }
        computerFirstResults.clear();
        System.gc();
        // 分析第一次结果，找到敌人的最佳点位
        bestPoint = doHumanSencondAnalysis(humanFirstResults,humanSencodResults);
        if(bestPoint!=null){
            //System.out.println("再不下这个棋子就输了");
            return bestPoint;
        }
        humanFirstResults.clear();
        System.gc();
        //没找到绝杀点，第三次结果分析
        return doThirdAnalysis();
    }


    //下第一步棋子，不需要复杂的计算，根据人类第一步棋子X值减1完成
    private Point getFirstPoint(ArrayList<Point> humans) {
        Point point = humans.get(0);
        if(point.x==0 || point.y==0 || point.x==maxX && point.y==maxY)
            return new Point(maxX/2, maxY/2);
        else{
            return new Point(point.x-1,point.y);
        }
    }

//	private int debugx,debugy;//用于DEBUG

    // 开始分析，扫描所有空白点，形成第一次分析结果
    private Point doFirstAnalysis(ArrayList<Point> comuters, ArrayList<Point> humans,ArrayList<Point> allFreePoints){
        int size = allFreePoints.size();
        Point computerPoint = null;
        Point humanPoint = null;
        int x,y;
        FirstAnalysisResult firstAnalysisResult;
        for (int i = 0; i < size; i++) {
            computerPoint = allFreePoints.get(i);
            //先把X、Y坐标记下来，因为在分析过程中会改变原来的对象
            x = computerPoint.x;
            y = computerPoint.y;
            if(x<currentRange.xStart || x>currentRange.xStop || y<currentRange.yStart || y>currentRange.yStop){
                continue;
            }

            //尝试在此位置上下一个棋子，并分析在“横向”这个方向上我方可形成的状态，如活4，活3，半活4，活2等所有状态
            firstAnalysisResult = tryAndCountResult(comuters,humans, computerPoint, HENG);
            computerPoint.set(x,y);//回复点位的原值，以供下次分析
            if(firstAnalysisResult!=null){//无返回结果此方向上不可能达到五个棋子，
                if(firstAnalysisResult.count==5)//等于5表示在此点上下棋子即可连成5个，胜利了，不再往下进行分析
                    return computerPoint;
                //记录第一次分析结果
                addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
            }

            //在“纵向”这个方向上重复上面的步骤
            firstAnalysisResult = tryAndCountResult(comuters,humans, computerPoint, ZHONG);
            computerPoint.set(x,y);
            if(firstAnalysisResult!=null){//死棋，不下
                if(firstAnalysisResult.count==5)
                    return computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
            }

            //正斜向
            firstAnalysisResult = tryAndCountResult(comuters,humans, computerPoint, ZHENG_XIE);
            computerPoint.set(x,y);
            if(firstAnalysisResult!=null){//死棋，不下
                if(firstAnalysisResult.count==5)
                    return computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
            }

            //反斜向
            firstAnalysisResult = tryAndCountResult(comuters,humans, computerPoint, FAN_XIE);
            computerPoint.set(x,y);
            if(firstAnalysisResult!=null){//死棋，不下
                if(firstAnalysisResult.count==5)
                    return computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
            }

            //在“横向”上分析此棋子可在敌方形成如何状态，如敌方的活3、半活4等
            firstAnalysisResult = tryAndCountResult(humans,comuters, computerPoint, HENG);
            computerPoint.set(x,y);
            if(firstAnalysisResult!=null){//死棋，不下
                if(firstAnalysisResult.count==5)
                    humanPoint = computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
            }

            //“纵向”
            firstAnalysisResult = tryAndCountResult(humans,comuters, computerPoint, ZHONG);
            computerPoint.set(x,y);
            if(firstAnalysisResult!=null){//死棋，不下
                if(firstAnalysisResult.count==5)
                    humanPoint = computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
            }

            //“正斜”
            firstAnalysisResult = tryAndCountResult(humans,comuters, computerPoint, ZHENG_XIE);
            computerPoint.set(x,y);
            if(firstAnalysisResult!=null){//死棋，不下
                if(firstAnalysisResult.count==5)
                    humanPoint = computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
            }

            //“反斜”
            firstAnalysisResult = tryAndCountResult(humans,comuters, computerPoint, FAN_XIE);
            computerPoint.set(x,y);
            if(firstAnalysisResult!=null){//死棋，不下
                if(firstAnalysisResult.count==5)
                    humanPoint = computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
            }
        }
        //如果没有绝杀棋子，第一次分析不需要返回结果
        return humanPoint;
    }

    //第二次分析，分析第一次形成的结果，第一次分析结果会把一步棋在四个方向上可形成的结果生成最多四个FirstAnalysisResult对象（敌我各四）
    //这里要把这四个对象组合成一个SecondAnalysisResult对象，
    private Point doComputerSencondAnalysis(Map<Point,ArrayList<FirstAnalysisResult>> firstResults,ArrayList<SecondAnalysisResult> sencodResults) {
        ArrayList<FirstAnalysisResult> list = null;
        SecondAnalysisResult sr = null;
        for (Point p : firstResults.keySet()) {
            sr = new SecondAnalysisResult(p);
            list = firstResults.get(p);
            for (FirstAnalysisResult result : list) {
                if(result.count==4){
                    if(result.aliveState==ALIVE){//经过前面的过滤，双方都排除了绝杀棋，有活4就下这一步了，再下一步就赢了
                        return result.point;//如果有绝杀，第一轮已返回，在此轮活4已经是好的棋子，直接返回，不再往下分析
                    }else{
                        sr.halfAlive4 ++;
                        computer4HalfAlives.add(sr);
                    }
                }else if(result.count==3){
                    if(result.aliveState==ALIVE){
                        sr.alive3++;
                        if(sr.alive3==1){
                            computer3Alives.add(sr);
                        }else{
                            computerDouble3Alives.add(sr);
                        }
                    }else{
                        sr.halfAlive3++;
                        computer3HalfAlives.add(sr);
                    }
                }else{//半活2在第一阶段已被排除，不再处理
                    sr.alive2++;
                    if(sr.alive2==1){
                        computer2Alives.add(sr);
                    }else{
                        computerDouble2Alives.add(sr);
                    }
                }
            }
            sencodResults.add(sr);
        }
        //没有找到活4
        return null;
    }

    //这个方法和上面的基本一样，但为了性能，少作几次判断，将人类和电脑的分开了
    private Point doHumanSencondAnalysis(Map<Point,ArrayList<FirstAnalysisResult>> firstResults,ArrayList<SecondAnalysisResult> sencodResults) {
        ArrayList<FirstAnalysisResult> list = null;
        SecondAnalysisResult sr = null;
        for (Point p : firstResults.keySet()) {
            sr = new SecondAnalysisResult(p);
            list = firstResults.get(p);
            for (FirstAnalysisResult result : list) {
                if(result.count==4){
                    if(result.aliveState==ALIVE){
                        human4Alives.add(sr);
                    }else{
                        sr.halfAlive4 ++;
                        human4HalfAlives.add(sr);
                    }
                }else if(result.count==3){
                    if(result.aliveState==ALIVE){
                        sr.alive3++;
                        if(sr.alive3==1){
                            human3Alives.add(sr);
                        }else{
                            humanDouble3Alives.add(sr);
                        }
                    }else{
                        sr.halfAlive3++;
                        human3HalfAlives.add(sr);
                    }
                }else{
                    sr.alive2++;
                    if(sr.alive2==1){
                        human2Alives.add(sr);
                    }else{
                        humanDouble2Alives.add(sr);
                    }
                }
            }
            sencodResults.add(sr);
        }
        //没有找到活4
        return null;
    }

    private void sleep(int miniSecond){
        try {
            Thread.sleep(miniSecond);
        } catch (InterruptedException e) {
        }
    }


    //第三次分析，双方都不可以制造活4，找双活3棋子，不行就找半活4，再不行就找单活3，双活2
    private Point doThirdAnalysis() {
        if(!computer4HalfAlives.isEmpty()){
            return computer4HalfAlives.get(0).point;
        }
        System.gc();
        sleep(300);
        Collections.sort(computerSencodResults);
        System.gc();

        //即将单活4，且我没有半活4以上的，只能堵
        Point mostBest = getBestPoint(human4Alives, computerSencodResults);
        if(mostBest!=null)
            return mostBest;

        Collections.sort(humanSencodResults);
        System.gc();

        mostBest = getBestPoint();
        if(mostBest!=null)
            return mostBest;

        //拿出各自排第一的，谁好就下谁
        return computerSencodResults.get(0).point;
    }

    //子类实现这个方法，并改变其顺序可以实现防守为主还是猛攻
    protected Point getBestPoint(){
        //即将单活4，且我没有半活4以上的，只能堵
        Point mostBest = getBestPoint(computerDouble3Alives, humanSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(computer3Alives, humanSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(humanDouble3Alives, computerSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(human3Alives, computerSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(computerDouble2Alives, humanSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(computer2Alives, humanSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(computer3HalfAlives, humanSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(human4HalfAlives, computerSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(humanDouble2Alives, computerSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(human2Alives, computerSencodResults);
        if(mostBest!=null)
            return mostBest;

        mostBest = getBestPoint(human3HalfAlives, computerSencodResults);
        return mostBest;
    }


    //第三次分析的最后一步，第二次结果已经过排序，在此可以从前面选出最好的棋子
    protected Point getBestPoint(ArrayList<SecondAnalysisResult> myBest,ArrayList<SecondAnalysisResult> yourSencodResults){
        if(!myBest.isEmpty()){
            if(myBest.size()>1){
                for (SecondAnalysisResult your : yourSencodResults) {
                    if(myBest.contains(your)){
                        return your.point;
                    }
                }
                return myBest.get(0).point;
            }else{
                return myBest.get(0).point;
            }
        }
        return null;
    }


    //第一次分析结果
    private final Map<Point,ArrayList<FirstAnalysisResult>> computerFirstResults = new HashMap<Point,ArrayList<FirstAnalysisResult>>();
    private final Map<Point,ArrayList<FirstAnalysisResult>> humanFirstResults = new HashMap<Point,ArrayList<FirstAnalysisResult>>();
    //第二次总结果
    protected final ArrayList<SecondAnalysisResult> computerSencodResults = new ArrayList<SecondAnalysisResult>();
    protected final ArrayList<SecondAnalysisResult> humanSencodResults = new ArrayList<SecondAnalysisResult>();
    //第二次分结果，电脑
    protected final ArrayList<SecondAnalysisResult> computer4HalfAlives = new ArrayList<SecondAnalysisResult>(2);
    protected final ArrayList<SecondAnalysisResult> computerDouble3Alives = new ArrayList<SecondAnalysisResult>(4);
    protected final ArrayList<SecondAnalysisResult> computer3Alives = new ArrayList<SecondAnalysisResult>(5);
    protected final ArrayList<SecondAnalysisResult> computerDouble2Alives = new ArrayList<SecondAnalysisResult>();
    protected final ArrayList<SecondAnalysisResult> computer2Alives = new ArrayList<SecondAnalysisResult>();
    protected final ArrayList<SecondAnalysisResult> computer3HalfAlives = new ArrayList<SecondAnalysisResult>();

    //第二次分结果，人类
    protected final ArrayList<SecondAnalysisResult> human4Alives = new ArrayList<SecondAnalysisResult>(2);
    protected final ArrayList<SecondAnalysisResult> human4HalfAlives = new ArrayList<SecondAnalysisResult>(5);
    protected final ArrayList<SecondAnalysisResult> humanDouble3Alives = new ArrayList<SecondAnalysisResult>(2);
    protected final ArrayList<SecondAnalysisResult> human3Alives = new ArrayList<SecondAnalysisResult>(10);
    protected final ArrayList<SecondAnalysisResult> humanDouble2Alives = new ArrayList<SecondAnalysisResult>(3);
    protected final ArrayList<SecondAnalysisResult> human2Alives = new ArrayList<SecondAnalysisResult>();
    protected final ArrayList<SecondAnalysisResult> human3HalfAlives = new ArrayList<SecondAnalysisResult>();

    //第一次分析前清空上一步棋子的分析结果
    private void initAnalysisResults(){
        computerFirstResults.clear();
        humanFirstResults.clear();
        //第二次总结果
        computerSencodResults.clear();
        humanSencodResults.clear();
        //第二次分结果
        computer4HalfAlives.clear();
        computerDouble3Alives.clear();
        computer3Alives.clear();
        computerDouble2Alives.clear();
        computer2Alives.clear();
        computer3HalfAlives.clear();

        //第二次分结果，人类
        human4Alives.clear();
        human4HalfAlives.clear();
        humanDouble3Alives.clear();
        human3Alives.clear();
        humanDouble2Alives.clear();
        human2Alives.clear();
        human3HalfAlives.clear();
        System.gc();
    }

    //加入到第一次分析结果中
    private void addToFirstAnalysisResult(FirstAnalysisResult result,Map<Point,ArrayList<FirstAnalysisResult>> dest){
        if(dest.containsKey(result.point)){
            dest.get(result.point).add(result);
        }else{
            ArrayList<FirstAnalysisResult> list = new ArrayList<FirstAnalysisResult>(1);
            list.add(result);
            dest.put(result.point, list);
        }
    }


    //第一次分析结果类
    private class FirstAnalysisResult{
        //连续数
        int count;
        //点位
        Point point;
        //方向
        int direction;
        //状态
        int aliveState;
        private FirstAnalysisResult(int count, Point point, int direction) {
            this(count, point, direction, ALIVE);
        }

        private FirstAnalysisResult(int count, Point point, int direction,int aliveState) {
            this.count = count;
            this.point = point;
            this.direction = direction;
            this.aliveState = aliveState;
        }

        private FirstAnalysisResult init(Point point,int direction,int aliveState){
            this.count = 1;
            this.point = point;
            this.direction = direction;
            this.aliveState = aliveState;
            return this;
        }

        private FirstAnalysisResult cloneMe(){
            return new FirstAnalysisResult(count, point, direction,aliveState);
        }
    }

    //第二次分析结果类
    class SecondAnalysisResult implements Comparable<SecondAnalysisResult>{
        int alive4 = 0;
        //活3数量
        int alive3 = 0;
        //半活4，一头封的
        int halfAlive4 = 0;
        //半活3，一头封的
        int halfAlive3 = 0;
        //活2数量
        int alive2 = 0;
        //点位
        Point point;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((point == null) ? 0 : point.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            SecondAnalysisResult other = (SecondAnalysisResult) obj;
            if (point == null) {
                if (other.point != null)
                    return false;
            } else if (!point.equals(other.point))
                return false;
            return true;
        }

        private SecondAnalysisResult(Point point) {
            this.point = point;
        }

        //第三次分析时，对第二次分析结果进行排序，此为排序回调函数
        @Override
        public int compareTo(SecondAnalysisResult another) {
            return compareTowResult(this, another);
        }
    }

    //返加-1则第一个参数优先，1则第二个参数优先，0则按原来顺序
    private int compareTowResult(SecondAnalysisResult oneResult,SecondAnalysisResult another){
        if(oneResult.alive4>another.alive4){
            return -1;
        }
        if(oneResult.alive4<another.alive4){
            return 1;
        }
        if(oneResult.halfAlive4>another.halfAlive4){
            return -1;
        }
        if(oneResult.halfAlive4<another.halfAlive4){
            return 1;
        }
        if(oneResult.alive3>another.alive3){
            return -1;
        }
        if(oneResult.alive3<another.alive3){
            return 1;
        }
        if(oneResult.alive2>another.alive2){
            return -1;
        }
        if(oneResult.alive2<another.alive2){
            return 1;
        }
        if(oneResult.halfAlive3>another.halfAlive3){
            return -1;
        }
        if(oneResult.halfAlive3>another.halfAlive3){
            return 1;
        }
        return 0;
    }


    //一个临时对象，供第一次分析时临时存放分析结果使用，如果分析出有活1以上（不含）的结果，则调用其cloneMe方法获得结果，否则抛弃此结果
    private final FirstAnalysisResult far = new FirstAnalysisResult(1, null, HENG);
    // 分析如果在当前位下一子，会形成某个方向上多少个子，参数：当前己方已下的所有点，当前要假设的点，需要判断的方向
    private FirstAnalysisResult tryAndCountResult(List<Point> myPoints,List<Point> enemyPoints, Point point,int direction) {
        int x = point.x;
        int y = point.y;
        FirstAnalysisResult fr = null;

        int maxCountOnThisDirection = maxCountOnThisDirection(point, enemyPoints, direction, 1);
        if(maxCountOnThisDirection<5){
            //无意义的棋子
            return null;//此方向不足五个空位，已排除己方已下的棋子
        }else if(maxCountOnThisDirection==5){
            //半死状态，当是一头通
            fr = far.init(point, direction,HALF_ALIVE);
        }else{
            //两头皆通
            fr = far.init(point, direction,ALIVE);
        }

        //在前和后的方向上计算一次
        point.set(x,y);
        countPoint(myPoints,enemyPoints,point,fr,direction,FORWARD);
        point.set(x,y);//不能漏了这里的一句
        countPoint(myPoints,enemyPoints,point,fr,direction,BACKWARD);

        if(fr.count<=1 || (fr.count==2 && fr.aliveState==HALF_ALIVE)){//活1，半活2及其以下结果，抛弃
            return null;
        }
        //返回复制的结果
        return fr.cloneMe();
    }

    //棋子出了墙
    private boolean isOutSideOfWall(Point point,int direction){
        if(direction==HENG){
            return point.x<0 || point.x>=maxX;//最大的X和Y值均在墙外所以用等号
        }else if(direction==ZHONG){
            return point.y<0 || point.y>=maxY;
        }else{//这里可能有问题
            return point.x<0 || point.y<0 || point.x>=maxX || point.y>=maxY;
        }
    }

    private Point pointToNext(Point point,int direction,boolean forward){
        switch (direction) {
            case HENG:
                if(forward)
                    point.x++;
                else
                    point.x--;
                break;
            case ZHONG:
                if(forward)
                    point.y++;
                else
                    point.y--;
                break;
            case ZHENG_XIE:
                if(forward){
                    point.x++;
                    point.y--;
                }else{
                    point.x--;
                    point.y++;
                }
                break;
            case FAN_XIE:
                if(forward){
                    point.x++;
                    point.y++;
                }else{
                    point.x--;
                    point.y--;
                }
                break;
        }
        return point;
    }

    //在某个方向（八个中的一个）可下多少棋子，这个方法是第一分析中的核心方法
    private void countPoint(List<Point> myPoints, List<Point> enemyPoints, Point point, FirstAnalysisResult fr,int direction,boolean forward) {
        if(myPoints.contains(pointToNext(point,direction,forward))){
            fr.count ++;
            if(myPoints.contains(pointToNext(point,direction,forward))){
                fr.count ++;
                if(myPoints.contains(pointToNext(point,direction,forward))){
                    fr.count ++;
                    if(myPoints.contains(pointToNext(point,direction,forward))){
                        fr.count ++;
                    }else if(enemyPoints.contains(point) || isOutSideOfWall(point,direction)){
                        fr.aliveState=HALF_ALIVE;
                    }
                }else if(enemyPoints.contains(point) || isOutSideOfWall(point,direction)){
                    fr.aliveState=HALF_ALIVE;
                }
            }else if(enemyPoints.contains(point) || isOutSideOfWall(point,direction)){
                fr.aliveState=HALF_ALIVE;
            }
        }else if(enemyPoints.contains(point) || isOutSideOfWall(point,direction)){
            fr.aliveState=HALF_ALIVE;
        }
    }

    //在某个方向上是否还能下到满五个棋子
    private int maxCountOnThisDirection(Point point,List<Point> enemyPoints,int direction,int count){
        int x=point.x,y=point.y;
        switch (direction) {
            //横向
            case HENG:
                point.x=point.x-1;
                while (!enemyPoints.contains(point) && point.x>=0 && count<6) {
                    count ++;
                    point.x-=1;
                }

                point.x=x+1;
                while (!enemyPoints.contains(point) && point.x<maxX && count<6) {
                    count ++;
                    point.x+=1;
                }
                break;
            //纵向
            case ZHONG:
                point.y=y-1;
                while (!enemyPoints.contains(point) && point.y>=0) {
                    count ++;
                    point.y-=1;
                }

                point.y=y+1;
                while (!enemyPoints.contains(point) && point.y<maxY && count<6) {
                    count ++;
                    point.y+=1;
                }
                break;
            //正斜向 /
            case ZHENG_XIE:
                point.set(x-1,y+1);
                while (!enemyPoints.contains(point) && point.x>=0 && point.y<maxY) {
                    count ++;
                    point.set(point.x-1,point.y+1);
                }

                point.set(x+1,y-1);
                while (!enemyPoints.contains(point) && point.x<maxX && point.y>=0 && count<6) {
                    count ++;
                    point.set(point.x+1,point.y-1);
                }
                break;
            //反斜 /
            case FAN_XIE:
                point.set(x-1,y-1);
                while (!enemyPoints.contains(point) && point.x>=0 && point.y>=0) {
                    count ++;
                    point.set(point.x-1,point.y-1);
                }

                point.set(x+1,y+1);
                while (!enemyPoints.contains(point) && point.x<maxX && point.y<maxY && count<6) {
                    count ++;
                    point.set(point.x+1,point.y+1);
                }
                break;
        }
        return count;
    }
}