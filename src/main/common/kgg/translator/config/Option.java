package kgg.translator.config;

import java.util.ArrayList;
import java.util.List;

public class Option {
//    private static boolean autoChatHud = false;
//    private static boolean addChatHudTip = true;
//    private static boolean autoToolTip = false;
//    private static boolean autoScreenText = false;
//    private static boolean autoWorldText = false;
//
//    public static boolean isAutoWorldText() {
//        return autoWorldText;
//    }
//
//    public static void setAutoWorldText(boolean autoWorldText) {
//        Options.autoWorldText = autoWorldText;
//    }
//
//    public static boolean isAddChatHudTip() {
//        return addChatHudTip;
//    }
//
//    public static void setAddChatHudTip(boolean addChatHudTip) {
//        Options.addChatHudTip = addChatHudTip;
//    }
//
//    public static boolean isAutoChatHud() {
//        return autoChatHud;
//    }
//
//    public static void setAutoChatHud(boolean autoChatHud) {
//        Options.autoChatHud = autoChatHud;
//    }
//
//    public static boolean isAutoToolTip() {
//        return autoToolTip;
//    }
//
//    public static void setAutoToolTip(boolean autoToolTip) {
//        Options.autoToolTip = autoToolTip;
//    }
//
//    public static boolean isAutoScreenText() {
//        return autoScreenText;
//    }
//
//    public static void setAutoScreenText(boolean autoScreenText) {
//        Options.autoScreenText = autoScreenText;
//    }

    private static final List<Option> options = new ArrayList<>();

    public static List<Option> getOptions() {
        return options;
    }

    public final String name;
    private boolean enable;
    public final boolean defaultValue;

    public Option(String name, boolean defaultValue) {
        this.name = name;
        this.enable = defaultValue;
        this.defaultValue = defaultValue;
        options.add(this);
    }
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
