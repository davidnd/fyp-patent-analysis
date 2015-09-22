(function(){
    angular.module('fyp').factory('ChartServices', function($http, Utils){
        var chartType = "Pie Chart";
        var cpcLevel = "Sections";
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
            generatePieChartData: generatePieChartData
        }
        function generatePieChartData(data){
            var pieData = [];
            var objectData = {
                name: "Count",
                colorByPoint: true
            };
            for(var i=0; i<data.length;i++){
                var temp = {};
                temp.name = Utils.trimDescription(data[i].description);
                temp.y = data[i].count;
                if(i==0){
                    temp.sliced = true;
                    temp.selected = true;
                }
                pieData.push(temp)
            }
            objectData.data = pieData;
            return [objectData];
        }
    });
})();