angular.module('SigmoidMeetup').factory('TrainService', function($resource) {
    return $resource('http://localhost:8000/v1/spark/train/:id', {}, {});

});
