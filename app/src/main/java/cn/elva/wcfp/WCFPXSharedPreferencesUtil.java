package cn.elva.wcfp;

import android.content.Context;
import android.provider.Settings;

import cn.elva.wcfp.utils.AESHelper;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by Elva on 2017/8/17.
 * Shared preferences util class
 */

public class WCFPXSharedPreferencesUtil {
    private static XSharedPreferences wcXSP = null;
    private static String ID = null;
    private static boolean isNeedReload = true;

    private static XSharedPreferences getXSPInstance() {
        if (wcXSP == null) {
            wcXSP = new XSharedPreferences(WCFPXSharedPreferencesUtil.class.getPackage().getName());
            wcXSP.makeWorldReadable();
            wcXSP.reload();
        } else {
            if (isNeedReload) {
                XposedBridge.log("Reload shared preferences");
                wcXSP.reload();
                isNeedReload = false;
            }
        }
        return wcXSP;
    }

    public static String getPwd(Context mContext) {
        String encPwd = getXSPInstance().getString(VersionInfo.PREF_KEY_PWD, null);
        if (encPwd != null) {
            final String key = AESHelper.encrypt(getID(mContext), VersionInfo.DU_EN_KEY);
            encPwd = AESHelper.decrypt(encPwd, key);
        }
        return encPwd;
    }

    public static boolean isModuleEnabled() {
        return getXSPInstance().getBoolean(VersionInfo.PREF_KEY_ENABLE, false);
    }

    public static String getID(Context mContext) {
        if (ID == null) {
            ID = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return ID;
    }

    public static void notifyReload() {
        isNeedReload = true;
    }
}
