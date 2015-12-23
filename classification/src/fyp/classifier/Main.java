package fyp.classifier;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import fyp.utils.Helper;
public class Main{
    private static int TOP_K_CLASS = 10;
    public static void main(String[] args) {
        Classifier classifier = new Classifier();
        // classifier.train();
        classifier.loadModels();
        Scanner in = new Scanner(System.in);
        System.out.println("Run (0/1): ");
        while(in.nextInt() != 0){
            List <Result> res = classifier.classify(Helper.readFile("/Users/phucnguyen/programming/fyp-patent-analysis/classification/src/fyp/utils/test.txt"));
            for (int i = 0; i < TOP_K_CLASS; i++) {
                System.out.println(res.get(i).getClassSymbol() + " " + res.get(i).getResult());
            }
            System.out.println("Run again (0/1): ");
        }
        // List <Result> test = new ArrayList <Result> ();
    }
}