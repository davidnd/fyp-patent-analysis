(function(){
    angular.module('fyp').controller('PieChartController', function($scope, ChartServices, Section, Subsection, Class, Subclass){
        angular.element(document).ready(function(){
            $scope.piechartElement = $('#pie-chart');
        });
        Section.query(function(sections){
            $scope.sectionData = ChartServices.generatePieChartData(sections, "Count in sections", true);
            ChartServices.generateDrillDownData(Section, $scope.sectionData.data, callback);
            function callback(data){
                $scope.sectionDrillDownData = data;
                $scope.showSection();
            }
        });
        // Subsection.query(function(subsecs){
        //     $scope.subsectionData = ChartServices.generatePieChartData(subsecs);
        // });
        Class.query(function(classes){
            $scope.classData = ChartServices.generatePieChartData(classes, "Count in classes", true);
            ChartServices.generateDrillDownData(Class, $scope.classData.data, callback);
            function callback(data){
                $scope.classDrillDownData = data;
            }
        });
        Subclass.query(function(subclasses){
            $scope.subclassData = ChartServices.generatePieChartData(subclasses, "Count in subclasses", false);
        });
        $scope.getChartType = function() {
            return ChartServices.getChartType();
        }
        $scope.$watch(function(){return ChartServices.getCpcLevel();}, function(newVal){
            if($scope.getChartType() == "Pie Chart"){
                displayChart(newVal);
            }
        }, true);
        $scope.$watch(function(){return ChartServices.getChartType();}, function(newVal){
            if(newVal == "Pie Chart")
                displayChart(ChartServices.getCpcLevel());
        });
        function displayChart(newVal){
            if(newVal == "Classes"){
                $scope.showClass();
            }
            else if(newVal == "Subclasses"){
                $scope.showSubclass();
            }
            else if(newVal = "Section")
                $scope.showSection();
        }
        function insertChart (data, drilldown, title) {
            $scope.piechart = new Highcharts.Chart({
                chart:{
                    renderTo: 'pie-chart',
                    type: 'pie'
                },
                title:{
                    text: 'Patents grouped by ' + title
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
                series: [data],
                drilldown: {
                    series: drilldown
                }
            });
        }
        $scope.showSection = function(){
            insertChart($scope.sectionData, $scope.sectionDrillDownData, 'sections');
        }
        $scope.showClass = function() {
            insertChart($scope.classData, $scope.classDrillDownData, "classes");
        }
        $scope.showSubclass = function(){
            insertChart($scope.subclassData, null, "sub-classes");
        }

    });
})();