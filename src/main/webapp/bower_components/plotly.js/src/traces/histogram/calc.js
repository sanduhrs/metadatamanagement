/**
* Copyright 2012-2017, Plotly, Inc.
* All rights reserved.
*
* This source code is licensed under the MIT license found in the
* LICENSE file in the root directory of this source tree.
*/


'use strict';

var isNumeric = require('fast-isnumeric');

var Lib = require('../../lib');
var Axes = require('../../plots/cartesian/axes');

var arraysToCalcdata = require('../bar/arrays_to_calcdata');
var binFunctions = require('./bin_functions');
var normFunctions = require('./norm_functions');
var doAvg = require('./average');
var cleanBins = require('./clean_bins');


module.exports = function calc(gd, trace) {
    // ignore as much processing as possible (and including in autorange) if bar is not visible
    if(trace.visible !== true) return;

    // depending on orientation, set position and size axes and data ranges
    // note: this logic for choosing orientation is duplicated in graph_obj->setstyles
    var pos = [],
        size = [],
        i,
        pa = Axes.getFromId(gd,
            trace.orientation === 'h' ? (trace.yaxis || 'y') : (trace.xaxis || 'x')),
        maindata = trace.orientation === 'h' ? 'y' : 'x',
        counterdata = {x: 'y', y: 'x'}[maindata],
        calendar = trace[maindata + 'calendar'],
        cumulativeSpec = trace.cumulative;

    cleanBins(trace, pa, maindata);

    // prepare the raw data
    var pos0 = pa.makeCalcdata(trace, maindata);

    // calculate the bins
    var binAttr = maindata + 'bins',
        binspec;
    if((trace['autobin' + maindata] !== false) || !(binAttr in trace)) {
        binspec = Axes.autoBin(pos0, pa, trace['nbins' + maindata], false, calendar);

        // adjust for CDF edge cases
        if(cumulativeSpec.enabled && (cumulativeSpec.currentbin !== 'include')) {
            if(cumulativeSpec.direction === 'decreasing') {
                binspec.start = pa.c2r(pa.r2c(binspec.start) - binspec.size);
            }
            else {
                binspec.end = pa.c2r(pa.r2c(binspec.end) + binspec.size);
            }
        }

        // copy bin info back to the source and full data.
        trace._input[binAttr] = trace[binAttr] = binspec;
    }
    else {
        binspec = trace[binAttr];
    }

    var nonuniformBins = typeof binspec.size === 'string',
        bins = nonuniformBins ? [] : binspec,
        // make the empty bin array
        i2,
        binend,
        n,
        inc = [],
        counts = [],
        total = 0,
        norm = trace.histnorm,
        func = trace.histfunc,
        densitynorm = norm.indexOf('density') !== -1;

    if(cumulativeSpec.enabled && densitynorm) {
        // we treat "cumulative" like it means "integral" if you use a density norm,
        // which in the end means it's the same as without "density"
        norm = norm.replace(/ ?density$/, '');
        densitynorm = false;
    }

    var extremefunc = func === 'max' || func === 'min',
        sizeinit = extremefunc ? null : 0,
        binfunc = binFunctions.count,
        normfunc = normFunctions[norm],
        doavg = false,
        pr2c = function(v) { return pa.r2c(v, 0, calendar); },
        rawCounterData;

    if(Array.isArray(trace[counterdata]) && func !== 'count') {
        rawCounterData = trace[counterdata];
        doavg = func === 'avg';
        binfunc = binFunctions[func];
    }

    // create the bins (and any extra arrays needed)
    // assume more than 5000 bins is an error, so we don't crash the browser
    i = pr2c(binspec.start);

    // decrease end a little in case of rounding errors
    binend = pr2c(binspec.end) + (i - Axes.tickIncrement(i, binspec.size, false, calendar)) / 1e6;

    while(i < binend && pos.length < 1e6) {
        i2 = Axes.tickIncrement(i, binspec.size, false, calendar);
        pos.push((i + i2) / 2);
        size.push(sizeinit);
        // nonuniform bins (like months) we need to search,
        // rather than straight calculate the bin we're in
        if(nonuniformBins) bins.push(i);
        // nonuniform bins also need nonuniform normalization factors
        if(densitynorm) inc.push(1 / (i2 - i));
        if(doavg) counts.push(0);
        // break to avoid infinite loops
        if(i2 <= i) break;
        i = i2;
    }

    // for date axes we need bin bounds to be calcdata. For nonuniform bins
    // we already have this, but uniform with start/end/size they're still strings.
    if(!nonuniformBins && pa.type === 'date') {
        bins = {
            start: pr2c(bins.start),
            end: pr2c(bins.end),
            size: bins.size
        };
    }

    var nMax = size.length;
    // bin the data
    for(i = 0; i < pos0.length; i++) {
        n = Lib.findBin(pos0[i], bins);
        if(n >= 0 && n < nMax) total += binfunc(n, i, size, rawCounterData, counts);
    }

    // average and/or normalize the data, if needed
    if(doavg) total = doAvg(size, counts);
    if(normfunc) normfunc(size, total, inc);

    // after all normalization etc, now we can accumulate if desired
    if(cumulativeSpec.enabled) cdf(size, cumulativeSpec.direction, cumulativeSpec.currentbin);


    var serieslen = Math.min(pos.length, size.length),
        cd = [],
        firstNonzero = 0,
        lastNonzero = serieslen - 1;
    // look for empty bins at the ends to remove, so autoscale omits them
    for(i = 0; i < serieslen; i++) {
        if(size[i]) {
            firstNonzero = i;
            break;
        }
    }
    for(i = serieslen - 1; i > firstNonzero; i--) {
        if(size[i]) {
            lastNonzero = i;
            break;
        }
    }

    // create the "calculated data" to plot
    for(i = firstNonzero; i <= lastNonzero; i++) {
        if((isNumeric(pos[i]) && isNumeric(size[i]))) {
            cd.push({p: pos[i], s: size[i], b: 0});
        }
    }

    arraysToCalcdata(cd, trace);

    return cd;
};

function cdf(size, direction, currentbin) {
    var i,
        vi,
        prevSum;

    function firstHalfPoint(i) {
        prevSum = size[i];
        size[i] /= 2;
    }

    function nextHalfPoint(i) {
        vi = size[i];
        size[i] = prevSum + vi / 2;
        prevSum += vi;
    }

    if(currentbin === 'half') {

        if(direction === 'increasing') {
            firstHalfPoint(0);
            for(i = 1; i < size.length; i++) {
                nextHalfPoint(i);
            }
        }
        else {
            firstHalfPoint(size.length - 1);
            for(i = size.length - 2; i >= 0; i--) {
                nextHalfPoint(i);
            }
        }
    }
    else if(direction === 'increasing') {
        for(i = 1; i < size.length; i++) {
            size[i] += size[i - 1];
        }

        // 'exclude' is identical to 'include' just shifted one bin over
        if(currentbin === 'exclude') {
            size.unshift(0);
            size.pop();
        }
    }
    else {
        for(i = size.length - 2; i >= 0; i--) {
            size[i] += size[i + 1];
        }

        if(currentbin === 'exclude') {
            size.push(0);
            size.shift();
        }
    }
}
