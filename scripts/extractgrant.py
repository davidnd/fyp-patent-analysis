import xml.etree.ElementTree as ET
import os, sys
def xmlSplitter(data, separator = lambda x: x.startswith("<?xml")):
    buff = []
    for line in data:
        if(separator(line)):
            if buff:
                yield "".join(buff)
                buff[:] = []
        buff.append(line)
    yield ''.join(buff)

docno = "29313375"
filename = "ipg110104.xml"
basedir = os.path.dirname(__file__)
relpath = "../data"
filepath = os.path.join(basedir, relpath, filename)

for item in xmlSplitter(open(filepath, 'r')):
    root = ET.fromstring(item)
    export = open('extract.xml', 'w+')
    appref = list(root.iter("application-reference"))
    if(len(appref)):
        docnumber = list(appref[0].iter("doc-number"))
        if(len(docnumber)):
            docnumber = docnumber[0].text
            if(docnumber == docno):
                export.write(item)
                print 'Found!'
                sys.exit()
print "Not found"