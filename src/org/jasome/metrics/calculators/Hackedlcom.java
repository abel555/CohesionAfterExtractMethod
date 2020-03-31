package org.jasome.metrics.calculators;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.jasome.input.Method;
import org.jasome.input.Type;
import org.jasome.metrics.Calculator;
import org.jasome.metrics.Metric;
import org.jasome.metrics.value.NumericValue;
import org.jasome.util.CalculationUtils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Hackedlcom{

    String methodName;
    String classPath;
    int classFields;
    int fieldsUsedInMethod;
    int methodArguments;
    String commitAfter;
    //@Override
    public int calculate(Type type, Method method) {
        this.methodName = methodName;
        List<FieldDeclaration> fieldDeclarations = type.getSource().getFields();
        Set<VariableDeclarator> variables = new HashSet<>();

        fieldDeclarations.stream().map(FieldDeclaration::getVariables).forEach(variables::addAll);
        //System.out.println(variables);

        MethodDeclaration extractMethod = method.getSource();

        // List<MethodDeclaration> methods = type.getMethods().stream().filter(method -> methodName.equals(method.getName())).map(Method::getSource).collect(Collectors.toList());
        if (extractMethod != null) {

            this.classFields = variables.size();
            int numberOfMethodsAccessingVariable = 0;
            for (VariableDeclarator variable : variables) {

                if (CalculationUtils.isFieldAccessedWithinMethod.getUnchecked(Pair.of(extractMethod, variable))) {
                    numberOfMethodsAccessingVariable++;
                }


            }
            this.fieldsUsedInMethod = numberOfMethodsAccessingVariable;

           // System.out.println(this.classPath + ";" + this.commitAfter + ";" + this.methodName + ";" + this.methodArguments + ";" + this.classFields + ";" + this.fieldsUsedInMethod);
        }

        return this.fieldsUsedInMethod;


    }

}