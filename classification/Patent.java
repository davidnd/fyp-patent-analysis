public class Patent{
    private String title, abs, text, claims;
    private String Section, Class, Subclass, Group;
    private String inventor, company;
    public Patent(){

    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setAbstract(String abs){
        this.abs = abs;
    }
    public void setText(String t){
        this.text = t;
    }
    public void setClaims(String c){
        this.claims = c;
    }
    public void setSection(String s){
        this.Section = s;
    }
    public void setClass(String c){
        this.Class = c;
    }   
    public void setSubclass(String s){
        this.Subclass = s;
    }
    public void setGroup(String g){
        this.Group = g;
    }
    public void setInventor(String i){
        this.inventor = i;
    }
    public void setCompany(String c){
        this.company = c;
    }
    public String getSectionCode(){
        return this.Section;
    }
    public String getClassCode(){
        return this.Class;
    }
    public String getSubclassCode(){
        return this.Subclass;
    }
    public String getTitle(){
        return this.title;
    }
    public String getAbstract(){
        return this.abs;
    }
    public String getClaims(){ return this.claims;}
    public String getText(){ return this.text; }
    public String getGroupCode(){ return this.Group; }
    public void print(){
        System.out.println("Section: " + getSectionCode());
        System.out.println("Class: " + getClassCode());
        System.out.println("Subclass: " + getSubclassCode());
        System.out.println("Group: " + getGroupCode());
        System.out.println("Title: " + getTitle());
        System.out.println("Abstract: " + getAbstract());
        System.out.println("Text: " + getText());
        System.out.println("Claims: " + getClaims());
    }
    public void clean(){
        //remove new line char
        this.title = Helper.removeNewLine(this.title);
        this.abs = Helper.removeNewLine(this.abs);
        this.claims = Helper.removeNewLine(this.claims);
        this.text = Helper.removeNewLine(this.text);

        this.title = Helper.removeStopWords(this.title);
        this.abs = Helper.removeStopWords(this.abs);
        this.text = Helper.removeStopWords(this.text);
        this.claims = Helper.removeStopWords(this.claims);
    }
}   