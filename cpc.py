import xml.etree.ElementTree as ET
from os import path
import utils
script_dir = path.dirname(__file__)
rel_path = "cpcschema/"
filename = "cpc-schema-A.xml"

abs_path = path.join(script_dir, rel_path)
f = open(abs_path, 'r')




