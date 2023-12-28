package com.upbad.apps.opgo;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import com.upbad.apps.opgo.plugin.IPlugin;
import com.upbad.apps.opgo.plugin.systemui.ClockStyle;
import com.upbad.apps.opgo.plugin.systemui.ClockStyleOp12;
import com.upbad.apps.opgo.plugin.systemui.ClockStyleOp13;
import com.upbad.apps.opgo.plugin.systemui.ClockStyleOp14;
import com.upbad.apps.opgo.plugin.uiengine.UIEngineClockStyleOp13;
import com.upbad.apps.opgo.util.HookParams;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.log;

public class Main implements IXposedHookLoadPackage {

    private static final IPlugin GetSDKLevelPlugin() {
        if(VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE)
            return new ClockStyleOp14();
        if(VERSION.SDK_INT == VERSION_CODES.TIRAMISU)
            return new ClockStyleOp13();
        if (VERSION.SDK_INT >= VERSION_CODES.S) return new ClockStyleOp12();

        return new ClockStyle();
    }

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) {
        switch (lpparam.packageName) {
            case HookParams.UIENGINE_PACKAGE_NAME:
                if (VERSION.SDK_INT == VERSION_CODES.TIRAMISU ) {
                    loadPlugins(new UIEngineClockStyleOp13(), lpparam, lpparam.classLoader);
                }
                break;
            case HookParams.SYSTEMUI_PACKAGE_NAME:
                loadPlugins(GetSDKLevelPlugin(), lpparam, lpparam.classLoader);
                break;
            default:
        }
    }

    private void loadPlugins(IPlugin plugin, LoadPackageParam lpparam, ClassLoader classLoader) {
        if(plugin == null)
        {
            log("No plugins found for this SDK version: "+ VERSION.SDK_INT);
            return;
        }

        try {
            plugin.hook(lpparam, classLoader);
        } catch (Error | Exception e) {
            log("loadPlugins error: " + e);
        }
    }
}
