SELECT
subclassid, date, subclasses.description
FROM patent.patents
LEFT JOIN (patent.patentsubclass)
ON patents.id = patentsubclass.patentid
LEFT JOIN (patent.subclasses)
ON subclasses.id = patentsubclass.subclassid
WHERE subclassid IS NOT NULL AND subclassid IN (
	SELECT * FROM(
		SELECT id FROM patent.subclasses
		ORDER BY count
		DESC LIMIT 10
		) 
	AS temp
)