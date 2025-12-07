package com.devicespooflab.hooks;

import com.devicespooflab.hooks.hooks.AdvertisingIdHooks;
import com.devicespooflab.hooks.hooks.BuildSerialHooks;
import com.devicespooflab.hooks.hooks.MediaDrmHooks;
import com.devicespooflab.hooks.hooks.SettingsHooks;
import com.devicespooflab.hooks.hooks.TelephonyHooks;
import com.devicespooflab.hooks.utils.ConfigManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Main entry point for DeviceSpoofLabs-Hooks Xposed module.
 *
 * This module hooks Android APIs to spoof device identifiers that cannot be
 * changed via Magisk resetprop. It works as a companion to DeviceSpoofLab-Magisk.
 *
 * What this module spoofs:
 * - IMEI, MEID (TelephonyManager)
 * - IMSI, ICCID (TelephonyManager)
 * - Phone number (TelephonyManager)
 * - Build.SERIAL (android.os.Build)
 * - Google Advertising ID (AdvertisingIdClient)
 * - MediaDrm device unique ID (Widevine)
 * - GSF ID (Settings.Secure)
 *
 * What this module does NOT spoof (handled by Magisk):
 * - Build.FINGERPRINT, Build.MODEL, Build.DEVICE, etc.
 * - ro.product.*, ro.build.* properties
 * - ANDROID_ID (partially - Magisk sets it, this is backup)
 * - WiFi/Bluetooth MAC addresses (causes connectivity issues)
 */
public class MainHook implements IXposedHookLoadPackage {

    // System packages to skip (no point in hooking these)
    private static final String[] SKIP_PACKAGES = {
            "android",
            "com.android.systemui",
            "com.android.settings",
            "com.android.phone",
            "com.google.android.gms"  // GMS is too complex, let LSPosed handle scope
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        for (String skipPkg : SKIP_PACKAGES) {
            if (lpparam.packageName.equals(skipPkg)) {
                return;
            }
        }

        ConfigManager.init();

        TelephonyHooks.hook(lpparam);
        BuildSerialHooks.hook(lpparam);
        AdvertisingIdHooks.hook(lpparam);
        MediaDrmHooks.hook(lpparam);
        SettingsHooks.hook(lpparam);
    }
}
