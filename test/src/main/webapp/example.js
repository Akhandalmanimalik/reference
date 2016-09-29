angular.module('ui.bootstrap.demo', ['ngAnimate', 'ui.bootstrap']);
angular.module('ui.bootstrap.demo').controller('ButtonsCtrl', function ($scope) {
  $scope.singleModel = 1;

  $scope.checkModel = {
	Employed: false,
    Self_Employed: false,
    Pension: false,
    Investments:false,
    Maternity:false,
    VehicleAllow:false,
    LivingAllow:false,
    Commission:false,
    Bonus:false,
    Other:false
  };

  $scope.checkResults = [];

  $scope.$watchCollection('checkModel', function () {
    $scope.checkResults = [];
    angular.forEach($scope.checkModel, function (value, key) {
      if (value) {
        $scope.checkResults.push(key);
      }
    });
  });
});