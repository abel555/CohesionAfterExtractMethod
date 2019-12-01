import com.github.javaparser.ast.type.Type;

import java.util.HashSet;
import java.util.Set;

public class ClassInfo {
    public String methodName;
    public int numberOfInstanceVariablesUsed;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getNumberOfInstanceVariablesUsed() {
        return numberOfInstanceVariablesUsed;
    }

    public void setNumberOfInstanceVariablesUsed(int numberOfInstanceVariablesUsed) {
        this.numberOfInstanceVariablesUsed = numberOfInstanceVariablesUsed;
    }

    public int getNumberOfMethodArguments() {
        return numberOfMethodArguments;
    }

    public void setNumberOfMethodArguments(int numberOfMethodArguments) {
        this.numberOfMethodArguments = numberOfMethodArguments;
    }

    public int numberOfMethodArguments;




    public ClassInfo(){

    }

}
