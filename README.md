# DeviceSpoofLab-Hooks

Companion LSPosed/Xposed module for [DeviceSpoofLab-Magisk](https://github.com/yubunus/DeviceSpoofLab-Magisk). Spoofs device identifiers that cannot be changed via Magisk `resetprop`.

> **Important**: This module does nothing by itself. You must enable it in LSPosed and select which apps are in scope.

## What This Module Spoofs

| Identifier   | Description                  |
| ------------ | ---------------------------- |
| IMEI/MEID    | Device cellular identifiers  |
| IMSI         | Subscriber identity (SIM)    |
| ICCID        | SIM card serial number       |
| Phone Number | Line 1 number                |
| Build.SERIAL | Device serial number         |
| GAID         | Google Advertising ID        |
| MediaDrm ID  | Widevine device unique ID    |
| GSF ID       | Google Services Framework ID |

## What This Module Does NOT Spoof

These are handled by the Magisk module via `resetprop`:

- Build.FINGERPRINT, Build.MODEL, Build.DEVICE, Build.PRODUCT
- All `ro.product.*` and `ro.build.*` properties
- ANDROID_ID (set via `settings put`)
- WiFi/Bluetooth MAC (causes connectivity issues)

## Requirements

- Rooted Android device
- [Magisk](https://github.com/topjohnwu/Magisk) installed
- [LSPosed](https://github.com/LSPosed/LSPosed) installed
- [DeviceSpoofLab-Magisk](https://github.com/yubunus/DeviceSpoofLab-Magisk) installed and configured

## Installation

Download the latest APK from the [Releases](https://github.com/yubunus/DeviceSpoofLab-Hooks/releases) page. No need to clone or build from source.

## Quick Setup

1. **Install LSPosed** via Magisk if not already installed

2. **Install DeviceSpoofLab-Magisk** and run `DeviceSpoofLab` to configure a persona

3. **Install the APK** (download from Releases)

   ```bash
   adb install DeviceSpoofLab-Hooks-v1.0.apk
   ```

4. **Open LSPosed Manager**

5. **Enable the module**:

   - Find "DeviceSpoofLab-Hooks" in the modules list
   - Toggle it ON

6. **Select target apps**:

   - Tap on the module
   - Go to "Scope"
   - Select the apps you want to spoof
   - **Do NOT select system apps or Google Play Services**

7. **Force stop target apps** or reboot your device

## Building from Source

```bash
# Clone the repository
git clone https://github.com/yubunus/DeviceSpoofLab.git
cd DeviceSpoofLab/DeviceSpoofLab-Hooks

# Build debug APK
./gradlew assembleDebug

# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## How It Works

1. The Magisk module writes device persona to `/data/adb/modules/devicespooflab/personas/current.conf`
2. This Xposed module reads that config when an app loads
3. If config is unavailable, random valid values are generated
4. All hooked API calls return the spoofed values

## Troubleshooting

**Module not working?**

- Ensure LSPosed is properly installed (check LSPosed Manager → Logs)
- Verify the target app is added to the module's scope
- Force stop the target app after enabling
- Check LSPosed logs for any errors

**Values not matching Magisk persona?**

- Ensure DeviceSpoofLab-Magisk is installed and a persona is configured
- The config file must be world-readable (`chmod 0644`)
- If config can't be read, random values are used instead

**App detecting the hook?**

- Some apps detect Xposed framework itself
- Consider using [Shamiko](https://github.com/LSPosed/LSPosed.github.io/releases) to hide LSPosed
- Don't add unnecessary apps to the scope

## License

MIT License - See [LICENSE](LICENSE) for details.

## Related

- [DeviceSpoofLab-Magisk](https://github.com/yubunus/DeviceSpoofLab-Magisk) - The Magisk module that handles system properties
