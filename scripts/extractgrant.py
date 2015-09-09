import xml.etree.ElementTree as ET
def xmlSplitter(data, separator = lambda x: x.startswith("<?xml")):
    buff = []
    for line in data:
        if(separator(line)):
            if buff:
                yield "".join(buff)
                buff[:] = []
        buff.append(line)
    yield ''.join(buff)

docno = "14394189"

for item in xmlSplitter(open('data.xml', 'r')):
    root = ET.fromstring(item)
    export = open('extract.xml', 'w+')
    appref = list(root.iter("application-reference"))
    if(len(appref)):
        docnumber = list(appref[0].iter("doc-number"))
        if(len(docnumber)):
            docnumber = docnumber[0].text
            if(docnumber == docno):
                export.write(item)
                break
