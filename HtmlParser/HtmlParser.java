// JsoupExample.java

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.util.stream.*; 
import java.lang.Math;
// import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;


public class HtmlParser {
    public static void main(String[] args) {
        int mode = args.length >= 1 ? Integer.parseInt(args[0]) : 0;
        int task = args.length >= 2 ? Integer.parseInt(args[1]) : 0;
        String stock = args.length >= 3 ? (args[2]) : "";
        int start = args.length >= 4 ? Integer.parseInt(args[3]) : 0;
        int end = args.length >= 5 ? Integer.parseInt(args[4]) : 0;

        if (mode == 0){
            Crawler crawler = new Crawler();
            crawler.startCrawl();
        }

        else if (mode == 1 && task == 0) {
            TaskSolver.task0("data.csv", "output.csv");
        }

        else if (mode == 1 && task == 1) {
            TaskSolver.task1("data.csv", "output.csv", stock, start, end);
        }
        else if (mode == 1 && task == 2) {
            TaskSolver.task2("data.csv", "output.csv", stock, start, end);
        }
        else if (mode == 1 && task == 3) {
            TaskSolver.task3("data.csv", "output.csv", stock, start, end);
        }
        else if (mode == 1 && task == 4) {
            TaskSolver.task4("data.csv", "output.csv", stock, start, end);
        }
    }
}

class TaskSolver {
    public static void task0(String inputCsvFile, String outputCsvFile) {
        try {
            // 读取 inputCsvFile 文件的内容
            List<String> lines = Files.readAllLines(Paths.get(inputCsvFile));

            // 将内容写入 outputCsvFile 文件
            Files.write(Paths.get(outputCsvFile), lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            
            System.out.println("Successfully exported data from data.csv to output.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void task1(String inputCsvFile, String outputCsvFile, String stock, int start, int end) {
        try {
            // 读取data.csv文件的内容
            String content = Files.readString(Paths.get(inputCsvFile));
            String[] line = content.split("\n");
            String[][] dataArray = new String[line.length][];
            for (int i = 0; i < line.length; i++) {
                dataArray[i] = line[i].split(",");
            }
            double[] specificData=new double[end-start+1];
    
            
            for (int i = 0; i < 133; i++) {
                String stockName = dataArray[0][i];
                if (stockName.equals(stock)) {
                    for (int j = start,k=0; j <= end; j++,k++) {
                        specificData[k]=Double.parseDouble(dataArray[j][i]);
                    }
                }
            }
            String[] movingAverages = calculateMovingAverage(specificData, 5);
            String output="";
            output+=stock+","+start+","+end+"\n";
            for (int i = 0; i < end+2-start-5; i++) {
                output+=movingAverages[i];
                if (i < end+2-start-5-1) {
                    output+=",";
                }
            }
            output+="\n";
            File file = new File(outputCsvFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 使用FileWriter构造函数的第二个参数来指定是否追加内容
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(output);
            }
                
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void task2(String inputCsvFile, String outputCsvFile, String stock, int start, int end) {
        try {
            // 读取data.csv文件的内容
            String content = Files.readString(Paths.get(inputCsvFile));
            String[] line = content.split("\n");
            String[][] dataArray = new String[line.length][];
            for (int i = 0; i < line.length; i++) {
                dataArray[i] = line[i].split(",");
            }
            double[] specificData=new double[end-start+1];
    
            
            for (int i = 0; i < 133; i++) {
                String stockName = dataArray[0][i];
                if (stockName.equals(stock)) {
                    for (int j = start,k=0; j <= end; j++,k++) {
                        specificData[k]=Double.parseDouble(dataArray[j][i]);
                    }
                }
            }
            double sum = 0;
            double total=0;
            double average=0;
            double n=(double)(end-start+1);
            String dev="";
            for (int i = 0; i <=end-start ; i++) {
                sum += specificData[i];
            }
            average= sum/n;
            for(int i = 0; i <= end-start; i++){
                total+=Math.pow(specificData[i]-average, 2);
            }
            double variance = total / (n-1);
            DecimalFormat df = new DecimalFormat("0.##");
            dev=df.format(Math.sqrt(variance));
            String output="";
            output+=stock+","+start+","+end+"\n";
            output+=dev+"\n";
            File file = new File(outputCsvFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 使用FileWriter构造函数的第二个参数来指定是否追加内容
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(output);
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void task3(String inputCsvFile, String outputCsvFile, String stock, int start, int end) {
        try {
            // 读取data.csv文件的内容
            String content = Files.readString(Paths.get(inputCsvFile));
            String[] line = content.split("\n");
            String[][] dataArray = new String[line.length][];
            for (int i = 0; i < line.length; i++) {
                dataArray[i] = line[i].split(",");
            }
            double[][] specificData=new double[end-start+1][133];
            double []sum =new double[133];
            double []total=new double[133];
            double []average=new double[133];
            double []variance=new double[133];
            double n=(double)(end-start+1);
            String []dev=new String[133];
            double []stdDev=new double[133];
            String[] format=new String[133];
            for (int i = 0; i < 133; i++) {
                    for (int j = start,k=0; j <= end; j++,k++) {
                        specificData[k][i]=Double.parseDouble(dataArray[j][i]);
                    }
                    for (int k = 0; k <=end-start ; k++) {
                        sum[i]+= specificData[k][i];
                    }
                    average[i]= sum[i]/n;

                    for(int k = 0; k <= end-start; k++){
                        total[i]+=Math.pow(specificData[k][i]-average[i], 2);
                    }
                    variance[i] = total[i] / (n-1);
                    DecimalFormat df = new DecimalFormat("0.##");
                    dev[i]=df.format(Math.sqrt(variance[i]));
                    stdDev[i]=Double.parseDouble(dev[i]);
            }
            for(int i=0;i<133;i++){
                for (int j = i + 1; j < 133; j++) {
                    if (stdDev[i] < stdDev[j]) {
                    // 交换标准差数组中的元素
                        double tempStdDeviation = stdDev[i];
                        stdDev[i] = stdDev[j];
                        stdDev[j] = tempStdDeviation;
                        // 交换股票名数组中的元素
                        String tempStockName = dataArray[0][i];
                        dataArray[0][i] = dataArray[0][j];
                        dataArray[0][j] = tempStockName;
                    }
                }
            DecimalFormat df = new DecimalFormat("0.##");
            format[i]=df.format(stdDev[i]);
            }
            String output="";
            output+=dataArray[0][0]+","+dataArray[0][1]+","+dataArray[0][2]+","+start+","+end+"\n";
            output+=format[0]+","+format[1]+","+format[2]+"\n";
            File file = new File(outputCsvFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 使用FileWriter构造函数的第二个参数来指定是否追加内容
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(output);
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void task4(String inputCsvFile, String outputCsvFile, String stock, int start, int end) {
        try {
            // 读取data.csv文件的内容
            String content = Files.readString(Paths.get(inputCsvFile));
            String[] line = content.split("\n");
            String[][] dataArray = new String[line.length][];
            for (int i = 0; i < line.length; i++) {
                dataArray[i] = line[i].split(",");
            }
            double[] specificData=new double[end-start+1];
            double sum = 0;
            double total=0;
            double pow=0;
            double average=0;
            double time=0;
            double timeavg=0;
            double slope=0;
            double  intercept=0;
            double n=(double)(end-start+1);
            String forslope="";
            String forinter="";
            for (int i = 0; i < 133; i++) {
                String stockName = dataArray[0][i];
                if (stockName.equals(stock)) {
                    for (int j = start,k=0; j <= end; j++,k++) {
                        specificData[k]=Double.parseDouble(dataArray[j][i]);
                    }
                }
            }
            for (int i = 0; i <=end-start ; i++) {
                sum += specificData[i];
                time+=start+i;
            }
            average=sum/n;
            timeavg=time/n;
            for (int i = 0; i <=end-start ; i++) {
                double count=(double)(start+i);
                total+=(count-timeavg)*(specificData[i]-average);  
                pow+=Math.pow(count-timeavg, 2);
            }
            slope=total/pow;
            intercept=average-slope*timeavg;
            DecimalFormat df = new DecimalFormat("0.##");
            forslope=df.format(slope);
            forinter=df.format(intercept);
            
            String output="";
            output+=stock+","+start+","+end+"\n";
            output+=forslope+","+forinter+"\n";
            File file = new File(outputCsvFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 使用FileWriter构造函数的第二个参数来指定是否追加内容
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(output);
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public static String[] calculateMovingAverage(double[] data, int windowSize) { 
            String[] movingAverages = new String[data.length - windowSize + 1];
            
            for (int i = 0; i <= data.length - windowSize; i++) {
                double sum = 0;
                for (int j = i; j < i + windowSize; j++) {
                    sum += data[j];
                }
                movingAverages[i] = formatNumber(sum / windowSize);
            }
            
            return movingAverages;
        }

        public static String formatNumber(double number) {
        DecimalFormat df = new DecimalFormat("0.##");
        return df.format(number);
        }
}


    class Crawler {
        public void startCrawl(){
            try {
                // 連線到網站並取得 HTML 內容
                Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();
                String title = doc.title();
                int day = Integer.parseInt(title.replaceAll("\\D", ""));
                Elements titleArray = doc.select("tbody > tr:nth-child(1) th");
                ArrayList<String> nameList = new ArrayList<String>();
                for (Element element : titleArray) {
                    nameList.add(element.text());
                }

                Pattern pattern = Pattern.compile("[0-9]+");
                Matcher matcher = pattern.matcher(doc.title());
                ArrayList<String> stockList = new ArrayList<String>();
                if (matcher.find()) {
                    Elements stockArray = doc.select("tbody > tr:nth-child(2) td");

                    for (Element element : stockArray) {
                        stockList.add(element.text());
                    }
                }

                // check curr day and whether the day is already in data.csv or not
                // saved to data.csv
                saveToCSV(day, nameList,stockList);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        
        public void saveToCSV(int day, ArrayList<String> nameList,ArrayList<String> stockList) {
            String csvFile = "data.csv";
            try {
                // 在每次写入数据前检查文件是否存在
                File file = new File(csvFile);
                boolean fileExists = file.exists();
                FileWriter writer = new FileWriter(csvFile, true); // append mode

                // 如果是第一次创建文件，或者文件不存在，则写入表头
                if (!fileExists) {
                    for (int i = 0; i < nameList.size(); i++) {
                        writer.append(nameList.get(i));
                        if (i < nameList.size() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                String content = Files.readString(Paths.get(csvFile));
                String[] line = content.split("\n");
                String output="";

                // 写入股票价格数据
                for (int i = 0; i < stockList.size(); i++) {
                    output+=(stockList.get(i));
                    if (i < stockList.size() - 1) {
                        output+=",";
                    }
                }
                boolean foundDuplicate = false;
        
                for (int i = 0; i < line.length; i++) {
                    if (output.equals(line[i])) {
                        foundDuplicate = true;
                        break;
                    }
                }
                
                // 如果找到相同的行，停止寫入
                if (!foundDuplicate) {
                    for (int i = 0; i < stockList.size(); i++) {
                        writer.append(stockList.get(i));
                        if (i < stockList.size() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}

