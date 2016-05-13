if (!String.prototype.format) {
  String.prototype.format = function() {
    var args = arguments;
    var result = this.replace(/{(\d+)}/g, function(match, number) { 
      return typeof args[number] != 'undefined'
        ? args[number]
        : match
      ;
    });
   return result; 
  };
}