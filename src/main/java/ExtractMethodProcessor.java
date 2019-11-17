import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.FileInputStream;
import java.io.IOException;
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
                        setUpMethodInfo(commitId, ref, extractMethodsInfo, lastCommitId[0]);
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

    private void setUpMethodInfo(String commitId, Refactoring ref, RefactorInfo extractMethodsInfo, String commitIdBefore) {
        extractMethodsInfo.setCommitIdBefore(commitIdBefore);
        extractMethodsInfo.setCommitIdAfter(commitId);
        extractMethodsInfo.addClassBefore(ref.getInvolvedClassesBeforeRefactoring().get(0));
        extractMethodsInfo.addClassAfter(ref.getInvolvedClassesAfterRefactoring().get(0));
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

}
