from unidecode import unidecode
from nltk.corpus import stopwords
import string

stopwordsList = set(stopwords.words('english'))
stopwordsList.add('fig')
table = string.maketrans(string.punctuation, ' '*len(string.punctuation))

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