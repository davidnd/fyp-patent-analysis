import xml.etree.ElementTree as ET
from nltk.corpus import stopwords
# import unicodedata, sys
from unidecode import unidecode
from model import *
import model, string

stopwordsList = set(stopwords.words('english'))
# table = dict.fromkeys(i for i in xrange(sys.maxunicode) if unicodedata.category(unichr(i)).startswith('P'))
table = string.maketrans(string.punctuation, ' '*len(string.punctuation))
session = Session()
def xmlSplitter(data, separator = lambda x: x.startswith("<?xml")):
    buff = []
    for line in data:
        if(separator(line)):
            if buff:
                yield "".join(buff)
                buff[:] = []
        buff.append(line)
    yield ''.join(buff)

def getElementText(element):
    if(element is None):
        return ""
    text = "".join(element.itertext())
    if(isinstance(text, unicode)):
        text = unidecode(text)
    text = text.translate(table)
    text = ''.join([i for i in text if not i.isdigit()])
    text = " ".join([i for i in text.lower().split() if i not in stopwordsList])
    return text
def getFirstDescendant(ancestor, name):
    descendant = list(ancestor.iter(name))
    if(len(descendant)):
        return descendant[0]
    else:
        return None
def getElementAsciiText(element):
    if(element is None):
        return ""
    text = element.text
    if(isinstance(text, unicode)):
        text = unidecode(text)
    return text
num = 0
for item in xmlSplitter(open('extract.xml', 'r')):
    docnumber = None
    abstract = None
    claimstext = None
    title = None
    description = None
    date = None
    cpccode = ""
    print "Indexing doc num ", num;
    root = ET.fromstring(item)
    # get abstract
    abstract = getElementText(root.find("abstract"))
    # print "ABSTRACT: " + abstract
    # get claims
    claimstext = getElementText(root.find("claims"))
    # print "CLAIM TEXT: " + claimstext
    # get title
    titleIter = list(root.iter("invention-title"))
    if(len(titleIter)):
        title = getElementText(titleIter[0])
        # print "TITLE: " + title
    # get description
    description = getElementText(root.find("description"))
    # print "DESCRIPTION length: ", len(description)
    # get document number and date
    appref = list(root.iter("application-reference"))
    if(len(appref)):
        docnumber = list(appref[0].iter("doc-number"))
        if(len(docnumber)):
            docnumber = docnumber[0].text
            print docnumber
            # print docnumber
        date = list(appref[0].iter('date'))
        if(len(date)):
            date = date[0].text
            date = date[:4] + "-" + date[4:6] + "-" + date[6:]
            # print date
    # get classification code
    cpclist = list(root.iter("classification-cpc"))
    for cpc in cpclist:
        section = cpc.find("section").text
        classlv = cpc.find("class").text
        subclass = cpc.find("subclass").text
        maingroup = cpc.find('main-group').text
        subgroup = cpc.find('subgroup').text
        cpccode = cpccode + section+classlv+subclass+maingroup+"/"+subgroup+";"
    # print cpccode
    if(session.query(Patent.id).filter(Patent.docnumber == docnumber).count()):
        print "Patent already indexed, move to next one"
        continue
    # print claimstext
    patent = Patent(docnumber, title, abstract, cpccode, claimstext, description, date)
    # get assignees
    assignees = list(root.iter("assignee"))
    assigneesList = []
    # print "Num of assignees: ", len(assignees)
    if(len(assignees) == 0):
        applicants = list(root.iter("us-applicant"))
        # print "Num of applicants: ", len(applicants)
        for app in applicants:
            attributes = app.attrib
            orgname = getFirstDescendant(app, "orgname")
            if(orgname is not None):
                name = getElementAsciiText(orgname)
            else:
                lastnameNode = getFirstDescendant(app, "last-name")
                lastname = getElementAsciiText(lastnameNode)

                firstnameNode = getFirstDescendant(app, "first-name")
                firstname = getElementAsciiText(firstnameNode)
                name = firstname + " " + lastname;
            countryNode = getFirstDescendant(app, "country")
            country = getElementAsciiText(countryNode)
            cityNode = getFirstDescendant(app, "city")
            city = getElementAsciiText(cityNode)
            tempAssignee = session.query(Assignee).filter(Assignee.orgname == name, Assignee.city == city, Assignee.country == country)
            if(tempAssignee.count()==0):
                applicant = Assignee(name, city, country)
                session.add(applicant)
                assigneesList.append(applicant)
            else:
                assigneesList.append(tempAssignee.first())
    else:
        for i in assignees:
            orgnameNode = getFirstDescendant(i, "orgname")
            orgname = getElementAsciiText(orgnameNode)
            countryNode = getFirstDescendant(i, "country")
            country = getElementAsciiText(countryNode)
            cityNode = getFirstDescendant(i, "city")
            city = getElementAsciiText(cityNode)
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
        firstnameNode = getFirstDescendant(i, "first-name")
        firstname = getElementAsciiText(firstnameNode)
        lastnameNode = getFirstDescendant(i, "last-name")
        lastname = getElementAsciiText(lastnameNode)
        countryNode = getFirstDescendant(i, "country")
        country = getElementAsciiText(countryNode)
        cityNode = getFirstDescendant(i, "city")
        city = getElementAsciiText(cityNode)
        stateNode = getFirstDescendant(i, 'state')
        state = getElementAsciiText(stateNode)
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
