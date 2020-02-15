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
import gzip from 'gzip-js'
import b64 from 'base64-js'

/* Decodes base64 and gunzips the parameter data */
export function load(label) {
  if (!label) throw Error(`[lobos-params] decode(..) must be passed the key of the parameters to load!`)

  // Lookup param data in map
  let entry = rawParamsMap[label]
  if (!entry) throw Error(`[lobos-params] decode(..) unable to locate parameters for label "${label}"!`)

  // Convert from base64 and decompress
  let bytes = b64.toByteArray(entry.data)
  if (entry.gzip) bytes = gzip.unzip(bytes, {})

  // Convert param data to string
  return bytes.reduce((prev, cur) => prev + String.fromCharCode(cur), '')
}

/* Gzipped and base64-ed raw parameter data keyed by 'params' (the extension stripped filename) */
const rawParamsMap = %PARAMS_BY_ID%
