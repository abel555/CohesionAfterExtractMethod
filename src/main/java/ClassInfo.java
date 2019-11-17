import com.github.javaparser.ast.type.Type;

import java.util.HashSet;
import java.util.Set;

public class ClassInfo {
    public String name;
    public float methodsNumber;
    public float methodParametersSum;
    public Set<Type> objectTypeParameters = new HashSet<Type>();

    public ClassInfo(){
        this.methodsNumber = 0;
        this.methodParametersSum = 0;
    }
    public float getCohesion(){
        return methodParametersSum / (methodsNumber * (objectTypeParameters.size()+0));
    }

}
