import MySQLdb as mysql
import MySQLdb.cursors
import json
from sklearn.feature_extraction.text import CountVectorizer
import numpy
from decimal import Decimal
from nltk.corpus import stopwords
import math
stopwordsList = set(stopwords.words('english'))
stopwordsList.add('fig')
stopwordsList.add('figs')

def getPatent(subclass):
    subclassid = subclass[0]
    # read_conn = mysql.connect("localhost","root","root","patent",cursorclass = MySQLdb.cursors.SSCursor)
    read_conn = mysql.connect("localhost","root","root","patent")
    read_cursor = read_conn.cursor()
    sqlpatent_subclass = "SELECT title, abstract, claims, description FROM patents where id in (select patentid from patentsubclass where subclassid=%d)" % (subclassid)
    read_cursor.execute(sqlpatent_subclass)
    results = read_cursor.fetchall()
    read_conn.close()
    return results

def buildVocabulary(patents):
    textarray = []
    temp = ""
    for p in patents:
        text = p[0] + " " + p[1] + " " + p[2] + " " + p[3]
        textarray.append(text)
        temp = temp + text + " "
    vectorizer = CountVectorizer(analyzer = "word",   \
                             tokenizer = None,    \
                             preprocessor = None, \
                             stop_words = None, \
                             max_features = 1000)
    count_vector = vectorizer.fit_transform(textarray)
    print count_vector.shape
    count_vector = count_vector.toarray()
    vocab = vectorizer.get_feature_names()
    count = numpy.sum(count_vector, axis=0)
    d = {}
    for tag, count in zip(vocab, count):
        d[tag] = count
    return d

json_array = []

try:
    with open('vocabulary.json') as jsonfile:
        json_array = json.load(jsonfile)
except Exception as e:
    print e
classcount = 0
def writeToJson(Class, vocab):
    global json_array
    global classcount
    json_data = {}
    json_data['class'] = Class[1]
    json_data['vocabulary'] = vocab
    for index, val in enumerate(json_array):
        if(val['class'] == Class[1]):
            del json_array[index]
            break
    classcount+=1
    json_array.append(json_data)
    print classcount

def generateGlobalVocabulary():
    json_array = []
    with open('vocabulary.json') as f:
        json_array = json.load(f) 
    vocab = {}
    for v in json_array:
        d = v['vocabulary']
        vocab.update(d)
    print len(vocab)
def test():
    global json_array
    read_conn = mysql.connect("localhost","root","root","patent")
    read_cursor = read_conn.cursor()
    sqlsubclass = "Select id, symbol, description from subclasses where count > 0"
    read_cursor.execute(sqlsubclass)
    subclasses = []
    subclasses = read_cursor.fetchall()
    for sub in subclasses:
        vocab = buildVocabulary(getPatent(sub))
        writeToJson(sub, vocab)
    with open('vocabulary.json', 'w+') as f:
        json.dump(json_array, f)
    generateGlobalVocabulary()
    read_conn.close()

def updatePriorP():
    json_array = []
    read_conn = mysql.connect("localhost","root","root","patent")
    read_cursor = read_conn.cursor()
    total = 0
    totalcountsql = "select sum(count) from patent.subclasses"
    read_cursor.execute(totalcountsql)
    total = read_cursor.fetchone()
    total = total[0]
    with open('vocabulary.json') as f:
        json_array = json.load(f)
    for i in json_array:
        cname = i['class']
        sql = "select count from subclasses where symbol = '%s'"%(cname)
        read_cursor.execute(sql)
        count = read_cursor.fetchone()[0]
        i['p'] = str(count/(1*total))
    with open('vocabulary.json', 'w+') as f:
        json.dump(json_array, f)
    read_conn.close()
def update_total_word_count():
    with open('vocabulary.json') as f:
        json_array = json.load(f)
    for i in json_array:
        total=0
        vocab = i['vocabulary']
        for key in vocab:
            total+=vocab[key]
        i['total_words'] = total
    with open('vocabulary.json', 'w+') as f:
        json.dump(json_array, f)
def classify(text):
    V = 25349
    textarray = []
    textarray.append(text)
    vectorizer = CountVectorizer(analyzer = "word",   \
                             tokenizer = None,    \
                             preprocessor = None, \
                             stop_words = stopwordsList, \
                             max_features = 1000)
    count_vector = vectorizer.fit_transform(textarray)
    count_vector = count_vector.toarray()
    words = vectorizer.get_feature_names()
    text = {}
    for tag, count in zip(words, count_vector[0]):
        text[tag] = count
    print text
    # load vocabulary
    prob = []
    json_array = []
    with open('vocabulary.json') as f:
        json_array = json.load(f)
    for c in json_array:
        vocab = c['vocabulary']
        classname = c['class']
        prior_p = Decimal(c['p'])
        total = c['total_words']
        d = {}
        d['class'] = classname
        likelihood_p = 0
        for key in text:
            # print key
            # occurences in class c
            occurences = 0
            try:
                occurences = vocab[key]
            except Exception as e:
                pass
            term_likelihood_p = (occurences + 1) / float(total + V)
            # occurences of key in document
            power = text[key]
            term_likelihood_p = power * math.log10(Decimal(term_likelihood_p))
            likelihood_p += term_likelihood_p
        probability = math.log10(prior_p) + likelihood_p
        d['probability'] = probability
        prob.append(d)
    prob.sort(key= lambda Class: (Class['probability']), reverse=True)
    print prob
    result = prob[:10]
    for r in result:
        print "class: ", r['class'], " Prob: ", r['probability']

with open('text.txt') as patent:
    text = patent.read()
    classify(text)



            

