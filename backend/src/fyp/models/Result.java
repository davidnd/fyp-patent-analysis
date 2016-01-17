package fyp.models;

public class Result{
    private double result;
    private String classSymbol;
    public Result(double result, String classSymbol){
        this.result = result;
        this.classSymbol = classSymbol;
    }
    public String getClassSymbol(){
        return this.classSymbol;
    }
    public double getResult(){
        return this.result;
    }
    public void setResult(double res){
        this.result = res;
    }
    public void setSymbol(String s){
        this.classSymbol = s;
    }
}