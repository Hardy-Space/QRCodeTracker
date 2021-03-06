package com.hualing.qrcodetracker.activities.main;

import android.os.Bundle;
import android.widget.Toast;

import com.hualing.qrcodetracker.R;
import com.hualing.qrcodetracker.activities.BaseActivity;
import com.hualing.qrcodetracker.aframework.yoni.ActionResult;
import com.hualing.qrcodetracker.aframework.yoni.YoniClient;
import com.hualing.qrcodetracker.bean.LoginParams;
import com.hualing.qrcodetracker.bean.LoginResult;
import com.hualing.qrcodetracker.dao.MainDao;
import com.hualing.qrcodetracker.global.GlobalData;
import com.hualing.qrcodetracker.global.TheApplication;
import com.hualing.qrcodetracker.util.AllActivitiesHolder;
import com.hualing.qrcodetracker.util.IntentUtil;
import com.hualing.qrcodetracker.util.JPushUtil;
import com.hualing.qrcodetracker.util.SharedPreferenceUtil;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LaunchActivity extends BaseActivity {

    private static final long DELAY = 3000;
    private MainDao mainDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLogic() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//                if (SharedPreferenceUtil.getUserType()== UserType.NON_SELECTED) {
//                    IntentUtil.openActivity(LaunchActivity.this,UserTypePickActivity.class);
//                }else if(SharedPreferenceUtil.getUserType()== UserType.EMPLOYEE){
                    if (SharedPreferenceUtil.ifHasLocalUserInfo()) {
                        //测试
//                        IntentUtil.openActivity(LaunchActivity.this,EmployeeMainActivity.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toLogin();
                            }
                        });
                    }else {
                        IntentUtil.openActivity(LaunchActivity.this,EmployeeLoginActivity.class);
                        AllActivitiesHolder.removeAct(LaunchActivity.this);
                    }
//                }else if (SharedPreferenceUtil.getUserType()== UserType.GUEST){
//                    IntentUtil.openActivity(LaunchActivity.this,GuestMainActivity.class);
//                }
            }
        },DELAY);
    }

    private void toLogin(){
        mainDao = YoniClient.getInstance().create(MainDao.class);

        final LoginParams loginParams = new LoginParams();
        loginParams.setUserName(SharedPreferenceUtil.getUserInfo()[0]);
        loginParams.setPassword(SharedPreferenceUtil.getUserInfo()[1]);

        Observable.create(new ObservableOnSubscribe<ActionResult<LoginResult>>() {
            @Override
            public void subscribe(ObservableEmitter<ActionResult<LoginResult>> e) throws Exception {
                ActionResult<LoginResult> nr = mainDao.login(loginParams);
                e.onNext(nr);
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Consumer<ActionResult<LoginResult>>() {
                    @Override
                    public void accept(ActionResult<LoginResult> result) throws Exception {
                        if (result.getCode() != 0) {
                            Toast.makeText(TheApplication.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                            IntentUtil.openActivity(LaunchActivity.this,EmployeeLoginActivity.class);
                        } else {
                            LoginResult loginResult = result.getResult();
                            GlobalData.userId = String.valueOf(loginResult.getUserId());
                            GlobalData.userName = loginResult.getUserName();
                            GlobalData.realName = loginResult.getTrueName();

                            //设置JPush别名
                            JPushUtil.setAlias(LaunchActivity.this,GlobalData.realName);

                            //之后获取和用户相关的服务就不需要额外传userId了
                            YoniClient.getInstance().setUser(GlobalData.userId);
                            IntentUtil.openActivity(LaunchActivity.this,EmployeeMainActivity.class);
                        }
                        AllActivitiesHolder.removeAct(LaunchActivity.this);
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
        return R.layout.activity_launch;
    }
}
