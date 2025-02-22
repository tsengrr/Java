import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class CodeGenerator {
    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("請輸入檔案名稱");
            return;
        }
        String fileName = args[0];
        String mermaidCode = "";
        try {
            mermaidCode = Files.readString(Paths.get(fileName));
        }
        catch (IOException e) {
            System.err.println("無法讀取文件 " + fileName);
            e.printStackTrace();
            return;
        }

        Parser.splitByClass(mermaidCode);

        
        
    }
}
class Parser {
    public static String splitByClass(String input) {

        String[] lines = input.split("\n");

        String[][] words = new String[lines.length][];

        for (int i = 0; i < lines.length; i++) {
            words[i] = lines[i].trim().split("\\s+");
        }

        // Printing the words array
        //for (String[] line : words) {
        //    for (String word : line) {
        //       System.out.print(word + "\n");
        //   }
        //    System.out.println();
        //}

        int i = 0;
        while (i<words.length) {
            String[] currLine = words[i];
            for (int j=0; j<currLine.length; j++) {
                if ("class".equals(currLine[j])){
                    String currClassName = currLine[j+1];
                    i = MakeJavaFromMermaid.MakeClassAndReturnEndIdx(currClassName, i+1, words);
                }
            }
            i += 1;
        }
        return "";
    }
}

class MakeJavaFromMermaid {
    public static int MakeClassAndReturnEndIdx(String className, int startIdx, String[][] words){
        String javaOutput = "public class " + className + " {\n";
        int i = startIdx;

        // for (int a=0; a<words.length; a++){
        //     for (int b=0; b<words[a].length; b++){
        //         System.out.println((a + ", "+ b + ", " + words[a][b]));
        //     }
        // }

        while (i < words.length) {
            // System.err.println(i);
            String[] line = words[i];
            if (line.length <= 1){
                i += 1;
                continue;
            }
            if ("class".equals(line[0])){
                break;
            }
            // call MakeMethod according to attribute or method and return a string, append it to javaOutput
            boolean isMethod = false;
            for (int j=2; j<line.length; j++){
                if (line[j].contains(")")){
                    javaOutput += MakeMethod(line, 4);
                    isMethod = true;
                }
            }
            if (!isMethod){
                javaOutput += MakeAttribute(line, 4);
            }
            
            i += 1;
        }
        javaOutput += "}";

        JavaOutputWriter.writeJavaOutput(javaOutput, className);

        int endIdx = i-1;

        return endIdx;
    }

    public static String MakeAttribute(String[] line, int indent){
        String output = "";
        for (int i=0; i< indent; i++){
            output += " ";
        }
        if(line[2].contains("+")){
            output += line[2].replace("+","public ") + " ";
        }
        else if(line[2].contains("-")){
            output += line[2].replace("-","private ") + " ";
        }

        output += line[3] + ";\n";
        
        return output;
    }

    public static String MakeMethod(String[] line, int indent){
        String output = "";
        String indentStr = "";
        for (int i=0; i< indent; i++){
            indentStr += " ";
        }
        output += indentStr;
        
        String returnType = line[line.length-1];
        if (line[line.length-1].contains(")"))
            returnType = "void";

        if(line[2].contains("+"))
            output += "public " + returnType + " " + line[2].replace("+","");
        else
            output += "private " + returnType + " " + line[2].replace("-","");

        // concate words into statement
        int i = 3;
        if (line[line.length-1].contains(")")){
            while (i < line.length) {
                output += " " + line[i];
                i += 1;
            } 
        }
        else{
        while (i < line.length-1) {
            output += " " + line[i];
            i += 1;
        }
        }   
        // start making return statement
        
        if (line[2].contains("set") || line[2].contains("get")){
            output += " {\n";
            output += indentStr;
            for (i=0; i< indent; i++){
                output += " ";
            }

            char lowerHead = Character.toLowerCase(line[2].charAt(4));
            int idxOfLeftBrackets = line[2].indexOf("(");

            if (line[2].contains("get"))
                output += "return " + lowerHead + line[2].substring(5, idxOfLeftBrackets);
            else{
                String temp = lowerHead + line[2].substring(5, idxOfLeftBrackets);
                output += "this." + temp + " = " + temp;
            }
            output += ";\n" + indentStr + "}\n";
        }
        else{
            
            HashMap<String, String> returnTypeMap = new HashMap<>();
            returnTypeMap.put("int", "0");
            returnTypeMap.put("boolean", "false");
            returnTypeMap.put("String", "");

            if (returnTypeMap.containsKey(returnType))
                output += " {return " + returnTypeMap.get(returnType) + ";}\n";
            else
                output += " {;}\n";
        }

        return output;
    }
}

class JavaOutputWriter {
    public static void writeJavaOutput(String javaOutput, String className){
        try {
            String output = className + ".java";

            File file = new File(output);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(javaOutput);
            }
        }
         catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
