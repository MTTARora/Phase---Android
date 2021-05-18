package com.rora.phase.utils.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rora.phase.R;

import javax.annotation.Nullable;

public class FragmentManagerHelper {

    public static Fragment getCurrentFrag(FragmentManager fm, int containerViewId) {
        Fragment fragment = null;
        int count = fm.getBackStackEntryCount();
        if (count != 0) {
            String tag = fm.getBackStackEntryAt(count - 1).getName();
            fragment = fm.findFragmentByTag(tag);
        }

        return fragment;
    }

    public static void replace(FragmentManager fm, int containerViewId, Fragment destFrag, @Nullable String backStackName) {
        FragmentTransaction transaction = fm.beginTransaction();

        //transaction.setCustomAnimations(
        //        R.anim.screen_fadein,  // enter
        //        R.anim.screen_fadeout,  // exit
        //        R.anim.screen_popup_show,   // popEnter
        //        R.anim.screen_popup_hide  // popExit
        //);
        transaction.replace(containerViewId, destFrag, destFrag.getClass().getSimpleName());
        transaction.addToBackStack(backStackName);
        transaction.commit();
    }

    public static void add(FragmentManager fm, int containerViewId, Fragment destFrag, @Nullable String backStackName) {
        FragmentTransaction transaction = fm.beginTransaction();

        //transaction.setCustomAnimations(
        //        R.anim.screen_fadein,  // enter
        //        R.anim.screen_fadeout,  // exit
        //        R.anim.screen_popup_show,   // popEnter
        //        R.anim.screen_popup_hide  // popExit
        //);
        transaction.add(containerViewId, destFrag, destFrag.getClass().getSimpleName());
        transaction.addToBackStack(backStackName);
        transaction.commit();
    }

}
