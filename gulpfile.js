/* jshint node:true */
'use strict';

var fs = require('fs');
var path = require('path');
var lo = require('lodash');

var gulp = require('gulp');
var replace = require('gulp-replace');
var print = require('gulp-print');
var concat = require('gulp-concat');
var del = require('del');

var webpack = require('webpack');
var webpackStream = require('webpack-stream');

var exec = require('child_process').exec;

var nodeExternals = require('webpack-node-externals'); // prevents dependencies from being included in node-targeted webpack

require('expose-loader');


// ------------------------ Build Dirs ------------------------

var targetDir = 'js/target';
var resourceDir = 'js/src/main/resources';
var sharedResourceDir = 'shared/src/main/resources';
var rawParamFile = 'new-joe-kuo-6.21201.gz'; // loader expects joe/kuo format gzipped
var paramWrapperFile = 'lobos-params.js';

var scalaJsDir = targetDir + '/scala-2.11';
var scalaJsFile = scalaJsDir + '/lobos-opt.js';

var distDir = 'js/dist';


// ------------------------ Webpack defaults ------------------------

var webpackOptions = {
  verbose: true,
  module: {
    loaders: [ { test: /\.js$/, loader: 'babel-loader?presets[]=es2015' } ]
  },
  resolve: {
    root: [ __dirname ],
    modulesDirectories: ["node_modules"],
    extensions: ["", ".js"]
  },
  context: __dirname
}


// ------------------------ Build all ------------------------

gulp.task('build', ['clean', 'scala-js', 'lobos-params', 'webpack-browser', 'webpack-node'], function() { });


// ------------------------ Build ScalaJS ------------------------

gulp.task('scala-js', function(cb) {
  exec('sbt fullOptJS', function (err, stdout, stderr) {
    console.log(stdout);
    console.log(stderr);
    cb(err);
  });
});


// ------------------------ Wrap parameter data ------------------------

gulp.task('lobos-params', function() {
  var paramData = fs.readFileSync(path.join(sharedResourceDir, rawParamFile), "base64");
  gulp.src(path.join(resourceDir, paramWrapperFile))
    .pipe(replace('%RAW_PARAMS%', paramData))
    .pipe(gulp.dest(scalaJsDir));
});


// ------------------------ Browser ------------------------

gulp.task('webpack-browser', ['lobos-params', 'scala-js'], function () {
  var options = {
    target: 'web',
    output: {
      libraryTarget: 'var',
      lib: 'lobos'
    },
    module: {
      loaders: [{ test: require.resolve('./'+scalaJsFile), loader: "expose?lobos" }]
    }
  };
  return gulp.src(scalaJsFile)
    .pipe(webpackStream(mergeOptions(webpackOptions, options)))
    .pipe(concat('lobos-web.js'))
    .pipe(gulp.dest(distDir))
});


// ------------------------ Node ------------------------

gulp.task('webpack-node', ['lobos-params', 'scala-js'], function () {
  var options = { target: 'node', externals: [nodeExternals()], output: { libraryTarget: 'commonjs2', lib: 'lobos' }, node: { global: true } };
  return gulp.src(scalaJsFile)
    .pipe(webpackStream(mergeOptions(webpackOptions, options)))
    .pipe(concat('lobos-node.js'))
    .pipe(gulp.dest(distDir))
});


// ------------------------ Clean ------------------------

gulp.task('clean', [], function(cb) {
  del([targetDir, distDir], { force: true }, cb);
});


// ------------------------ Util ------------------------

function mergeOptions(a, b) {
  return lo.mergeWith(lo.cloneDeep(a), b, function(c, d) { return lo.isArray(c) && lo.isArray(d) ? c.concat(d) : undefined });
}


module.exports = { };
