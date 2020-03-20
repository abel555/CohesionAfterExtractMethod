package org.method;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("/Users/abel/Documents/ClasesU/ClonesPruebas.txt"); //args[1]


        extractMethodProcessor.analizeProjects("https://github.com/AigeStudio/WheelPicker.git"); //argo[0]

    }

}





