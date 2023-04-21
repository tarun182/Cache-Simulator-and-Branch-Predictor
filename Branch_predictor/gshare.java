import java.util.*;
import java.io.*; 
// import java.util.Scanner;
// import java.lang.Math;
import java.util.Arrays;

public class gshare extends sim{
    int no_of_predictions = 0;
    int no_of_mispredictions = 0;
    double misprediction_rate = 0;
    int M1;
    int N;
    /**
     * @param str
     */
    void gshare_bp(String[] str){
        double a = 2;
        double M2 = Integer.parseInt(str[1]);
        N = Integer.parseInt(str[2]);
        double size = Math.pow(a, M2);
        M1 = (int) M2;
        int table_size = (int)size;
        String[] GlobalBHistory = new String[N];
        Arrays.fill(GlobalBHistory, "0");
        Integer[] predictions = new Integer[table_size];
        Arrays.fill(predictions, 4);
        List<Integer> prediction_table = Arrays.asList(predictions);
        int taken_down_limt = 4;
        int taken_up_limt = 7;
        int get_index = 0;
        int updated_index_value =0;
        try {
            File file_obj = new File(str[3]);
            Scanner file_read = new Scanner(file_obj);
            while (file_read.hasNextLine()) {
                String data_line = file_read.nextLine();
                no_of_predictions++;
                get_index = gshare_bp_get_index(data_line,GlobalBHistory);
                int index_value = prediction_table.get(get_index);
                updated_index_value = gshare_bp_func(data_line, index_value, taken_down_limt, taken_up_limt);
                String[] data_line_split = data_line.split("\\s+");
                String data_string = data_line_split[1];
                char data_char =data_string.charAt(0); 
                if(data_char == 't'){
                    String temp = "1";
                    for (int i = (N - 2); i >= 0; i--) {                
                    GlobalBHistory[i+1] = GlobalBHistory[i];
                    }
                    GlobalBHistory[0] = temp;    
                }else{
                    String temp = "0";
                    for (int i = (N-2) ; i >= 0; i--) {                
                    GlobalBHistory[i+1] = GlobalBHistory[i];
                    }
                    GlobalBHistory[0] = temp; 
                }
                // for(int i=0; i<N;i++){
                //     System.out.print(GlobalBHistory[i]);
                // }
                
                // = (int)pred.charAt(1);
                //char prediction = pred.charAt(0);
                //if 
                prediction_table.set(get_index, updated_index_value);
            }
            file_read.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
          double temp1 = (double)no_of_mispredictions;
          double temp3 = (double)no_of_predictions;
          misprediction_rate = ((temp1*100.0)/(temp3));
          System.out.println("COMMAND");
          System.out.println("./sim"+" "+str[0]+" "+str[1]+" "+str[2]+" "+str[3]);
          System.out.println("OUTPUT");
          System.out.println("number of predictions:\t\t"+no_of_predictions);
          System.out.println("number of mispredictions:\t"+no_of_mispredictions); 
          System.out.println("misprediction rate:\t\t"+String.format("%.2f", misprediction_rate)+"%");
        System.out.println("FINAL GSHARE CONTENTS");
        for(int i=0; i<prediction_table.size(); i++){
            System.out.println(i +" "+ prediction_table.get(i));
        }

    }
    int gshare_bp_func(String data_line, int index_value,int taken_down_limt, int taken_up_limt){
        char temp_predict='t';
        String[] data_line_split = data_line.split("\\s+");
        String data_string = data_line_split[1];
        char data_char =data_string.charAt(0); 

        if (index_value < taken_down_limt && index_value>=0){
            temp_predict = 'n';
        }
        else if (index_value >= taken_down_limt && index_value <= taken_up_limt ){
            temp_predict = 't';
        }
        if (temp_predict != data_char){
            no_of_mispredictions++;
        }

        if (data_char=='t'){
            if (index_value<taken_up_limt)
            index_value++;
        }
        else if (data_char == 'n'){
            if (index_value>0)
            index_value--;
        }
        // char index = (char)(index_value+'0');
        // String pred = ""+temp_predict;
        // pred = ""+index;
        return index_value;

    }

    int gshare_bp_get_index(String data_line, String[] GlobalBHistory){
        String[] data_line_split = data_line.split("\\s+");
        String hex = data_line_split[0];
        String bin = hex2bin(hex);
        String bin_index= bin.substring(bin.length()-M1-2 ,bin.length()-2);
        String bin_index1 = bin_index.substring(bin_index.length()-N,bin_index.length());
        String bin_index2 = bin_index.substring(0,bin_index.length()-N);
        String bin_index3="";
        for(int i=0;i<N;i++){
            String currBit = GlobalBHistory[i];
            if (currBit.charAt(0) == bin_index1.charAt(i)){
                bin_index3+="0";
            }else{
                bin_index3+="1";
            }
        }
        String bin_index4 = bin_index2 + bin_index3;
        int dec_index= Integer.parseInt(bin_index4,2); 
        return dec_index;
    }

    String hex2bin(String hex){
        int temp = (Integer.parseInt(hex, 16));
        String temp2 = Integer.toBinaryString(temp);
        return temp2;
    }
    // void bimodal_bp_func(String data_line, int taken_down_limt, int taken_up_limt){

    // }
}
