import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

public class MethodVIsitor extends VoidVisitorAdapter<ClassInfo> {

    @Override
    public void visit(final MethodDeclaration n, ClassInfo info ) {

        NodeList<Parameter> parameters = n.getParameters();
        Set<Type> distinctMethodsParams = new HashSet<Type>();
        for (Parameter par:parameters) {
            //info.objectTypeParameters.add(par.getType());
            distinctMethodsParams.add(par.getType());
        }
       // Set<Type> p = Sets.intersection(info.objectTypeParameters, distinctMethodsParams);
        //info.methodParametersSum += p.size() + 0;
        super.visit(n, info);
    }

}
