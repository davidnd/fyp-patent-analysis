USE patent;
drop trigger if exists patent.update_count_subsection; 
DELIMITER //
create trigger update_count_subsection after UPDATE on patent.subsections
FOR EACH ROW
BEGIN
	DECLARE diff integer;
	set @diff := NEW.count - OLD.count;
    IF @diff > 0 THEN
		UPDATE patent.sections SET count=count+@diff WHERE id = NEW.section_id;
	END IF;
END;//
DELIMITER ;

    
