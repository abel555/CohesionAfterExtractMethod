package org.method;

import com.sun.tools.corba.se.idl.IncludeGen;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {


        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("E:\\projectsinfoMuestra2.txt"); //args[1]
        List<String> repos = extractMethodProcessor.readUrlRepos("E:\\projects-urls2.txt");
        for (String rep:repos){
            extractMethodProcessor.analizeProjects(rep); //argo[0]
        }


    }

}





