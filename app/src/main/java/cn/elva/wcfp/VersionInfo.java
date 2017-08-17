package cn.elva.wcfp;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Catalyst on 2017/8/14.
 * This is the version const of W.C.
 */

public class VersionInfo {
    /**
     * Payment dialog class name. So far for sure
     */
    public static String keyboardViewClassName = "com.tencent.mm.plugin.wallet.pay.ui.WalletPayUI";

    /**
     * Password input view class name
     */
    public static String pwdViewClassName = null;

    public static void init(String versionName, int versionCode) {
        switch (versionName) {
            case "6.3.30":
                pwdViewClassName = "com.tencent.mm.plugin.wallet_core.ui.";

                break;
            case "6.5.8":
                pwdViewClassName = "";
                break;
            case "6.5.10":
                break;
        }
    }
}
