import java.util.*;
import java.io.*; 
// import java.util.Scanner;
// import java.lang.Math;
import java.util.Arrays;

public class hybrid extends sim{
    int no_of_predictions = 0;
    int no_of_mispredictions = 0;
    double misprediction_rate = 0;
    int M1;
    int N;
    int bi;
    /**
     * @param str
     */
    void hybrid_bp(String[] str){
        double a = 2;
        double M2 = Integer.parseInt(str[2]);
        N = Integer.parseInt(str[3]);
        double bi_temp = Integer.parseInt(str[4]);
        bi = (int)bi_temp;
        double size = Math.pow(a, M2);
        double sizebi = Math.pow(a, bi_temp);
        
        M1 = (int) M2;
        double p = Integer.parseInt(str[1]);
        double sizeK = Math.pow(a,p);
        int K = (int)p;
        int table_size = (int)size;
        int bi_table_size = (int)sizebi;
        int k_table_size = (int)sizeK;
        Integer[] predictionsK = new Integer[k_table_size];
        Arrays.fill(predictionsK, 1);
        List<Integer> hybrid_prediction_table = Arrays.asList(predictionsK);
        String[] GlobalBHistory = new String[N];
        Arrays.fill(GlobalBHistory, "0");

        Integer[] predictions = new Integer[table_size];
        Arrays.fill(predictions, 4);
        List<Integer> prediction_table = Arrays.asList(predictions);

        Integer[] bi_predictions = new Integer[bi_table_size];
        Arrays.fill(bi_predictions, 4);
        List<Integer> bi_prediction_table = Arrays.asList(bi_predictions);

        int get_index = 0;
        int updated_index_value =0;
        try {
            File file_obj = new File(str[5]);
            Scanner file_read = new Scanner(file_obj);
            while (file_read.hasNextLine()) {
                String data_line = file_read.nextLine();
                no_of_predictions++;
                int hybrid_get_index = get_index(data_line, K );
                int bimodal_get_index = get_index(data_line, bi);
                int gshare_get_index = gshare_bp_get_index(data_line,GlobalBHistory);
                int bimodal_index_value = bi_prediction_table.get(bimodal_get_index);
                int gshare_index_value = prediction_table.get(gshare_get_index);
                // get gshare prediction 
                char get_gshare_prediction = get_prediction(gshare_index_value);
                char get_bimodal_prediction = get_prediction(bimodal_index_value);
                // get bimodal prediction
                if (hybrid_prediction_table.get(hybrid_get_index)>=2){
                    
                    updated_index_value = update_index(get_gshare_prediction, data_line, gshare_index_value);
                    prediction_table.set(gshare_get_index, updated_index_value);
                }else{
                    updated_index_value = update_index(get_bimodal_prediction, data_line, bimodal_index_value);
                    bi_prediction_table.set(bimodal_get_index, updated_index_value);
                }
                // update based on the K index
                
                
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

                if ((data_char == get_gshare_prediction)&& !(data_char == get_bimodal_prediction)){
                    if (hybrid_prediction_table.get(hybrid_get_index)<3){
                        int x = hybrid_prediction_table.get(hybrid_get_index)+1;
                        hybrid_prediction_table.set(hybrid_get_index,x);
                    }
                }else if(!(data_char == get_gshare_prediction)&& (data_char == get_bimodal_prediction)){
                    if (hybrid_prediction_table.get(hybrid_get_index)>0){
                        int x = hybrid_prediction_table.get(hybrid_get_index)-1;
                        hybrid_prediction_table.set(hybrid_get_index,x);
                    }
                }
                    
                // for(int i=0; i<N;i++){
                //     System.out.print(GlobalBHistory[i]);
                // }
                
                // = (int)pred.charAt(1);
                //char prediction = pred.charAt(0);
                //if 
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
          System.out.println("./sim"+" "+str[0]+" "+str[1]+" "+str[2]+" "+str[3]+" "+str[4]+" "+str[5]);
          System.out.println("OUTPUT");
          System.out.println("number of predictions:\t\t"+no_of_predictions);
          System.out.println("number of mispredictions:\t"+no_of_mispredictions); 
          System.out.println("misprediction rate:\t\t"+String.format("%.2f", misprediction_rate)+"%");
        System.out.println("FINAL CHOOSER CONTENTS");
        for(int i=0; i<hybrid_prediction_table.size(); i++){
            System.out.println(i +" "+ hybrid_prediction_table.get(i));
        }
        System.out.println("FINAL GSHARE CONTENTS");
        for(int i=0; i<prediction_table.size(); i++){
            System.out.println(i +" "+ prediction_table.get(i));
        }
        System.out.println("FINAL BIMODAL CONTENTS");
        for(int i=0; i<bi_prediction_table.size(); i++){
            System.out.println(i +" "+ bi_prediction_table.get(i));
        }

    }

    char get_prediction(Integer index_value){
        char temp_predict='t';
        

        if (index_value < 4 && index_value>=0){
            temp_predict = 'n';
        }
        else if (index_value >= 4 && index_value <= 7 ){
            temp_predict = 't';
        }
        return temp_predict;

    } 

    int update_index(char temp_predict, String data_line, int index_value){
        String[] data_line_split = data_line.split("\\s+");
        String data_string = data_line_split[1];
        char data_char =data_string.charAt(0); 

        if (temp_predict != data_char){
            no_of_mispredictions++;
        }

        if (data_char=='t'){
            if (index_value<7)
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

    int get_index(String data_line, Integer m){
        String[] data_line_split = data_line.split("\\s+");
        String hex = data_line_split[0];
        String bin = hex2bin(hex);
        String bin_index= bin.substring(bin.length()-m-2 ,bin.length()-2);
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
    // void bimodal_bp_func(String data_line, int taken_down_limt, int taken_up_limt){

    // }
}
