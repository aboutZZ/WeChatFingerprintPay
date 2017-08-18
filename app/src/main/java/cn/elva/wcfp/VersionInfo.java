package cn.elva.wcfp;

import java.util.Locale;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Catalyst on 2017/8/14.
 * This is the version const
 */

public class VersionInfo {

    public static final String SHARED_PREFERENCE_KEY = VersionInfo.class.getPackage().getName()+"_preferences";
    public static final String PREF_KEY_ENABLE = "enable";
    public static final String PREF_KEY_PWD = "pwd";
    public static final String DU_EN_KEY = "ssg9W5h7va0vhmMU";
    /**
     * Payment dialog class name. So far for sure
     */
    static String payUIClassName = "com.tencent.mm.plugin.wallet.pay.ui.WalletPayUI";

    /**
     * Password input view class name
     */
    static String pwdViewClassName = null;

    static String pwdViewLayoutName = null;
    static String pwdViewEditTxtWidgetName = null;
    static String pwdViewTitleWidgetName = null;
    static String pwdKeyboardWidgetName = null;
    static int fpImageResourceID = 0;


    static void init(String versionName, int versionCode) {
        XposedBridge.log(String.format(Locale.CHINA, "Found WeChat version : %s (%d)", versionName, versionCode));
        switch (versionName) {
            case "6.3.30":
                pwdViewClassName = "com.tencent.mm.plugin.wallet_core.ui.k";
                pwdViewLayoutName = "khU";
                pwdViewEditTxtWidgetName = "onS";
                pwdViewTitleWidgetName = "khQ";
                pwdKeyboardWidgetName = "gJg";
                fpImageResourceID = 2130838333;
                break;
            case "6.5.8":
                pwdViewClassName = "";
                break;
            case "6.5.10":
                break;
        }
    }
}
