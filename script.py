import xml.etree.ElementTree as ET
from nltk.corpus import stopwords
# import unicodedata, sys
from unidecode import unidecode

import model, string

stopwordsList = set(stopwords.words('english'))
# table = dict.fromkeys(i for i in xrange(sys.maxunicode) if unicodedata.category(unichr(i)).startswith('P'))
table = string.maketrans("", "")
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
        return None
    text = "".join(element.itertext())
    if(isinstance(text, unicode)):
        text = unidecode(text)
    text = text.translate(table, string.punctuation)
    text = ''.join([i for i in text if not i.isdigit()])
    text = " ".join([i for i in text.lower().split() if i not in stopwordsList])
    return text

count = 0
num = 0
for item in xmlSplitter(open('sampleapp.xml', 'r')):
    # print count
    count+=1
    if(count == 2):
        break
    root = ET.fromstring(item)
    abstract = getElementText(root.find("abstract"))
    print "ABSTRACT: " + abstract
    claimsText = getElementText(root.find("claims"))
    print "CLAIM TEXT: " + claimsText
    titleIter = list(root.iter("invention-title"))
    if(len(titleIter)):
        title = getElementText(titleIter[0])
        print "TITLE: " + title
    # pubRefIter = root.iter("publication-reference")
    # for child in pubRef:
    #     docId = child.iter("doc-number")
    #     for subchild in docId:
    #         print subchild.text
    #         break
    #     break
    #
    # description = root.find("description")
