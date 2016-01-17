use patent;

create view patent_query as 
select patents.id, patents.title, patents.abstract, patents.cpccode, patents.claims, patents.description, patents.`date`, 
assignees.orgname, assignees.city, assignees.country,
GROUP_CONCAT(distinct concat(inventors.firstname, " ", inventors.lastname) separator ', ') as inventor_name, inventors.city as inventor_city, inventors.country as inventor_country
from patents
left join patent.patentinventor on patents.id = patentinventor.patentid
left join inventors on patentinventor.inventorid = inventors.id
left join patentassignee on patents.id = patentassignee.patentid
left join assignees on assignees.id = patentassignee.assigneeid
group by patents.id;