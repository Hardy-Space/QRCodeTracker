package com.hualing.qrcodetracker.activities.operation_bcp.bcp_in;

import android.os.Bundle;

import com.hualing.qrcodetracker.R;
import com.hualing.qrcodetracker.activities.BaseActivity;
import com.hualing.qrcodetracker.activities.operation_wl.wl_in.WLInRKDInputActivity;
import com.hualing.qrcodetracker.util.AllActivitiesHolder;
import com.hualing.qrcodetracker.util.IntentUtil;
import com.hualing.qrcodetracker.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

public class BcpInMainActivity extends BaseActivity {


    @BindView(R.id.title)
    TitleBar mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLogic() {
        mTitle.setEvents(new TitleBar.AddClickEvents() {
            @Override
            public void clickLeftButton() {
                AllActivitiesHolder.removeAct(BcpInMainActivity.this);
            }

            @Override
            public void clickRightButton() {

            }
        });
    }

    @Override
    protected void getDataFormWeb() {

    }

    @Override
    protected void debugShow() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_bcp_in_main;
    }

    @OnClick(R.id.createRKD)
    public void onViewClicked() {
        IntentUtil.openActivity(this, BCPInRKDInputActivity.class);

    }

}
