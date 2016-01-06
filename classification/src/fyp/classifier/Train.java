package fyp.classifier;
public class Train{
    public static int TOP_K_CLASS = 10;
    public static Classifier classifier;
    public static void main(String[] args) throws Exception{
        classifier = new Classifier();
        classifier.train();
    }
}