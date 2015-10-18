angular.module('SigmoidMeetup').controller('StationsIndexController', function(StationService, $scope, $location) {
    $scope.stations = StationService.query();

    $scope.showStation = function(id) {
        $location.path('/stations/' + id);
    };
});