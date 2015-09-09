module.exports = function(sequelize, DataTypes){
	var Class = sequelize.define("Class", {
		id: {type: DataTypes.INTEGER, primaryKey:true},
		symbol: DataTypes.STRING,
		description: DataTypes.STRING,
		count: DataTypes.STRING,
		subsection_id: DataTypes.INTEGER
	},{
		timestamps: false,
		tableName: 'classes'
	});
	return Class;
};