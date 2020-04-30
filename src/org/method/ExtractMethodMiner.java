package org.method;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("/Users/abel/Desktop/faninV3.txt");
        List<String> repos = extractMethodProcessor.readUrlRepos("/Users/abel/Desktop/projects-urls.txt");

        for(String rep: repos){
            extractMethodProcessor.analizeProjects(rep);
        }
        //extractMethodProcessor.analizeProjects("https://github.com/h2oai/h2o-2.git");
        //extractMethodProcessor.analizeProjects("https://github.com/tyzlmjj/PagerBottomTabStrip.git");
        //extractMethodProcessor.analizeProjects("https://github.com/intuit/karate.git");

    }

}





