var Subclass = require('./Subclass');
module.exports = function(sequelize, DataTypes){
	var Patent = sequelize.define("Patent", {
		id: {type: DataTypes.INTEGER, primaryKey:true},
		docnumber: DataTypes.INTEGER,
		title: DataTypes.STRING,
		abstract: DataTypes.TEXT,
		cpccode: DataTypes.STRING,
		claims: DataTypes.TEXT,
		description: DataTypes.TEXT,
		date: DataTypes.DATE
	},{
		timestamps: false,
		tableName: 'patents'
	});
	return Patent;
};