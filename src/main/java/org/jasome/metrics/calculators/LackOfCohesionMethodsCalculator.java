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

public class LackOfCohesionMethodsCalculator{

    String methodName;
    String classPath;
    int classFields;
    int fieldsUsedInMethod;
    int methodArguments;
    //@Override
    public Set<Metric> calculate(Type type, String methodName, String classPath) {
        this.methodName = methodName;
        this.classPath = classPath;
        List<FieldDeclaration> fieldDeclarations = type.getSource().getFields();
        Set<VariableDeclarator> variables = new HashSet<>();

        fieldDeclarations.stream().map(FieldDeclaration::getVariables).forEach(variables::addAll);
        //System.out.println(variables);

        MethodDeclaration extractMethod = null;

        for (Method method:type.getMethods()) {
            if (method.getSource().getNameAsString().equals(methodName)){

                this.methodArguments = method.getSource().getParameters().size();
                extractMethod = method.getSource();
            }
        }
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

            System.out.println(this.classPath + ";" + this.methodName + ";" + this.methodArguments + ";" + this.classFields + ";" + this.fieldsUsedInMethod);
        }

        try {
            /*NumericValue numberOfMethods = NumericValue.of(methods.size());
            NumericValue numberOfVariables = NumericValue.of(variables.size());

            NumericValue averageNumberOfMethodsAccessingEachVariable = total.divide(numberOfVariables);

            NumericValue numberOfMethodsAsRational = numberOfMethods.divide(NumericValue.ONE);
            NumericValue numerator = averageNumberOfMethodsAccessingEachVariable.minus(numberOfMethodsAsRational);

            NumericValue denominator = NumericValue.ONE.minus(numberOfMethodsAsRational);

            NumericValue lackOfCohesionMethods = numerator.divide(denominator);*/
            return ImmutableSet.of(Metric.of("LCOM*", "Lack of Cohesion Methods (H-S)", 2));
        } catch (ArithmeticException e) {
            return ImmutableSet.of();
        }


    }

}
