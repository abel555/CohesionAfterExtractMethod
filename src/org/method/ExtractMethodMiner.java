package org.method;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("/Users/abel/Desktop/ClassMetrics2muestra.txt");


        List<String> repos =  extractMethodProcessor.readUrlRepos("/Users/abel/Downloads/projects2.txt");
        //extractMethodProcessor.checkout("/Users/abel/Documents/ClasesU/Seminario/CohesionAfterExtractMethod/emp/WordPress-Android/");
        /*for (String rep:repos){

        }*/
            extractMethodProcessor.analizeProjects("https://github.com/esoxjem/MovieGuide.git");

       // System.out.println(extractMethodProcessor.hackedJasomeLcom("/Users/abel/Desktop/Driver.java"));

    }

}





