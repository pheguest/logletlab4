
function milo() {
  
  function factorial(x) {
    
    var xint = Math.abs(Math.floor(x)); // abs() is trying to stop bugs 
    var tmpfac = x;
    for (var iter = 1 ; iter < xint ; iter++) {
      tmpfac = tmpfac * (x - iter);
    }
    
    return tmpfac;
  }


  var html = []; /* start the HTML output string */
  var MiloColor = document.getElementById("color");
  var MiloFontSize=document.getElementById("fontsize");
  html.push('<font color="'+MiloColor.value + '" size="' + parseInt(MiloFontSize.value) + '" >');
  var MiloDiv=  document.getElementById("milodiv");

  with(Math){
    var MiloInput = document.getElementById("num");
    var num = parseFloat(eval(MiloInput.value));
    var firstnum_element = document.getElementById("firstnum");
    var firstnum = parseFloat(eval(firstnum_element.value));
    var step_element = document.getElementById("step");
    var step = parseFloat(eval(step_element.value));
    var MiloFunc = document.getElementById("func");
  }
  
  var flotarray = [];
  var i;
  var fx;
  var x = firstnum;
  for (i = 0 ; i < num ; i++ ){
    with(Math){
      x = parseFloat(firstnum + (i * step)); 
      html.push('x = ' + x + '  , ');
      fx = parseFloat(eval(MiloFunc.value));
      html.push('f(x) = ' + fx +'<br>');
      flotarray.push([x,fx]);
    }
  }

  html.push('</font>');
  MiloDiv.innerHTML = html.join('');

  /* flot plotting */
  $.plot($("#miloplot"), [{
      data: flotarray,
          points: { show: true, fillColor: MiloColor.value, radius: 5},
          color: MiloColor.value}]
        );



}
