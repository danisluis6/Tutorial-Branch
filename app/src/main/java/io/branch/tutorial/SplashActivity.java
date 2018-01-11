package io.branch.tutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import io.branch.tutorial.util.MonsterPreferences;

/**
 * - EXPLORE
 *      -> $android_deeplink_path
 *      -> @link(https://docs.branch.io/pages/deep-linking/routing/)
 *
 * - LINK:
 *      -> @link(https://github.com/BranchMetrics/Branch-Example-Deep-Linking-Branchster-Android)
 */

public class SplashActivity extends Activity {

    private TextView txtLoading;
    private ImageView imgSplash1, imgSplash2;
    private final int ANIM_DURATION = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get loading messages from XML definitions.
        final String[] loadingMessages = getResources().getStringArray(R.array.loading_messages);
        txtLoading = findViewById(R.id.txtLoading);
        imgSplash1 = findViewById(R.id.imgSplashFactory1);
        imgSplash2 = findViewById(R.id.imgSplashFactory2);
        imgSplash2.setVisibility(View.INVISIBLE);
        imgSplash1.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                //If not Launched by clicking Branch link
                if (branchUniversalObject == null) {
                    proceedToAppTransparent();
                } else if (!branchUniversalObject.getMetadata().containsKey("$android_deeplink_path")) {
                    /*
                     * In case the clicked link has $android_deeplink_path the Branch will launch the MonsterViewer automatically since AutoDeeplinking feature is enabled.
                     * Launch Monster viewer activity if a link clicked without $android_deeplink_path
                     *
                     * Don't understand Android Application
                     */
                    MonsterPreferences prefs = MonsterPreferences.getInstance(getApplicationContext());
                    prefs.saveMonster(branchUniversalObject);
                    Intent intent = new Intent(SplashActivity.this, ViewerActivity.class);
                    intent.putExtra(ViewerActivity.MY_MONSTER_OBJ_KEY, prefs.getLatestMonsterObj());
                    startActivity(intent);
                    finish();
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    private void proceedToAppTransparent() {
        Animation animSlideIn = AnimationUtils.loadAnimation(this, R.anim.push_down_in);
        animSlideIn.setDuration(ANIM_DURATION);
        animSlideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MonsterPreferences prefs = MonsterPreferences.getInstance(getApplicationContext());
                Intent intent;
                if (prefs.getMonsterName() == null || prefs.getMonsterName().length() == 0) {
                    prefs.setMonsterName("");
                     intent = new Intent(SplashActivity.this, CreatorActivity.class);
                } else {
                    // Create a default monster
                    intent = new Intent(SplashActivity.this, ViewerActivity.class);
                    intent.putExtra(ViewerActivity.MY_MONSTER_OBJ_KEY, prefs.getLatestMonsterObj());
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imgSplash1.setVisibility(View.VISIBLE);
        imgSplash2.setVisibility(View.VISIBLE);
        imgSplash2.startAnimation(animSlideIn);
    }
}
