package com.devicespooflab.hooks.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

/**
 * Manages configuration for spoofed values.
 * Reads from standalone config file or uses embedded Pixel 7 Pro defaults.
 * No Magisk dependency - works as standalone LSPosed module.
 */
public class ConfigManager {

    // Config file paths (tried in order)
    // NOTE: Using different paths from Magisk module (/data/adb/modules/devicespooflab)
    // to avoid conflicts. This LSPosed module is standalone.
    private static final String[] CONFIG_PATHS = {
        "/data/data/com.devicespooflab.hooks/files/device_profile.conf",  // App's private storage
        "/sdcard/DeviceSpoofLab-Hooks/device_profile.conf",               // SD card fallback
        "/data/local/tmp/DeviceSpoofLab-Hooks/device_profile.conf"        // World-readable fallback (Redroid)
    };

    // Cached configuration
    private static Map<String, String> allProperties = null;

    // Legacy cached identifiers (for backward compatibility)
    private static String cachedIMEI = null;
    private static String cachedMEID = null;
    private static String cachedIMSI = null;
    private static String cachedICCID = null;
    private static String cachedPhoneNumber = null;
    private static String cachedSerial = null;
    private static String cachedGAID = null;
    private static String cachedGSFId = null;
    private static String cachedAndroidId = null;
    private static byte[] cachedMediaDrmId = null;
    private static String cachedAppSetId = null;

    public static void init() {
        allProperties = readConfigFile();
    }

    private static Map<String, String> readConfigFile() {
        Map<String, String> config = new HashMap<>();

        // Try each config path in order
        for (String configPath : CONFIG_PATHS) {
            File configFile = new File(configPath);
            Log.i("DeviceSpoofLab-DEBUG", "Trying config: " + configPath + " exists=" + configFile.exists() + " canRead=" + configFile.canRead());
            if (configFile.exists() && configFile.canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();

                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }

                        int equalIndex = line.indexOf('=');
                        if (equalIndex > 0) {
                            String key = line.substring(0, equalIndex).trim();
                            String value = line.substring(equalIndex + 1).trim();

                            if (value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1, value.length() - 1);
                            }

                            config.put(key, value);
                        }
                    }
                    Log.i("DeviceSpoofLab-DEBUG", "Config loaded from " + configPath + " (" + config.size() + " entries, imei.tac=" + config.get("imei.tac") + ")");
                    return config;
                } catch (Exception e) {
                    Log.e("DeviceSpoofLab-DEBUG", "Failed to read " + configPath + ": " + e.getMessage());
                }
            }
        }

        Log.w("DeviceSpoofLab-DEBUG", "No config file found, using embedded defaults");
        return getEmbeddedDefaults();
    }

    /**
     * Get embedded Pixel 7 Pro defaults (Android 15)
     * Used when no config file is available
     */
    private static Map<String, String> getEmbeddedDefaults() {
        Map<String, String> defaults = new HashMap<>();

        // Device Identity (48 properties)
        defaults.put("ro.product.brand", "google");
        defaults.put("ro.product.manufacturer", "Google");
        defaults.put("ro.product.model", "Pixel 7 Pro");
        defaults.put("ro.product.name", "cheetah");
        defaults.put("ro.product.device", "cheetah");
        defaults.put("ro.product.board", "cheetah");
        defaults.put("ro.hardware", "cheetah");
        defaults.put("ro.board.platform", "gs201");

        // Partition-specific (8 partitions × 5 props)
        String[] partitions = {"product", "system", "system_ext", "vendor", "vendor_dlkm", "odm", "bootimage", "system_dlkm"};
        for (String partition : partitions) {
            defaults.put("ro.product." + partition + ".brand", "google");
            defaults.put("ro.product." + partition + ".manufacturer", "Google");
            defaults.put("ro.product." + partition + ".model", "Pixel 7 Pro");
            defaults.put("ro.product." + partition + ".name", "cheetah");
            defaults.put("ro.product." + partition + ".device", "cheetah");
        }

        // Build Info (41 properties)
        defaults.put("ro.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");
        defaults.put("ro.build.id", "AP4A.241205.013");
        defaults.put("ro.build.display.id", "AP4A.241205.013");
        defaults.put("ro.build.version.incremental", "12621605");
        defaults.put("ro.build.type", "user");
        defaults.put("ro.build.tags", "release-keys");
        defaults.put("ro.build.description", "cheetah-user 15 AP4A.241205.013 12621605 release-keys");
        defaults.put("ro.build.product", "cheetah");
        defaults.put("ro.build.device", "cheetah");
        defaults.put("ro.build.characteristics", "nosdcard");
        defaults.put("ro.build.flavor", "cheetah-user");

        defaults.put("ro.build.version.release", "15");
        defaults.put("ro.build.version.release_or_codename", "15");
        defaults.put("ro.build.version.release_or_preview_display", "15");
        defaults.put("ro.build.version.sdk", "35");
        defaults.put("ro.build.version.codename", "REL");
        defaults.put("ro.build.version.security_patch", "2024-12-05");

        // Partition build fingerprints
        defaults.put("ro.product.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");
        defaults.put("ro.product.build.id", "AP4A.241205.013");
        defaults.put("ro.product.build.tags", "release-keys");
        defaults.put("ro.product.build.type", "user");
        defaults.put("ro.product.build.version.incremental", "12621605");
        defaults.put("ro.product.build.version.release", "15");
        defaults.put("ro.product.build.version.release_or_codename", "15");
        defaults.put("ro.product.build.version.sdk", "35");

        defaults.put("ro.system.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");
        defaults.put("ro.system_ext.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");
        defaults.put("ro.vendor.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");
        defaults.put("ro.odm.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");
        defaults.put("ro.bootimage.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");
        defaults.put("ro.system_dlkm.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");
        defaults.put("ro.vendor_dlkm.build.fingerprint", "google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys");

        defaults.put("ro.vendor.build.version.release", "15");
        defaults.put("ro.vendor.build.version.release_or_codename", "15");
        defaults.put("ro.vendor_dlkm.build.version.release", "15");
        defaults.put("ro.vendor_dlkm.build.version.release_or_codename", "15");
        defaults.put("ro.odm.build.version.release", "15");
        defaults.put("ro.odm.build.version.release_or_codename", "15");
        defaults.put("ro.bootimage.build.version.release", "15");
        defaults.put("ro.bootimage.build.version.release_or_codename", "15");
        defaults.put("ro.system_dlkm.build.version.release", "15");
        defaults.put("ro.system_dlkm.build.version.release_or_codename", "15");

        // Security (13 properties)
        defaults.put("ro.debuggable", "0");
        defaults.put("ro.secure", "1");
        defaults.put("ro.adb.secure", "1");
        defaults.put("ro.build.selinux", "0");
        defaults.put("ro.boot.verifiedbootstate", "green");
        defaults.put("ro.boot.flash.locked", "1");
        defaults.put("ro.boot.vbmeta.device_state", "locked");
        defaults.put("ro.boot.warranty_bit", "0");
        defaults.put("sys.oem_unlock_allowed", "0");
        defaults.put("ro.boot.veritymode", "enforcing");
        defaults.put("ro.crypto.state", "encrypted");
        defaults.put("ro.kernel.qemu", "0");
        defaults.put("ro.boot.qemu", "0");

        // Anti-emulator detection - hide all qemu/ranchu/goldfish properties
        defaults.put("ro.boot.qemu.avd_name", "");
        defaults.put("ro.boot.qemu.camera_hq_edge_processing", "0");
        defaults.put("ro.boot.qemu.camera_protocol_ver", "0");
        defaults.put("ro.boot.qemu.cpuvulkan.version", "0");
        defaults.put("ro.boot.qemu.gltransport.drawFlushInterval", "0");
        defaults.put("ro.boot.qemu.gltransport.name", "");
        defaults.put("ro.boot.qemu.hwcodec.avcdec", "0");
        defaults.put("ro.boot.qemu.hwcodec.hevcdec", "0");
        defaults.put("ro.boot.qemu.hwcodec.vpxdec", "0");
        defaults.put("ro.boot.qemu.settings.system.screen_off_timeout", "0");
        defaults.put("ro.boot.qemu.virtiowifi", "0");
        defaults.put("ro.boot.qemu.vsync", "0");

        // Hardware (12 properties)
        defaults.put("ro.boot.hardware", "cheetah");
        defaults.put("ro.boot.hardware.vulkan", "mali");
        defaults.put("ro.boot.hardware.gltransport", "");
        defaults.put("ro.boot.mode", "normal");
        defaults.put("ro.product.cpu.abi", "arm64-v8a");
        defaults.put("ro.product.cpu.abilist", "arm64-v8a,armeabi-v7a,armeabi");
        defaults.put("ro.product.cpu.abilist64", "arm64-v8a");
        defaults.put("ro.product.cpu.abilist32", "armeabi-v7a,armeabi");
        defaults.put("ro.arch", "arm64");
        defaults.put("ro.sf.lcd_density", "512");
        defaults.put("ro.treble.enabled", "true");

        // Override emulator-specific hardware
        defaults.put("ro.hardware.vulkan", "mali");
        defaults.put("ro.hardware.gralloc", "gs201");
        defaults.put("ro.hardware.power", "gs201-power");
        defaults.put("ro.hardware.egl", "mali");
        defaults.put("ro.soc.model", "gs201");
        defaults.put("ro.soc.manufacturer", "Google");

        // Screen dimensions (for display hooks)
        defaults.put("screen.width", "1440");
        defaults.put("screen.height", "3120");
        defaults.put("screen.density", "512");

        // CPU/Memory hardware specs (Pixel 7 Pro: Tensor G2, 12GB RAM)
        defaults.put("dalvik.vm.heapsize", "576m");
        defaults.put("dalvik.vm.heapgrowthlimit", "256m");
        defaults.put("dalvik.vm.heapmaxfree", "8m");
        defaults.put("dalvik.vm.heapminfree", "512k");
        defaults.put("dalvik.vm.heapstartsize", "8m");
        defaults.put("dalvik.vm.heaptargetutilization", "0.75");

        // Carrier/GSM (7 properties)
        defaults.put("gsm.operator.alpha", "T-Mobile");
        defaults.put("gsm.operator.numeric", "310260");
        defaults.put("gsm.sim.operator.alpha", "T-Mobile");
        defaults.put("gsm.sim.operator.numeric", "310260");
        defaults.put("gsm.sim.operator.iso-country", "us");
        defaults.put("persist.sys.timezone", "America/Los_Angeles");
        defaults.put("persist.sys.usb.config", "none");

        // WebView User-Agent
        defaults.put("webview.user_agent", "Mozilla/5.0 (Linux; Android 15; Pixel 7 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36");

        return defaults;
    }

    private static String getConfigValue(String key) {
        if (allProperties == null) {
            init();
        }
        return allProperties.get(key);
    }

    private static boolean hasConfigValue(String key) {
        String value = getConfigValue(key);
        return value != null && !value.isEmpty();
    }

    /**
     * Generic system property accessor
     * Returns spoofed value for the given property key
     */
    public static String getSystemProperty(String key, String defaultValue) {
        String value = getConfigValue(key);
        return (value != null) ? value : defaultValue;
    }

    public static String getIMEI() {
        if (cachedIMEI == null) {
            String tac = getConfigValue("imei.tac");
            if (tac != null && !tac.isEmpty()) {
                cachedIMEI = RandomGenerator.generateIMEIWithTAC(tac);
                Log.i("DeviceSpoofLab-DEBUG", "IMEI generated with TAC " + tac + ": " + cachedIMEI);
            } else {
                cachedIMEI = RandomGenerator.generateIMEI();
                Log.i("DeviceSpoofLab-DEBUG", "IMEI generated with default TAC: " + cachedIMEI);
            }
        }
        return cachedIMEI;
    }

    public static String getMEID() {
        if (cachedMEID == null) {
            cachedMEID = RandomGenerator.generateMEID();
        }
        return cachedMEID;
    }

    public static String getIMSI() {
        if (cachedIMSI == null) {
            cachedIMSI = RandomGenerator.generateIMSI();
        }
        return cachedIMSI;
    }

    public static String getICCID() {
        if (cachedICCID == null) {
            cachedICCID = RandomGenerator.generateICCID();
        }
        return cachedICCID;
    }

    public static String getPhoneNumber() {
        if (cachedPhoneNumber == null) {
            cachedPhoneNumber = RandomGenerator.generatePhoneNumber();
        }
        return cachedPhoneNumber;
    }

    public static String getSerial() {
        if (cachedSerial == null) {
            if (hasConfigValue("SERIAL_NUMBER")) {
                cachedSerial = getConfigValue("SERIAL_NUMBER");
            } else {
                cachedSerial = RandomGenerator.generateSerial();
            }
        }
        return cachedSerial;
    }

    public static String getGAID() {
        if (cachedGAID == null) {
            cachedGAID = RandomGenerator.generateGAID();
        }
        return cachedGAID;
    }

    public static String getGSFId() {
        if (cachedGSFId == null) {
            cachedGSFId = RandomGenerator.generateGSFId();
        }
        return cachedGSFId;
    }

    public static String getAndroidId() {
        if (cachedAndroidId == null) {
            if (hasConfigValue("ANDROID_ID")) {
                cachedAndroidId = getConfigValue("ANDROID_ID");
            } else {
                cachedAndroidId = RandomGenerator.generateAndroidId();
            }
        }
        return cachedAndroidId;
    }

    public static byte[] getMediaDrmId() {
        if (cachedMediaDrmId == null) {
            cachedMediaDrmId = RandomGenerator.generateMediaDrmId();
        }
        return cachedMediaDrmId;
    }

    public static String getAppSetId() {
        if (cachedAppSetId == null) {
            cachedAppSetId = RandomGenerator.generateGAID(); // Same UUID format
        }
        return cachedAppSetId;
    }

    public static boolean isConfigAvailable() {
        if (allProperties == null) {
            init();
        }
        return !allProperties.isEmpty();
    }

    // ==================== Build Field Accessors ====================

    public static String getBuildFingerprint() {
        return getConfigValue("ro.build.fingerprint");
    }

    public static String getBuildModel() {
        return getConfigValue("ro.product.model");
    }

    public static String getBuildDevice() {
        return getConfigValue("ro.product.device");
    }

    public static String getBuildManufacturer() {
        return getConfigValue("ro.product.manufacturer");
    }

    public static String getBuildBrand() {
        return getConfigValue("ro.product.brand");
    }

    public static String getBuildProduct() {
        return getConfigValue("ro.product.name");
    }

    public static String getBuildBoard() {
        return getConfigValue("ro.product.board");
    }

    public static String getBuildHardware() {
        return getConfigValue("ro.hardware");
    }

    public static String getBuildBootloader() {
        String bootloader = getConfigValue("ro.bootloader");
        // If not in config, generate random one
        if (bootloader == null || bootloader.isEmpty()) {
            bootloader = RandomGenerator.generateBootloader();
        }
        return bootloader;
    }

    public static String getBuildId() {
        return getConfigValue("ro.build.id");
    }

    public static String getBuildDisplay() {
        return getConfigValue("ro.build.display.id");
    }

    public static String getBuildTags() {
        return getConfigValue("ro.build.tags");
    }

    public static String getBuildType() {
        return getConfigValue("ro.build.type");
    }

    public static String getBuildVersionRelease() {
        return getConfigValue("ro.build.version.release");
    }

    public static int getBuildVersionSdk() {
        String sdk = getConfigValue("ro.build.version.sdk");
        try {
            return Integer.parseInt(sdk);
        } catch (Exception e) {
            return 35; // Default to Android 15
        }
    }

    public static String getBuildVersionSecurityPatch() {
        return getConfigValue("ro.build.version.security_patch");
    }

    public static String getBuildVersionIncremental() {
        return getConfigValue("ro.build.version.incremental");
    }

    public static String getBuildVersionCodename() {
        return getConfigValue("ro.build.version.codename");
    }

    public static String getBuildDescription() {
        return getConfigValue("ro.build.description");
    }

    public static String getBuildCharacteristics() {
        return getConfigValue("ro.build.characteristics");
    }

    public static String getBuildFlavor() {
        return getConfigValue("ro.build.flavor");
    }

    public static String getWebViewUserAgent() {
        return getConfigValue("webview.user_agent");
    }

    // CPU/ABI accessors
    public static String getCpuAbi() {
        return getConfigValue("ro.product.cpu.abi");
    }

    public static String getCpuAbiList() {
        return getConfigValue("ro.product.cpu.abilist");
    }

    public static String getCpuAbiList64() {
        return getConfigValue("ro.product.cpu.abilist64");
    }

    public static String getCpuAbiList32() {
        return getConfigValue("ro.product.cpu.abilist32");
    }
}
