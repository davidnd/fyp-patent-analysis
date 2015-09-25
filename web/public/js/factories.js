(function(){
    angular.module('fyp').factory('Utils', function($http){
        var displayType = "piechart";
        return {
            //util methods here
            trimDescription: trimDescription
        };
        
        function trimDescription(description){
            var index1 = description.indexOf(',');
            var index2 = description.indexOf(';');
            index1 = (index1>-1 ? index1 : description.length);
            index2 = (index2>-1 ? index2 : description.length);
            return description.substring(0, Math.min(index2, index1));
        }
    });
    angular.module('fyp').factory('Section', function($resource){
        return $resource('/sections/:id', {}, 
        {
            getDrillDown: { method: 'GET', params: {id: '@id'}, isArray:true}
        });
    });
    angular.module('fyp').factory('Subsection', function($resource){
    	return $resource('/subsections');
    });
    angular.module('fyp').factory('Class', function($resource){
        return $resource('/classes/:id', {},
            {
                getDrillDown: { method: 'GET', params: {id: '@id'}, isArray: true}
            });
    });
    angular.module('fyp').factory('Subclass', function($resource){
        return $resource('/subclasses');
    });
})();
