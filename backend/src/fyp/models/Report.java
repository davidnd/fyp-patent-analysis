package fyp.models;
import java.util.List;
import java.util.ArrayList;
public class Report{
    private String label;
    private List <String> predict;
    public Report(String l, List <String> p){
        this.label = l;
        this.predict = p;
    }
    public boolean check(int k){
        if(k > this.predict.size()){
            System.out.println("K is too large");
            return false;
        }
        for (int i = 0; i<k; i++) {
            if(this.predict.get(i).equals(this.label)){
                return true;
            }
        }
        return false;
    }
}