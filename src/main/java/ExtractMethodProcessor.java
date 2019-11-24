import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtractMethodProcessor {

    public List<String> repos;
    private static final String UTF_8 = "utf-8";

    public ExtractMethodProcessor(String path) {
        this.repos = readUrlRepos(path);
    }

    public List<String> readUrlRepos(String filePath){
        List<String> allLines = new ArrayList<>();
        try {
            allLines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allLines;
    }
    public void abcd(String rep) throws Exception {
        GitService gitService = new GitServiceImpl();
        final boolean[] saveCommit = {false};
        final int[] pos = new int[1];
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        Repository repo = gitService.cloneIfNotExists("emp/" + FilenameUtils.getBaseName(rep), rep);
        List<RefactorInfo> extractMethodsInfoList = new ArrayList<RefactorInfo>();
        final String[] lastCommitId = new String[1];



        try {
            miner.detectAll(repo, "master", new RefactoringHandler() {


                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                if (saveCommit[0]){
                    extractMethodsInfoList.get(pos[0]).setCommitIdBefore(commitId);
                    saveCommit[0] = false;
                }
                RefactorInfo extractMethodsInfo = new RefactorInfo();
                for (Refactoring ref : refactorings) {
                    if(ref.getRefactoringType() == RefactoringType.EXTRACT_OPERATION) {
                        setUpMethodInfo(commitId, ref, extractMethodsInfo, lastCommitId[0], "");
                    }
                }
                if(extractMethodsInfo.getCommitIdAfter() != null) {
                    extractMethodsInfoList.add(extractMethodsInfo);
                    saveCommit[0] = true;
                    pos[0] = extractMethodsInfoList.size() - 1;
                }
                lastCommitId[0] = commitId;

                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }


        for (RefactorInfo refInfo : extractMethodsInfoList) {
            ClassInfo resultBefore = new ClassInfo();
            ClassInfo resultAfter = new ClassInfo();
            //visitClass
            String split = repo.getDirectory().getAbsolutePath().split("\\.")[0];
            try {
                gitService.checkout(repo, refInfo.getCommitIdBefore());
                String classFileBefore = getJavaFIle(Paths.get(split), refInfo.getClassBefore().get(0));
                System.out.println(classFileBefore);
                visitor(classFileBefore, resultBefore);
            }
            catch (Exception e) {
                System.out.println(e);
            }
            try {
                gitService.checkout(repo, refInfo.getCommitIdAfter());
                String classFileAfter = getJavaFIle(Paths.get(split), refInfo.getClassAfter().get(0));

                visitor(classFileAfter, resultAfter);
                if (!Float.isInfinite(resultAfter.getCohesion()) && resultAfter.name != null)
                System.out.println(rep + "," + resultBefore.name + "," + resultAfter.name + "," + resultBefore.getCohesion() + "," + resultAfter.getCohesion());
            }
            catch (Exception e) {
                System.out.println(e);
            }



        }
    }
    public void analizeProjects(String rep) throws Exception {
        GitService gitService = new GitServiceImpl();
        final boolean[] saveCommit = {false};
        final int[] pos = new int[1];
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        Repository repo = gitService.cloneIfNotExists("emp/" + FilenameUtils.getBaseName(rep), rep);
        List<RefactorInfo> extractMethodsInfoList = new ArrayList<RefactorInfo>();
        final String[] lastCommitId = new String[1];



        try {
            miner.detectAll(repo, "master", new RefactoringHandler() {


                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                if (saveCommit[0]){
                    extractMethodsInfoList.get(pos[0]).setCommitIdBefore(commitId);
                    saveCommit[0] = false;
                }
                RefactorInfo extractMethodsInfo = new RefactorInfo();
                for (Refactoring ref : refactorings) {
                    if(ref.getRefactoringType() == RefactoringType.EXTRACT_OPERATION) {
                        ExtractOperationRefactoring nn= (ExtractOperationRefactoring) ref;

                        setUpMethodInfo(commitId, ref, extractMethodsInfo, lastCommitId[0], nn.getExtractedOperation().getName());

                        String stadistics = nn.getExtractedOperation().getParameters() + ";" + nn.getExtractedOperation().getVisibility() + ";" + nn.getExtractedOperation().getReturnParameter();
                        System.out.println(stadistics);
                    }
                }
                if(extractMethodsInfo.getCommitIdAfter() != null) {
                    extractMethodsInfoList.add(extractMethodsInfo);
                    saveCommit[0] = true;
                    pos[0] = extractMethodsInfoList.size() - 1;
                }
                lastCommitId[0] = commitId;

                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }


        for (RefactorInfo refInfo : extractMethodsInfoList) {
            String towrite = null ;
            String coheBefore = null;
            String split = repo.getDirectory().getAbsolutePath().split("\\.")[0];
            try {
                gitService.checkout(repo, refInfo.getCommitIdBefore());
                String classFileBefore = getJavaFIle(Paths.get(split), refInfo.getClassBefore().get(0));
                String cohesionBefore = executeJasome(classFileBefore, "MetricsBeforeExtract.xml");
                coheBefore = cohesionBefore;

                //System.out.println(cohesionBefore);
                towrite = classFileBefore + ";" + cohesionBefore;
                /*try(FileWriter fw = new FileWriter("CohesionValuesBefore.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw))
                {
                    out.println(classFileBefore + ";" + cohesionBefore);
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }*/
            }
            catch (Exception e) {
                System.out.println(e);
            }
            try {
                gitService.checkout(repo, refInfo.getCommitIdAfter());
                String classFileAfter = getJavaFIle(Paths.get(split), refInfo.getClassAfter().get(0));
                String cohesionAfter = executeJasome(classFileAfter, "MetricsAfterExtract.xml");
               // System.out.println(cohesionAfter);
                try(FileWriter fw = new FileWriter("CohesionValuesBeforeAndAfter.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw))
                {

                        out.println(towrite + ";" + classFileAfter + ";" + refInfo.getMethodName() + ";"+ cohesionAfter);

                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }
                /*try(FileWriter fw = new FileWriter("CohesionValuesAfter.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw))
                {
                    out.println(classFileAfter + ";" + refInfo.getMethodName() + ";"+ cohesionAfter);
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }*/

            }
            catch (Exception e) {
                System.out.println(e);
            }



        }
    }

    private void setUpMethodInfo(String commitId, Refactoring ref, RefactorInfo extractMethodsInfo, String commitIdBefore, String methodName) {
        extractMethodsInfo.setCommitIdBefore(commitIdBefore);
        extractMethodsInfo.setCommitIdAfter(commitId);
        extractMethodsInfo.addClassBefore(ref.getInvolvedClassesBeforeRefactoring().get(0));
        extractMethodsInfo.addClassAfter(ref.getInvolvedClassesAfterRefactoring().get(0));
        extractMethodsInfo.setMethodName(methodName);

    }
    public String getJavaFIle(Path root, String clas){
        List<String> filesList = new ArrayList<>();
        String javaFile = null;
        try (Stream<Path> walk = Files.walk(root)) {

            filesList = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".java")).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String dir: filesList) {
            if (dir.endsWith(getClass(clas) + ".java")){
                javaFile = dir;
            }
        }
        return javaFile;
    }
    public String getClass (String base){
        return base.substring(base.lastIndexOf(".") + 1);
    }

    public void visitor(String path, ClassInfo result) {
        //System.out.println("el path es: " + path);
        try(FileInputStream in = new FileInputStream(path)){
            CompilationUnit cu;
            try {

                cu = StaticJavaParser.parse(in, Charset.forName(UTF_8));
                ClassVisitor classVIsitor = new ClassVisitor();
                cu.accept(classVIsitor, result);



            } catch (Error e) {
                System.out.println(String.format("Critical Javaparser error while processing the file %s.", path));
            }

        } catch (Exception e) {
            // We can ignore small errors here
            System.out.println(String.format("Error while processing the file %s.", path));
        }
    }
    public String executeJasome(String path, String fileName) {
        String cohesion = null;
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("/Users/Abel/Documents/ClasesU/Seminario/jasome-0.6.8-alpha/bin/jasome", path);
        try {

            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }


            int exitVal = process.waitFor();
            if (exitVal == 0) {


                Document doc = convertStringToXMLDocument(output.toString());
                NodeList list = doc.getElementsByTagName("Metric");
                for (int i=0; i<list.getLength(); i++) {
                    Element element = (Element)list.item(i);
                    if(element.getAttribute("name").equals("LCOM*")){
                        cohesion = element.getAttribute("value");
                    }
                }


               /* System.out.println("Success!");
                //System.out.println(output);
                //System.exit(0);
                try(FileWriter fw = new FileWriter(fileName, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw))
                {
                    out.println(output);
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }*/
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cohesion;
    }

    private Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
