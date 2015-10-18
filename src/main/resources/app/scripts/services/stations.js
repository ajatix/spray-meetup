angular.module('SigmoidMeetup').factory('StationService', function($resource) {
    return $resource('http://localhost:8000/v1/spark/station/:id', {}, {});

});
