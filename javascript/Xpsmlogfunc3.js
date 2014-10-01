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

function Xpsmlogfunc() {

  /* the main data structure */ 
  var logobj = 
    { 
      data: {
        x: [], 
        y: [], 
        yfit: [],
        residuals: [],
        sr: [], /* standardized residuals */ 
        n: [], /* how many points */
      },
      curve: {
        n: 256,
        t: [],
        yfit: [],
      },
      flot: {
        data: [],
        curve: [],
        residuals: [],
        sr: [],
        resline: [],
      },
      fp: {
      },
      cc: {
      }, // for component data 
      MCiter: 5e3,
      anneal : 25,
      anneal_iter : 3,
      number_of_loglets: 1,
      energy_best : 1e23,
      parameters : {
        a0 : 1.0,
        k0 : 1.0,
        b0 : 1.0,
      },
      constraints: {
        alow0: 12.0,
        ahigh0: 1.0,
        klow0: 1.0,
        khigh0: 1.0,
        blow0: 1.0,
        bhigh0: 1.0,
      },

    };

  var number_of_loglets;

  var numberpulsesDOM = document.getElementById("1logletradio");
  if (numberpulsesDOM.checked) {
    number_of_loglets = 1;
  }
  numberpulsesDOM = document.getElementById("2logletradio");
  if (numberpulsesDOM.checked) {
    number_of_loglets = 2;
  }

  numberpulsesDOM = document.getElementById("3logletradio");
  if (numberpulsesDOM.checked) {
    number_of_loglets = 3;
  }

  logobj.number_of_loglets = number_of_loglets;

  var x = [];
  var y = [];

  /* lets load in the data from the table */ 

  var datanumrows = document.getElementById("numrows");
  var numrows; 
  numrows = datanumrows.value;

  logobj.data.n = numrows;

  var datapoint;
  var datapointfill; 
  var i;

  /* get the data from the input table */ 
  for (i = 0 ; i < numrows ; i++ ) {
    datapoint = "x"+i;
    datapointfill = document.getElementById(datapoint);
    x[i] = parseFloat(datapointfill.value);
    datapoint = "y"+i;
    datapointfill = document.getElementById(datapoint);
    y[i] = parseFloat(datapointfill.value) ; 
  }
  
  logobj.data.x = x.slice(0);
  logobj.data.y = y.slice(0);
  

  /* get constraint values from the constraint input table */ 
  var constraintId;
  for (i = 0 ; i < number_of_loglets ; i++ ) {

    if (i == 0) { // first logistic pulse is a 4 parameter logisitc (displacement) 
      constraintId = document.getElementById('dlow');
      logobj.constraints['dlow'] = parseFloat(constraintId.value);
      constraintId = document.getElementById('dhigh');
      logobj.constraints['dhigh'] = parseFloat(constraintId.value);
    }

    constraintId = document.getElementById('alow' + i);
    logobj.constraints["alow"+i] = parseFloat(constraintId.value);
    constraintId = document.getElementById('ahigh' + i);
    logobj.constraints["ahigh"+i] = parseFloat(constraintId.value);
    
    constraintId = document.getElementById('klow' + i);
    logobj.constraints["klow"+i] = parseFloat(constraintId.value);
    constraintId = document.getElementById('khigh' + i);
    logobj.constraints["khigh"+i] = parseFloat(constraintId.value);
    
    constraintId = document.getElementById('blow' + i);
    logobj.constraints["blow"+i] = parseFloat(constraintId.value);
    constraintId = document.getElementById('bhigh' + i);
    logobj.constraints["bhigh"+i] = parseFloat(constraintId.value);
    
  }

  var plotwidth;
  var plotheight;
  var tmpDOM5 = document.getElementById("MCiter");
  logobj.MCiter = parseFloat(tmpDOM5.value);
  tmpDOM5 = document.getElementById("anneal");
  logobj.anneal = parseFloat(tmpDOM5.value);
  tmpDOM5 = document.getElementById("num_anneal");
  logobj.anneal_iter = parseFloat(tmpDOM5.value);
  tmpDOM5 = document.getElementById("flotwidth");
  plotwidth = parseInt(tmpDOM5.value);
  tmpDOM5 = document.getElementById("flotheight");
  plotheight = parseInt(tmpDOM5.value);

  var flothtml = [];
  var tmpDOM6 = document.getElementById("flotplots");
  var tmpDOMplotfit = document.getElementById("plotfit");
  var tmpDOMplotfp = document.getElementById("plotfp");
  var tmpDOMplotcc = document.getElementById("plotcc");
  var tmpDOMplotres = document.getElementById("plotres");
  flothtml.push('<table border=2>');
  if (tmpDOMplotfit.checked) {
    flothtml.push('<tr><td><div id="placeholder"');
    flothtml.push('style="width:'+ plotwidth +'px;height:' + plotheight + 'px;" ></div></td></tr>');
  }
  if (tmpDOMplotfp.checked) {
    flothtml.push('<tr><td><div id="placeholder2" ');
    flothtml.push('style="width:'+plotwidth+'px;height:'+plotheight+'px;"></div>');
  }
  if (tmpDOMplotcc.checked) {
    flothtml.push('<tr><td><div id="placeholder3" ');
    flothtml.push('style="width:'+plotwidth+'px;height:'+plotheight+'px;"></div>');
  }
  if (tmpDOMplotres.checked){
    flothtml.push('<tr><td><div id="placeholder4" ');
    flothtml.push('style="width:'+plotwidth+'px;height:'+(plotheight / 4)+'px;"></div>');
  }
  flothtml.push('</td></tr></table>');
  tmpDOM6.innerHTML = flothtml.join('');

  /* what norm are we using */ 

  logobj.norm = sumsquares;
  var DOM_norm = document.getElementById("leastsquares");
  if (DOM_norm.checked) { logobj.norm = sumsquares ;}
  DOM_norm = document.getElementById("robust");
  if (DOM_norm.checked) { logobj.norm = robust ;}

  /* call the main Nonlinear Constrained Least Squares parameter Estimation function */ 
  /* and time it */ 
  var loglet_timer_start = new Date();
  loglet_MC_anneal_regression(logobj); 
  var loglet_timer_stop = new Date();

  /* create fitted curves from the parameters */ 
  create_curves(logobj); 


  if (tmpDOMplotfit.checked) {

    /* flot plotting */ 
    $.plot($("#placeholder"), 
           [
            {
            data: logobj.flot.curve,
                lines: { show: true},
                color: "blue"
                },
            {
            data: logobj.flot.data,
                points: { show: true, fillColor: "red"},
                color: "red"
                },
            ]);
  
  }

  flotarray = [];
  /* the fisher pry munged data */
  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    flotarray.push(  {
      data: logobj.flot["fpdata"+j],
      points: { show: true, fillColor: "red"},
      color: "red"
    });
  }

  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    flotarray.push(  {
      data: logobj.flot["fpline"+j],
      lines: { show: true},
      color: "blue"
    });
  }



  if (tmpDOMplotfp.checked) {
    $.plot($("#placeholder2"), 
           flotarray,
           {
           yaxis: {min: -2, max: 2,
                 ticks: [[-2,"1%"] , [-1,"10%"], [0,"50%"],  [1 , "90%"], [2,"99%"]]}
           }
           );
    
  }


//------------------------------------------------------------------------
//------------------------------------------------------------------------
 flotarray2 = [];
  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    flotarray2.push(  {
      data: logobj.flot["ccdata"+j],
      points: { show: true, fillColor: "red"},
      color: "red"
    });
  }

  for (j = 0 ; j < logobj.number_of_loglets ; j++) {
    flotarray2.push(  {
      data: logobj.flot["cccurve"+j],
      lines: { show: true},
      color: "blue"
    });
  }



  if (tmpDOMplotcc.checked) {
    $.plot($("#placeholder3"), 
           flotarray2 );
  }




//------------------------------------------------------------------------
//------------------------------------------------------------------------


  if (tmpDOMplotres.checked) {

    /* residual plotting  (Standardized residuals)*/ 
    $.plot($("#placeholder4"), 
           [
            {
            data: logobj.flot.resline,
                lines: { show: true},
                color: "blue"
                },
            {
            data: logobj.flot.sr, 
                points: { show: true, fillColor: "red"},
                color: "red"
                },
            ]);
  
  }
 
  var html = []; /* start the HTML output string */
  html.push('<h5>');
  for (i = 0 ; i < logobj.number_of_loglets ; i++) {
    html.push('Loglet Pulse ' +  (i + 1) + '<br>');
    if (i == 0) { 
      html.push('d = ');
      html.push(logobj.parameters["d"].toPrecision(4));
      html.push('<br>');
    }
    
    html.push('a (dt) = ');
    html.push(logobj.parameters["a"+i].toPrecision(4));
    html.push('<br>k (carry cap) = ');
    html.push(logobj.parameters["k"+i].toPrecision(4));
    html.push('<br>b (tm midpoint) = ');
    html.push(logobj.parameters["b"+i].toPrecision(4));
    html.push('<p>');
  }
  html.push('<p>');
  html.push('<br>energy (depends on norm (L2 or L1)) = ');
  html.push(logobj.energy_best.toPrecision(7));
  html.push('<br>time in ms = ' + (loglet_timer_stop - loglet_timer_start));
  html.push('<p>');
  html.push('</h5>');


  /**
   * Set the generated html into the container div.
   */
  var LogletParameters =  document.getElementById("logletParameters");
  LogletParameters.innerHTML = html.join('');

  /*    DUMP OUT DATA in MATLAB format */ 

  dump_data_comma = document.getElementById('psmradio2');
  dump_data_tab = document.getElementById('psmradio1');

  if (dump_data_comma.checked) {var delim = '  ,  ';}
  if (dump_data_tab.checked) {var delim = '\t';}
  
  var psmtxtout = document.getElementById('psmoutput');

  psmtxtout.value = 'data = [ \n';
  for (i=0 ; i < logobj.data.n ; i++) {
    psmtxtout.value += logobj.data.x[i] + delim + logobj.data.y[i] + '\n';
  }
  psmtxtout.value += ']\n\n'; 

  psmtxtout.value += 'residuals = [ \n';
  for (i=0 ; i < logobj.data.n ; i++) {
    psmtxtout.value += logobj.data.x[i] + delim + logobj.data.residuals[i] + '\n';
  }
  psmtxtout.value += ']\n\n'; 

  psmtxtout.value += 'standardized_residuals = [ \n';
  for (i=0 ; i < logobj.data.n ; i++) {
    psmtxtout.value += logobj.data.x[i] + delim + logobj.data.sr[i] + '\n';
  }
  psmtxtout.value += ']\n\n'; 

  psmtxtout.value += 'curve = [ \n';
  for (i=0 ; i < logobj.curve.n ; i++) {
    psmtxtout.value += logobj.curve.t[i] + delim + logobj.curve.yfit[i] + '\n';
  }
  psmtxtout.value += ']\n\n'; 

  
  var n;
  var k;
  for (n = 0 ; n < logobj.number_of_loglets ; n++ ) {
    psmtxtout.value += 'fpdata' + n + ' = [ \n' ;
    for (k = 0 ; k < logobj.fp["datax"+n].length ; k++ ) {
      psmtxtout.value += logobj.fp["datax"+n][k] + delim 
        +  logobj.fp["datay"+n][k] + '\n';
    }
    psmtxtout.value += ']\n\n'; 
  }


  for (n = 0 ; n < logobj.number_of_loglets ; n++ ) {
    psmtxtout.value += 'fpline' + n + ' = [ \n' ;
    for (k = 0 ; k < logobj.fp["linex"+n].length ; k++ ) {
      psmtxtout.value += logobj.fp["linex"+n][k] + delim 
        +  logobj.fp["liney"+n][k] + '\n';
    }
    psmtxtout.value += ']\n\n'; 
  }

  
  psmtxtout.value += 'paramaters_a_k_b = [ \n';
  for (n = 0 ; n < logobj.number_of_loglets ; n++ ) {
    psmtxtout.value += logobj.parameters["a"+n] + delim +
      + logobj.parameters["k"+n] + delim +
      + logobj.parameters["b"+n] + '\n';
  }
  psmtxtout.value += ']\n\n'; 

 for (n = 0 ; n < logobj.number_of_loglets ; n++ ) {
    psmtxtout.value += 'ccdata' + n + ' = [ \n' ;
    for (k = 0 ; k < logobj.cc["datax"+n].length ; k++ ) {
      psmtxtout.value += logobj.cc["datax"+n][k] + delim 
        +  logobj.cc["datay"+n][k] + '\n';
    }
    psmtxtout.value += ']\n\n'; 
  }

 for (n = 0 ; n < logobj.number_of_loglets ; n++ ) {
    psmtxtout.value += 'cccurve' + n + ' = [ \n' ;
    for (k = 0 ; k < logobj.cc["linex"+n].length ; k++ ) {
      psmtxtout.value += logobj.cc["linex"+n][k] + delim 
        +  logobj.cc["liney"+n][k] + '\n';
    }
    psmtxtout.value += ']\n\n'; 
  }



}

/*******   END of Xpsmlogfun() **********************************/


function psmcreateform() {

  var html = [];
  var numrows = 0;
  var x = [];
  var y = [];
  var xlabel;
  var ylabel;
  var description;
  var constraints = {};
  var number_of_loglets;

  var psmdom2 =  document.getElementById("DOM2");
  var datafill = document.getElementById("filldata");

  if (datafill.value != "user data") {
    
    $.ajaxSetup({ async: false});
    $.getJSON(datafill.value + '.json', function (json) 
              {
                x = json.data.x.slice(0);
                y = json.data.y.slice(0);
                xlabel= json.data.xlabel;
                ylabel = json.data.ylabel;
                description = json.data.description;
                number_of_loglets = json.data.number_of_loglets;
              }
              );
    numrows = x.length;
    html.push('<br>' + description + '<br>');
    html.push('<table border=1>');
    html.push('<tr><td>' + xlabel + '</td><td>' + ylabel + '</td></tr>')

  }
  else {
    html.push("<br> User Data</br>");
    html.push('<table border=1>');
    html.push('<tr><td>x</td><td>y</td></tr>');

    var psmtext = document.getElementById("psmtext");

    var st = psmtext.value;
    var Ast = st.split("\n"); // split into rows 
    var paste_numrows = Ast.length; 
    var ii;
    var xs = [];
    var ys = [];
    for (ii = 0 ; ii < paste_numrows ; ii++) {
      // tab or comma deliminated data
      if ( Ast[ii].split(",",2)[1] != null ){ ys[ii] = Ast[ii].split(",")[1]; xs[ii] = Ast[ii].split(",")[0];}
      if ( Ast[ii].split("\t",2)[1] != null ){ ys[ii] = Ast[ii].split("\t")[1]; xs[ii] = Ast[ii].split("\t")[0];}
    }
    var xss = [];
    var yss = [];
    var numgoodrows = 0;
    var iii =0;
    for (ii = 0 ; ii < paste_numrows ; ii++) { 
      if ( xs[ii] != null && ys[ii] != null) {
        xss[iii] = xs[ii];
        yss[iii] = ys[ii];
        iii++;
      }
    }
    numgoodrows = iii;
    numrows = numgoodrows; 
    // parse the floating point values
    for (ii = 0 ; ii < numgoodrows ; ii++) { 
      x[ii] = parseFloat(xss[ii]);
      y[ii] = parseFloat(yss[ii]);
    }

  }

  var i;

  for (i = 0 ; i < numrows ; i++) {
    html.push('<tr>');
    html.push('<td> <input type="text" id="x' + i + '" size=7> </td>');
    html.push('<td> <input type="text" id="y' + i + '" size=11> </td>');
    html.push('</tr>');
  }
  html.push('</table>');


  /*  Set the generated html into the container div. */
  // up above now var psmdom2 =  document.getElementById("DOM2");

  html.push('<p>number of data points:<br>');
  html.push('<input type="text" id="numrows" size=7>');
  //html.push('<br><input type="text" id="newfilename" size=14> ');
  psmdom2.innerHTML = html.join('');
  
  var datanumrows = document.getElementById("numrows");
  datanumrows.value = numrows;
 
  //var newfilename = document.getElementById("newfilename");
  //newfilename = datafill.value + '.json';
  


  var datapoint;
  var datapointfill; 


  for (i = 0 ; i < numrows ; i++ ) {
    datapoint = "x"+i;
    datapointfill = document.getElementById(datapoint);
    datapointfill.value = x[i];
    datapoint = "y"+i;
    datapointfill = document.getElementById(datapoint);
    datapointfill.value = y[i];
  }

  
  var tmpDOMloglet;
  if (parseInt(number_of_loglets) === 1) {
    tmpDOMloglet = document.getElementById("1logletradio");
    tmpDOMloglet.checked = true;
  }
  if (parseInt(number_of_loglets) === 2) {
    tmpDOMloglet = document.getElementById("2logletradio");
    tmpDOMloglet.checked = true;
  }
  if (parseInt(number_of_loglets) === 3) {
    tmpDOMloglet = document.getElementById("3logletradio");
    tmpDOMloglet.checked = true;
  }

  /* lets set jQuery back to the default async mode, in case flot */
  /* or other libraries needs it to be the default */
  $.ajaxSetup({ async: true});
}



function create_constraints() {
  
  var number_of_loglets;

  var numberpulsesDOM = document.getElementById("1logletradio");
  if (numberpulsesDOM.checked ) {
    number_of_loglets = 1;
  }
  numberpulsesDOM = document.getElementById("2logletradio");
  if (numberpulsesDOM.checked ) {
    number_of_loglets = 2;
  }

  numberpulsesDOM = document.getElementById("3logletradio");
  if (numberpulsesDOM.checked ) {
    number_of_loglets = 3;
  }

  var datafill = document.getElementById("filldata");
  var datanumrows = document.getElementById("numrows");
  var constraints = {}; 
  var annealparam;
  var anneal_iter;
  var MCiter;  
  if (datafill.value != "user data") {
    $.ajaxSetup({ async: false});

    $.getJSON(datafill.value + '.json', function (json) 
              {
                MCiter = json.data.MCiter;
                annealparam = json.data.annealparam;
                anneal_iter = json.data.anneal_iter;
                constraints = json.data.constraints;
              }
             );
  }    
  
  var html2 = [];
  /* create contstraint input table */ 
  for (i = 0 ; i<number_of_loglets ; i++) {
    html2.push('pulse = ' + (i + 1)); 
    html2.push('<table>');
    html2.push('<tr>');
    if (i == 0) {
      html2.push('<td>disp (d)</td>');
      html2.push('<td><input type="text" size=7 id="dlow"></td>');
      html2.push('<td><input type="text" size=7 id="dhigh"></td>');
      html2.push('</tr><tr>');
    }
    html2.push('<td>a (growth rate)</td>');
    html2.push('<td><input type="text" size=7 id="alow' + i + '"></td>');
    html2.push('<td><input type="text" size=7 id="ahigh' + i + '"></td>');
    html2.push('</tr><tr>');
    html2.push('<td>k (carrying capacity)');
    html2.push('<td><input type="text" size=7 id="klow' + i + '"></td>');
    html2.push('<td><input type="text" size=7 id="khigh' + i + '"></td>');
    html2.push('</tr><tr>');
    html2.push('<td>b (midpoint)');
    html2.push('<td><input type="text" size=7 id="blow' + i + '"></td>');
    html2.push('<td><input type="text" size=7 id="bhigh' + i + '"></td>');
    html2.push('</tr>');
    html2.push('</table>');
    html2.push('<p>');
  }

  if (datafill.value !== "user data") {
    var tmpDOM4 = document.getElementById("MCiter");
    if (MCiter != null) tmpDOM4.value = MCiter;
    tmpDOM4 = document.getElementById("anneal");
    if( annealparam != null) tmpDOM4.value = annealparam;
    tmpDOM4 = document.getElementById("num_anneal");
    if (anneal_iter != null) tmpDOM4.value = anneal_iter;
    var psmdom3 = document.getElementById("constraints");
    psmdom3.innerHTML = html2.join('');
    var constraintId;
    for (i = 0 ; i < number_of_loglets ; i++ ) {

      if (i == 0) {
        constraintId = document.getElementById('dlow');
        constraintId.value = 0.0;
        constraintId = document.getElementById('dhigh');
        constraintId.value = 0.0;
      }

      constraintId = document.getElementById('alow' + i);
      if (  constraints['alow'+i] != null)  constraintId.value = constraints['alow'+i];
      constraintId = document.getElementById('ahigh' + i);
      if (  constraints['ahigh'+i] != null)  constraintId.value = constraints['ahigh'+i];

      constraintId = document.getElementById('klow' + i);
      if (  constraints['klow'+i] != null)  constraintId.value = constraints['klow'+i];
      constraintId = document.getElementById('khigh' + i);
      if (  constraints['khigh'+i] != null)  constraintId.value = constraints['khigh'+i];

      constraintId = document.getElementById('blow' + i);
      if (  constraints['blow'+i] != null)  constraintId.value = constraints['blow'+i];
      constraintId = document.getElementById('bhigh' + i);
      if (  constraints['bhigh'+i] != null)  constraintId.value = constraints['bhigh'+i];

    }
  }
  else {
    var psmdom3 = document.getElementById("constraints");
    psmdom3.innerHTML = html2.join('');
  }

  /* lets set jQuery back to the default async mode, in case flot */
  /* or other libraries needs it to be the default */
  $.ajaxSetup({ async: true});
}


function guessconstraints() {

  var x = new Array();
  var y = new Array();  

  /* lets load in the data from the table */ 

  var datanumrows = document.getElementById("numrows");
  var numrows; 
  numrows = datanumrows.value;
  var datapoint;
  var datapointfill; 

  /* get the data from the input table */ 
  for (var i = 0 ; i < numrows ; i++ ) {
    datapoint = "x"+i;
    datapointfill = document.getElementById(datapoint);
    x[i] = parseFloat(datapointfill.value);
    datapoint = "y"+i;
    datapointfill = document.getElementById(datapoint);
    y[i] = parseFloat(datapointfill.value) ; 
  }

  var number_of_loglets;
  var numberpulsesDOM = document.getElementById("1logletradio");
  if (numberpulsesDOM.checked ) {
    number_of_loglets = 1;
  }
  numberpulsesDOM = document.getElementById("2logletradio");
  if (numberpulsesDOM.checked ) {
    number_of_loglets = 2;
  }

  numberpulsesDOM = document.getElementById("3logletradio");
  if (numberpulsesDOM.checked ) {
    number_of_loglets = 3;
  }


  /* hueristic guess of starting constraints */
  var rows = x.length; 
  var xstart = x[0];
  var xstop  = x[rows-1];
  var ystart = y[0];
  var ystop =  y[rows - 1]; 

  var datapoint;
  var datapointefill;
  var constraintID;
  // set displacement to zero by default 
  constraintId = document.getElementById('dlow');
  constraintId.value = 0.0;
  constraintId = document.getElementById('dhigh');
  constraintId.value = 0.0;

  for (i = 0 ; i < number_of_loglets ; i++ ) {
    datapoint = "alow"+i;
    datapointfill = document.getElementById(datapoint);
    datapointfill.value = (xstop - xstart) / (8 * number_of_loglets);
    datapoint = "ahigh"+i;
    datapointfill = document.getElementById(datapoint);
    datapointfill.value = (xstop - xstart) * (1 / number_of_loglets);

    datapoint = "klow"+i;
    datapointfill = document.getElementById(datapoint);
    datapointfill.value = (ystop - ystart) / (number_of_loglets * 2);
    datapoint = "khigh"+i;
    datapointfill = document.getElementById(datapoint);
    datapointfill.value = (ystop - ystart) * (4 / number_of_loglets);

    datapoint = "blow"+i;
    datapointfill = document.getElementById(datapoint);
    datapointfill.value = xstart +  (((xstop - xstart)/number_of_loglets) * (i));
    datapoint = "bhigh"+i;
    datapointfill = document.getElementById(datapoint);
    datapointfill.value = xstart + (((xstop - xstart)/number_of_loglets) * (i+1));

  }
  
}


