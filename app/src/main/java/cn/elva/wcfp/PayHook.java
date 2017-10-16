package cn.elva.wcfp;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import cn.elva.wcfp.utils.FingerprintHandler;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static cn.elva.wcfp.VersionInfo.PKG_NAME;

/**
 * Created by Elva on 2017/8/16.
 * WeChat payment hook logic class
 */

public class PayHook implements IXposedHookLoadPackage {

    private FingerprintHandler fHandler = null;
    private Activity paymentLayer = null;
    private EditText etInputEditText = null;
    public static boolean isVersionSupported = false;

    private static void init() {
        Context sysContext = (Context) XposedHelpers.callMethod(
                XposedHelpers.callStaticMethod(
                        XposedHelpers.findClass("android.app.ActivityThread", null),
                        "currentActivityThread"),
                "getSystemContext");
        try {
            PackageInfo pkgInfo = sysContext.getPackageManager().getPackageInfo(PKG_NAME, 0);
            XposedBridge.log(String.format(Locale.getDefault(), "WCFP: Found WeChat version : %s (%d)", pkgInfo.versionName, pkgInfo.versionCode));
            if ((isVersionSupported = VersionInfo.checkVersion(pkgInfo.versionCode))) {
                VersionInfo.initMinify(pkgInfo.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            XposedBridge.log(e);
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(PKG_NAME)) {
            return;
        }
        if (!WCFPXSharedPreferencesUtil.isModuleEnabled()) {
            return;
        }
        init();
        if (!isVersionSupported) {
            return;
        }
        // Start hooking
        XposedHelpers.findAndHookMethod(
                VersionInfo.payUIClassName,
                lpparam.classLoader,
                "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (!WCFPXSharedPreferencesUtil.isModuleEnabled()) {
                            fHandler = null;
                            return;
                        }
                        XposedBridge.log("pay UI onResume");
                        paymentLayer = (Activity) param.thisObject;
                        if (fHandler == null) {
                            initFingerPrint(paymentLayer);
                        }
                        if (fHandler != null) {
                            fHandler.startAuth(
                                    (FingerprintManager) paymentLayer.getSystemService(Context.FINGERPRINT_SERVICE),
                                    null);
                        }
                    }
                });
        XposedHelpers.findAndHookMethod(
                VersionInfo.payUIClassName,
                lpparam.classLoader,
                "onPause",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("pay UI onPause");
                        if (fHandler != null) {
                            fHandler.cancelAuth();
                        }
                    }
                });
        XposedHelpers.findAndHookMethod(
                VersionInfo.payUIClassName,
                lpparam.classLoader,
                "onDestroy",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("pay UI onDestroy");
                        if (fHandler != null) {
                            fHandler.cancelAuth();
                        }
                        fHandler = null;
                        paymentLayer = null;
                        etInputEditText = null;
                    }
                });
        XposedHelpers.findAndHookConstructor(
                VersionInfo.pwdViewClassName,
                lpparam.classLoader,
                Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("pwdView constructor");
                        if (!WCFPXSharedPreferencesUtil.isModuleEnabled()) {
                            return;
                        }
                        final RelativeLayout pwdViewLayout = (RelativeLayout) XposedHelpers.getObjectField(param.thisObject, VersionInfo.pwdViewLayoutName);
                        // Hide password view
                        etInputEditText = (EditText) XposedHelpers.getObjectField(pwdViewLayout, VersionInfo.pwdViewEditTxtWidgetName);
                        if (fHandler != null) {
                            fHandler.setmEditText(etInputEditText);
                        }
                        final TextView tvTitle = (TextView) XposedHelpers.getObjectField(param.thisObject, VersionInfo.pwdViewTitleWidgetName);
                        tvTitle.setText("支付指纹验证");
                        etInputEditText.setVisibility(View.GONE);
                        // Set payment dialog title
                        // Hide keyboard
                        final View vKeyboard = (View) XposedHelpers.getObjectField(param.thisObject, VersionInfo.pwdKeyboardWidgetName);
                        vKeyboard.setVisibility(View.GONE);
                        final RelativeLayout fpIconRL = new RelativeLayout(paymentLayer);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        fpIconRL.setLayoutParams(layoutParams);
                        ImageView fpIconImgView = new ImageView(paymentLayer);
                        fpIconImgView.setImageResource(VersionInfo.fpImageResourceID);
                        fpIconRL.addView(fpIconImgView);
                        pwdViewLayout.addView(fpIconRL);
                        fpIconImgView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pwdViewLayout.removeView(fpIconRL);
                                etInputEditText.setVisibility(View.VISIBLE);
                                vKeyboard.setVisibility(View.VISIBLE);
                                tvTitle.setText("请输入支付密码");
                            }
                        });
                    }
                });
    }

    private void initFingerPrint(Context context) {
        KeyguardManager keyguardManager =
                (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager =
                (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);

        if (!fingerprintManager.isHardwareDetected()) {
            Toast.makeText(context, "Your device doesn't support fingerprint authentication", Toast.LENGTH_LONG).show();
        } else if (context.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Please enable the fingerprint permission", Toast.LENGTH_LONG).show();
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            Toast.makeText(
                    context,
                    "No fingerprint configured. Please register at least one fingerprint in your device's Settings",
                    Toast.LENGTH_LONG).show();
        } else if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(
                    context,
                    "Please enable lock screen security in your device's Settings",
                    Toast.LENGTH_LONG).show();
        } else {
            fHandler = new FingerprintHandler(context);
        }
    }
}
