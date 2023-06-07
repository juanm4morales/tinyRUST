package Utils;

import SymbolTable.ClassEntry;

import java.util.HashSet;

public class StringUtils {
    public static String multiTabs(int tabs) {
        String string = "";
        for (int i = 0; i < tabs; i++) {
            string = string.concat("\t");
        }
        return string;
    }

    public static String getClassesString(HashSet<ClassEntry> classes) {
        String classesString = "";
        int i = 0;
        int size = classes.size();
        for (ClassEntry classEntry:classes) {
            if (i<size-1){
                classesString = classesString.concat(classEntry.getId()+", ");
            }
            else {
                classesString = classesString.concat(classEntry.getId());
            }
        }
        return classesString;
    }
}
