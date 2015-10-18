angular.module('SigmoidMeetup').controller('StationsShowController', function(StationService, $scope, $routeParams) {
    $scope.station = StationService.get({id: $routeParams.id});
});