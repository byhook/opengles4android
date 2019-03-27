package com.onzhou.opengles.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.onzhou.common.router.PageRouter;
import com.onzhou.opengles.base.AbsBaseActivity;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class SampleActivity extends AbsBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengles_sample);
    }


    public void onBasisClick(View view) {
        PageRouter.getInstance().routeBasisPage();
    }
}
