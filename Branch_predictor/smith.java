import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner;
import java.lang.Math;

public class smith extends sim{
    int no_of_predictions = 0;
    int no_of_mispredictions = 0;
    double misprediction_rate = 0;

    void smith_bp(String[] str){
        double a = 2;
        double B = Integer.parseInt(str[1]); 
        double temp = Math.pow(a, B);
        double taken_down_limt_double = temp/2;  
        double taken_up_limt_double = temp;
        int taken_down_limt = (int)taken_down_limt_double;
        int taken_up_limt = (int)taken_up_limt_double;
        double temp2 = temp/2;
        int initial_counter = (int)temp2;
        int updated_counter = initial_counter;
        try {
            File file_obj = new File(str[2]);
            Scanner file_read = new Scanner(file_obj);
            while (file_read.hasNextLine()) {
                String data_line = file_read.nextLine();
                no_of_predictions++;
                updated_counter = smith_bp_func(data_line, updated_counter, taken_down_limt, taken_up_limt);
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
        System.out.println("FINAL COUNTER CONTENT:\t\t"+updated_counter);
    }
    int smith_bp_func(String data_line, int updated_counter, int taken_down_limt, int taken_up_limt){
        String[] data_line_split = data_line.split("\\s+");
        String data_string = data_line_split[1];
        char data_char =data_string.charAt(0); 
        char temp_predict = 'n';
        if (updated_counter < taken_down_limt && updated_counter >= 0){
            temp_predict = 'n';
        }
        else if (updated_counter >= taken_down_limt && updated_counter < taken_up_limt ){
            temp_predict = 't';
        }
        
        if (temp_predict != data_char){
            no_of_mispredictions++;
        }

        if (data_char=='t'){
            if (updated_counter<taken_up_limt-1)
                updated_counter++;
        }
        else if (data_char == 'n'){
            if (updated_counter>0)
                updated_counter--;
        }
        return updated_counter;
    }
    
    
}
