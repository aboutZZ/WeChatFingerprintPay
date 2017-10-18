package cn.elva.wcfp;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Catalyst on 2017/8/14.
 * This is the version const
 */

public class VersionInfo {
    public static final String PKG_NAME = "com.tencent.mm";
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

    private static final Set<Integer> supportVersionSet = new HashSet<Integer>() {{
        add(920);
        add(1080);
        add(1081);
        add(1100);
    }};

    /**
     * Check if the supported version of WeChat is installed
     *
     * @param versionCode The version code
     * @return True if the supported version is installed
     */
    public static boolean checkVersion(int versionCode) {
        return supportVersionSet.contains(versionCode);
    }

    static void initMinify(int versionCode) {
        switch (versionCode) {
            case 920://6.3.30 G
                pwdViewClassName = "com.tencent.mm.plugin.wallet_core.ui.k";
                pwdViewLayoutName = "khU";
                pwdViewEditTxtWidgetName = "onS";
                pwdViewTitleWidgetName = "khQ";
                pwdKeyboardWidgetName = "gJg";
                fpImageResourceID = 2130838333;
                break;
            case 1080://6.5.10 G
                pwdViewClassName = "com.tencent.mm.plugin.wallet_core.ui.l";
                pwdViewLayoutName = "ryM";
                pwdViewEditTxtWidgetName = "wjX";
                pwdViewTitleWidgetName = "ryI";
                pwdKeyboardWidgetName = "nnZ";
                fpImageResourceID = 2130838290;
                break;
            case 1081://"6.5.13 G
                pwdViewClassName = "com.tencent.mm.plugin.wallet_core.ui.l";
                pwdViewLayoutName = "rNe";
                pwdViewEditTxtWidgetName = "wFP";
                pwdViewTitleWidgetName = "rMZ";
                pwdKeyboardWidgetName = "npM";
                fpImageResourceID = 2130838299;
                break;
            case 1100://6.5.13 J
                pwdViewClassName = "com.tencent.mm.plugin.wallet_core.ui.l";
                pwdViewLayoutName = "rLB";
                pwdViewEditTxtWidgetName = "wDJ";
                pwdViewTitleWidgetName = "rLw";
                pwdKeyboardWidgetName = "nol";
                fpImageResourceID = 2130838298;
                break;
            default:
                break;
        }
    }
}
