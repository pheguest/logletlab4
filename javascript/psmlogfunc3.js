/**********************************************************************/ 
/*                                                                    */
/* javascript constrained non-linear regression library               */
/*                                                                    */
/* Copyright (C) 2008-2010  The Rockefeller University, NY, NY.       */
/* All Rights Reserved                                                */
/*                                                                    */
/* Javscript code written by Perrin S. Meyer, based on the            */
/* "loglet lab" software sponsored by http://phe.rockefeller.edu      */ 
/*                                                                    */
/* These javascript library functions are licensed under the          */
/* GNU Lesser (Library) General Public License version 2.1 (LGPL 2.1) */
/* http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt              */
/*                                                                    */
/*     email me to discuss other license options.                     */
/*                                                                    */
/*  p e r r i n m e y e r _at_ y a h o o _dot_ c o m                  */
/*                                                                    */
/**********************************************************************/ 


function loglet_MC_anneal_regression(logobj) {

  /* this function uses a simple Monte-Carlo based */
  /* "simulated annealing" algorithm to perform    */
  /* constrained non-linear regression.            */
  /* see http://lizardinthesun.com/cloudblog/      */
  /* for details.                                  */

  var i; // iteration variables (not sqrt(-1)) 
  var j; 

  var point = {}; // a point in parameter space 
  for (i = 0 ; i < logobj.number_of_loglets ; i++) {
    if (i == 0) point["d"] = 0.0;
    point["a"+i] = 1.0;
    point["k"+i] = 1.0;
    point["b"+i] = 1.0;
  }

  var y_try = [];
  var iter = 0; /* initialize (I'm a c programer at heart) */
  var energy = 0; 

  var anneal = {};
  var sample = {}; 
  for (i = 0 ; i < logobj.number_of_loglets ; i++) {
    if (i == 0) sample["d"] = [];
    sample["a"+i] = [];
    sample["k"+i] = [];
    sample["b"+i] = [];

    if (i == 0) anneal["d"] = [];
    anneal["a"+i] = [];
    anneal["k"+i] = [];
    anneal["b"+i] = [];
  }

  /* from page 22 of book "javascript the good parts" */
  /* used to initialize an array of objects */ 
  if (typeof Object.create !== 'function') {
    Object.create = function (o) {
      var F = function () {} ;
      F.prototype = o;
      return new F();
    }
  }

  var sample_element = {energy : 1.0, index : 1.0};
  var sampleIndex = [];
  for (i = 0 ; i < logobj.MCiter ; i++) {
    sampleIndex[i] = Object.create(sample_element);
  }

  for (i = 0 ; i< logobj.number_of_loglets ; i++ ) {
    if (i == 0) anneal["min_d"] = logobj.constraints["dlow"];
    if (i == 0) anneal["max_d"] = logobj.constraints["dhigh"];
    anneal["min_a"+i] = logobj.constraints["alow"+i];
    anneal["max_a"+i] = logobj.constraints["ahigh"+i];
    anneal["min_k"+i] = logobj.constraints["klow"+i];
    anneal["max_k"+i] = logobj.constraints["khigh"+i];
    anneal["min_b"+i] = logobj.constraints["blow"+i];
    anneal["max_b"+i] = logobj.constraints["bhigh"+i];
  }

   /**************  START MC Anneal ****************************************************/

  for (var aiter = 0 ; aiter < logobj.anneal_iter ; aiter++) {

    for (iter = 0 ; iter < logobj.MCiter ;  iter++) {
      for (i = 0 ; i < logobj.number_of_loglets ; i++) {
        if (i == 0) point["d"] = randnum(anneal["min_d"],anneal["max_d"]);
        point["a"+i] = randnum(anneal["min_a"+i],anneal["max_a"+i]);
        point["k"+i] = randnum(anneal["min_k"+i],anneal["max_k"+i]);
        point["b"+i] = randnum(anneal["min_b"+i],anneal["max_b"+i]);
      }

      for (i=0 ; i < logobj.data.x.length ; i++) {
        y_try[i] = 0;
      }
      for (i = 0 ; i<logobj.data.x.length ; i++) {
        for (j = 0 ; j < logobj.number_of_loglets ; j++) {
          y_try[i] = y_try[i] + loglet(logobj.data.x[i],point["a"+j],point["k"+j],point["b"+j]);
          if (j == 0) y_try[i] = y_try[i] + point["d"];
        }
      }

      for (i = 0 ; i < logobj.number_of_loglets ; i++) {
        if (i == 0) sample["d"][iter] = point["d"];
        sample["a"+i][iter] = point["a"+i];
        sample["k"+i][iter] = point["k"+i];
        sample["b"+i][iter] = point["b"+i];
      }

      energy = logobj.norm(logobj.data.y,y_try); 
      sampleIndex[iter].energy = energy;
      sampleIndex[iter].index = iter;

      if (energy < logobj.energy_best) {
        for (j = 0 ; j < logobj.number_of_loglets ; j++) {
          if ( j == 0 ) logobj.parameters["d"] = point["d"];
          logobj.parameters["a"+j] = point["a"+j];
          logobj.parameters["k"+j] = point["k"+j];
          logobj.parameters["b"+j] = point["b"+j];
        }
        logobj.energy_best = energy;
      }

    }

    // sort, in order to contract parameter constraints (simulated annealing)
    sampleIndex.sort(
                     function(tmp1,tmp2) {
                       if (tmp1.energy > tmp2.energy) return 1;
                       if (tmp2.energy > tmp1.energy) return -1;
                       return 0;
                     }
                     );

    for (j = 0 ; j < logobj.number_of_loglets ; j++) {

      if (j == 0) {
        for (i = 0 ; i < logobj.anneal ; i++ ) { anneal["d"][i] = sample["d"][sampleIndex[i].index]; }
        anneal["d"].sort(sortByNumbers);
        anneal["min_d"] = anneal["d"][0];
        anneal["max_d"] = anneal["d"][anneal["d"].length -1];
      }

      for (i = 0 ; i < logobj.anneal ; i++ ) { anneal["a"+j][i] = sample["a"+j][sampleIndex[i].index]; }
      anneal["a"+j].sort(sortByNumbers);
      anneal["min_a"+j] = anneal["a"+j][0];
      anneal["max_a"+j] = anneal["a"+j][anneal["a"+j].length -1];

      for (i = 0 ; i < logobj.anneal ; i++ ) { anneal["k"+j][i] = sample["k"+j][sampleIndex[i].index]; }
      anneal["k"+j].sort(sortByNumbers);
      anneal["min_k"+j] = anneal["k"+j][0];
      anneal["max_k"+j] = anneal["k"+j][anneal["k"+j].length -1];

      for (i = 0 ; i < logobj.anneal ; i++ ) { anneal["b"+j][i] = sample["b"+j][sampleIndex[i].index]; }
      anneal["b"+j].sort(sortByNumbers);
      anneal["min_b"+j] = anneal["b"+j][0];
      anneal["max_b"+j] = anneal["b"+j][anneal["b"+j].length -1];

    }
  }
  /*** END MC Anneal **********************************************************************/

}


function create_curves(logobj) {

  /* make the fitted logistic curve  */
  var i ; /* iteration variable */ 
  var j ; /* iteration variable */ 
  var iter ; /* iteration variable */ 

  var sl = 1.2; /* slice factor, makes generated curves longer or shorter */ 

  var xstart = logobj.parameters.b0 - sl * logobj.parameters.a0;
  var xstop =  logobj.parameters["b"+(logobj.number_of_loglets-1)] + sl * logobj.parameters["a"+(logobj.number_of_loglets -1)];

  /* the main curve */ 
  logobj.curve.t = linspace(xstart,xstop,logobj.curve.n); 
  for (i=0 ; i < logobj.curve.t.length ; i++) {
    logobj.curve.yfit[i] = 0;
  }
  for (i = 0 ; i<logobj.curve.t.length ; i++) {
    for (j = 0 ; j < logobj.number_of_loglets ; j++) {
      logobj.curve.yfit[i] = logobj.curve.yfit[i] + 
        loglet(logobj.curve.t[i],
               logobj.parameters["a"+j],
               logobj.parameters["k"+j],
               logobj.parameters["b"+j]);
      logobj.curve.yfit[i] = logobj.curve.yfit[i] + logobj.parameters["d"]; 
    }
  }

  /* the residuals */ 
  for (i=0 ; i < logobj.data.x.length ; i++) {
    logobj.data.yfit[i] = 0;
    logobj.data.residuals[i] = 0;
  }
  for (i = 0; i < logobj.data.x.length; i++){
    for (j = 0 ; j < logobj.number_of_loglets ; j++) {
      logobj.data.yfit[i] = logobj.data.yfit[i] + 
        loglet(logobj.data.x[i],
               logobj.parameters["a"+j],
               logobj.parameters["k"+j],
               logobj.parameters["b"+j]);
      logobj.data.yfit[i] = logobj.data.yfit[i] + logobj.parameters["d"]; 
    }
    logobj.data.residuals[i] = logobj.data.y[i] - logobj.data.yfit[i];
  }

  var mean = 0.0;
  var tmpsum = 0.0;
  var N = logobj.data.x.length;
  for (iter = 0 ; iter < N ; iter++) {
    tmpsum += logobj.data.residuals[iter];
  }  
  mean = tmpsum / N;
  var sd = 0.0 ; //the (estimated) standard deviation 
  for (iter = 0 ; iter < N ; iter++) {
    sd += (logobj.data.residuals[iter] - mean) * (logobj.data.residuals[iter] - mean);
  }  
  sd = Math.sqrt( (1 / (N -1)) * sd);

  for (iter = 0 ; iter < N ; iter++) {
    logobj.data.sr[iter] = (logobj.data.residuals[iter] - mean) / sd;
  }

  // put in flot form
  for (iter = 0 ; iter < logobj.data.x.length ; iter++) {
    logobj.flot.data.push([logobj.data.x[iter], logobj.data.y[iter]]);
  }

  for (iter = 0 ; iter < logobj.data.x.length ; iter++) {
    logobj.flot.residuals.push([logobj.data.x[iter], logobj.data.residuals[iter]]);
  }

  for (iter = 0 ; iter < logobj.data.x.length ; iter++) {
    logobj.flot.sr.push([logobj.data.x[iter], logobj.data.sr[iter]]);
  }

  // create zero line for residuals
  logobj.flot.resline.push([logobj.curve.t[0], 0.0]);
  logobj.flot.resline.push([logobj.curve.t[logobj.curve.n-1], 0.0]);


  // put in flot form 
  for (iter = 0; iter < logobj.curve.n; iter++){
    logobj.flot.curve.push([logobj.curve.t[iter], logobj.curve.yfit[iter]]);
  }


  var fp = {}; /* to hold modified fp parameters and temp data  */ 
  var cc = {}; /* to hold modified component parameters and temp data  */ 
  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    fp["a"+j] = logobj.parameters["a"+j];
    fp["k"+j] = logobj.parameters["k"+j];
    fp["b"+j] = logobj.parameters["b"+j];
  }

  /* makes the fisher pry plots look right */ 
  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    if (fp["a"+j] > 0.0 && fp["k"+j] < 0.0 ) {
      fp["a"+j] = -1.0 * fp["a"+j];
      fp["k"+j] = -1.0 * fp["k"+j];
    }
    if (fp["a"+j] < 0.0 && fp["k"+j] < 0.0 ) {
      fp["a"+j] = -1.0 * fp["a"+j];
      fp["k"+j] = -1.0 * fp["k"+j];
    }    
  }  


  /* lets make the fisher pry transformed data */ 
  /* and now compoent logisitcs (called cc for some reason */ 
  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    fp["x"+j] = [];
    fp["y"+j] = [];
    cc["x"+j] = [];
    cc["y"+j] = [];
    for ( i = 0 ; i < logobj.data.x.length ; i++ ) {
      if ( (logobj.data.x[i] > (logobj.parameters["b"+j] - Math.abs(logobj.parameters["a"+j] * sl)))
           &&
           (logobj.data.x[i] < (logobj.parameters["b"+j] + Math.abs(logobj.parameters["a"+j] * sl))) ) {
        fp["x"+j].push(logobj.data.x[i]); 
        cc["x"+j].push(logobj.data.x[i]); 
        fp["y"+j].push(logobj.data.y[i] - logobj.parameters["d"] ); 
        cc["y"+j].push(logobj.data.y[i] - logobj.parameters["d"] ); 
      }
    }
  }

  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    for (i = 0 ; i < cc["x"+j].length ; i++ ) {
      for (iter = 0 ; iter < logobj.number_of_loglets ; iter++ ) {
        if ( iter != j) {
          cc["y"+j][i] -= loglet(cc["x"+j][i],
                                 logobj.parameters["a"+iter],
                                 logobj.parameters["k"+iter],
                                 logobj.parameters["b"+iter]);
        }
      }
    }
  }
  


  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    for (i = 0 ; i < fp["x"+j].length ; i++ ) {
      for (iter = 0 ; iter < logobj.number_of_loglets ; iter++ ) {
        if ( iter != j) {
          fp["y"+j][i] -= loglet(fp["x"+j][i],
                                 logobj.parameters["a"+iter],
                                 logobj.parameters["k"+iter],
                                 logobj.parameters["b"+iter]);
        }
      }
      if (logobj.parameters["k"+j] < 0.0) { fp["y"+j][i] += -1.0 * logobj.parameters["k"+j];}
      if (fp["y"+j][i] < 0.0 || fp["y"+j][i] >= Math.abs(logobj.parameters["k"+j])) { 
        fp["y"+j][i] = 0.0;
      }
      else {
        fp["y"+j][i] = fp["y"+j][i] / ( Math.abs( logobj.parameters["k"+j]) - fp["y"+j][i]);
      }
    }
  }  

  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    logobj.flot["fpdata"+j] = [];
    logobj.flot["ccdata"+j] = [];
    logobj.fp["datax" + j] = [];
    logobj.fp["datay" + j] = [];
    logobj.cc["datax" + j] = [];
    logobj.cc["datay" + j] = [];
    for (i = 0 ; i < fp["x"+j].length ; i++ ) {
      logobj.flot["fpdata"+j].push([fp["x"+j][i],log10(fp["y"+j][i])]);
      logobj.flot["ccdata"+j].push([cc["x"+j][i],cc["y"+j][i]]);
      logobj.fp["datax" + j].push(fp["x"+j][i]);
      logobj.fp["datay" + j].push(fp["y"+j][i]);
      logobj.cc["datax" + j].push(cc["x"+j][i]);
      logobj.cc["datay" + j].push(cc["y"+j][i]);
    }
  }

  /* now lets make the fisher py lines */ 
  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    fp["xl"+j] = linspace(xstart,xstop,100); // this looks like bug? xstart should be changing.. 
    fp["yl"+j] = [];
    for (i = 0 ; i < fp["xl"+j].length ; i++ ) {
      fp["yl"+j][i] = loglet(fp["xl"+j][i],
                             fp["a"+j],
                             fp["k"+j],
                             fp["b"+j]);
      fp["yl"+j][i] = fp["yl"+j][i] / fp["k"+j];  /* create fraction f (y/k) */
      fp["yl"+j][i] = fp["yl"+j][i] / ( 1 - fp["yl"+j][i]); /* create f / (1 - f) */ 
    }
  }
  

  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    logobj.flot["fpline"+j] = [];
    logobj.fp["linex" + j] = [];
    logobj.fp["liney" + j] = [];
    for (i = 0 ; i < fp["xl"+j].length ; i++ ) {
      logobj.flot["fpline"+j].push([fp["xl"+j][i],log10(fp["yl"+j][i])]);
      logobj.fp["linex"+j].push(fp["xl"+j][i]);
      logobj.fp["liney"+j].push(fp["yl"+j][i]);
    }
  }

  /* now lets make component curves */ 
  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    xstart = logobj.parameters["b"+j] - sl * logobj.parameters["a"+j];
    xstop =  logobj.parameters["b"+j] + sl * logobj.parameters["a"+j];
    cc["xl"+j] = linspace(xstart,xstop,100); // this looks like bug? xstart should be changing.. 
    cc["yl"+j] = [];
    for (i = 0 ; i < cc["xl"+j].length ; i++ ) {
      cc["yl"+j][i] = loglet(cc["xl"+j][i],fp["a"+j],fp["k"+j], fp["b"+j]); 
    }
  }
                         
  
  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    logobj.flot["cccurve"+j] = [];
    logobj.cc["linex" + j] = [];
    logobj.cc["liney" + j] = [];
    for (i = 0 ; i < cc["xl"+j].length ; i++ ) {
      logobj.flot["cccurve"+j].push([cc["xl"+j][i],cc["yl"+j][i]]);
      logobj.cc["linex"+j].push(cc["xl"+j][i]);
      logobj.cc["liney"+j].push(cc["yl"+j][i]);
    }
  }

  



}/* end create_curves */ 



/* the main "wavelet"  */ 
function loglet(t,a,k,b) {
    return ( k / (1.0 + Math.exp( (4.394449154672439 / (-1.0 * a)) * (t - b))));
    // log_e(81) = 4.394449154672439 
}

function log10(x) {
  return Math.log(x)/Math.log(10);
}


function randnum(low,high) {
  return (high - low) * Math.random() + low;
}


function sumsquares(x,y) {
  var energy = 0.0; 
    for (var sumiter = 0 ; sumiter < x.length ; sumiter++) {
      energy +=  (y[sumiter] - x[sumiter]) * (y[sumiter] - x[sumiter]);
    }
    return energy; 
}

function robust(x,y) {
  var energy = 0.0; 
  for (var sumiter = 0 ; sumiter < x.length ; sumiter++) {
    energy +=  Math.sqrt((y[sumiter] - x[sumiter]) * (y[sumiter] - x[sumiter]));
  }
  return energy; 
}


function linspace(xstart,xstop,n) {
  var x = []; 
  var step = 0.0; // arbitrary 
  step = (xstop - xstart) / (n - 1.0);
  x[0] = xstart;
  x[n-1] = xstop;
  for (var i = 1 ; i <= (n-2) ; i++) {
    x[i] = xstart + (i * step);
  }
  return x; 
}

// utility function for sorting by numbers 
// (not alphebetically, which is js default) 
function sortByNumbers(tmp1,tmp2) {
  if (tmp1 > tmp2) return 1;
  if (tmp2 > tmp1) return -1;
  return 0;
}

