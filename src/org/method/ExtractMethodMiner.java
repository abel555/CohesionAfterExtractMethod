package org.method;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("/Users/abel/Desktop/macMethodMetrics.txt");


        extractMethodProcessor.analizeProjects("https://github.com/tyzlmjj/PagerBottomTabStrip.git");

    }

}





