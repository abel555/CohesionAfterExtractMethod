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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.github.javaparser.ast.body.Parameter;

public class ExtractMethodProcessor {

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

    public void analizeProjects(String rep) throws Exception {

        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        StringBuilder localFile = new StringBuilder();
        localFile.append("/Users/abel/Documents/ClasesU/Seminario/CohesionAfterExtractMethod/emp/");localFile.append(FilenameUtils.getBaseName(rep));
        Repository repo = gitService.cloneIfNotExists(localFile.toString(), rep);
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
                calculateMetricsBeforeRefactoring(gitService, repo, refInfo, i, nn, split, toWriteBefore);
                calculateMetricsAfterRefactoring(rep, gitService, repo, refInfo, i, nn, split, toWriteBefore);
            }
        }

    }

    public void calculateMetricsAfterRefactoring(String rep, GitService gitService, Repository repo, RefactorInfo refInfo, int i, ExtractOperationRefactoring nn, String split, StringBuilder toWriteBefore) {
        try {
            gitService.checkout(repo, refInfo.getCommitIdAfter());
            String classFileAfter = getJavaFIle(Paths.get(split), refInfo.getClassAfter().get(i));
            StringBuilder toWrite = new StringBuilder();
            toWrite.append(classFileAfter);toWrite.append(";");
            toWrite.append(refInfo.getOriginMethodNameAfter().get(i));toWrite.append(";");

            toWrite.append(hackedJasomeConsole(classFileAfter, refInfo.getOriginMethodNameAfter().get(i), getParametersListAsStrings(nn.getSourceOperationAfterExtraction().getParametersWithoutReturnType())));
            toWrite.append(refInfo.getExtractedMethodName().get(i));
            toWrite.append(";");
            toWrite.append(nn.getExtractedOperation().getParametersWithoutReturnType().toString());
            toWrite.append(";");
            String linkTocommit = rep.split("\\.git")[0] + "/commit/";
            toWrite.append( linkTocommit + refInfo.getCommitIdBefore());
            toWrite.append(";");
            toWrite.append( linkTocommit + refInfo.getCommitIdAfter());
            toWrite.append(";");

            toWrite.append(hackedJasomeConsole(classFileAfter, refInfo.getExtractedMethodName().get(i), getParametersListAsStrings(nn.getExtractedOperation().getParametersWithoutReturnType())));
            writeOutPutFIle(toWriteBefore, toWrite);
        }
        catch (Exception e) {
            //System.out.println(e);
        }
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

    public void calculateMetricsBeforeRefactoring(GitService gitService, Repository repo, RefactorInfo refInfo, int i, ExtractOperationRefactoring nn, String split, StringBuilder toWriteBefore) {
        try {
            gitService.checkout(repo, refInfo.getCommitIdBefore());
            String classFileBefore = getJavaFIle(Paths.get(split), refInfo.getClassBefore().get(i));

            toWriteBefore.append(classFileBefore);toWriteBefore.append(";");
            toWriteBefore.append(refInfo.getOriginMethodName().get(i));toWriteBefore.append(";");

            toWriteBefore.append(hackedJasomeConsole(classFileBefore, refInfo.getOriginMethodName().get(i), getParametersListAsStrings(nn.getSourceOperationBeforeExtraction().getParametersWithoutReturnType())));

        }
        catch (Exception e) {
            //System.out.println(e);
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
            if (dir.endsWith(getClass(clas) + ".java")){
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