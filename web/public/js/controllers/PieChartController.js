(function(){
    angular.module('fyp').controller('PieChartController', function($scope, ChartServices, Section, Subsection, Class, Subclass){
        angular.element(document).ready(function(){
            $scope.pieChartContext = $('#pie-chart');
        });
        Section.query(function(sections){
            $scope.sectionData = ChartServices.generatePieChartData(sections);
            $scope.showSection();
        });
        Subsection.query(function(subsecs){
            $scope.subsectionData = ChartServices.generatePieChartData(subsecs);
        });
        Class.query(function(classes){
            $scope.classData = ChartServices.generatePieChartData(classes);
        });
        Subclass.query(function(subclasses){
            $scope.subclassData = ChartServices.generatePieChartData(subclasses);
        });
        $scope.getChartType = function() {
            return ChartServices.getChartType();
        }
        $scope.showSection = function(){
            var chart = new Highcharts.Chart({
                chart:{
                    renderTo: 'pie-chart',
                    type: 'pie'
                },
                title:{
                    text: 'Patents grouped by sections'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: false
                        },
                        showInLegend: true
                    }
                },
                series: $scope.sectionData
            });
        }
    });
})();