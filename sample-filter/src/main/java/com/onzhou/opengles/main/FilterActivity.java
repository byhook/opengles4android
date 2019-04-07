package com.onzhou.opengles.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.onzhou.common.table.RouteTable;
import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.filter.GrayFilter;
import com.onzhou.opengles.filter.OriginFilter;
import com.onzhou.opengles.filter.QuarterMirrorFilter;
import com.onzhou.opengles.view.OpenGLView;
import com.onzhou.opengles.filter.R;

/**
 * @anchor: andy
 * @date: 2019-03-15
 * @description:
 */
@Route(path = RouteTable.PAGE_FILTER)
public class FilterActivity extends AbsBaseActivity {

    private ViewGroup mRootLayer;

    private OpenGLView mGlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setupViews();
    }

    private void setupViews() {
        mRootLayer = (ViewGroup) findViewById(R.id.linear_root_layer);

        mGlView = new OpenGLView(this);
        mRootLayer.addView(mGlView, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.filter_default) {
            mGlView.setFilter(new OriginFilter());
        } else if (itemId == R.id.filter_gray) {
            mGlView.setFilter(new GrayFilter());
        } else if (itemId == R.id.filter_quarter_mirror) {
            mGlView.setFilter(new QuarterMirrorFilter());
        }
        return super.onOptionsItemSelected(item);
    }
}