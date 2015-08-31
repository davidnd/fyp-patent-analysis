from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Date, create_engine, MetaData, Text

engine = create_engine('mysql://root:root@localhost/patent')
Base = declarative_base()

class Patent(Base):
    __tablename__ = 'patents'
    id = Column(Integer, primary_key=True)
    docId = Column(String(20))
    title = Column(String(250))
    abstract = Column(Text)
    classificatonCode = Column(String(50))
    claims = Column(Text)
    summary = Column(Text)
    field = Column(Text)
    background = Column(Text)
    date = Column(Date)
    def __init__(self, docId, title, abstract, classificatonCode, claims, summary, background, date):
        self.docId = docId
        self.title = title
        self.abstract = abstract
        self.classificatonCode = classificatonCode
        self.claims = claims
        self.summary = summary
        self.background = background
        self.date = date


Base.metadata.create_all(engine)
