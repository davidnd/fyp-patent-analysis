from sqlalchemy.orm import sessionmaker
from sqlalchemy import create_engine
from model import Patent, Section, Subsection, Class, Subclass

engine = create_engine('mysql://root:root@localhost/patent')
Session = sessionmaker(bind=engine)
session = Session()

patents = session.query(Patent);
num = 0
for patent in patents:
	id = patent.id
	cpccode = patent.cpccode
	print cpccode
	if(cpccode):
		cpccodes = [x[:4] for x in cpccode.split(';') if x]
		cpccodes = set(cpccodes)
		for code in cpccodes:
			print code
			subclass = session.query(Subclass).filter(Subclass.symbol == code).first()
			# add sublcass for patent
			patent.subclasses.append(subclass)
			subclass.count+=1
			session.add(subclass)
	session.add(patent)
	num+=1
	if(num==10):
		break
session.commit()





