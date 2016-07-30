/*
 * This template will embed the contents of the sobol sequence dimension
 * parameters and make it available as a module for lobos.
 *
 * Data is in the format used by Joe/Kuo (whitespace-delimited variable 
 * length ascii w/ header), then gzipped and base64 encoded.
 *
 * Note: Provided Joe/Kuo data allows for sequences of up to 21,201
 * dimensions. Truncate the parameter file to significantly reduce
 * filesize and startup time when high dimensional sequences aren't needed.
 */
import gzip from 'gzip-js';
import b64 from 'base64-js';

/* Decodes base64 and gunzips the parameter data */
export function decode() {
  var bytes = gzip.unzip(b64.toByteArray(rawBase64Gz), {})
  var string = bytes.reduce(function(prev, cur) { return prev + String.fromCharCode(cur) }, '')
  return string;
}

/* Gzipped and base64-ed raw parameter data */
export const rawBase64Gz = "%RAW_PARAMS%"
