package org.method;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor(args[1]);


        extractMethodProcessor.analizeProjects(args[0]);

    }

}





