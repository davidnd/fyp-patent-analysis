USE patent;
drop trigger if exists patent.update_count_class; 
DELIMITER //
create trigger update_count_class after UPDATE on patent.classes
FOR EACH ROW
BEGIN
	DECLARE diff integer;
	set @diff := NEW.count - OLD.count;
    IF @diff > 0 THEN
		UPDATE patent.subsections SET count=count+@diff WHERE id = NEW.subsection_id;
	END IF;
END;//
DELIMITER ;

    
