package org.method;

import com.sun.tools.corba.se.idl.IncludeGen;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {


        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("E:\\csploitSmells.txt"); //args[1]
        List<String> repos = extractMethodProcessor.readUrlRepos("E:\\projects-urls.txt");
       /* for (String rep:repos){
        }*/
            extractMethodProcessor.analizeProjects("https://github.com/cSploit/android.git"); //argo[0]


    }

}





