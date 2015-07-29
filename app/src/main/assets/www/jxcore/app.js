var fs = require('fs');

try {
// requiring a node module
  var jsnice = require('json-nice');

//using it
  var obj = {a: 1, b: 2};
  console.log(jsnice(obj));
} catch(e) {
  console.error("Seems like you didn't copy node_modules folder from sample/jxcore");
}

// execpath
console.log("execPath", process.execPath);

// cwd
console.log("process.cwd", process.cwd());

// iOS user directory
console.log("userPath", fs.readdirSync(process.userPath));


// ---- THIS DID NOT WORK
var zetta = require('zetta');

zetta
  .name('droid-zetta')
  .link('http://paulosv.herokuapp.com')
  .listen(23456);

