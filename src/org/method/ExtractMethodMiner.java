package org.method;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("/Users/abel/Desktop/ClassMetricsFixed.txt");


        List<String> repos =  extractMethodProcessor.readUrlRepos("/Users/abel/Desktop/projects-urls.txt");

        for (String rep:repos){

        extractMethodProcessor.analizeProjects(rep);
        }

       // System.out.println(extractMethodProcessor.hackedJasomeLcom("/Users/abel/Desktop/Driver.java"));

    }

}





