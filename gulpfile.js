'use strict';

var fs = require('fs');
var path = require('path');
var exec = require('child_process').execSync;
var lo = require('lodash');

var gulp = require('gulp');
var babel = require('gulp-babel');
var replace = require('gulp-replace');
var print = require('gulp-print');
var concat = require('gulp-concat');
var del = require('del');

var connect = require('gulp-connect');

var webpack = require('webpack');
var webpackStream = require('webpack-stream');

require('expose-loader');


// ------------------------ Build Params ------------------------

var targetDir = 'js/target';
var resourceDir = 'js/src/main/resources';
var paramDir = 'shared/src/main/resources/params';
var paramWrapperFile = 'lobos-params.js';

var scalaJsDir = targetDir + '/scala-2.13';  // Always build js using the latest scala
var scalaJsFile = scalaJsDir + '/lobos-opt.js';

var distDir = 'js/dist';

var mainModule = 'lobos.js';
var standalone = 'lobos-standalone.js';

var babelPresets = ['es2015'];

// ------------------------ Webpack defaults ------------------------

var webpackOptions = {
  verbose: true,
  module: {
    loaders: [ { test: new RegExp(distDir + "/.*\.js$"), loader: 'babel-loader', query: { presets: babelPresets } } ]
  },
  resolve: {
    root: [ '', __dirname + '/js/dist' ],
    modulesDirectories: ["node_modules"],
    extensions: ["", ".js"]
  }
}


// ------------------------ Build all ------------------------

gulp.task('build', ['clean', 'scalajs', 'params', 'standalone'], function() { });


// ------------------------ ScalaJS ------------------------

gulp.task('scalajs', function() {
  exec('sbt fullOptJS', { stdio: [0,1,2] });
  return gulp.src(scalaJsFile)
    .pipe(concat(mainModule))
    .pipe(babel({ presets: babelPresets }))
    .pipe(gulp.dest(distDir))
});


// ------------------------ Wrap parameter data ------------------------

gulp.task('params', function() {
  gulp.src(path.join(resourceDir, paramWrapperFile))
    .pipe(replace('%PARAMS_BY_ID%', paramsByIdStr()))
    .pipe(babel({ presets: babelPresets }))
    .pipe(gulp.dest(distDir));
});

/* Generates the ParamMap object string to be injected into lobos-params.js */
function paramsByIdStr() {
  var content = fs.readdirSync(paramDir)
    .map(filename => path.join(paramDir, filename))
    .filter(filepath => fs.statSync(filepath).isFile())
    .map(filepath => {
      var filename = path.basename(filepath);
      var data = fs.readFileSync(filepath, "base64");
      var id = filename.replace(/\.gz$/, '');
      var isGzip = filename.length != id.length;
      return `"${id}": { data: "${data}", id: "${id}", src: "${filename}", gzip: ${isGzip} }`;
    }).join(', ');

  return `{ ${content} }`;
}



// ------------------------ standalone ------------------------

gulp.task('standalone', ['params', 'scalajs'], function () {
  var options = {
    target: 'web',
    output: {
      libraryTarget: 'var',
      lib: 'lobos'
    },
    module: {
      loaders: [{ test: new RegExp(distDir + '/' + mainModule), loader: "expose?lobos" }]
    }
  };

  return gulp.src(path.join(distDir, mainModule))
    .pipe(webpackStream(mergeOptions(webpackOptions, options)))
    .pipe(concat(standalone))
    .pipe(gulp.dest(distDir))
});

// ------------------------ demo-server ------------------------

gulp.task('demo-server', [], function() {
  connect.server({ root: ['.'], port: 8000, livereload: false });
});


// ------------------------ clean ------------------------

gulp.task('clean', [], function() {
  del.sync([targetDir, distDir], { force: true });
});


// ------------------------ util ------------------------

function mergeOptions(a, b) {
  return lo.mergeWith(lo.cloneDeep(a), b, function(c, d) { return lo.isArray(c) && lo.isArray(d) ? c.concat(d) : undefined });
}


module.exports = {};
