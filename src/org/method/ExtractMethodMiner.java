package org.method;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("/Users/abel/Desktop/mac-smells-HOLO.txt");
        List<String> repos = extractMethodProcessor.readUrlRepos("/Users/abel/Desktop/projects-urls.txt");
/*
        for(String rep: repos){
            extractMethodProcessor.analizeProjects(rep);
        }*/
        extractMethodProcessor.analizeProjects("https://github.com/Prototik/HoloEverywhere.git");
       // extractMethodProcessor.analizeProjects("https://github.com/tyzlmjj/PagerBottomTabStrip.git");

    }

}





