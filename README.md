# FlowLayout
自定义流布局可以设置留白的平分

直接传入布局view来展示
 for (int i = 0; i < strings.size(); i++) {
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.item_label_tag,
                    mFlowLayout, false);
            tv.setText(strings.get(i));
            mFlowLayout1.addView(tv);
        }
