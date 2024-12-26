package kgg.translator.option;

public class TranslateOption {
    public static Option useSecondaryTranslator = new Option("useSecondaryTranslator", true);
    public static Option splitStyledTextIntoSegments = new Option("splitStyledTextIntoSegments", false);
    public static void register() {}
}
