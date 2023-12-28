package com.upbad.apps.opgo.plugin.systemui;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.TextView;

import com.upbad.apps.opgo.plugin.IPlugin;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.upbad.apps.opgo.util.LogUtil.log;

public class ClockStyleOp14 implements IPlugin {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam, ClassLoader classLoader) {
        log("SystemUI hook() started");

        // in com.oplus.systemui.common.clock.OplusClockExImpl
        // found function: public boolean setTextWithRedOneStyle(@NotNull TextView textView, @NotNull CharSequence charSequence)
        XposedHelpers.findAndHookMethod("com.oplus.systemui.common.clock.OplusClockExImpl",
                classLoader,
                "setTextWithRedOneStyle",
                TextView.class,
                CharSequence.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        TextView textView = (TextView) param.args[0];
                        CharSequence text = (CharSequence) param.args[1];

                        if (textView == null) {
                            param.setResult(false);
                            log("OplusClockExImpl textview was null");
                            return;
                        }
                        if (text == null || TextUtils.isEmpty(text)) text = "time";

                        textView.setText(text);
                        param.setResult(false);
                    }
                });

        XposedHelpers.findAndHookMethod("com.oplus.keyguard.utils.KeyguardUtils",
                classLoader,
                "getSpannedHourString",
                Context.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        Context context = (Context) param.args[0];
                        String text = (String) param.args[1];

                        if (context == null || TextUtils.isEmpty(text)) {
                            text = "";
                        }
                        param.setResult(new SpannableStringBuilder(text.toString()));
                    }
                });

        log("SystemUI hook() ended");
    }
}
