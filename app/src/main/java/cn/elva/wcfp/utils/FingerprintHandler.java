package cn.elva.wcfp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;

import cn.elva.wcfp.WCFPXSharedPreferencesUtil;

/**
 * Created by Elva on 2017/8/16.
 * Fingerprint authentication helper class
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    /*
      Be aware that use the CancellationSignal to cancel authentication whenever your app can no longer process user input (for example when your app goes into the background).
    */

    private CancellationSignal cancellationSignal = null;
    private Context mContext = null;
    private EditText mEditText = null;

    public FingerprintHandler(@NonNull Context mContext, @NonNull EditText mEditText) {
        this.mContext = mContext;
        this.mEditText = mEditText;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        if (mContext != null)
            if (mContext.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Fingerprint permission denied", Toast.LENGTH_SHORT).show();
                return;
            }
        cancellationSignal = new CancellationSignal();
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void cancelAuth() {
        if (this.cancellationSignal != null && !this.cancellationSignal.isCanceled()) {
            this.cancellationSignal.cancel();
            this.cancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        showToast(errString);
    }

    @Override
    public void onAuthenticationFailed() {
        showToast("Authentication failed");
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showToast(helpString);
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        String pwd = WCFPXSharedPreferencesUtil.getPwd(mContext);
        //TODO 这里逻辑有待修改
        if (pwd != null && pwd.length() > 0) {
            if (mEditText != null) {
                mEditText.setText(pwd);
            }
        } else {
            showToast("Sorry, but you have not set the password in WeChatFingerprintPay yet");
        }
    }

    private void showToast(CharSequence msg) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
    }

}
