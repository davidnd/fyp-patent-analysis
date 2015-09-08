USE patent;
drop trigger if exists patent.update_count_subclass; 
DELIMITER //
create trigger update_count_subclass after UPDATE on patent.subclasses
FOR EACH ROW
BEGIN
	DECLARE diff integer;
	set @diff := NEW.count - OLD.count;
    IF @diff > 0 THEN
		UPDATE patent.classes SET count=count+@diff WHERE id = NEW.class_id;
	END IF;
END;//
DELIMITER ;

    
