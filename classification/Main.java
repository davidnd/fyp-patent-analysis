class Main{
    public static void main(String[] args) {
        DatabaseConnector connector = new DatabaseConnector();
        connector.connect();
        String root = "../data/wipo-alpha/wipo-alpha/train/";
        String dir = "/Users/phucnguyen/programming/fyp-patent-analysis/classification/../data/wipo-alpha/wipo-alpha/train/B/30/B/003/IT0000317_08022001.xml";

        Patent p = Parser.parse(dir);
        if(p == null)
            System.out.println("Patent is null");
        else {
            p.clean();
            connector.insertPatent(p);
        }
    } 
}