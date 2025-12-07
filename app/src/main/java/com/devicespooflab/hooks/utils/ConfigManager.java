package com.devicespooflab.hooks.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages configuration for spoofed values.
 * Reads from Magisk module config file, falls back to random values if unavailable.
 */
public class ConfigManager {

    private static final String CONFIG_PATH = "/data/adb/modules/devicespooflab/personas/current.conf";

    private static Map<String, String> configCache = null;
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

    public static void init() {
        configCache = readConfigFile();
    }

    private static Map<String, String> readConfigFile() {
        Map<String, String> config = new HashMap<>();

        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists() || !configFile.canRead()) {
            return config;
        }

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
        } catch (Exception e) {
            // Failed to read config, return empty map
            // Caller will fall back to random values
        }

        return config;
    }

    private static String getConfigValue(String key) {
        if (configCache == null) {
            init();
        }
        return configCache.get(key);
    }

    private static boolean hasConfigValue(String key) {
        String value = getConfigValue(key);
        return value != null && !value.isEmpty();
    }

    public static String getIMEI() {
        if (cachedIMEI == null) {
            // Config doesn't store IMEI (Magisk can't spoof it), always generate
            cachedIMEI = RandomGenerator.generateIMEI();
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

    public static boolean isConfigAvailable() {
        if (configCache == null) {
            init();
        }
        return !configCache.isEmpty();
    }
}
