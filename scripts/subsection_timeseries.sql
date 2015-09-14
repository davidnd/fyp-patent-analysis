
SELECT
	subsections.id as id, date, subsections.description
	FROM patent.patents
        LEFT JOIN (patent.patentsubclass)
        ON patents.id = patentsubclass.patentid
        LEFT JOIN (patent.subclasses)
        ON subclasses.id = patentsubclass.subclassid
        LEFT JOIN (patent.classes)
        ON classes.id = subclasses.class_id
        LEFT JOIN (patent.subsections)
        ON subsections.id = classes.subsection_id
	WHERE subclassid IS NOT NULL
        AND subsections.id IN (
			SELECT * FROM(
				SELECT id FROM patent.subsections
				ORDER BY count
				DESC LIMIT 10
				) 
			AS temp)
        order by subsections.id