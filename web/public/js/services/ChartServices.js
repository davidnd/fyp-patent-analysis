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
            }, 
            processStackedAreaChartData: processStackedAreaChartData,
            processLineChartData: processLineChartData
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
                timeseriesData.subclassData = res;
            }, function(err){
                console.log("Error loading time series data");
            });

            $http.get('/timeseries/classes').then(function(data){
                var res = data.data;
                timeseriesData.classData = res;
            }, function(err){
                console.log("Error loading time series data");
            });

            $http.get('/timeseries/sections').then(function(data){
                var res = data.data;
                timeseriesData.sectionData = res;
            }, function(err){
                console.log("Error loading time series data");
            });
        }
        function processStackedAreaChartData(data){
            var startingDate = new Date("Jan-01-2005").getTime();
            // var startingDate = 1072915200000;
            var timeArray = [];
            var results = [];
            for(var i = 0; i<data.length; i++){
                var values = data[i].values;
                for(var j = 0; j<values.length; j++){
                    var point = values[j];
                    if(timeArray.indexOf(point[0])>-1 || parseInt(point[0]) < startingDate){
                        continue;
                    }
                    timeArray.push(point[0]);
                }
            }
            timeArray = timeArray.sort(function(x, y){
                return parseInt(x) - parseInt(y); 
            });
            for(var k=0; k<data.length; k++){
                var object = {};
                object.key = Utils.trimDescription(data[k].key);
                var values = data[k].values;
                var normalizedValues = [];
                for(var j=0; j<timeArray.length; j++){
                    var checker = checkTimeInValues(timeArray[j], values);
                    if(checker.result){
                        normalizedValues.push([timeArray[j], values[checker.index][1]]);
                    }else{
                        normalizedValues.push([timeArray[j], 0]);
                    }
                }
                object.values = normalizedValues;
                results.push(object);
            }
            return results;
        };
        function checkTimeInValues(time, values){
            for(var i=0; i<values.length;i++){
                var point = values[i];
                if(point[0]==time){
                    return {result: true, index: i};
                }
            }
            return {result:false, index: -1};
        };

        function processLineChartData(data){
            var startingDate = new Date("Jan-01-2005").getTime();
            var results = [];
            for(var i=0; i<data.length; i++){
                var object = {};
                var objectData = [];
                object.name = Utils.trimDescription(data[i].key);
                for(var j=0; j<data[i].values.length; j++){
                    var temp = data[i].values[j];
                    var year = new Date(temp[0]).getFullYear();
                    year = year + '-' + '12' + '-' + '31';
                    var newDate = new Date(year).getTime();
                    if(newDate > startingDate)
                        objectData.push([newDate, temp[1]]);
                }
                objectData = objectData.sort(function(x, y){
                    return parseInt(x[0]) - parseInt(y[0]);
                });
                var yearlyObject = [];
                var yearlyData = [];
                var currentYear = null;
                for(var k=0; k<objectData.length; k++){
                    var temp = objectData[k];
                    if(currentYear == null||currentYear !=temp[0]){
                        if(currentYear != null){
                            yearlyData.push(yearlyObject);
                            yearlyObject = [];
                        }
                        currentYear = temp[0];
                        yearlyObject[0] = currentYear;
                        yearlyObject[1] = temp[1];
                    }else{
                        yearlyObject[1]+=temp[1];
                    }
                }
                yearlyData.push(yearlyObject);
                object.data = yearlyData;
                results.push(object);
            }
            return results;
        }
    });
})();