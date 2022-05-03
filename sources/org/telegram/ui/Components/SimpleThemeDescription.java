package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.ThemeDescription;

public class SimpleThemeDescription {
    public static ArrayList<ThemeDescription> createThemeDescriptions(ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate, String... strArr) {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>(strArr.length);
        for (String str : strArr) {
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, str));
        }
        return arrayList;
    }
}