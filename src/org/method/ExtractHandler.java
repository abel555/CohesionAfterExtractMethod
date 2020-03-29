package org.method;

import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.List;

public class ExtractHandler extends RefactoringHandler {
    boolean saveCommit = false;
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

        if (saveCommit){
            extractMethodsInfoList.get(pos).setCommitIdBefore(commitId);
            saveCommit = false;
        }
        RefactorInfo extractMethodsInfo = new RefactorInfo();
        for (Refactoring ref : refactorings) {
            if(ref.getRefactoringType().equals(RefactoringType.EXTRACT_OPERATION)) {

                if(extractMethodsInfo.isEmpty())
                    extractMethodsInfo.setUpRefactorInfo(lastCommitId, commitId, ref);
                else {
                    ExtractOperationRefactoring nn = (ExtractOperationRefactoring) ref;

                    ExtractOperationRefactoring n2 = (ExtractOperationRefactoring)extractMethodsInfo.getRefactoring().get(extractMethodsInfo.getRefactoring().size() -1);
                    if (!nn.getExtractedOperation().getName().equals(n2.getExtractedOperation().getName())){
                        extractMethodsInfo.addRefactoringData(ref);
                    }
                }

            }
        }
        if(extractMethodsInfo.getCommitIdAfter() != null) {
            extractMethodsInfoList.add(extractMethodsInfo);

            saveCommit = true;
            pos = extractMethodsInfoList.size() - 1;
        }
        lastCommitId = commitId;


    }
}
