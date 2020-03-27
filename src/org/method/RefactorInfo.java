package org.method;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import javafx.util.Pair;
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



    public Pair<Integer, Integer> getSourceOperationCodeRangeBefore() {
        return sourceOperationCodeRangeBefore;
    }

    public Pair<Integer, Integer> getSourceOperationCodeRangeAfter() {
        return sourceOperationCodeRangeAfter;
    }

    public Pair<Integer, Integer> getExtracOperationCodeRange() {
        return extracOperationCodeRange;
    }

    Pair<Integer, Integer> sourceOperationCodeRangeBefore;
    Pair<Integer, Integer> sourceOperationCodeRangeAfter;
    Pair<Integer, Integer> extracOperationCodeRange;

    String commitIdBefore;
    String commitIdAfter;
    List<String> classBefore;
    List<String> classAfter;
    List<String> extractedMethodName;
    List<String> originMethodName;
    List<String> originMethodNameAfter;
    List<Refactoring> refactoring;

    public List<String> getClassBefore() {
        return classBefore;
    }

    public void setClassBefore(List<String> classBefore) {
        this.classBefore = classBefore;
    }

    public List<String> getClassAfter() {
        return classAfter;
    }

    public void setClassAfter(List<String> classAfter) {
        this.classAfter = classAfter;
    }

    public List<String> getExtractedMethodName() {
        return extractedMethodName;
    }

    public void setExtractedMethodName(List<String> extractedMethodName) {
        this.extractedMethodName = extractedMethodName;
    }

    public List<String> getOriginMethodName() {
        return originMethodName;
    }

    public void setOriginMethodName(List<String> originMethodName) {
        this.originMethodName = originMethodName;
    }

    public List<String> getOriginMethodNameAfter() {
        return originMethodNameAfter;
    }

    public void setOriginMethodNameAfter(List<String> originMethodNameAfter) {
        this.originMethodNameAfter = originMethodNameAfter;
    }

    public List<Refactoring> getRefactoring() {
        return refactoring;
    }

    public void setRefactoring(List<Refactoring> refactoring) {
        this.refactoring = refactoring;
    }

    public void setUpRefactorInfo(String commitIdBefore, String commitIdAfter, Refactoring refactoring) {


        this.commitIdBefore = commitIdBefore;
        this.commitIdAfter = commitIdAfter;
        addRefactoringData(refactoring);

    }

    public void addRefactoringData(Refactoring ref) {
        ExtractOperationRefactoring nn = (ExtractOperationRefactoring) ref;
        this.refactoring.add(ref);
        this.classBefore.add(ref.getInvolvedClassesBeforeRefactoring().get(0));
        this.classAfter.add(ref.getInvolvedClassesAfterRefactoring().get(0));
        this.extractedMethodName.add(nn.getExtractedOperation().getName());
        this.originMethodName.add(nn.getSourceOperationBeforeExtraction().getName());
        this.originMethodNameAfter.add(nn.getSourceOperationAfterExtraction().getName());

        this.sourceOperationCodeRangeBefore = new Pair<Integer, Integer>(((ExtractOperationRefactoring) ref).
                                                        getSourceOperationCodeRangeBeforeExtraction().
                                                        getStartLine(),
                                                        ((ExtractOperationRefactoring) ref).
                                                        getSourceOperationCodeRangeBeforeExtraction().
                                                        getEndLine());
        this.sourceOperationCodeRangeAfter = new Pair<Integer, Integer>(((ExtractOperationRefactoring) ref).
                                                        getSourceOperationCodeRangeAfterExtraction().
                                                        getStartLine(),
                                                        ((ExtractOperationRefactoring) ref).
                                                        getSourceOperationCodeRangeAfterExtraction().
                                                        getEndLine());
        this.extracOperationCodeRange = new Pair<Integer, Integer>(((ExtractOperationRefactoring) ref).
                                                        getExtractedOperationCodeRange().
                                                        getStartLine(),
                                                        ((ExtractOperationRefactoring) ref).
                                                        getExtractedOperationCodeRange().
                                                        getEndLine());


    }


    public RefactorInfo(){
        commitIdAfter = null;
        commitIdBefore = null;
        classBefore = new ArrayList<>();
        classAfter = new ArrayList<>();
        extractedMethodName = new ArrayList<>();
        originMethodName = new ArrayList<>();
        originMethodNameAfter = new ArrayList<>();
        refactoring = new ArrayList<>();
    }

    public boolean isEmpty(){
        if (commitIdBefore == null || commitIdAfter == null)
            return true;
        else return false;
    }
}
