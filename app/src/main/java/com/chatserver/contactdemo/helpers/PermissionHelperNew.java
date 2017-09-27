package com.chatserver.contactdemo.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.chatserver.contactdemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Manish Kumar
 *         <p>
 *         PermissionsHelper
 *         <p>
 *         <p>
 *         This class is use for  handle run time permission in application
 *         <p>
 */
public class PermissionHelperNew {

    /**
     * Request code for permissions
     */
    public static final int PermissionrequestCode = 1223;
    /**
     * preferences name where permission action is saved
     */
    private static final String Prefsname = "runtimepermission";
    /**
     * preference key for save permission action
     */
    private static final String KEY_PERMISSION_ACTION = "permission_action";
    /**
     * All Permission which is use in Application
     */
    private static final List<String> permissions = Arrays.asList(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );
    /**
     * use for check user is go to setting screen
     */
    public static boolean requestingPermissionFromSetting = false;

    /**
     * use for get {@link SharedPreferences} of  {@link #Prefsname}
     *
     * @param context
     * @return
     */
    public static SharedPreferences getPrefs(Context context) {
        if (context == null) return null;
        return context.getSharedPreferences(Prefsname,
                Context.MODE_PRIVATE);
    }

    /**
     * use for set value in {@link #Prefsname} in string format
     *
     * @param ctx
     * @param key
     * @param value
     */
    public static void setStringKeyvaluePrefs(Context ctx, String key,
                                              String value) {
        SharedPreferences prefs = getPrefs(ctx);
        if (prefs == null) return;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();

    }

    /**
     * use for get String value from {@link #Prefsname}
     *
     * @param ctx
     * @param key
     * @return
     */
    public static String getStringKeyvaluePrefs(Context ctx, String key) {
        SharedPreferences prefs = getPrefs(ctx);
        if (prefs == null) return "";
        return prefs.getString(key, "");
    }

    /**
     * use for check permission action is saved in {@link #Prefsname}
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermissionActionSaved(Context context, String permission) {
        String savedPermission = getStringKeyvaluePrefs(context, KEY_PERMISSION_ACTION);
        return savedPermission.contains(permission);
    }

    /**
     * use for save permission action in {@link #Prefsname}
     *
     * @param context
     * @param permission
     */
    public static void savePermissionAction(Context context, String permission) {
        if (!checkPermissionActionSaved(context, permission)) {
            String savedPermission = getStringKeyvaluePrefs(context, KEY_PERMISSION_ACTION);
            if (savedPermission.trim().isEmpty()) {
                savedPermission = permission;
            } else {
                savedPermission += "," + permission;
            }
            setStringKeyvaluePrefs(context, KEY_PERMISSION_ACTION, savedPermission);
        }
    }

    /**
     * use for get all permission list which is set in  {@link #permissions}
     *
     * @return
     */
    static List<String> getPermissionConstants() {
        return permissions;
    }


    /**
     * use for handle permission request result
     *
     * @param context
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    public static boolean onRequestPermissionsResult(Activity context,
                                                     int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != PermissionrequestCode) return false;

        if (permissions != null && permissions.length > 0) {
            String permissionAction = permissions[0];
            savePermissionAction(context, permissionAction);
            if (grantResults[0] == -1) {

                if (isPermissionDenyWithNeverAsk(context, permissionAction)) {
                    return true;
                }

                showDenyAlert(context);
                return false;
            }
        }
        return true;


    }

    /**
     * use for check any permission not to granted
     * If any permission not granted then request for that permission send from here
     *
     * @param context
     * @return
     */
    public static boolean needPermissions(Activity context) {

        List<String> allPermissions = getPermissionConstants();
        List<String> neverAskPermissions = new ArrayList<>();
        for (String permission : allPermissions) {
            if (!isPermissionGranted(context, permission)) {
                if (checkPermissionActionSaved(context, permission)) {
                    if (isPermissionDenyWithNeverAsk(context, permission)) {
                        neverAskPermissions.add(permission);
                        continue;
                    }
                }

                ActivityCompat.requestPermissions(scanForActivity(context),
                        new String[]{permission}, PermissionrequestCode);
                return true;
            }
        }
        if (!neverAskPermissions.isEmpty()) {
            showNeverAskAlert(context);
            return true;
        }
        return false;

    }
//    public static boolean checkAllPermissionGranted (Context context) {
//
//        List<String> allPermissions = getPermissionConstants();
//        List<String> neverAskPermissions = new ArrayList<>();
//        for (String permission : allPermissions) {
//            if (!isPermissionGranted(context, permission)) {
//                if (checkPermissionActionSaved(context, permission)) {
//                    if (isPermissionDenyWithNeverAsk(context, permission)) {
//                        neverAskPermissions.add(permission);
//                        continue;
//                    }
//                }
//
////                ActivityCompat.requestPermissions(scanForActivity(context),
////                        new String[]{permission}, PermissionrequestCode);
//                return true;
//            }
//        }
//        if (!neverAskPermissions.isEmpty()) {
//           // showNeverAskAlert(context);
//            return true;
//        }
//        return false;
//
//    }

    /**
     * use for show alert when any permission is deny by user
     *
     * @param context
     */
    public static void showDenyAlert(final Activity context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.app_name));

        builder.setMessage(R.string.text_permission_deny_message);

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                needPermissions(context);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.finish();
            }
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * use for show alert when permission are deny with never ask again
     *
     * @param context
     */
    public static void showNeverAskAlert(final Activity context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("ROOOZ");

        builder.setMessage(R.string.text_permission_never_message);

        builder.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestingPermissionFromSetting = true;
                goToAppSettingPage(context);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.finish();
            }
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * use for goto Settings->Apps->ourApp
     *
     * @param activity
     */
    private static void goToAppSettingPage(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    /**
     * use for find activity instance from context
     *
     * @param cont
     * @return
     */
    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    /**
     * use for check permission is @{@link PackageManager#PERMISSION_GRANTED}
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * use for check RunTime Permission is required or not
     *
     * @return
     */
    public static boolean areExplicitPermissionsRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    /**
     * use for check permission deny with never ask again
     *
     * @param activity
     * @param permission
     * @return
     */
    private static boolean isPermissionDenyWithNeverAsk(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }


}
