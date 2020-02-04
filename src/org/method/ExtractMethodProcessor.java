package org.method;

import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.jgit.lib.Repository;
import org.jasome.input.FileScanner;
import org.jasome.input.Method;
import org.jasome.input.Project;
import org.jasome.input.Type;
import org.jasome.metrics.calculators.*;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.github.javaparser.ast.body.Parameter;

public class ExtractMethodProcessor {
    List<String> metricsList =  Arrays.asList(

            "AHF","AIF","Aa","Ad","Ai","Ait","Ao",
            "Av","ClRCi","ClTCi","DIT","HMd","HMi","LCOM*",
            "MHF","MIF","Ma","Md","Mi","Mit","Mo","NF","NM","NMA","NMI",
            "NOA","NOCh","NOD","NOL","NOPa","NORM","NPF","NPM","NSF","NSM",
            "PMR","PMd","PMi","RTLOC","SIX","TLOC","WMC");

    public String outPutFileName;
    private static final String UTF_8 = "utf-8";
    LackOfCohesionMethodsCalculator calculator = new LackOfCohesionMethodsCalculator();
    CyclomaticComplexityCalculator cyclomaticComplexityCalculator = new CyclomaticComplexityCalculator();
    FanCalculator fanCalculator = new FanCalculator();
    McclureCalculator mcclureCalculator = new McclureCalculator();
    NestedBlockDepthCalculator nestedBlockDepthCalculator = new NestedBlockDepthCalculator();

    public ExtractMethodProcessor(String path) {
        this.outPutFileName = path;
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
    public  void testRefactoringMiner(String rep) throws Exception{
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        StringBuilder localFile = new StringBuilder();
        localFile.append("emp/");localFile.append(FilenameUtils.getBaseName(rep));
        Repository repo = gitService.cloneIfNotExists(localFile.toString(), rep);
        testHandler hanlder = new testHandler();
        try {
            miner.detectAll(repo, "master", hanlder);

        } catch (Exception e) {
            //System.out.println(e);
        }
    }
    public void analizeProjects(String rep) throws Exception {

        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        StringBuilder localFile = new StringBuilder();
        localFile.append("/Users/abel/Documents/ClasesU/Seminario/CohesionAfterExtractMethod/emp/");localFile.append(FilenameUtils.getBaseName(rep));
        Repository repo = gitService.cloneIfNotExists(localFile.toString(), rep);
        RefactoringHandler extractHandler = new ExtractHandler();
        RefactoringHandler testHandler = new testHandler();
        try {

            miner.detectAll(repo, "master", extractHandler);

        } catch (Exception e) {
            //System.out.println(e);
        }
        List<RefactorInfo> refactorInfoList = ((ExtractHandler) extractHandler).getExtractMethodsInfoList();
        System.out.println(refactorInfoList.size());

        for (RefactorInfo refInfo :refactorInfoList ) {


            for (int i = 0; i< refInfo.getRefactoring().size() ; i++){
                ExtractOperationRefactoring nn = (ExtractOperationRefactoring) refInfo.getRefactoring().get(i);
                String split = repo.getDirectory().getAbsolutePath().split("\\.")[0];
                StringBuilder toWriteBefore = new StringBuilder();
                try {
                    gitService.checkout(repo, refInfo.getCommitIdBefore());
                    String classFileBefore = getJavaFIle(Paths.get(split), refInfo.getClassBefore().get(i));

                    toWriteBefore.append(classFileBefore);toWriteBefore.append(";");
                    String linkTocommit = rep.split("\\.git")[0] + "/commit/" + refInfo.getCommitIdBefore();
                    toWriteBefore.append( linkTocommit);
                    toWriteBefore.append(";");
                    toWriteBefore.append(executeJasome(classFileBefore));

                }
                catch (Exception e) {
                    //System.out.println(e);
                }
                try {
                    gitService.checkout(repo, refInfo.getCommitIdAfter());
                    String classFileAfter = getJavaFIle(Paths.get(split), refInfo.getClassAfter().get(i));
                    StringBuilder toWrite = new StringBuilder();
                    toWrite.append(classFileAfter);toWrite.append(";");
                    String linkTocommit = rep.split("\\.git")[0] + "/commit/" + refInfo.getCommitIdAfter();
                    toWrite.append( linkTocommit);
                    toWrite.append(";");
                    toWrite.append(executeJasome(classFileAfter));
                    try(FileWriter fw = new FileWriter(outPutFileName, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw))
                    {
                        toWriteBefore.append(toWrite);
                        out.println(toWriteBefore);



                    } catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                    toWriteBefore = null;
                    toWrite = null;

                }
                catch (Exception e) {
                    //System.out.println(e);
                }
            }
        }
        //aa
        refactorInfoList = null;

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

    public String executeJasome(String path) {
        String metrics = "";
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
                for (String metric:metricsList) {
                    boolean flag = false;
                    for (int i = 0; i < list.getLength(); i++) {
                        if (list.item(i).getParentNode().getParentNode().getNodeName() == "Class") {
                            Element element = (Element) list.item(i);

                            if(metric.equals(element.getAttribute("name"))){
                                metrics = metrics + element.getAttribute("value") + ";";
                                flag = true;
                                break;
                            }
                        }

                    }
                    if(!flag){
                        metrics = metrics + "null" + ";";
                    }
                }
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return metrics;
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

    public String hackedJasome(String dir, int starLine, String methodName) {


        StringBuilder response = new StringBuilder();

        File scanDir = new File(dir).getAbsoluteFile();
        FileScanner scanner = new FileScanner(scanDir);
        IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
        scanner.setFilter(fileFilter);
        Project scannerOutput = scanner.scan();
        //ProcessorFactory.getProcessor().process(scannerOutput);
        Set<Type> types = scannerOutput.getPackages().stream().findFirst().get().getTypes();

        for (Type type:types){
            Set<Method> methods = type.getMethods();

            for (Method method:methods) {

                if (/*method.getSource().getBegin().get().line == starLine &&*/
                        method.getSource().getNameAsString().equals(methodName)){
                    response.append(method.getSource().getParameters().size());
                    response.append(";");
                    response.append(cyclomaticComplexityCalculator.calculate(method));
                    response.append(";");
                    response.append(fanCalculator.calculate(method));
                    response.append(";");
                    response.append(mcclureCalculator.calculate(method));
                    response.append(";");
                    response.append(nestedBlockDepthCalculator.calculate(method));
                    response.append(";");

                    break;

                }
            }
        }
        return  response.toString();
    }
    public String hackedJasomeConsole(String dir, String methodName, List<String> originMethodParameters) {
        StringBuilder response = new StringBuilder();
        File scanDir = new File(dir).getAbsoluteFile();
        FileScanner scanner = new FileScanner(scanDir);
        IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
        scanner.setFilter(fileFilter);
        Project scannerOutput = scanner.scan();

        Set<Type> types = scannerOutput.getPackages().stream().findFirst().get().getTypes();
        for (Type type:types){
            Set<Method> methods = type.getMethods();


            for (Method method:methods) {

                if ( method.getSource().getNameAsString().equals(methodName)) {

                    if (method.getSource().getParameters().size() == originMethodParameters.size()) {
                        boolean isTheMethod= true;
                        int i = 0;
                        for (Parameter pr : method.getSource().getParameters()) {
                            if (!pr.getType().toString().equals(originMethodParameters.get(i).toString())) {
                                isTheMethod = false;
                            }
                            i++;
                            if (!isTheMethod) {
                                break;
                            }
                        }
                        if (isTheMethod) {
                            response.append(method.getSource().getParameters().size());
                            response.append(";");
                            response.append(cyclomaticComplexityCalculator.calculate(method));
                            response.append(";");
                            response.append(fanCalculator.calculate(method));
                            response.append(";");
                            response.append(mcclureCalculator.calculate(method));
                            response.append(";");
                            response.append(nestedBlockDepthCalculator.calculate(method));
                            response.append(";");

                        }
                    }


                }
            }
        }
        return response.toString();
    }
    public List<String> getParametersListAsStrings(List<UMLParameter> parameters){
        List<String> listAsString = new ArrayList<>();
        for (UMLParameter pr : parameters) {
            listAsString.add(pr.getType().toString());
        }
        return listAsString;
    }




}
/* for (UMLParameter pr : nn.getExtractedOperation().getParametersWithoutReturnType()) {
         extractedMethodParameters.add(pr.getType());
         }
         for (UMLParameter pr : nn.getSourceOperationBeforeExtraction().getParametersWithoutReturnType()){
         originMethodParameters.add(pr.getType());
         }
         for (UMLParameter pr : nn.getSourceOperationAfterExtraction().getParametersWithoutReturnType()){
         originMethodParametersAfter.add(pr.getType());
         }*/