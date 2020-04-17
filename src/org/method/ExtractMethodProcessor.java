package org.method;

import com.opencsv.CSVReader;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.github.javaparser.ast.body.Parameter;

public class ExtractMethodProcessor {

    public String outPutFileName;
    String projectName;
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

    public void analizeProjects(String rep) throws Exception {

        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        StringBuilder localFile = new StringBuilder();
        localFile.append("emp/");
        localFile.append(FilenameUtils.getBaseName(rep));
        Repository repo = gitService.cloneIfNotExists(localFile.toString(), rep);
        projectName = repo.toString().split("\\\\")[1];
        RefactoringHandler extractHandler = new ExtractHandler();
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
                StringBuilder toWrite = new StringBuilder();
                if (refInfo.getClassBefore().get(i)!= null && refInfo.getClassAfter().get(i) !=null){



                    toWriteBefore = smellsInfo(rep, gitService, repo, refInfo, i, nn, split);


                    writeOutPutFIle(toWriteBefore, toWrite);

                }
            }
        }

    }



    public StringBuilder calculateMetricsAfterRefactoring(String rep, GitService gitService, Repository repo, RefactorInfo refInfo, int i, ExtractOperationRefactoring nn, String split) {
        StringBuilder toWrite = new StringBuilder();
        try {
            gitService.checkout(repo, refInfo.getCommitIdAfter());
            String classFileAfter = getJavaFIle(Paths.get(split), refInfo.getClassAfter().get(i));

            toWrite.append(classFileAfter);toWrite.append(";");
            List<Pair<String, String>> clonesAfter= detectClones(repo, refInfo.getCommitIdAfter(), refInfo.getClassAfter().get(i), split);
           String codeCloneLinesAfter = compareCodeRangeClone(clonesAfter, refInfo.getSourceOperationCodeRangeAfter());
            toWrite.append(refInfo.getOriginMethodNameAfter().get(i));toWrite.append(";");
            toWrite.append(codeCloneLinesAfter);

           // toWrite.append(hackedJasomeConsole(classFileAfter, refInfo.getOriginMethodNameAfter().get(i), getParametersListAsStrings(nn.getSourceOperationAfterExtraction().getParametersWithoutReturnType())));
            toWrite.append(refInfo.getExtractedMethodName().get(i));
            toWrite.append(";");
            toWrite.append(nn.getExtractedOperation().getParametersWithoutReturnType().toString());
            toWrite.append(";");


            //toWrite.append(hackedJasomeConsole(classFileAfter, refInfo.getExtractedMethodName().get(i), getParametersListAsStrings(nn.getExtractedOperation().getParametersWithoutReturnType())));

           List<Pair<String, String>> clonesInExtractedMethod= detectClones(repo, refInfo.getCommitIdAfter(), refInfo.getClassAfter().get(i), split);
           String codeCloneLinesInExtractedMethod= compareCodeRangeClone(clonesInExtractedMethod, refInfo.getExtracOperationCodeRange());
           toWrite.append(codeCloneLinesInExtractedMethod);
           // System.out.println(toWrite);
            return toWrite;

        }
        catch (Exception e) {
            //System.out.println(e);
        }
        return toWrite;
    }

    public StringBuilder smellsInfo(String rep,GitService gitService, Repository repo, RefactorInfo refInfo, int i, ExtractOperationRefactoring nn, String split) throws Exception {
        StringBuilder toWriteBefore = new StringBuilder();

            toWriteBefore.append(projectName).append(";");
            String linkTocommit = rep.split("\\.git")[0] + "/commit/";
            toWriteBefore.append(linkTocommit + refInfo.getCommitIdBefore());
            toWriteBefore.append(";");
            toWriteBefore.append(linkTocommit + refInfo.getCommitIdAfter());
            toWriteBefore.append(";");

            toWriteBefore.append(refInfo.getClassBefore().get(i));
            toWriteBefore.append(";");
            toWriteBefore.append(refInfo.getClassAfter().get(i));
            toWriteBefore.append(";");
            toWriteBefore.append(refInfo.getOriginMethodName().get(i));
            toWriteBefore.append(";");
            toWriteBefore.append(refInfo.getExtractedMethodName().get(i));
            toWriteBefore.append(";");
        List<Pair<String, String>> clonesBefore=  detectClones(repo, refInfo.getCommitIdBefore(), refInfo.getClassBefore().get(i), split);

        String codeCloneLinesBefore = compareCodeRangeClone(clonesBefore, refInfo.getSourceOperationCodeRangeBefore());
            toWriteBefore.append(codeCloneLinesBefore).append(";");

        List<Pair<String, String>> clonesAfter= detectClones(repo, refInfo.getCommitIdAfter(), refInfo.getClassAfter().get(i), split);

        String codeCloneLinesAfter = compareCodeRangeClone(clonesAfter, refInfo.getSourceOperationCodeRangeAfter());
            toWriteBefore.append(codeCloneLinesAfter).append(";");

        List<Pair<String, String>> clonesInExtractedMethod=  detectClones(repo, refInfo.getCommitIdAfter(), refInfo.getClassAfter().get(i), split);

        String codeCloneLinesInExtractedMethod= compareCodeRangeClone(clonesInExtractedMethod, refInfo.getExtracOperationCodeRange());
            toWriteBefore.append(codeCloneLinesInExtractedMethod).append(";");



            toWriteBefore.append(nn.getSourceOperationBeforeExtraction().getParametersWithoutReturnType().size()).append(";");
            toWriteBefore.append(nn.getSourceOperationAfterExtraction().getParametersWithoutReturnType().size()).append(";");
            toWriteBefore.append(nn.getExtractedOperation().getParametersWithoutReturnType().size()).append(";");

            toWriteBefore.append(nn.getSourceOperationBeforeExtraction().getBody().statementCount()).append(";");
            toWriteBefore.append(nn.getSourceOperationAfterExtraction().getBody().statementCount()).append(";");
            toWriteBefore.append(nn.getExtractedOperation().getBody().statementCount()).append(";");

        return toWriteBefore;
    }

    public StringBuilder calculateMetricsBeforeRefactoring(String rep,GitService gitService, Repository repo, RefactorInfo refInfo, int i, ExtractOperationRefactoring nn, String split) {
        StringBuilder toWriteBefore = new StringBuilder();
        try {
            toWriteBefore.append(projectName).append(";");
            String linkTocommit = rep.split("\\.git")[0] + "/commit/";
            toWriteBefore.append( linkTocommit + refInfo.getCommitIdBefore());
            toWriteBefore.append(";");
            toWriteBefore.append( linkTocommit + refInfo.getCommitIdAfter());
            toWriteBefore.append(";");
            gitService.checkout(repo, refInfo.getCommitIdBefore());
            //String classFileBefore = getJavaFIle(Paths.get(split), refInfo.getClassBefore().get(i));
            toWriteBefore.append(refInfo.getClassBefore().get(i));toWriteBefore.append(";");
            toWriteBefore.append(refInfo.getClassAfter().get(i));toWriteBefore.append(";");
            toWriteBefore.append(refInfo.getOriginMethodName().get(i));toWriteBefore.append(";");
            //toWriteBefore.append(hackedJasomeConsole(classFileBefore, refInfo.getOriginMethodName().get(i), getParametersListAsStrings(nn.getSourceOperationBeforeExtraction().getParametersWithoutReturnType())));

        }
        catch (Exception e) {
            //System.out.println(e);
        }
        return toWriteBefore;
    }

    public void writeOutPutFIle(StringBuilder toWriteBefore, StringBuilder toWrite) {
        try(FileWriter fw = new FileWriter(outPutFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            toWriteBefore.append(toWrite);
            out.println(toWriteBefore);

        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
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
            String[] javaclass = dir.split("/");
            String shortDir = javaclass[javaclass.length -1];
            if (shortDir.equals(getClass(clas) + ".java")){
                javaFile = dir;
            }
        }
        return javaFile;
    }
    public String getClass (String base){
        return base.substring(base.lastIndexOf(".") + 1);
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
                            System.out.println("never entre");
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

    public boolean createFolderIn(String path){
        //Creating a File object
        File file = new File(path);
        //Creating the directory
        return file.mkdirs();
    }
    static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }


    public static void copyFile(String from, String to) throws IOException{
        Path src = Paths.get(from);
        Path dest = Paths.get(to);
        Files.copy(src, dest);
    }

    public void executeOpenAnalzyer(String jarPath,String projectName, String projectPath){
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command(jarPath+ "OpenStaticAnalyzerJava", "-projectName=" + projectName, "-projectBaseDir=" + projectPath, "-resultsDir=Results",
                "-cloneGenealogy=true", "-cloneMinLines=4", "-currentDate=" + projectName);
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
            System.out.println(output);
            System.out.println("\nCódigo de salida: "+ exitVal);


        } catch (InterruptedException | IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public List<Pair<String, String>> detectClones(Repository repo, String commit, String classToAnalyze , String split) throws IOException {
        GitService gitService = new GitServiceImpl();
        List<Pair<String, String>> clones = null;
        try {
            gitService.checkout(repo, commit);
            String classFile = getJavaFIle(Paths.get(split), classToAnalyze);

            if (classFile != null) {
                String[] classN = classFile.split("\\\\");
                String className = classN[classN.length - 1];
                String destFolder = className.replace(".", "");
                createFolderIn(destFolder);
                copyFile(classFile, destFolder + "/" + className);
                executeOpenAnalzyer("E:\\Downloads\\OpenStaticAnalyzer-4.0.0-x64-Windows\\Java\\", destFolder, destFolder);
                clones = readDataLineByLine("Results\\" + destFolder + "\\java\\" + destFolder + "\\" + destFolder + "-CloneInstance.csv");
                FileUtils.deleteDirectory(new File((destFolder)));
                FileUtils.deleteDirectory(new File("Results"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clones;
    }
    public static List<Pair<String, String>> readDataLineByLine(String file) throws IOException {
        List<Pair<String, String>> duplicatesRange = new ArrayList<>();
        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);
            csvReader.readNext();
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Pair<String, String> cloneRange = new Pair(nextRecord[5], nextRecord[7]);
                duplicatesRange.add(cloneRange);
            }
            csvReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return duplicatesRange;
    }
    private String compareCodeRangeClone(List<Pair<String, String>> clones, Pair<Integer, Integer> operationCodeRange) {

        int clonesNumber = 0;
        String cloneLines = "Sin clones";
        if (clones == null){
            return ";;";
        }
        for(Pair<String, String> cloneRange: clones){
            int startClone = Integer.parseInt(cloneRange.getKey());
            int endClone = Integer.parseInt(cloneRange.getValue());
            int startMethod = operationCodeRange.getKey();
            int endMethod = operationCodeRange.getValue();
            if (startClone >= startMethod &&
                endClone <= endMethod){
                cloneLines = cloneRange.toString() + clones.toString();
                clonesNumber = clones.size();
                break;
            }
        }
        return clonesNumber + ";" + cloneLines + ";";
    }
}
