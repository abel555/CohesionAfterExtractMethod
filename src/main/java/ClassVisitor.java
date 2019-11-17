import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashSet;
import java.util.Set;

public class ClassVisitor extends VoidVisitorAdapter<ClassInfo> {
    @Override
    public void visit(final ClassOrInterfaceDeclaration c, ClassInfo info){


        info.name = c.getNameAsString();
        info.methodsNumber = c.getMethods().size() + 0;
        for (MethodDeclaration method: c.getMethods()) {
            NodeList<Parameter> parameters = method.getParameters();

            for (Parameter par:parameters) {
                info.objectTypeParameters.add(par.getType());


            }
        }
        MethodVIsitor methodVIsitor = new MethodVIsitor();
        c.accept(methodVIsitor, info);
        super.visit(c,info);
       // System.out.println(c.getName());
       // System.out.println(c.getMethods());
    }

}
