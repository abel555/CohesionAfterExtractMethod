package org.method;

import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.List;

public class ExtractHandler extends RefactoringHandler {
    public int getnCommits() {
        return nCommits;
    }

    public int getnRefactorings() {
        return nRefactorings;
    }

    public int getnExtractMethods() {
        return nExtractMethods;
    }

    int nCommits = 0;
    int nRefactorings = 0;
    int nExtractMethods = 0;

    boolean saveCommit = false;

    public String getLastCommitId() {
        return lastCommitId;
    }

    String lastCommitId = null;
    int pos;
    List<RefactorInfo> extractMethodsInfoList;
    public List<RefactorInfo> getExtractMethodsInfoList() {
        return extractMethodsInfoList;
    }

    public ExtractHandler() {
        this.extractMethodsInfoList = new ArrayList<>();
        //saveCommit = false;
    }


    @Override
    public void handle(String commitId, List<Refactoring> refactorings) {
        nCommits += 1;
        if (lastCommitId == null){
            lastCommitId = commitId;
        }
        for (Refactoring ref : refactorings) {
            nRefactorings += 1;
            if(ref.getRefactoringType().equals(RefactoringType.EXTRACT_OPERATION)) {
                nExtractMethods += 1;
            }
        }
    }
}
