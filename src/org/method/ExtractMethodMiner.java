package org.method;

import com.sun.tools.corba.se.idl.IncludeGen;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("E:\\clonesPruebas.txt"); //args[1]


        extractMethodProcessor.analizeProjects("https://github.com/Dimezis/BlurView.git"); //argo[0]


    }

}





