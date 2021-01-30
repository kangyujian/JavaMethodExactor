package dreamcold.parser;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import dreamcold.util.FileWriter;
import dreamcold.visiter.FunctionVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileParser {

    private String code;
    private CompilationUnit m_CompilationUnit;
    private String className;

    public FileParser(String code) {
        this.code = code;
    }

    public FileParser(String code, String className) {
        this.code = code;
        this.className = className;
    }

    public CompilationUnit getParsedFile() {
        return m_CompilationUnit;
    }

    public void extractFeatures() throws ParseException, IOException {
        m_CompilationUnit = parseFileWithRetries(code);

        FunctionVisitor functionVisitor = new FunctionVisitor();

        functionVisitor.visit(m_CompilationUnit, null);
        ArrayList<MethodDeclaration> nodes = functionVisitor.getMethodDeclarations();
        for (int i=0;i<nodes.size();i++){
            MethodDeclaration methodDeclaration=nodes.get(i);
            List<Parameter> parameters = methodDeclaration.getParameters();
            StringBuilder sb=new StringBuilder();
            for (Parameter parameter:parameters){
                sb.append(parameter.getType().toString()).append(" ");
            }
            sb.delete(sb.length() - 2, sb.length());
            String classNameStr=className;
            String parametersStr=sb.toString();
            String returnTypeStr=methodDeclaration.getType().toString();
            String bodyContentStr=methodDeclaration.getBody().toString().replaceAll("\r|\n","");
            String methodNameStr=methodDeclaration.getName();
            sb=new StringBuilder();
            sb.append(className);
            sb.append("@");
            sb.append(returnTypeStr);
            sb.append("@");
            sb.append(parametersStr);
            sb.append("@");
            sb.append(bodyContentStr);
            sb.append("@");
            sb.append(methodNameStr);
            sb.append("\n");
            String line=sb.toString();
            FileWriter.wirteContent(line);
        }

    }

    public CompilationUnit parseFileWithRetries(String code) throws IOException {
        final String classPrefix = "public class Test {";
        final String classSuffix = "}";
        final String methodPrefix = "SomeUnknownReturnType f() {";
        final String methodSuffix = "return noSuchReturnValue; }";

        String originalContent = code;
        String content = originalContent;
        CompilationUnit parsed = null;
        try {
            parsed = JavaParser.parse(content);
        } catch (ParseProblemException e1) {
            // Wrap with a class and method
            try {
                content = classPrefix + methodPrefix + originalContent + methodSuffix + classSuffix;
                parsed = JavaParser.parse(content);
            } catch (ParseProblemException e2) {
                // Wrap with a class only
                content = classPrefix + originalContent + classSuffix;
                parsed = JavaParser.parse(content);
            }
        }

        return parsed;
    }
}
