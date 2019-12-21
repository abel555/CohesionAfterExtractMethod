import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtractMethodMiner {
    public static void main(String[] args) throws Exception {
        ExtractMethodProcessor extractMethodProcessor = new ExtractMethodProcessor("/Users/Abel/Documents/projects-urls.txt");
       //System.out.println(extractMethodProcessor.repos);
      for (String repo:extractMethodProcessor.repos ) {
            extractMethodProcessor.analizeProjects(repo);

       }


      //extractMethodProcessor.analizeProjects(extractMethodProcessor.repos.get(2));
     //extractMethodProcessor.executeJasome("/Users/Abel/Documents/ClasesU/Arqui/ArquiVoiceMail/src/Main.java", "pruebas.xml");



    }

}
