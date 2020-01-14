import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.refactoringminer.api.Refactoring;

import java.util.ArrayList;
import java.util.List;

public class RefactorInfo {

    public String getCommitIdBefore() {
        return commitIdBefore;
    }

    public void setCommitIdBefore(String commitIdBefore) {
        this.commitIdBefore = commitIdBefore;
    }

    public String getCommitIdAfter() {
        return commitIdAfter;
    }

    public void setCommitIdAfter(String commitIdAfter) {
        this.commitIdAfter = commitIdAfter;
    }

    public String getClassBefore() {
        return classBefore;
    }

    public void setClassBefore(String classBefore) {
        this.classBefore = classBefore;
    }

    public String getClassAfter() {
        return classAfter;
    }

    public void setClassAfter(String classAfter) {
        this.classAfter = classAfter;
    }


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public Refactoring getRefactoring() {
        return refactoring;
    }

    public void setRefactoring(Refactoring refactoring) {
        this.refactoring = refactoring;
    }
    String commitIdBefore;
    String commitIdAfter;
    String classBefore;
    String classAfter;
    String methodName;

    public int getStarLine() {
        return starLine;
    }

    public void setStarLine(int starLine) {
        this.starLine = starLine;
    }

    int starLine;



    Refactoring refactoring;

    public RefactorInfo(String commitIdBefore, String commitIdAfter, Refactoring refactoring) {
        ExtractOperationRefactoring nn= (ExtractOperationRefactoring) refactoring;
        this.commitIdBefore = commitIdBefore;
        this.commitIdAfter = commitIdAfter;
        this.classBefore = refactoring.getInvolvedClassesBeforeRefactoring().get(0);
        this.classAfter = refactoring.getInvolvedClassesAfterRefactoring().get(0);
        this.methodName =  nn.getExtractedOperation().getName();
        this.starLine = nn.getExtractedOperationCodeRange().getStartLine();


    }




    public RefactorInfo(){

    }
}
