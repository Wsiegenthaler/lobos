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

var webpack = require('webpack');
var webpackStream = require('webpack-stream');

require('expose-loader');


// ------------------------ Build Params ------------------------

var targetDir = 'js/target';
var resourceDir = 'js/src/main/resources';
var sharedResourceDir = 'shared/src/main/resources';
var rawParamFile = 'new-joe-kuo-6.21201.gz'; // loader expects joe/kuo format gzipped
var paramWrapperFile = 'lobos-params.js';

var scalaJsDir = targetDir + '/scala-2.11';
var scalaJsFile = scalaJsDir + '/lobos-opt.js';

var distDir = 'js/dist';

var mainModule = 'lobos.js';
var webFile = 'lobos-web.js';

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

gulp.task('build', ['clean', 'scalajs', 'params', 'web'], function() { });


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
  var paramData = fs.readFileSync(path.join(sharedResourceDir, rawParamFile), "base64");
  gulp.src(path.join(resourceDir, paramWrapperFile))
    .pipe(replace('%RAW_PARAMS%', paramData))
    .pipe(babel({ presets: babelPresets }))
    .pipe(gulp.dest(distDir));
});


// ------------------------ web ------------------------

gulp.task('web', ['params', 'scalajs'], function () {
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
    .pipe(concat(webFile))
    .pipe(gulp.dest(distDir))
});


// ------------------------ Clean ------------------------

gulp.task('clean', [], function() {
  del.sync([targetDir, distDir], { force: true });
});


// ------------------------ Util ------------------------

function mergeOptions(a, b) {
  return lo.mergeWith(lo.cloneDeep(a), b, function(c, d) { return lo.isArray(c) && lo.isArray(d) ? c.concat(d) : undefined });
}


module.exports = {};
