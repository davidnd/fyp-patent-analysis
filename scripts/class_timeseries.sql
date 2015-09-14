
SELECT
		classes.id as id, date, classes.description
		FROM patent.patents
        LEFT JOIN (patent.patentsubclass)
        ON patents.id = patentsubclass.patentid
        LEFT JOIN (patent.subclasses)
        ON subclasses.id = patentsubclass.subclassid
        LEFT JOIN (patent.classes)
        ON classes.id = subclasses.class_id
		WHERE subclassid IS NOT NULL
        AND classes.id IN (
			SELECT * FROM(
				SELECT id FROM patent.classes
				ORDER BY count
				DESC LIMIT 10
				) 
			AS temp)
        order by classes.id