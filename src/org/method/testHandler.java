package org.method;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.api.RefactoringType;

import java.util.List;

public class testHandler extends RefactoringHandler {
    @Override
    public void handle(String commitId, List<Refactoring> refactorings) {

        for (Refactoring ref : refactorings) {
            if(ref.getRefactoringType().equals(RefactoringType.EXTRACT_OPERATION)) {
                System.out.println(ref.getRefactoringType()+"->"+ RefactoringType.EXTRACT_OPERATION);
                break;
            }
        }
    }
}
