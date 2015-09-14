var models = require('../models');
exports.getSectionsTS = function (req, res) {
	models.sequelize.query("SELECT\
		subclassid, date, subclasses.description\
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
				var date = year + '-' + month;
				date = new Date(date).getTime();
				if(lastid == null){
					lastid = row.subclassid;
					currentid = lastid;
					obj.key = description;
					var point = [date, 1];
					obj.values.push(point);
					data.push(obj);
				}
				currentid = row.subclassid;
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
			res.send(data);
		}).catch(function(err){
			res.render('error', {
				message: err.message,
				error: err
			});
		});
	}