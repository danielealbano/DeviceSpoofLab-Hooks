package com.devicespooflab.hooks;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Main activity for DeviceSpoofLab-Hooks LSPosed module.
 * Auto-creates config file on first launch.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setPadding(50, 50, 50, 50);
        textView.setTextSize(16);

        // Auto-create config file on first launch
        File configFile = new File(getFilesDir(), "device_profile.conf");

        if (!configFile.exists()) {
            try {
                createDefaultConfig(configFile);
                textView.setText(
                    "✅ DeviceSpoofLab-Hooks Setup Complete!\n\n" +
                    "Config file created at:\n" +
                    configFile.getAbsolutePath() + "\n\n" +
                    "Target Device: Google Pixel 7 Pro (Android 15)\n\n" +
                    "Next Steps:\n" +
                    "1. Open LSPosed Manager\n" +
                    "2. Enable this module\n" +
                    "3. Select target apps in Scope\n" +
                    "4. Restart target apps\n\n" +
                    "No manual file pushing required!\n" +
                    "Module is standalone - no Magisk conflicts.\n\n" +
                    "Check logs: adb logcat | grep DeviceSpoofLab"
                );
                Toast.makeText(this, "Config file created successfully!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                textView.setText(
                    "❌ Failed to create config file:\n" +
                    e.getMessage() + "\n\n" +
                    "The module will use embedded defaults.\n" +
                    "Everything will still work!"
                );
                Toast.makeText(this, "Using embedded defaults", Toast.LENGTH_LONG).show();
            }
        } else {
            textView.setText(
                "✅ DeviceSpoofLab-Hooks\n\n" +
                "Config file exists at:\n" +
                configFile.getAbsolutePath() + "\n\n" +
                "Target Device: Google Pixel 7 Pro (Android 15)\n\n" +
                "Status: Ready\n\n" +
                "To reconfigure:\n" +
                "1. Edit the config file, OR\n" +
                "2. Delete it and reopen this app\n\n" +
                "Module works independently from Magisk.\n" +
                "No file conflicts!"
            );
        }

        setContentView(textView);
    }

    private void createDefaultConfig(File configFile) throws IOException {
        String defaultConfig =
            "# DeviceSpoofLab-Hooks Auto-Generated Config\n" +
            "# Target: Google Pixel 7 Pro (Android 15)\n" +
            "# This file is in app's private storage - no Magisk conflicts!\n\n" +

            "# Device Identity\n" +
            "ro.product.brand=google\n" +
            "ro.product.manufacturer=Google\n" +
            "ro.product.model=Pixel 7 Pro\n" +
            "ro.product.name=cheetah\n" +
            "ro.product.device=cheetah\n" +
            "ro.product.board=cheetah\n" +
            "ro.hardware=cheetah\n" +
            "ro.board.platform=gs201\n\n" +

            "# Partition-specific (8 partitions)\n" +
            "ro.product.product.brand=google\n" +
            "ro.product.product.manufacturer=Google\n" +
            "ro.product.product.model=Pixel 7 Pro\n" +
            "ro.product.product.name=cheetah\n" +
            "ro.product.product.device=cheetah\n" +
            "ro.product.system.brand=google\n" +
            "ro.product.system.manufacturer=Google\n" +
            "ro.product.system.model=Pixel 7 Pro\n" +
            "ro.product.system.name=cheetah\n" +
            "ro.product.system.device=cheetah\n" +
            "ro.product.system_ext.brand=google\n" +
            "ro.product.system_ext.manufacturer=Google\n" +
            "ro.product.system_ext.model=Pixel 7 Pro\n" +
            "ro.product.system_ext.name=cheetah\n" +
            "ro.product.system_ext.device=cheetah\n" +
            "ro.product.vendor.brand=google\n" +
            "ro.product.vendor.manufacturer=Google\n" +
            "ro.product.vendor.model=Pixel 7 Pro\n" +
            "ro.product.vendor.name=cheetah\n" +
            "ro.product.vendor.device=cheetah\n" +
            "ro.product.vendor_dlkm.brand=google\n" +
            "ro.product.vendor_dlkm.manufacturer=Google\n" +
            "ro.product.vendor_dlkm.model=Pixel 7 Pro\n" +
            "ro.product.vendor_dlkm.name=cheetah\n" +
            "ro.product.vendor_dlkm.device=cheetah\n" +
            "ro.product.odm.brand=google\n" +
            "ro.product.odm.manufacturer=Google\n" +
            "ro.product.odm.model=Pixel 7 Pro\n" +
            "ro.product.odm.name=cheetah\n" +
            "ro.product.odm.device=cheetah\n" +
            "ro.product.bootimage.brand=google\n" +
            "ro.product.bootimage.manufacturer=Google\n" +
            "ro.product.bootimage.model=Pixel 7 Pro\n" +
            "ro.product.bootimage.name=cheetah\n" +
            "ro.product.bootimage.device=cheetah\n" +
            "ro.product.system_dlkm.brand=google\n" +
            "ro.product.system_dlkm.manufacturer=Google\n" +
            "ro.product.system_dlkm.model=Pixel 7 Pro\n" +
            "ro.product.system_dlkm.name=cheetah\n" +
            "ro.product.system_dlkm.device=cheetah\n\n" +

            "# Build Info\n" +
            "ro.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n" +
            "ro.build.id=AP4A.241205.013\n" +
            "ro.build.display.id=AP4A.241205.013\n" +
            "ro.build.version.incremental=12621605\n" +
            "ro.build.type=user\n" +
            "ro.build.tags=release-keys\n" +
            "ro.build.description=cheetah-user 15 AP4A.241205.013 12621605 release-keys\n" +
            "ro.build.product=cheetah\n" +
            "ro.build.device=cheetah\n" +
            "ro.build.characteristics=nosdcard\n" +
            "ro.build.flavor=cheetah-user\n" +
            "ro.build.version.release=15\n" +
            "ro.build.version.release_or_codename=15\n" +
            "ro.build.version.sdk=35\n" +
            "ro.build.version.codename=REL\n" +
            "ro.build.version.security_patch=2024-12-05\n\n" +

            "# Partition build fingerprints\n" +
            "ro.product.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n" +
            "ro.system.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n" +
            "ro.system_ext.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n" +
            "ro.vendor.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n" +
            "ro.odm.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n" +
            "ro.bootimage.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n" +
            "ro.system_dlkm.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n" +
            "ro.vendor_dlkm.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys\n\n" +

            "# Security (Anti-emulator)\n" +
            "ro.debuggable=0\n" +
            "ro.secure=1\n" +
            "ro.adb.secure=1\n" +
            "ro.build.selinux=0\n" +
            "ro.boot.verifiedbootstate=green\n" +
            "ro.boot.flash.locked=1\n" +
            "ro.boot.vbmeta.device_state=locked\n" +
            "ro.boot.warranty_bit=0\n" +
            "sys.oem_unlock_allowed=0\n" +
            "ro.boot.veritymode=enforcing\n" +
            "ro.crypto.state=encrypted\n" +
            "ro.kernel.qemu=0\n" +
            "ro.boot.qemu=0\n\n" +

            "# Hardware\n" +
            "ro.boot.hardware=cheetah\n" +
            "ro.boot.mode=normal\n" +
            "ro.product.cpu.abi=arm64-v8a\n" +
            "ro.product.cpu.abilist=arm64-v8a,armeabi-v7a,armeabi\n" +
            "ro.product.cpu.abilist64=arm64-v8a\n" +
            "ro.product.cpu.abilist32=armeabi-v7a,armeabi\n" +
            "ro.arch=arm64\n" +
            "ro.sf.lcd_density=512\n" +
            "ro.treble.enabled=true\n" +
            "screen.width=1440\n" +
            "screen.height=3120\n" +
            "screen.density=512\n\n" +

            "# Carrier/GSM\n" +
            "gsm.operator.alpha=T-Mobile\n" +
            "gsm.operator.numeric=310260\n" +
            "gsm.sim.operator.alpha=T-Mobile\n" +
            "gsm.sim.operator.numeric=310260\n" +
            "gsm.sim.operator.iso-country=us\n" +
            "persist.sys.timezone=America/Los_Angeles\n" +
            "persist.sys.usb.config=none\n\n" +

            "# WebView User-Agent\n" +
            "webview.user_agent=Mozilla/5.0 (Linux; Android 15; Pixel 7 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36\n\n" +

            "# Auto-generated identifiers (leave blank for random)\n" +
            "# ro.serialno=\n" +
            "# ro.bootloader=\n" +
            "# ANDROID_ID=\n";

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            fos.write(defaultConfig.getBytes());
            fos.flush();
        }
    }
}
