package fyp.models;

import java.util.List;
import java.util.ArrayList;

import fyp.classifier.Classifier;

public class TestResult{
    private int id;
    private List <Result> results;
    public TestResult(int id){
        this.id = id;
    }
    public void setResults(List <Result> r){
        this.results = r;
    }
    public List <Result> getResults(){
        return this.results;
    }
    public int getId(){
        return this.id;
    }
    public String getClassifiedLabel(){
        String s = "";
        for (int i = 0; i < Classifier.THRES_HOLD; i++) {
            s+=this.results.get(i).getClassSymbol() + ";";
        }
        return s;
    }
}