package fyp.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import fyp.utils.Helper;
import fyp.models.Patent;
import fyp.utils.DatabaseConnector;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class WIPOParser{

    public Patent parse(File file){
        Patent p = new Patent();
        try{
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(file);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            while(eventReader.hasNext()){
                XMLEvent event = eventReader.nextEvent();
                if(event.isStartElement()){
                    StartElement startE = event.asStartElement();
                    if(startE.getName().getLocalPart() == "record"){
                        p = new Patent();
                    }
                    if(startE.getName().getLocalPart().equals("ipcs")){
                        Iterator <Attribute> attr = startE.getAttributes();
                        while(attr.hasNext()){
                            Attribute a = attr.next();
                            if(a.getName().toString().equals("mc")){
                                setIPC(p, a.getValue());
                                break;
                            }
                        }
                    }
                    if(startE.getName().getLocalPart().equals("ti")){
                        p.setTitle(eventReader.getElementText());
                    }
                    if(startE.getName().getLocalPart().equals("ab")){
                        p.setAbstract(eventReader.getElementText());
                    }
                    if(startE.getName().getLocalPart().equals("cl")){
                        p.setClaims(eventReader.getElementText());
                    }
                    if(startE.getName().getLocalPart().equals("txt")){
                        p.setText(eventReader.getElementText());
                    }
                }
            }   
        }catch(Exception e){
            e.printStackTrace();
            Helper.writeLog("fyp/log/WIPO.log", file.getAbsolutePath());
            return null;
        }
        return p;
    }
    public void setIPC(Patent p, String ipc){
        p.setSection(ipc.substring(0, 1));
        p.setClass(ipc.substring(0, 3));
        p.setSubclass(ipc.substring(0, 4));
        p.setGroup(ipc.substring(0, 7));
    }
    public void parseDir(String path, DatabaseConnector db){
        File dirs = new File(path);
        File [] files = dirs.listFiles();
        for (File f: files) {
            if(f.isFile() && Helper.isXML(f.getName())){
                System.out.println("Parsing " + f.getName());
                Patent p = parse(f);
                if(p == null) continue;
                p.clean();
                db.insertPatent(p, "test");
            }
            else if(f.isDirectory())
                parseDir(f.getAbsolutePath(), db);
        }
    }
}