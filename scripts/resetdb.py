from sqlalchemy import Table, Column, Integer, String, Date, create_engine, MetaData, delete
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

engine = create_engine('mysql://root:root@localhost/patent')
Base = declarative_base()
Session = sessionmaker(bind=engine)
session = Session()
meta = MetaData()
patentassignee = Table('patentassignee', meta, autoload=True, autoload_with=engine)
patentinventor = Table('patentinventor', meta, autoload=True, autoload_with=engine)
patent = Table('patents', meta, autoload=True, autoload_with=engine)
assignees = Table('assignees', meta, autoload=True, autoload_with=engine)
inventors = Table('inventors', meta, autoload=True, autoload_with=engine)

stmt1 = patentassignee.delete()
stmt2 = patentinventor.delete()
stmt3 = patent.delete()
stmt4 = assignees.delete()
stmt5 = inventors.delete()

session.execute(stmt1)
session.execute(stmt2)
session.execute(stmt3)
session.execute(stmt4)
session.execute(stmt5)
session.commit()

patentassignee.drop(engine, checkfirst=True)
patentinventor.drop(engine, checkfirst=True)
patent.drop(engine, checkfirst=True)
assignees.drop(engine, checkfirst=True)
inventors.drop(engine, checkfirst=True)
