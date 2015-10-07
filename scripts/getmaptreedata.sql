select patent.sections.id, patent.sections.description, patent.subsections.description, patent.classes.description, 
patent.subclasses.description, patent.sections.count as section_count, subsections.count as subsec_count,
patent.classes.count as class_count, patent.subclasses.count as subclass_count from 
patent.patentsubclass
left join patent.subclasses on patentsubclass.subclassid = subclasses.id
left join patent.classes on classes.id = subclasses.class_id
left join patent.subsections on subsections.id = classes.subsection_id
left join patent.sections on sections.id = subsections.section_id