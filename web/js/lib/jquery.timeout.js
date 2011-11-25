/* taken from http://www.kryogenix.org/days/2009/07/03/not-blocking-the-ui-in-tight-javascript-loops

It prevents the UI to freeze for intensive array processing. 

Or, at leat, provides a loop function for a progress bar or something...


Usage: 

$.eachCallback(someArray, function() {
  // "this" is the array item, just like $.each
}, function(loopcount) {
  // here you get to do some UI updating
  // loopcount is how far into the loop you are
});

$("li").eachCallback(function() {
  // do something to this
}, function(loopcount) {
  // update the UI
});

*/

jQuery.eachCallback = function(arr, process, callback) {
    var cnt = 0;
    function work() {
        var item = arr[cnt];
        process.apply(item);
        callback.apply(item, [cnt]);
        cnt += 1;
        if (cnt < arr.length) {
            setTimeout(work, 1);
        }
    }
    setTimeout(work, 1);
};
jQuery.fn.eachCallback = function(process, callback) {
    var cnt = 0;
    var jq = this;
    function work() {
        var item = jq.get(cnt);
        process.apply(item);
        callback.apply(item, [cnt]);
        cnt += 1;
        if (cnt < jq.length) {
            setTimeout(work, 1);
        }
    }
    setTimeout(work, 1);
};
