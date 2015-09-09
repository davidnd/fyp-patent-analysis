import xml.etree.ElementTree as ET
from os import path
import utils, sys
from sqlalchemy.orm import sessionmaker
from sqlalchemy import create_engine
from model import Section, Subsection, Class, Subclass

engine = create_engine('mysql://root:root@localhost/patent')
Session = sessionmaker(bind=engine)
session = Session()

script_dir = path.dirname(__file__)
rel_path = "cpcschema/"
filename = "cpc-scheme-Y.xml"

def getTitleText(node):
	titlepart = node.findall('title-part')
	parts = []
	for part in titlepart:
		text = utils.getElementAsciiText(part.find('text'))
		parts.append(text)
	return ";".join(parts)

abs_path = path.join(script_dir, rel_path)
schema = open(abs_path + filename, 'r')
schematext = schema.read()
root = ET.fromstring(schematext)

sectionnode = root.find("classification-item")
sectionsymbol = sectionnode.find("classification-symbol").text

section = session.query(Section).filter(Section.symbol == sectionsymbol).first();
sectionid = section.id;

check = session.query(Subsection).filter(Subsection.section_id == sectionid);
if(check.count()):
	print "This document is already indexed. script stopping..."
	sys.exit()

subsectionlist = sectionnode.findall('classification-item')
for sub in subsectionlist:
	# symbol
	subsectionsymbol = sub.find('classification-symbol').text
	# description
	titlenode = utils.getFirstDescendant(sub, "class-title")

	titletext = getTitleText(titlenode)
	# create a new subsection
	subsection = Subsection(subsectionsymbol, titletext)
	# many to one rela
	section.subsections.append(subsection)

	classlist = sub.findall('classification-item')
	for clas in classlist:
		classsymbol = clas.find("classification-symbol").text
		classtitle = clas.find('class-title')
		classdesc = getTitleText(classtitle)
		print classdesc

		newclass = Class(classsymbol, classdesc)

		subclasslist = clas.findall('classification-item')

		for subclass in subclasslist:
			subclasssymbol = subclass.find('classification-symbol').text
			subclasstitle = subclass.find('class-title')
			subclassdesc = getTitleText(subclasstitle)

			newsubclass = Subclass(subclasssymbol, subclassdesc)
			session.add(newsubclass)
			newclass.subclasses.append(newsubclass)

		session.add(newclass)
		subsection.classes.append(newclass)
	session.add(subsection)
	section.subsections.append(subsection)
session.add(section)
session.commit()

