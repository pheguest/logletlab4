# use source("psm3.r")
# to load this file 

## this function seems to clear the workspace
rm(list=ls())

# start up help web browser
help.start()

f = scan("f3.txt");
upr = scan("up15r.txt")
upi = scan("up15i.txt")

## try to make a complex vector ...

p = upr + (upi * 1.0i);

# plot(f,20 * log10(abs(p)),log="x")

# plot(f,Arg(p) * (180.0/pi),log="x")

## both these plots seem to work.  
## pi is constant pi 


## from lotka.fit loglet fit file, Lotka publications
lx = scan("lotkax.txt")
ly = scan("lotkay.txt")

sx = scan("sunx.txt")
sy = scan("suny.txt")

sunlinear <- lm(sy ~ sx)
# this is not showing up for some reason ... 
# summary(sunlinear) 
plot(sx,sy)
abline(sunlinear)



