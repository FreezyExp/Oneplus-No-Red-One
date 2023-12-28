# Description

Picking this up where zehuanli left off.

This is an Xposed module to prevent Oneplus from making red "1" digits on the clocks.

If you hate the red "1" characters on the lock screen clock and AOD (Always-On Display) clock like me, you may try this Xposed module to get rid of it.

Supports OOS 11, OOS 12, OOS 13 and OOS 14 (I did not check AOD not tested).

## Issue with the OnePlus clock widget
On OOS 13, the OnePlus clock widget has a standalone package `com.oneplus.deskclock` that renders the red "1" digits. This package has been obfuscated in a way such that a fix would either be *temporary* (that stops working after an update of this widget) or *overkilling* (hooking an Android-wide function which introduces unnecessary computations to other unrelated codes). Thus, I would not fix it and just use some other clock widgets.

For those desired, there are two functions in the package `com.oneplus.deskclock` that use `SpannableStringBuilder` to set a red color for "1"s. Modify these two functions to stop this behavior. Alternatively, you may hook the function `setSpan` of `SpannableStringBuilder` to stop its execution whenever a character '1' is being modified. Again, this is either temporary or overkilling.

## Limited tests done on Android 14
As I do not have AOD enabled, I also did not test if my fixes apply to all the AOD clocks as well

## Note on XPosed Resource Replacement
I tried first to use XPosed Resource Replacement to set the red clock color to white, but that did not seem to work.
As this was referenced in all functions related to the Red-One it seemed like a clean fix.



```
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if(VERSION.SDK_INT < VERSION_CODES.UPSIDE_DOWN_CAKE)
            return;

        switch (resparam.packageName) {
            default:
                return;
            case "com.android.systemui":
                try {
                    resparam.res.setReplacement(resparam.packageName, "color", "red_clock_hour_color", Color.WHITE);
                    log(resparam.packageName +" changed red to white"); // seen in log, but red one remained :-(

                    XResources.setSystemWideReplacement(resparam.packageName, "color", "red_clock_hour_color", Color.WHITE); // this line always threw NoRedError with resource not found exceptions
                    log(resparam.packageName +" changed red to white system wide too");
                } catch (Error | Exception e) {
                    log(resparam.packageName +" NoRed error: " + e);
                }
        }
    }
```
