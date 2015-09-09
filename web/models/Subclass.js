module.exports = function(sequelize, DataTypes){
	var Subclass = sequelize.define("subclass", {
		id: {type: DataTypes.INTEGER, primaryKey:true},
		symbol: DataTypes.STRING,
		description: DataTypes.STRING,
		count: DataTypes.STRING,
		class_id: DataTypes.INTEGER
	},{
		timestamps: false,
		tableName: 'subclasses'
	});
	return Subclass;
};