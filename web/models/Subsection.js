module.exports = function(sequelize, DataTypes){
	var Subsection = sequelize.define("subsection", {
		id: {type: DataTypes.INTEGER, primaryKey:true},		
		symbol: DataTypes.STRING,
		description: DataTypes.STRING,
		count: DataTypes.STRING,
		section_id: DataTypes.INTEGER
	},{
		timestamps: false,
		tableName: 'subsections'
	});
	return Subsection;
};