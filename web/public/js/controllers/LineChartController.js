(function() {
    angular.module('fyp').controller('LineChartController', function($scope, ChartServices){
        angular.element(document).ready(function(){
            $scope.linechartElement = $('#line-chart');
        });
        $scope.getChartType = function(){
            return ChartServices.getChartType();
        }
        $scope.$watch(function(){return ChartServices.getCpcLevel();}, function(newVal){
            if($scope.getChartType() == "Line Chart"){
                displayChart(newVal);
            }
        }, true);
        $scope.$watch(function(){return ChartServices.getChartType();}, function(newVal){
            if(newVal == "Line Chart")
                displayChart(ChartServices.getCpcLevel());
        });

        function displayChart(cpcLevel){
            if(cpcLevel == "Classes"){
                $scope.showClass();
            }
            else if(cpcLevel == "Subclasses"){
                $scope.showSubclass();
            }
            else if(cpcLevel = "Section")
                $scope.showSection();
        }
        function insertChart(data, title){
            $("#line-chart").highcharts({
                chart: {
                    type: 'spline'
                },
                title: {
                    text: title
                },
                subtitle: {
                    text: 'Irregular time data in Highcharts JS'
                },
                xAxis: {
                    type: 'datetime',
                    title: {
                        text: 'Time line'
                    }
                },
                yAxis: {
                    title: {
                        text: 'Count'
                    },
                    min: 0
                },

                plotOptions: {
                    spline: {
                        marker: {
                            enabled: true
                        }
                    }
                },
                series: data
            });
        }
        $scope.showSection = function() {
            if(typeof $scope.sectionData == 'undefined'){
                $scope.sectionData = ChartServices.processLineChartData(ChartServices.timeseriesData().sectionData);
            }
            insertChart($scope.sectionData, "Patents in section");
        }
        $scope.showClass = function() {
            if(typeof $scope.classData == 'undefined'){
                $scope.classData = ChartServices.processLineChartData(ChartServices.timeseriesData().classData);
            }
            insertChart($scope.classData, "Patents in classes");
        }
        $scope.showSubclass = function() {
            if(typeof $scope.subclassData == 'undefined'){
                $scope.subclassData = ChartServices.processLineChartData(ChartServices.timeseriesData().subclassData);
            }
            insertChart($scope.subclassData, "Patents in sub-classes");
        }
    });
})();