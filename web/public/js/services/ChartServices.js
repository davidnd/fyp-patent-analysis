(function(){
    angular.module('fyp').factory('ChartServices', function($http, Utils, $q){
        var chartType = "Pie Chart";
        var cpcLevel = "Sections";
        var timeseriesData = {};
        getTimeSeriesData();
        return {
            getChartType: function(){
                return chartType;
            },
            getCpcLevel: function() {
                return cpcLevel;
            },
            setChartType: function(type) {
                chartType = type;
            },
            setCpcLevel: function(argument) {
                cpcLevel = argument;
            }, 
            generatePieChartData: generatePieChartData,
            generateDrillDownData: generateDrillDownData,
            timeseriesData: function(){
                return timeseriesData;
            }
        }
        function generatePieChartData(data, name, drilldown){
            var pieData = [];
            var objectData = {
                name: name, 
                colorByPoint: true
            };
            for(var i=0; i<data.length;i++){
                var trimmedDes = Utils.trimDescription(data[i].description);
                var temp = {};
                temp.name = trimmedDes;
                temp.y = data[i].count;
                if(drilldown){
                    temp.drilldown = data[i].id;
                }
                pieData.push(temp)
            }
            objectData.data = pieData;
            return objectData;
        }
        function getDrillDownDataItem(Service, item, callback){
            var id = item.drilldown;
            var drillDataItem = {};
            drillDataItem.id = id;
            drillDataItem.name = item.name;
            drillDataItem.data = [];
            return Service.getDrillDown({id: id}).$promise.then(function(data){
                for(var j=0; j<data.length; j++){
                    var des = Utils.trimDescription(data[j].description);
                    drillDataItem.data.push([des, data[j].count]);
                }
                return callback(drillDataItem);
            });
        }
        function generateDrillDownData(Service, base, callback) {
            var promises = [];
            var drillDownData = [];
            angular.forEach(base, function(item, index){
                promises.push(getDrillDownDataItem(Service, item, function(data){
                    drillDownData.push(data);
                }));
            });
            $q.all(promises).then(function() {
                callback(drillDownData);
            });
        }
        function getTimeSeriesData(){
            $http.get('/timeseries/subclasses').then(function(data){
                var res = data.data;
                Utils.normalizeChartData(res);
                timeseriesData.subclassData = res;
            }, function(err){
                console.log("Error loading time series data");
            });

            $http.get('/timeseries/classes').then(function(data){
                var res = data.data;
                Utils.normalizeChartData(res);
                timeseriesData.classData = res;
            }, function(err){
                console.log("Error loading time series data");
            });

            $http.get('/timeseries/sections').then(function(data){
                var res = data.data;
                Utils.normalizeChartData(res);
                timeseriesData.sectionData = res;
            }, function(err){
                console.log("Error loading time series data");
            });
        }
    });
})();