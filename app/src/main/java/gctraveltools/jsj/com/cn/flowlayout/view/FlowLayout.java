package gctraveltools.jsj.com.cn.flowlayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * -------- 日期 ---------- 维护人 ------------ 变更内容 --------
 * 刘泽			    新增 类
 * 刘泽			    流布局可实现平分留白
 */
public class FlowLayout extends ViewGroup {
    private final int DEFAULT_SPACING = 15;//默认的间距的值
    private int horizontalSpacing = DEFAULT_SPACING;//水平间距
    private int verticalSpacing = DEFAULT_SPACING;//水平间距

    //用来存放所有的Line对象
    private ArrayList<Line> lineList = new ArrayList<Line>();
    private boolean mRemainSpacing = false;

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context) {
        super(context);
    }

    /**
     * 设置水平间距
     *
     * @param horizontalSpacing
     */
    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    public void setRemainSpacing(boolean remainSpacing) {
        mRemainSpacing = remainSpacing;
    }

    /**
     * 设置垂直间距
     *
     * @param verticalSpacing
     */
    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    /**
     * 遍历所有的TextView，去分行,排座位表()
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //先清除集合，目的是为了防止onMeasure执行多次
        lineList.clear();
        //1.获取FlowLayout的宽度
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        //2.获取实际用于比较的宽度，就是总宽度-两边的内边距
        int noPaddingWidth = width - getPaddingLeft() - getPaddingRight();

        //3.遍历所有的TextView，
        Line line = new Line();//准备Line对象，用于存放TextView
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);//获取子View
            childView.measure(0, 0);//通知childView的onMeasure执行

            //4.如果当前line中木有TextView，那么直接放入，因为我们要保证每行必须至少有一个
            if (line.getViewList().size() == 0) {
                line.addLineView(childView);//将childView放入line中
            } else if (line.getLineWidth() + horizontalSpacing + childView.getMeasuredWidth() > noPaddingWidth) {
                //5.如果childView的宽+当前line的宽+水平间距大于noPaddingWidth,说明childView需要放入下一行
                lineList.add(line);//先保存之前的Line，否则会造成Line丢失

                line = new Line();//创建新的Line
                line.addLineView(childView);//将childView存放到新的Line中
            } else {
                //6.如果小于noPaddingWidth,则直接加入到当前Line中
                line.addLineView(childView);
            }
            //7.如果当前是最后一个子View，则将line对象存入lineList中，否则会造成最后的Line丢失
            if (i == (getChildCount() - 1)) {
                lineList.add(line);//将最后的Line对象保存
            }
        }

        //for循环结束了，lineList中存放所有的Line对象，而line对象中又记录当前行的所有TextView;
        //为了能容纳所有line的View，需要计算flowLayout的高度
        int height = getPaddingTop() + getPaddingBottom();//首先算上padding
        for (int i = 0; i < lineList.size(); i++) {
            height += lineList.get(i).getLineHeight();//再加上所有行的高度
        }
        height += (lineList.size() - 1) * verticalSpacing;//最后算上行的垂直间距
        //设置当前FLowLayout的高度
        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
    }

    /**
     * 对号入座，将每个Line的所有的TextView摆放到对应的位置上,
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        for (int i = 0; i < lineList.size(); i++) {
            Line line = lineList.get(i);//得到Line对象

            //从第二行开始，每行的top总是比上一行的top多一个行高和垂直间距
            if (i > 0) {
                paddingTop += lineList.get(i - 1).getLineHeight() + verticalSpacing;
            }

            ArrayList<View> viewList = line.getViewList();//获取每行的View的集合
            //1.计算出每行的留白的宽度
            int remainSpacing = getLineRemainSpacing(line);
            //2.计算每个view可以平分到多少
            float perSpacing = remainSpacing * 1f / viewList.size();

            for (int j = 0; j < viewList.size(); j++) {
                View childView = viewList.get(j);
                //3.每个View需要将平分到的perSpacing增加到自身的宽度上面
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (childView.getMeasuredWidth() + ((mRemainSpacing && i != lineList.size() - 1) ? perSpacing : 0))
                        , View.MeasureSpec.EXACTLY);
                childView.measure(widthMeasureSpec, 0);

                if (j == 0) {
                    //每行的第一个View
                    childView.layout(paddingLeft, paddingTop, paddingLeft + childView.getMeasuredWidth(),
                            paddingTop + childView.getMeasuredHeight());
                } else {
                    //从第二个开始，left总是比前一个View的right多一个horizontalSpacing
                    View preView = viewList.get(j - 1);//获取前一个View
                    int left = preView.getRight() + horizontalSpacing;
                    childView.layout(left, preView.getTop(), left + childView.getMeasuredWidth(),
                            preView.getBottom());
                }
            }
        }
    }

    /**
     * 获取指定Line的留白的值
     *
     * @param line
     * @return
     */
    private int getLineRemainSpacing(Line line) {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - line.getLineWidth();
    }

    /**
     * 封装每一行的数据
     *
     * @author Administrator
     */
    class Line {
        private ArrayList<View> viewList = new ArrayList<View>();//用来存放当前行中所有的TextView
        private int width;//当前行所有TextView的宽+水平间距
        private int height;//当前行的高度

        /**
         * 添加View到viewList中
         *
         * @param view
         */
        public void addLineView(View view) {
            if (!viewList.contains(view)) {
                viewList.add(view);

                //更新width的值
                if (viewList.size() == 1) {
                    //说明当前是第一个子View,那么width则直接赋值为当前view的宽度
                    width += view.getMeasuredWidth();
                } else {
                    //如果不是第一个，则在当前width的基础上+view的宽+horizontalSpacing
                    width += view.getMeasuredWidth() + horizontalSpacing;
                }
                //更新height的值
                height = Math.max(height, view.getMeasuredHeight());
            }
        }

        /**
         * 获取View的集合
         *
         * @return
         */
        public ArrayList<View> getViewList() {
            return viewList;
        }

        /**
         * 获取行的宽度
         *
         * @return
         */
        public int getLineWidth() {
            return width;
        }

        /**
         * 获取行高
         *
         * @return
         */
        public int getLineHeight() {
            return height;
        }
    }

}
