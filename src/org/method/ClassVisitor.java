package org.method;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassVisitor extends VoidVisitorAdapter<ClassInfo> {
    @Override
    public void visit(final ClassOrInterfaceDeclaration c, ClassInfo info){

        List<FieldDeclaration> fieldDeclarations = c.getFields();
        Set<VariableDeclarator> variables = new HashSet<>();

        fieldDeclarations.stream().map(FieldDeclaration::getVariables).forEach(variables::addAll);
        System.out.println(variables);
        List<MethodDeclaration> method = c.getMethodsByName(info.getMethodName());
/*
        for (VariableDeclarator variable : variables) {
            int numberOfMethodsAccessingVariable = 0;
                if (CalculationUtils.isFieldAccessedWithinMethod.getUnchecked(Pair.of(method, variable))) {
                    numberOfMethodsAccessingVariable++;
                }

            total = total.plus(NumericValue.of(numberOfMethodsAccessingVariable));
        }
*/

        MethodVIsitor methodVIsitor = new MethodVIsitor();
        c.accept(methodVIsitor, info);
        super.visit(c,info);
       // System.out.println(c.getName());
       // System.out.println(c.getMethods());
    }

}
