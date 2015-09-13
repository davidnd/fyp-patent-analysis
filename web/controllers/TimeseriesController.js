var models = require('../models');
exports.getSectionsTS = function (req, res) {
	models.sequelize.query("SELECT\
		subclassid, date\
		FROM patent.patents\
		LEFT JOIN (patent.patentsubclass)\
		ON patents.id = patentsubclass.patentid\
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
				var date = new Date(row.date).getTime();
				if(lastid == null){
					lastid = row.subclassid;
					currentid = lastid;
					obj.key = currentid;
					obj.values.push(date);
					data.push(obj);	
				}
				currentid = row.subclassid;
				if(currentid != lastid){
					obj = {};
					obj.values = [];
					lastid = currentid;
					obj.key = lastid;
					obj.values.push(date);
					data.push(obj);
				}else{
					obj.values.push(date);
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