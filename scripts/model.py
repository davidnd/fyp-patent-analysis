from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Table, Column, Integer, String, Date, create_engine, MetaData, Text, ForeignKey
from sqlalchemy.orm import relationship, sessionmaker
from sqlalchemy.dialects.mysql import LONGTEXT

engine = create_engine('mysql://root:root@localhost/patent')
Base = declarative_base()
Session = sessionmaker(bind=engine)

patentassignee = Table('patentassignee', Base.metadata,
    Column('patentid', Integer, ForeignKey('patents.id')),
    Column('assigneeid', Integer, ForeignKey('assignees.id'))
)

patentinventor = Table('patentinventor', Base.metadata,
    Column('patentid', Integer, ForeignKey('patents.id')),
    Column('inventorid', Integer, ForeignKey('inventors.id'))
)

patentsubclass = Table('patentsubclass', Base.metadata,
    Column('patentid', Integer, ForeignKey('patents.id')),
    Column('subclassid', Integer, ForeignKey('subclasses.id'))
)

class Patent(Base):
    __tablename__ = 'patents'
    id = Column(Integer, primary_key=True)
    docnumber = Column(String(20))
    title = Column(String(250))
    abstract = Column(Text)
    cpccode = Column(String(250))
    claims = Column(LONGTEXT)
    description = Column(LONGTEXT)
    date = Column(Date)
    assignees = relationship("Assignee", secondary=patentassignee)
    inventors = relationship("Inventor", secondary=patentinventor)
    subclasses = relationship('Subclass', secondary=patentsubclass)
    def __init__(self, docnumber, title, abstract, cpccode, claims, description, date):
        self.docnumber = docnumber[:20]
        self.title = title[:250]
        self.abstract = abstract
        self.cpccode = cpccode[:250]
        self.claims = claims
        self.description = description
        self.date = date

class Inventor(Base):
    __tablename__ = 'inventors'
    id = Column(Integer, primary_key=True)
    lastname = Column(String(50))
    firstname = Column(String(50))
    city = Column(String(50))
    state = Column(String(50))
    country = Column(String(50))
    def __init__(self, lastname, firstname, city, state, country):
        self.lastname = lastname[:50]
        self.firstname = firstname[:50]
        self.city = city[:50]
        self.state = state[:50]
        self.country = country[:50]


class Assignee(Base):
    __tablename__ = "assignees"
    id = Column(Integer, primary_key=True)
    orgname = Column(String(100))
    city = Column(String(50))
    country = Column(String(50))
    def __init__(self, orgname, city, country):
        self.orgname = orgname[:100]
        self.city = city[:50]
        self.country = country[:50]

class Section(Base):
    __tablename__ = "sections"
    id = Column(Integer, primary_key=True)
    symbol=Column(String(1))
    description=Column(String(100))
    count=Column(Integer)
    subsections=relationship("Subsection")
    def __init__(self, symbol, desc):
        self.symbol = symbol
        self.description = desc
        self.count = 0

class Subsection(Base):
    __tablename__ = "subsections"
    id = Column(Integer, primary_key=True)
    section_id = Column(Integer, ForeignKey('sections.id'))
    symbol=Column(String(3))
    description=Column(String(100))
    count=Column(Integer)
    classes=relationship("Class")
    def __init__(self, symbol, desc):
        self.symbol = symbol
        self.description = desc
        self.count = 0

class Class(Base):
    __tablename__ = "classes"
    id = Column(Integer, primary_key=True)
    subsection_id = Column(Integer, ForeignKey('subsections.id'))
    symbol=Column(String(3))
    description=Column(Text)
    count=Column(Integer)
    subclasses=relationship('Subclass')
    def __init__(self, symbol, desc):
        self.symbol = symbol
        self.description = desc
        self.count = 0

class Subclass(Base):
    __tablename__="subclasses"
    id = Column(Integer, primary_key=True)
    class_id=Column(Integer, ForeignKey('classes.id'))
    symbol=Column(String(4))
    description=Column(Text)
    count=Column(Integer)
    def __init__(self, symbol, desc):
        self.symbol = symbol
        self.description = desc
        self.count = 0

Base.metadata.create_all(engine, checkfirst=True)
