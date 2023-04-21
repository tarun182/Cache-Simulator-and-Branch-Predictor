import java.util.*;
import java.io.*; 
// import java.util.Scanner;
// import java.lang.Math;

public class bimodal extends sim{
    int no_of_predictions = 0;
    int no_of_mispredictions = 0;
    double misprediction_rate = 0;
    int M2;
    /**
     * @param str
     */
    void bimodal_bp(String[] str){
        double a = 2;
        double m = Integer.parseInt(str[1]);
        double size = Math.pow(a, m);
        M2 = (int) m;
        int table_size = (int)size;
        Integer[] predictions = new Integer[table_size];
        Arrays.fill(predictions, 4);
        List<Integer> prediction_table = Arrays.asList(predictions);
        int taken_down_limt = 4;
        int taken_up_limt = 7;
        int get_index = 0;
        int updated_index_value =0;
        try {
            File file_obj = new File(str[2]);
            Scanner file_read = new Scanner(file_obj);
            while (file_read.hasNextLine()) {
                String data_line = file_read.nextLine();
                no_of_predictions++;
                get_index = bimodal_bp_get_index(data_line);
                int index_value = prediction_table.get(get_index);
                updated_index_value = bimodal_bp_func(data_line, index_value, taken_down_limt, taken_up_limt);
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
          System.out.println("./sim"+" "+str[0]+" "+str[1]+" "+str[2]);
          System.out.println("OUTPUT");
          System.out.println("number of predictions:\t\t"+no_of_predictions);
          System.out.println("number of mispredictions:\t"+no_of_mispredictions); 
          System.out.println("misprediction rate:\t\t"+String.format("%.2f", misprediction_rate)+"%");
        System.out.println("FINAL BIMODAL CONTENTS");
        for(int i=0; i<prediction_table.size(); i++){
            System.out.println(i +" "+ prediction_table.get(i));
        }

    }
    int bimodal_bp_func(String data_line, int index_value,int taken_down_limt, int taken_up_limt){
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
        return index_value;

    }

    int bimodal_bp_get_index(String data_line){
        String[] data_line_split = data_line.split("\\s+");
        String hex = data_line_split[0];
        String bin = hex2bin(hex);
        String bin_index= bin.substring(bin.length()-M2-2 ,bin.length()-2);
        int dec_index= Integer.parseInt(bin_index,2);  
        // System.out.println(bin);
        // System.out.println(bin_index);
        // System.out.println(dec_index);
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
