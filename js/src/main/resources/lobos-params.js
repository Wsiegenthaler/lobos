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
export function decode(paramId) {
  var entry = rawParamsById[paramId || dfltParamId];
  var bytes = b64.toByteArray(entry.data);
  if (entry.gzip) bytes = gzip.unzip(bytes, {});
  var string = bytes.reduce(function(prev, cur) { return prev + String.fromCharCode(cur) }, '');
  return string;
}

/* Default 'paramId' to load if none specified at runtime */
const dfltParamId = "%DEFAULT_PARAMS%";

/* Gzipped and base64-ed raw parameter data keyed by 'paramId' (the extension stripped filename) */
const rawParamsById = %PARAMS_BY_ID%;
