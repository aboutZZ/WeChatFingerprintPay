package cn.elva.wcfp;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Catalyst on 2017/8/14.
 * This is the version const of W.C.
 */

public class VersionInfo {

    public static final String SHARED_PREFERENCE_KEY = "wcfp";
    public static final String PREF_KEY_ENABLE = "enable";
    public static final String PREF_KEY_PWD = "pwd";
    public static final String DU_EN_KEY = "ssg9W5h7va0vhmMU";
    /**
     * Payment dialog class name. So far for sure
     */
    public static String keyboardViewClassName = "com.tencent.mm.plugin.wallet.pay.ui.WalletPayUI";

    /**
     * Password input view class name
     */
    public static String pwdViewClassName = null;

    public static String pwdViewLayoutName = null;
    public static String pwdViewEditTxtWidgetName = null;
    public static String pwdViewTitleWidgetName = null;
    public static String pwdKeyboardWidgetName = null;
    public static int fpImageResourceID = 0;


    public static void init(String versionName, int versionCode) {
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
