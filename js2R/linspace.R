linspace <- function(xstart,xstop,n) {

  x = 0 
  i = 1 #counter

  step = 0.0 #arbitrary 

  step = (xstop - xstart) / (n - 1.0)

  x[0] = xstart

  x[n-1] = xstop

  while(i <= (n-2)){
    x[i] = xstart + (i * step)
    i = i+1
  }

  return (x)

}

