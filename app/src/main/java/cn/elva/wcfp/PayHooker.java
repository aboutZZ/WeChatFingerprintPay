package cn.elva.wcfp;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.widget.Toast;

import cn.elva.wcfp.utils.FingerprintHandler;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Elva on 2017/8/16.
 * WeChat payment hook
 */

public class PayHooker implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private static final String PKG_NAME = "com.tencent.mm";
    private FingerprintHandler fHandler = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(PKG_NAME)) {
            Context sysContext = (Context) XposedHelpers.callMethod(
                    XposedHelpers.callStaticMethod(
                            XposedHelpers.findClass("android.app.ActivityThread", null),
                            "currentActivityThread"),
                    "getSystemContext");
            PackageInfo pkgInfo = sysContext.getPackageManager().getPackageInfo(PKG_NAME, 0);
            VersionInfo.init(pkgInfo.versionName, pkgInfo.versionCode);
            // Start hooking
            XposedHelpers.findAndHookMethod(
                    VersionInfo.keyboardViewClassName,
                    lpparam.classLoader,
                    "onResume",
                    Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Activity act = (Activity) param.thisObject;
                            if (fHandler == null) {
                                initFingerPrint(act);
                            }
                            if (fHandler != null) {
                                fHandler.startAuth(
                                        (FingerprintManager) act.getSystemService(Context.FINGERPRINT_SERVICE),
                                        null);
                            }
                        }
                    });
            XposedHelpers.findAndHookMethod(
                    VersionInfo.keyboardViewClassName,
                    lpparam.classLoader,
                    "onPause",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (fHandler != null) {
                                fHandler.cancelAuth();
                            }
                        }
                    });
            XposedHelpers.findAndHookMethod(
                    VersionInfo.keyboardViewClassName,
                    lpparam.classLoader,
                    "onDestroy",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (fHandler != null) {
                                fHandler.cancelAuth();
                            }
                            fHandler = null;
                        }
                    });
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

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
