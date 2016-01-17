package fyp.models;

import fyp.utils.Helper;
import java.util.Date;
public class Patent{
    private String title, abs, text, claims;
    private String Section, Class, Subclass, Group;
    private String inventor, company;
    private String i_city, i_country, a_city, a_country;
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
    public void setInventorCity (String s){
        this.i_city = s;
    }
    public void setInventorCountry(String s){
        this.i_country = s;
    }
    public void setCompanyCity(String s){
        this.a_city = s;
    }
    public void setCompanyCountry (String s){
        this.a_country = s;
    }
    public void setDate(Date d){
        this.date = d;
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
    public String getAbstract(){return this.abs;}
    public String getClaims(){ return this.claims;}
    public String getText(){ return this.text; }
    public String getGroupCode(){ return this.Group;}
    public Date getDate(){return this.date;}
    public String getCompanyName(){return this.company;}
    public String getInventorName(){return this.inventor;}
    public String getCompanyCountry(){return this.a_country;}
    public String getCompanyCity(){return this.a_city;}
    public String getInventorCity(){return this.i_city;}
    public String getInventorCountry(){return this.i_country;}
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