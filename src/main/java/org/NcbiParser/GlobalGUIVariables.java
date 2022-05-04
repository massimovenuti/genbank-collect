package org.NcbiParser;

public class GlobalGUIVariables {
    private static GUIVariables guiVariables;

    public static GUIVariables get() {
        if (guiVariables == null)
            guiVariables = new GUIVariables();
        return guiVariables;
    }
}
