package io.branch.tutorial;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.util.BranchEvent;
import io.branch.tutorial.fragment.InfoFragment;
import io.branch.tutorial.util.MonsterImageView;
import io.branch.tutorial.util.MonsterPreferences;

/**
 * Created by lorence on 11/01/2018.
 *
 */

public class ViewerActivity extends FragmentActivity implements InfoFragment.OnFragmentInteractionListener {

    public static final String MY_MONSTER_OBJ_KEY = "my_monster_obj_key";


    MonsterImageView monsterImageView_;
    BranchUniversalObject myMonsterObject_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monster_viewer);
        initUI();
    }

    private void initUI(){
        monsterImageView_ = (MonsterImageView) findViewById(R.id.monster_img_view);
        if (Branch.getInstance().isAutoDeepLinkLaunch(this)) {
            MonsterPreferences pref = MonsterPreferences.getInstance(this);
            myMonsterObject_ = BranchUniversalObject.getReferredBranchUniversalObject();
            pref.saveMonster(myMonsterObject_);
        } else {
            myMonsterObject_ = getIntent().getParcelableExtra(MY_MONSTER_OBJ_KEY);
        }

        if (myMonsterObject_ != null) {
            String monsterName = getString(R.string.monster_name);
            if (!TextUtils.isEmpty(myMonsterObject_.getTitle())) {
                monsterName = myMonsterObject_.getTitle();
            } else if (myMonsterObject_.getMetadata().containsKey("monster_name")) {
                monsterName = myMonsterObject_.getMetadata().get("monster_name");
            }
            ((TextView) findViewById(R.id.txtName)).setText(monsterName);

            String description = MonsterPreferences.getInstance(this).getMonsterDescription();
            if (!TextUtils.isEmpty(myMonsterObject_.getDescription())) {
                description = myMonsterObject_.getDescription();
            }
            ((TextView) findViewById(R.id.txtDescription)).setText(description);

            // set my monster image
            monsterImageView_.setMonster(myMonsterObject_);
            myMonsterObject_.addUserInteraction(BranchEvent.VIEW);
        }

        // More info
        findViewById(R.id.infoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                InfoFragment infoFragment = InfoFragment.newInstance();
                ft.replace(R.id.container, infoFragment).addToBackStack("info_container").commit();
            }
        });

    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        }
    }

    @Override
    public void onFragmentInteraction() {
        //no-op
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initUI();
    }
}