import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.refactoringminer.api.Refactoring;

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


    public String getExtractedMethodName() {
        return extractedMethodName;
    }

    public void setExtractedMethodName(String extractedMethodName) {
        this.extractedMethodName = extractedMethodName;
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
    String extractedMethodName;

    public int getExtractMethodStarLine() {
        return extractMethodStarLine;
    }

    public void setExtractMethodStarLine(int extractMethodStarLine) {
        this.extractMethodStarLine = extractMethodStarLine;
    }

    int extractMethodStarLine;

    public int getOriginMethodStarline() {
        return originMethodStarline;
    }

    public void setOriginMethodStarline(int originMethodStarline) {
        this.originMethodStarline = originMethodStarline;
    }

    public String getOriginMethodName() {
        return originMethodName;
    }

    public void setOriginMethodName(String originMethodName) {
        this.originMethodName = originMethodName;
    }

    int originMethodStarline;
    String originMethodName;

    public int getOriginMethodStarlineAfter() {
        return originMethodStarlineAfter;
    }

    public void setOriginMethodStarlineAfter(int originMethodStarlineAfter) {
        this.originMethodStarlineAfter = originMethodStarlineAfter;
    }

    public String getOriginMethodNameAfter() {
        return originMethodNameAfter;
    }

    public void setOriginMethodNameAfter(String originMethodNameAfter) {
        this.originMethodNameAfter = originMethodNameAfter;
    }

    int originMethodStarlineAfter;
    String originMethodNameAfter;



    Refactoring refactoring;

    public RefactorInfo(String commitIdBefore, String commitIdAfter, Refactoring refactoring) {
        ExtractOperationRefactoring nn= (ExtractOperationRefactoring) refactoring;
        this.commitIdBefore = commitIdBefore;
        this.commitIdAfter = commitIdAfter;
        this.classBefore = refactoring.getInvolvedClassesBeforeRefactoring().get(0);
        this.classAfter = refactoring.getInvolvedClassesAfterRefactoring().get(0);
        this.extractedMethodName =  nn.getExtractedOperation().getName();
        this.extractMethodStarLine = nn.getExtractedOperationCodeRange().getStartLine();
        this.originMethodStarline = nn.getSourceOperationCodeRangeBeforeExtraction().getStartLine();
        this.originMethodName = nn.getSourceOperationBeforeExtraction().getName();
        this.originMethodStarlineAfter = nn.getSourceOperationCodeRangeAfterExtraction().getStartLine();
        this.originMethodNameAfter = nn.getSourceOperationAfterExtraction().getName();





    }




    public RefactorInfo(){

    }
}
