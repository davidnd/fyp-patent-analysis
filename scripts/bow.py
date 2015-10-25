import MySQLdb as mysql
import MySQLdb.cursors
from sklearn.feature_extraction.text import CountVectorizer
import numpy


def getPatent(subclassid):
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
    with open('text.txt', 'w+') as f:
        f.write(temp)
    vectorizer = CountVectorizer(analyzer = "word",   \
                             tokenizer = None,    \
                             preprocessor = None, \
                             stop_words = None,   \
                             max_features = 5000)
    count_vector = vectorizer.fit_transform(textarray)
    count_vector = count_vector.toarray()
    vocab = vectorizer.get_feature_names()
    count = numpy.sum(count_vector, axis=0)
    d = {}
    for tag, count in zip(vocab, count):
        d[tag] = count
    return d
# # def writeToJson(array):

def test():
    read_conn = mysql.connect("localhost","root","root","patent")
    read_cursor = read_conn.cursor()
    sqlsubclass = "Select id, symbol, description from subclasses where count > 0"
    read_cursor.execute(sqlsubclass)
    subclasses = []
    subclasses = read_cursor.fetchall()
    vocab = buildVocabulary(getPatent(1))
    print vocab
    read_conn.close()

test()