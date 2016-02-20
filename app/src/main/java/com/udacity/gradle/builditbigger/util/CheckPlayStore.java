package com.udacity.gradle.builditbigger.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.udacity.gradle.builditbigger.BuildItBiggerApplication;
import com.udacity.gradle.builditbigger.R;


public class CheckPlayStore {
    private final static String TAG = "LEE: <" + CheckPlayStore.class.getSimpleName() + ">";

    @SuppressWarnings("UnusedReturnValue")
    public static boolean upgradeToPaid(final Activity activity) {
        @SuppressWarnings("UnusedAssignment") ImageView upgrade_paid = null;

        //---------------------------------------------------------------------------------------------------------
        // IMPORTANT NOTE: the following #IFDEF and #ENDIF directives are processed in build.gradle prior to javac
        //                 CODE IN THIS BLOCK DEPENDS ON PRODUCT 'FLAVOR' AND IS DYNAMICALLY EDITED BY GRADLE
        //---------------------------------------------------------------------------------------------------------
        //#IFDEF 'free'
        //upgrade_paid = (ImageView) activity.findViewById(R.id.upgrade_to_paid);
        //#ENDIF
        //---------------------------------------------------------------------------------------------------------

        //noinspection ConstantConditions
        if (upgrade_paid != null) {
            Log.v(TAG, "using FREE version.");
            upgrade_paid.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String packageId = activity.getApplicationContext().getPackageName();
                    packageId = packageId.replace(".free", ".paid");
                    Log.v(TAG, "packageId="+packageId);
                    try {
                        // from: http://stackoverflow.com/questions/3239478/how-to-link-to-android-market-app
                        String upgradeLink = "http://market.android.com/details?id=" + packageId;
                        if (CheckPlayStore.isGooglePlayInstalled(activity.getApplicationContext())) {
                            upgradeLink = "market://details?id=" + packageId;
                        }
                        Log.v(TAG, "upgradeLink=" + upgradeLink);
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(upgradeLink)));
                        BuildItBiggerApplication.getInstance().trackEvent("adMob", "upgrade paid", "click");
                    }
                    catch (ActivityNotFoundException e) {
                        Log.e(TAG, "APP id='"+packageId+"' NOT FOUND ON PLAYSTORE!");
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setTitle(activity.getResources().getString(R.string.app_not_found));
                        alertDialogBuilder
                                .setMessage(activity.getResources().getString(R.string.not_on_playstore))
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        Log.v(TAG, "click!");
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        BuildItBiggerApplication.getInstance().trackEvent("adMob", "upgrade paid", "FAIL");
                    }
                }
            });
            return true;
        }
        else {
            Log.v(TAG, "using PAID version.");
            return false;
        }
    }

    // from: http://stackoverflow.com/questions/15401748/how-to-detect-if-google-play-is-installed-not-market
    private static boolean isGooglePlayInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try
        {
            PackageInfo info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES);
            String label = (String) info.applicationInfo.loadLabel(pm);
            app_installed = (label != null && ! label.equals("Market"));
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        Log.v(TAG, "isGooglePlayInstalled=" + app_installed);
        return app_installed;
    }

}
