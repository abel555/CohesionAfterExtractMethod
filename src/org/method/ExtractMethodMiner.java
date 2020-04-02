package org.method;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {

        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("/Users/abel/Desktop/classMacMetrics.txt");

        List<String> repos =  extractMethodProcessor.readUrlRepos("/Users/abel/Desktop/projects-urls.txt");

        for (String rep:repos){
            extractMethodProcessor.analizeProjects(rep);

        }

    }

}





