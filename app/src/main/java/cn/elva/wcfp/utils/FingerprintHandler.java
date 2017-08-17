package cn.elva.wcfp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.widget.Toast;

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

    public FingerprintHandler(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        if (mContext.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext,"Fingerprint permission denied",Toast.LENGTH_SHORT).show();
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
        Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(mContext, "Authentication failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(mContext, helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        Toast.makeText(mContext, "Success!", Toast.LENGTH_LONG).show();
    }

}
