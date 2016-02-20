package fyp.parser;
import fyp.models.Patent;
import fyp.utils.DatabaseConnector;
import fyp.utils.Helper;
import fyp.query.ESIndexer;

import javax.xml.stream.XMLInputFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.File;
import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.stream.Stream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
public class USPTOParser implements Runnable{
    private String path;
    // private DatabaseConnector db;
    public static final String logFolder = "log/";
    private String esURL = "http://localhost:9200/patents/";
    private String indexType = "uspto";
    public USPTOParser(String path){
        this.path = path;
        // this.db = db;
        this.esURL += indexType + "/";
    }

    public Patent parseString(String content){
        if(content == "") return null;
        Patent p = new Patent();
        try{
            String title = null, abs = null, text = "", claims = "", inventor = null, company = null, city = null, country = null;
            String docid = null, ipc = "";
            Date date = null;
            byte [] byteArray = content.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = factory.createXMLEventReader(in);
            boolean des = false;
            while(eventReader.hasNext()){
                XMLEvent event = eventReader.nextEvent();
                if(event.isStartElement()){
                    StartElement startElement = event.asStartElement();
                    if(startElement.getName().getLocalPart().equals("invention-title")){
                        title = parseAllText(event, eventReader, "invention-title");
                    }
                    if(startElement.getName().getLocalPart().equals("abstract")){
                        event = eventReader.nextEvent();
                        if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("p")){
                            abs = parseAllText(event, eventReader, "p");
                        }
                    }
                    if(startElement.getName().getLocalPart().equals("description")){
                        event = eventReader.nextEvent();
                        while(!(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("description"))){
                            if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("p")){
                                text =  text + parseAllText(event, eventReader, "p") + ". ";
                                if(text.split("\\s+").length >= 1000)
                                    break;
                            }
                            event = eventReader.nextEvent();
                        }
                    }
                    if(startElement.getName().getLocalPart().equals("claim-text")){
                        if(claims.split("\\s+").length < 500)
                            claims =  claims + parseAllText(event, eventReader, "claim-text") + " ";
                    }
                    if(startElement.getName().getLocalPart().equals("applicants")){
                        inventor = parseInventors(event, eventReader);
                    }
                    if(startElement.getName().getLocalPart().equals("assignees")){
                        String [] res = parseCompany(event, eventReader);
                        company = res[0];
                        city = res[1];
                        country = res[2];
                    }
                    if(startElement.getName().getLocalPart().equals("publication-reference")){
                        date = parseDate(event, eventReader);
                    }
                    if(startElement.getName().getLocalPart().equals("application-reference")){
                        docid = parseDocId(event, eventReader);
                    }
                    if(startElement.getName().getLocalPart().equals("classification-ipcr")){
                        ipc = ipc + parseIPC(event, eventReader) + ";";
                    }
                }
            }
            if(docid == null) return null;
            p.setTitle(title);
            p.setAbstract(abs);
            p.setText(text);
            p.setClaims(claims);
            p.setCompany(company);
            p.setInventor(inventor);
            p.setCity(city);
            p.setCountry(country);
            p.setDate(date);
            p.setDocId(docid);
            if(ipc.equals("")){
                p.setIPC(null);
            }
            else
                p.setIPC(ipc);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return p;
    }
    public String parseIPC(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException{
        String section = "", c = "",subclass = "", maingroup = "", subgroup = "";
        while(!(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("classification-ipcr"))){
            if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("section")){
                section = eventReader.getElementText();
            }
            if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("class")){
                c = eventReader.getElementText();
            }
            if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("subclass")){
                subclass = eventReader.getElementText();
            }
            if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("main-group")){
                maingroup = eventReader.getElementText();
            }
            if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("subgroup")){
                subgroup = eventReader.getElementText();
            }
            event = eventReader.nextEvent();
        }
        return section + c + subclass + maingroup + "/" + subgroup;
    }
    public String[] parseCompany(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException{
        String company = "";
        String country = "";
        String city = "";
        while(!(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("assignees"))){
            if(event.isStartElement()){
                StartElement startE = event.asStartElement();
                if(startE.getName().getLocalPart().equals("orgname")){
                    event = eventReader.nextEvent();
                    if(event.isCharacters()){
                        company = company + event.asCharacters().getData() + ";";
                    }
                }
                if(startE.getName().getLocalPart().equals("city")){
                    event = eventReader.nextEvent();
                    if(event.isCharacters()){
                        city = event.asCharacters().getData();
                    }
                }
                if(startE.getName().getLocalPart().equals("country")){
                    event = eventReader.nextEvent();
                    if(event.isCharacters()){
                        country = event.asCharacters().getData();
                    }
                }
            }
            event = eventReader.nextEvent();
        }
        return new String[]{company, city, country};
    }
    public Date parseDate(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException, ParseException{
        String d = null;
        while(!(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("publication-reference"))){
            if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("date")){
                d = eventReader.getElementText();
                break;
            }
            event = eventReader.nextEvent();
        }
        if(d == null) return null;
        d = d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6);
        Date date = Date.valueOf(d);
        return date;
    }
    public String parseDocId(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException{
        String id = null;
        while(!(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("application-reference"))){
            if(event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("doc-number")){
                id = eventReader.getElementText();
                break;
            }
            event = eventReader.nextEvent();
        }
        return id;
    }
    public String parseInventors(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException{
        String inventors = "";
        String temp = "";
        while(!(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("applicants"))){
            if(event.isStartElement()){
                StartElement startE = event.asStartElement();
                if(startE.getName().getLocalPart().equals("applicant"))
                    temp = "";
                if(startE.getName().getLocalPart().equals("last-name")){
                    event = eventReader.nextEvent();
                    if(event.isCharacters())
                        temp = temp + " " + event.asCharacters().getData();
                }
                if(startE.getName().getLocalPart().equals("first-name")){
                    event = eventReader.nextEvent();
                    if(event.isCharacters())
                        temp = temp + " " + event.asCharacters().getData();
                }
            }
            if(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("applicant")){
                inventors = inventors + temp + ";";
            }
            event = eventReader.nextEvent();
        }
        return inventors;
    }
    public String parseAllText(XMLEvent event, XMLEventReader eventReader, String tag)throws XMLStreamException{
        String text = "";
        while(!(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(tag))){
            if(event.isStartElement() || event.isEndElement()){
                event = eventReader.nextEvent();
                continue;
            }
            if(event.isCharacters())
                text += event.asCharacters().getData() + " ";
            event = eventReader.nextEvent();
        }
        if(text.equals("")) return null;
        return text;
    }
    public String getLogFileName(){
        File f = new File(this.path);
        String name = null;
        int index = f.getName().lastIndexOf('.');
        if (index > 0) {
            name = f.getName().substring(0, index+1) + "log";
        }
        return name;
    }
    public String check(){
        String logFile = getLogFileName();
        File dir = new File(this.logFolder);
        System.out.println(dir.getAbsolutePath());
        try{
            if(!dir.exists()){
                dir.createNewFile();
            }    
        }catch(Exception e){
            e.printStackTrace();
        }
        
        String docid = null;
        File [] files = dir.listFiles();
        for(File f: files){
            if(f.getName().equals(logFile)){
                // Log file exists
                try(BufferedReader bf = new BufferedReader(new FileReader(f))){
                    String line = bf.readLine();
                    String last = null;
                    while(line != null){
                        last = line;
                        line = bf.readLine();
                    };
                    if(last == null) return null;
                    System.out.println(last);
                    String [] temp = last.split("=");
                    if(temp.length == 2) return temp[1];
                    else if(temp[0].equals("Done")) return "Done";
                    else return null;
                } catch(Exception e){
                    System.out.println("Could not open the log file: " + f.getName());
                    e.printStackTrace();
                }
            }
        }
        return docid;
    }
    public void parse(String docid){
        boolean start = true;
        if(docid != null){
            start = false;
        }
        List <Patent> patents = new ArrayList <Patent>(1000);
        int count = 0;
        File f = new File(this.path);
        String name = getLogFileName();
        String logPath = this.logFolder + name;
        String currentDocid = null;
        Path file = Paths.get(this.path);
        Patent p;
        Helper.writeLog(logPath, "PARSING FILE : " + f.getName(), false);
        try{
            Stream<String> lines = Files.lines(file);
            String content = "";
            for(String line: (Iterable<String>) lines::iterator){
                if(line.startsWith("<?xml")){
                    p = parseString(content);
                    if(p == null){
                        content = "";
                        continue;
                    }
                    if(start && !p.getDocId().equals(docid)){
//                        System.out.println("Count = " + count++ + " Doc id = " + p.getDocId());
                        p.clean();
                        patents.add(p);
                        if(patents.size() == 1000){
                            currentDocid = p.getDocId();
                            // db.insertPatent(patents, "patents");
                            ESIndexer.index(patents, this.esURL);
                            patents.clear();
//                            return;
                        }
                    }
                    if(!start && p.getDocId().equals(docid)){
                        System.out.println("Previous doc found!!!");
                        System.out.println("Resuming...");
                        start = true;
                    }
                    content = "";
                }
                content += line;
            }
            // if(patents.size() > 0){
            //     db.insertPatent(patents, "patents");
            // }
            Helper.writeLog(logPath, "Done", true);
        }
        catch(Exception e){
            if(currentDocid != null){
                Helper.writeLog(logPath, "currentDocid=" + currentDocid, true);
            }
            e.printStackTrace();
        }
    }
    public void run(){
        String docid = check();
        if(docid == null)
            parse(null);
        else if(!docid.equals("Done"))
            parse(docid);
        else
            System.out.println("The file was already parsed!");
        Helper.executeShellCommand("rm " + path);
        // this.db.close();
    }
}