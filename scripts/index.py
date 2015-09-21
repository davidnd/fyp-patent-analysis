from model import Assignee, Inventor, Patent, Subclass
import xml.etree.ElementTree as ET
from sqlalchemy.orm import sessionmaker
from sqlalchemy import create_engine
import utils, os

filename = "ipg130101.xml"
start = True
startpoint = "12854740"
basedir = os.path.dirname(__file__)
relpath = "../data"
filepath = os.path.join(basedir, relpath, filename)
engine = create_engine('mysql://root:root@localhost/patent')
Session = sessionmaker(bind=engine)
session = Session()

def updatemissingsubclass(e):
    f = open('missingsubclass.txt', 'a')
    f.write(e + "\n")
    f.close()
num = 0
for item in utils.xmlSplitter(open(filepath, 'r')):
    docnumber = ""
    abstract = ""
    claimstext = ""
    title = ""
    description = ""
    date = None
    cpccode = ""
    root = ET.fromstring(item)
    # get abstract
    abstract = utils.getElementText(root.find("abstract"))
    # print "ABSTRACT: " + abstract
    # get claims
    claimstext = utils.getElementText(root.find("claims"))
    # print "CLAIM TEXT: " + claimstext
    # get title
    titleIter = list(root.iter("invention-title"))
    if(len(titleIter)):
        title = utils.getElementText(titleIter[0])
        # print "TITLE: " + title
    # get description
    description = utils.getElementText(root.find("description"))
    # print "DESCRIPTION length: ", len(description)
    # get document number and date
    appref = list(root.iter("application-reference"))
    if(len(appref)):
        docnumber = list(appref[0].iter("doc-number"))
        if(docnumber != startpoint and start == False):
            continue
        else:
            start = True
        if(len(docnumber)):
            docnumber = docnumber[0].text
            print "Indexing doc num ", num;
            print docnumber
            # print docnumber
        date = list(appref[0].iter('date'))
        if(len(date)):
            date = date[0].text
            date = date[:4] + "-" + date[4:6] + "-" + date[6:]
            # print date
    if(session.query(Patent.id).filter(Patent.docnumber == docnumber).count()):
        print "Patent already indexed, move to next one"
        continue
    cpcnodes = list(root.iter("classification-cpc"))
    if(len(cpcnodes) == 0):
        cpcnodes = list(root.iter("classification-ipcr"))
        if(len(cpcnodes) == 0):
            print "This patent does not have cpc codes"
            continue
    # get classification code
    for cpc in cpcnodes:
        section = cpc.find("section").text
        classlv = cpc.find("class").text
        subclass = cpc.find("subclass").text
        maingroup = cpc.find('main-group').text
        subgroup = cpc.find('subgroup').text
        cpccode = cpccode + section+classlv+subclass+maingroup+"/"+subgroup+";"
    # print cpccode
    
    patent = Patent(docnumber, title, abstract, cpccode, claimstext, description, date)
    cpccodes = []
    # assign patent to subclass and update count
    if(cpccode):
        cpccodes = [x[:4] for x in cpccode.split(';') if x]
        cpccodes = set(cpccodes)
        for code in cpccodes:
            subclass = session.query(Subclass).filter(Subclass.symbol == code).first()
            # add sublcass for patent
            if(subclass is None):
                updatemissingsubclass(code)
                continue;
            patent.subclasses.append(subclass)
            subclass.count+=1
            session.add(subclass)
    # get assignees
    assignees = list(root.iter("assignee"))
    assigneesList = []
    # print "Num of assignees: ", len(assignees)
    if(len(assignees) == 0):
        applicants = list(root.iter("us-applicant"))
        # print "Num of applicants: ", len(applicants)
        for app in applicants:
            attributes = app.attrib
            orgname = utils.getFirstDescendant(app, "orgname")
            if(orgname is not None):
                name = utils.getElementAsciiText(orgname)
            else:
                lastnameNode = utils.getFirstDescendant(app, "last-name")
                lastname = utils.getElementAsciiText(lastnameNode)

                firstnameNode = utils.getFirstDescendant(app, "first-name")
                firstname = utils.getElementAsciiText(firstnameNode)
                name = firstname + " " + lastname;
            countryNode = utils.getFirstDescendant(app, "country")
            country = utils.getElementAsciiText(countryNode)
            cityNode = utils.getFirstDescendant(app, "city")
            city = utils.getElementAsciiText(cityNode)
            tempAssignee = session.query(Assignee).filter(Assignee.orgname == name, Assignee.city == city, Assignee.country == country)
            if(tempAssignee.count()==0):
                applicant = Assignee(name, city, country)
                session.add(applicant)
                assigneesList.append(applicant)
            else:
                assigneesList.append(tempAssignee.first())
    else:
        for i in assignees:
            orgnameNode = utils.getFirstDescendant(i, "orgname")
            orgname = utils.getElementAsciiText(orgnameNode)
            countryNode = utils.getFirstDescendant(i, "country")
            country = utils.getElementAsciiText(countryNode)
            cityNode = utils.getFirstDescendant(i, "city")
            city = utils.getElementAsciiText(cityNode)
            tempAssignee = session.query(Assignee).filter(Assignee.orgname == orgname, Assignee.city == city, Assignee.country == country)
            if(tempAssignee.count()==0):
                assignee = Assignee(orgname, city, country)
                session.add(assignee)
                assigneesList.append(assignee)
            else:
                assigneesList.append(tempAssignee.first())
    for a in assigneesList:
        patent.assignees.append(a)
    #get inventors
    inventors = list(root.iter("inventor"))
    inventorsList = []
    for i in inventors:
        firstnameNode = utils.getFirstDescendant(i, "first-name")
        firstname = utils.getElementAsciiText(firstnameNode)
        lastnameNode = utils.getFirstDescendant(i, "last-name")
        lastname = utils.getElementAsciiText(lastnameNode)
        countryNode = utils.getFirstDescendant(i, "country")
        country = utils.getElementAsciiText(countryNode)
        cityNode = utils.getFirstDescendant(i, "city")
        city = utils.getElementAsciiText(cityNode)
        stateNode = utils.getFirstDescendant(i, 'state')
        state = utils.getElementAsciiText(stateNode)
        tempInventor = session.query(Inventor).filter(Inventor.lastname == lastname, Inventor.firstname == firstname, Inventor.city == city,Inventor.state == state, Inventor.country == country)
        if(tempInventor.count()==0):
            inventor = Inventor(lastname, firstname, city, state, country)
            session.add(inventor)
            inventorsList.append(inventor)
        else:
            inventorsList.append(tempInventor.first())
    for i in inventorsList:
        patent.inventors.append(i)
    session.add(patent)
    session.commit()
    print "Done"
    print
    num+=1;
