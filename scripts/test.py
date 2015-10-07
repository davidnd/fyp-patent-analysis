from nltk.corpus import stopwords

stopwordsList = set(stopwords.words('english'))
stopwordsList.add('fig')
for w in stopwordsList:
    print w
print len(stopwordsList)