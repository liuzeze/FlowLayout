package gctraveltools.jsj.com.cn.flowlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.ArrayList;

import gctraveltools.jsj.com.cn.flowlayout.view.FlowLayout;

public class MainActivity extends AppCompatActivity {

    private FlowLayout mFlowLayout;
    private FlowLayout mFlowLayout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFlowLayout = (FlowLayout) findViewById(R.id.FlowLayout);
        mFlowLayout1 = (FlowLayout) findViewById(R.id.FlowLayout1);


        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i % 4 == 3) {
                strings.add("这是一个大条目" + i);
            } else {
                strings.add("条目" + i);
            }
        }
        mFlowLayout1.setRemainSpacing(true);
        mFlowLayout.setRemainSpacing(false);
        mFlowLayout.removeAllViews();
        mFlowLayout1.removeAllViews();
        for (int i = 0; i < strings.size(); i++) {
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.item_label_tag,
                    mFlowLayout, false);
            tv.setText(strings.get(i));
            mFlowLayout1.addView(tv);
        } for (int i = 0; i < strings.size(); i++) {
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.item_label_tag,
                    mFlowLayout, false);
            tv.setText(strings.get(i));
            mFlowLayout.addView(tv);
        }
    }
}
