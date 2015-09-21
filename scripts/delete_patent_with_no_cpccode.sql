-- select * from patent.patents where cpccode=""
delete from patent.patentinventor where patentid in (
	select id from patent.patents where cpccode=""
);
delete from patent.patentassignee where patentid in (
	select id from patent.patents where cpccode=""
);

delete from patent.patents where cpccode="" 

-- select count(*) from patent.patents where cpccode="" 