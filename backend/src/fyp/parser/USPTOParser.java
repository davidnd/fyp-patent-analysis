package fyp.parser;
import fyp.models.Patent;
import fyp.utils.DatabaseConnector;
import fyp.utils.Helper;

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
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class USPTOParser extends Parser{
    public Patent parse(File file){return null;}

    public Patent parse(String content){
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
                            }
                            event = eventReader.nextEvent();
                        }
                    }
                    if(startElement.getName().getLocalPart().equals("claim-text")){
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
        String [] res = {company, city, country};
        return res;
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
        d = d.substring(0, 4) + "/" + d.substring(4, 6) + "/" + d.substring(6);
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = df.parse(d);
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
    public void parseDir(String path, DatabaseConnector db){
        File dirs = new File(path);
        File [] files = dirs.listFiles();
        for (File f: files) {
            if(f.isFile() && Helper.isXML(f.getName())){
                System.out.println("Parsing " + f.getName());
                Path file = Paths.get(f.getAbsolutePath());
                Patent p;
                int count = 0;
                try{
                    Stream<String> lines = Files.lines(file);
                    String content = "";
                    for(String line: (Iterable<String>) lines::iterator){
                        if(line.startsWith("<?xml")){
                            p = parse(content);
                            if(p == null && count != 0){
                                System.out.println("BUGSSS");
                                return;
                            }
                            if(p != null){
                                System.out.println(count + ": ");
                                p.print();
                            }
                            count++;
                            if(count == 1000){
                                return;   
                            } 
                            content = "";
                        }
                        content += line;
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                return;
            }
            else if(f.isDirectory())
                parseDir(f.getAbsolutePath(), db);
        }
    }
}