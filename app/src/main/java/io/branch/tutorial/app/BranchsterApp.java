package io.branch.tutorial.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.branch.referral.Branch;

/**
 * Created by lorence on 11/01/2018.
 *
 */

public class BranchsterApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Branch.getAutoInstance(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

