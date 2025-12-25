# DeviceSpoofLab-Hooks: LSPosed Module

A comprehensive device spoofing LSPosed module that intercepts Android APIs at the Java level to spoof device properties, solving the problem where apps read properties directly from Zygote, bypassing traditional property modification methods.

## Problem Solved

Target apps retrieve device properties in multiple ways:

- Reading `Build.*` static fields directly (e.g., `Build.FINGERPRINT`)
- Using `SystemProperties.get()` reflection to bypass Magisk
- Reading from Zygote cache before Magisk modifications apply

This module uses a **hybrid approach**:

- **Magisk (boot-time)**: Spoofs critical `Build.*` fields to prevent Cronet/security library detection
- **LSPosed (runtime)**: Hooks SystemProperties reflection and runtime API calls

## Target Device Profile

**Google Pixel 7 Pro** running **Android 15** (Build AP4A.241205.013)

## What Gets Spoofed

### System Properties (126+ properties)

- **Device Identity** (48 props): brand, manufacturer, model, device name across 8 partitions
- **Build Info** (41 props): fingerprint, build ID, version info, security patches
- **Security** (13 props): debuggable=0, secure=1, verified boot state, anti-emulator flags
- **Hardware** (12 props): CPU ABI, screen resolution/density, hardware platform
- **Identifiers** (4 props): serial number, bootloader version
- **Carrier/GSM** (7 props): operator info, SIM details, timezone

### Build Class Fields

- All `Build.*` fields: MANUFACTURER, BRAND, MODEL, DEVICE, FINGERPRINT, VERSION.\*
- CPU ABI arrays: SUPPORTED_ABIS, SUPPORTED_64_BIT_ABIS, SUPPORTED_32_BIT_ABIS

### Device Identifiers

- IMEI/MEID, IMSI/ICCID (with Luhn checksum validation)
- Phone Number, ANDROID_ID, GSF ID, GAID, App Set ID, MediaDrm ID

### Additional Spoofing

- WebView User-Agent (matches Pixel 7 Pro)
- PackageManager hardware features
- Emulator detection bypass (hides emulator files)

## Requirements

- Rooted Android device (Android 8.0+ / SDK 26+)
- [LSPosed](https://github.com/LSPosed/LSPosed) installed
- [DeviceSpoofLab-Magisk](https://github.com/yubunus/DeviceSpoofLab-Magisk): Required for boot-time spoofing of critical Build fields (prevents Cronet detection)

## Installation

### Prerequisites: Use [DeviceSpoofLab-Magisk](https://github.com/yubunus/DeviceSpoofLab-Magisk) for Boot Spoofing

**CRITICAL**: To prevent Cronet crashes in security-sensitive apps, you MUST spoof these properties at boot via Magisk:

```properties
# Critical Build Properties (prevent Cronet detection)
ro.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys
ro.build.id=AP4A.241205.013
ro.build.display.id=AP4A.241205.013
ro.build.tags=release-keys
ro.build.type=user

# Device Identity
ro.product.brand=google
ro.product.manufacturer=Google
ro.product.model=Pixel 7 Pro
ro.product.name=cheetah
ro.product.device=cheetah
ro.product.board=cheetah
ro.hardware=cheetah

# Build.VERSION Fields
ro.build.version.release=15
ro.build.version.sdk=35
ro.build.version.security_patch=2024-12-05

# Security Flags
ro.debuggable=0
ro.secure=1
ro.kernel.qemu=0
ro.boot.qemu=0
```

**Why boot spoofing is required**: Some apps use Cronet (Chrome's networking library) which performs integrity checks on Build fields during initialization. If Build fields are modified after boot, Cronet detects tampering and crashes with SIGTRAP. Boot spoofing ensures these values are set before any app reads them.

### LSPosed Module Setup

1. Install [LSPosed](https://github.com/LSPosed/LSPosed) on your device
2. Install the DeviceSpoofLab-Hooks apk from [releases](https://github.com/yubunus/DeviceSpoofLab-Hooks/releases)
3. **Run app once** Run action the app once using LSPOSED(it auto-creates config in app's private storage)
4. Enable the module in LSPosed Manager
5. Select target apps in LSPosed scope
6. Restart target apps

## Quick Setup

1. **Install LSPosed** via Magisk/KernelSU if not already installed

2. **Install the APK** (build from source or download from releases)

   ```bash
   adb install app-debug.apk
   ```

3. **Open LSPosed Manager** → Enable "DeviceSpoofLab-Hooks"

4. **Select target apps**: Tap the module → Scope → Select apps to spoof

5. **Restart target apps** or reboot device

## Building from Source

```bash
# Clone the repository
git clone https://github.com/yubunus/DeviceSpoofLab-Hooks.git
cd DeviceSpoofLab-Hooks

# Build debug APK
./gradlew assembleDebug

# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## How It Works

### Hybrid Architecture: Boot Spoofing + Runtime Hooks

#### Layer 1: Magisk Boot Spoofing (Critical Build Properties)

**Happens**: During system boot, before any app starts
**Spoofs**: `Build.*` static fields via system properties
**Why**: Prevents Cronet and security libraries from detecting tampering

Apps that read `Build.FINGERPRINT` directly get the spoofed value set at boot. No runtime modification needed.

#### Layer 2: SystemProperties Interception (Runtime Hook)

**Happens**: When app calls `SystemProperties.get()` via reflection
**Spoofs**: All `ro.*` properties dynamically

Apps using reflection to bypass Magisk are caught:

```java
XposedHelpers.findAndHookMethod("android.os.SystemProperties", classLoader, "get",
    String.class, new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) {
            String key = (String) param.args[0];
            String spoofed = ConfigManager.getSystemProperty(key, null);
            if (spoofed != null) param.setResult(spoofed);
        }
    });
```

#### Layer 3: Runtime API Hooks

**Happens**: When app calls Android APIs (TelephonyManager, Settings, etc.)
**Spoofs**: IMEI, ANDROID_ID, GAID, etc.

Only `Build.getSerial()` is hooked (method call, not field access):

```java
XposedHelpers.findAndHookMethod(buildClass, "getSerial",
    new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) {
            param.setResult(ConfigManager.getSerial());
        }
    });
```

#### Layer 4: Emulator Detection Bypass

Defensive hooks - only hide files that don't exist on real devices.

## Configuration

### Config File Format

Standard properties file (`key=value`):

```properties
ro.product.model=Pixel 7 Pro
ro.build.fingerprint=google/cheetah/cheetah:15/AP4A.241205.013/12621605:user/release-keys
ro.kernel.qemu=0
webview.user_agent=Mozilla/5.0 (Linux; Android 15; Pixel 7 Pro)...
```

### Random Value Generation

Leave fields blank for auto-generation:

```properties
ro.serialno=          # Auto-generates 12-char alphanumeric
ro.bootloader=        # Auto-generates cheetah-1.2-{8-char-hex}
ANDROID_ID=           # Auto-generates 16-char hex
```

## Troubleshooting

**Module not working?**

- Check LSPosed is active and module is enabled
- Verify target app is in scope
- Force stop and relaunch app
- Check logs: `adb logcat | grep DeviceSpoofLab`

**App crashes on startup (SIGTRAP/Cronet)?**

- Some apps have strict security checks
- BuildHooks is automatically disabled for these apps
- They still get spoofing via SystemPropertiesHooks (safe)
- Check logs for "Skipping BuildHooks" message

**Config file not loading?**

- Check file exists: `adb shell cat /data/data/com.devicespooflab.hooks/files/device_profile.conf`
- Module will use embedded defaults if config not found

**Properties not spoofed?**

- Check SystemPropertiesHooks loaded in logs
- Ensure property key is in config
- Some properties auto-generated if blank

## Compatibility

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Tested**: Android 13, 14, 15
- **Real Device Safe**: All hooks are non-intrusive

## Architecture Comparison

| Feature                           | Magisk Only | LSPosed Only | Magisk + LSPosed (Hybrid) |
| --------------------------------- | ----------- | ------------ | ------------------------- |
| Boot-time Build.\* spoofing       | ✅          | ❌           | ✅                        |
| Cronet/security library safe      | ✅          | ❌           | ✅                        |
| SystemProperties reflection hook  | ❌          | ✅           | ✅                        |
| TelephonyManager hooks            | ❌          | ✅           | ✅                        |
| WebView User-Agent spoofing       | ❌          | ✅           | ✅                        |
| Emulator file hiding              | ❌          | ✅           | ✅                        |
| Runtime API spoofing (GAID, etc.) | ❌          | ✅           | ✅                        |
| **Works with Cronet**             | ✅          | ❌           | ✅                        |

## License

MIT License - See [LICENSE](LICENSE) file for details

## Disclaimer

This tool is for educational and authorized testing purposes only. Users are responsible for complying with applicable laws and terms of service.
