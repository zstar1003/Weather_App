package com.baidu.location.demo.view;

import android.content.Context;
import android.widget.TextView;

import com.baidu.location.demo.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * 自定义图表的MarkerView(点击坐标点，弹出提示框)
 */
class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private String unitName;
    /**
     *
     * @param context
     *            上下文
     * @param layoutResource
     *            资源文件
     * @param unitName
     *            Y轴数值计量单位名称
     */
    public CustomMarkerView(Context context, int layoutResource, final String unitName) {
        super(context, layoutResource);
        // 显示布局中的文本框
        tvContent = (TextView) findViewById(R.id.txt_tips);
        this.unitName = unitName;
    }

    // 每次markerview回调重绘，可以用来更新内容
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // 设置Y周数据源对象Entry的value值为显示的文本内容
        tvContent.setText("" + e.getVal()+unitName);
    }

    @Override
    public int getXOffset(float xpos) {
        // 水平居中
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // 提示框在坐标点上方显示
        return -getHeight();
    }
}
