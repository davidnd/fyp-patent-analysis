var models = require('../models');
function processData(results){
	var dateformat = "yyyy-mm";
	console.log("Processing data....");
	var data = [];
	var lastid=null;
	var currentid=null;
	var obj = {};
	obj.values = [];
	for(var i=0; i<results.length; i++){
		var row = results[i];
		var description = row.description;
		var date = row.date;
		var year = date.getFullYear();
		var month = ("0" + (date.getMonth() + 1)).slice(-2);
		//yearly
		// var date = year + '-' + '12' + '-' + '31';
		//monthly
		var date = year + '-' + month;
		date = new Date(date).getTime();
		if(lastid == null){
			lastid = row.id;
			currentid = lastid;
			obj.key = description;
			var point = [date, 1];
			obj.values.push(point);
			data.push(obj);
		}
		currentid = row.id;
		//new subclass
		if(currentid != lastid){
			obj = {};
			obj.values = [];
			lastid = currentid;
			obj.key = description;
			var point = [date, 1];
			obj.values.push(point);
			data.push(obj);
		}else{
			//same as latest subclass
			for(var j=0; j<obj.values.length; j++){
				var point = obj.values[j];
				//time already in values
				if(point[0] == date){
					point[1]++;
					break;
				}
			}
			//new time, add a new point
			if(j==obj.values.length){
				var point = [date, 1];
				obj.values.push(point);
			}
		}
	}
	return data;
}
exports.getSubclassTS = function (req, res) {
	models.sequelize.query("SELECT\
		subclassid as id, date, subclasses.description\
		FROM patent.patents\
		LEFT JOIN (patent.patentsubclass)\
		ON patents.id = patentsubclass.patentid\
		LEFT JOIN (patent.subclasses)\
        ON subclasses.id = patentsubclass.subclassid\
		WHERE subclassid IS NOT NULL AND subclassid IN (\
			SELECT * FROM(\
				SELECT id FROM patent.subclasses\
				ORDER BY count\
				DESC LIMIT 10\
				) AS temp\
		)"
		,{type: models.sequelize.QueryTypes.SELECT}
		).then(function(results){
			console.log(results.length);
			res.send(processData(results));
		}).catch(function(err){
			res.render('error', {
				message: err.message,
				error: err
			});
		});
}
exports.getSectionTS = function (req, res) {
	models.sequelize.query("SELECT\
		sections.id as id, date, sections.description\
		FROM patent.patents\
        LEFT JOIN (patent.patentsubclass)\
        ON patents.id = patentsubclass.patentid\
        LEFT JOIN (patent.subclasses)\
        ON subclasses.id = patentsubclass.subclassid\
        LEFT JOIN (patent.classes)\
        ON classes.id = subclasses.class_id\
        LEFT JOIN (patent.subsections)\
        ON subsections.id = classes.subsection_id\
        LEFT JOIN (patent.sections)\
        ON sections.id = subsections.section_id\
		WHERE subclassid IS NOT NULL\
		ORDER BY sections.id", 
		{type: models.sequelize.QueryTypes.SELECT}
		).then(function(results){
			console.log(results.length);
			res.send(processData(results));
		}).catch(function(err){
			res.render('error', {
				message: err.message,
				error: err
			});
		});

}
