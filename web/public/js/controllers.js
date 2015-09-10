(function(){
	angular.module('fyp').controller('IndexController', function($scope, Section, Subsection, Class, Subclass, Utils){
    	$scope.tabdata = {};
    	Section.query(function(sections){
    		// var ctx = document.getElementById("sectioncv").getContext("2d");
    		$scope.tabdata.sections = Utils.generatePieChartData(sections);
    		$scope.showtab(1);
    	});
    	Subsection.query(function(subsecs){
    		$scope.tabdata.subsecs = Utils.generatePieChartData(subsecs);
    	});
    	Class.query(function(classes){
    		$scope.tabdata.classes = Utils.generatePieChartData(classes);
    	});
    	Subclass.query(function(subclasses){
    		$scope.tabdata.subclasses = Utils.generatePieChartData(subclasses);
    	});
    	$scope.showtab = function(tab){
    		console.log(tab);
    		$scope.selected = tab;
    		$("#tab-div>div.active").removeClass('active');
    		if(tab == 1){
    			$("#section-div").addClass('active');
    			var ctx = document.getElementById("tab1").getContext("2d");
    			var chart = new Chart(ctx).Pie($scope.tabdata.sections);
    		}
    		if(tab == 2){
    			$("#subsec-div").addClass('active');
    			var ctx = document.getElementById("tab2").getContext("2d");
    			var chart = new Chart(ctx).Pie($scope.tabdata.subsecs);
    		}
    		if(tab == 3){
    			$("#class-div").addClass('active');
    			var ctx = document.getElementById("tab3").getContext("2d");
    			var chart = new Chart(ctx).Pie($scope.tabdata.classes);
    		}
    		if(tab == 4){
    			$("#subclass-div").addClass('active');
    			var ctx = document.getElementById("tab4").getContext("2d");
    			var chart = new Chart(ctx).Pie($scope.tabdata.subclasses);
    		}
    	}
    });
})();
