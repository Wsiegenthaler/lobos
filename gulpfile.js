'use strict'

let fs = require('fs')
let path = require('path')
let exec = require('child_process').execSync
let lo = require('lodash')

let gulp = require('gulp')
let babel = require('gulp-babel')
let replace = require('gulp-replace')
let print = require('gulp-print')
let concat = require('gulp-concat')
let del = require('del')

let connect = require('gulp-connect')

let webpack = require('webpack')
let webpackStream = require('webpack-stream')

require('expose-loader')


// ------------------------ Build Params ------------------------

let targetDir = 'js/target'
let resourceDir = 'js/src/main/resources'
let paramDir = 'shared/src/main/resources/params'
let paramWrapperFile = 'lobos-params.js'

let scalaJsDir = targetDir + '/scala-2.13'  // Always build js using the latest scala
let scalaJsFile = scalaJsDir + '/lobos-opt.js'

let distDir = 'js/dist'

let mainModule = 'lobos.js'
let standaloneModule = 'lobos-standalone.js'

let babelPresets = ['@babel/env']

// ------------------------ Webpack defaults ------------------------

let webpackOptions = {
  module: {
    rules: [
      {
        test: new RegExp(distDir + "/.*\.js$"),
        loader: 'babel-loader',
        query: { presets: babelPresets }
      }
    ]
  },
  resolve: {
    modules: [ __dirname + '/js/dist', "node_modules"],
    extensions: [".js"]
  }
}


// ------------------------ Build all ------------------------

const build = gulp.series(clean, scalajs, params, standalone)


// ------------------------ ScalaJS ------------------------

function scalajs() {
  exec('sbt fullOptJS', { stdio: [0,1,2] })
  return gulp.src(scalaJsFile)
    .pipe(concat(mainModule))
    .pipe(babel({ presets: babelPresets }))
    .pipe(gulp.dest(distDir))
}


// ------------------------ Wrap parameter data ------------------------

function params() {

  /* Generates the ParamMap object string to be injected into lobos-params.js */
  function paramsByIdStr() {
    let content = fs.readdirSync(paramDir)
      .map(filename => path.join(paramDir, filename))
      .filter(filepath => fs.statSync(filepath).isFile())
      .map(filepath => {
        let filename = path.basename(filepath)
        let data = fs.readFileSync(filepath, "base64")
        let id = filename.replace(/\.gz$/, '')
        let isGzip = filename.length != id.length
        return `"${id}": { data: "${data}", id: "${id}", src: "${filename}", gzip: ${isGzip} }`
      }).join(', ')
  
    return `{ ${content} }`
  }
  
  return gulp.src(path.join(resourceDir, paramWrapperFile))
    .pipe(replace('%PARAMS_BY_ID%', paramsByIdStr()))
    .pipe(babel({ presets: babelPresets }))
    .pipe(gulp.dest(distDir))
}


// ------------------------ standalone ------------------------

function standalone(done) {
  let options = {
    target: 'web',
    output: {
      libraryTarget: 'var',
      library: 'lobos'
    },
    module: {
      rules: [
        {
          test: new RegExp(distDir + '/' + mainModule),
          loader: "expose-loader?lobos"
        }
      ]
    }
  }
  
  return gulp.src(path.join(distDir, mainModule))
    .pipe(webpackStream(merge(webpackOptions, options)))
    .pipe(concat(standaloneModule))
    .pipe(gulp.dest(distDir))
}


// ------------------------ clean ------------------------

function clean(done) {
  del.sync([targetDir, distDir], { force: true })
  done()
}


// ------------------------ util ------------------------

const merge = (a, b) => lo.mergeWith(lo.cloneDeep(a), b, (c, d) => lo.isArray(c) && lo.isArray(d) ? c.concat(d) : undefined)


// ------------------------ export tasks ------------------------

module.exports = { build }
