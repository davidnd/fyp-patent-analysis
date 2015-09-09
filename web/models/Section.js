module.exports = function(sequelize, DataTypes){
	var Section = sequelize.define("Section", {
		id: {type: DataTypes.INTEGER, primaryKey:true},
		symbol: DataTypes.STRING,
		description: DataTypes.STRING,
		count: DataTypes.STRING
	},{
		timestamps: false,
		tableName: 'sections'
	});
	return Section;
};