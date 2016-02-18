package fyp.models;

import fyp.utils.Helper;
import java.sql.Date;
public class Patent{
    private String title, abs, text, claims, docid;
    private String Section, Class, Subclass, Group, ipc;
    private String inventor, company;
    private String country, city;
    private Date date;
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
    public void setCity(String s){
        this.city = s;
    }
    public void setCountry (String s){
        this.country = s;
    }
    public void setDate(Date d){
        this.date = d;
    } 
    public void setDocId(String id){this.docid = id;}
    public void setIPC(String ipc){this.ipc = ipc;}
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
    public String getIPC(){return this.ipc;}
    public String getDocId(){return this.docid;}
    public String getAbstract(){return this.abs;}
    public String getClaims(){ return this.claims;}
    public String getText(){ return this.text; }
    public String getGroupCode(){ return this.Group;}
    public Date getDate(){return this.date;}
    public String getCompany(){return this.company;}
    public String getInventor(){return this.inventor;}
    public String getCountry(){return this.country;}
    public String getCity(){return this.city;}
    public void print(){
        System.out.println("Docid: " + getDocId());
        System.out.println("Date: " + getDate());
        System.out.println("Section: " + getSectionCode());
        System.out.println("Class: " + getClassCode());
        System.out.println("Subclass: " + getSubclassCode());
        System.out.println("Group: " + getGroupCode());
        System.out.println("Title: " + getTitle());
        System.out.println("Abstract: " + getAbstract());
        System.out.println("Text: " + getText());
        System.out.println("Claims: " + getClaims());
        System.out.println("City: " + getCity());
        System.out.println("Country: " + getCountry());
        System.out.println("Inventor: " + getInventor());
        System.out.println("Company: " + getCompany());
        System.out.println("IPC: " + getIPC());
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