## cameron -- the javascript code is confusing things.  lets just ignore it.

## i'm going to write pseudo code which I want you to translate into R()
## please use my simple variable names for now, we can create a data structure later if we need to. 

## please create a new R file called cc_sunflower.R and check it into git.
## as you make progress below, please check cc_sunflower.R into git (dont change the name)



## the hardcoded sunflower data 
x = c(7.0, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84)
y = c(17.93, 36.36, 67.76, 98.1, 131, 169.5, 205.5, 228.3, 247.1, 250.5, 253.8, 254.5)

## for now we are only going to have three paremeters (forget displacement (d))
# and initialize them to zero for now
# a <- 0
# k <- 0
# b <- 0

## we need to provide an initial guess for the simple three parameter logistic

a_guess=40
k_guess=250
b_guess=30

## now perform the nonlinear regression

# sunlogistic <- nls(sy ~ ( k / (1 + exp( (log(81)/-a) * (sx - b)))),start=c(k_guess, a_guess, b_guess))
# eventually this should be a function call but fine to hardcode this for now

## now extract the fitted parameters

## ( have not used R for a long time, this is psuedocode). you have to figure this one out yourself. 
a <- sunlogistc(a)
k <- sunlogistc(k)
b <- sunlogistc(b)

## now print a,k,b to the screen

print_to_screen(a,k,b)

## a should be ~50.16
## k should be ~260.9
## b should be ~34.2

## let me know if this makes sense.  
## once this is working I can create simple pseudocode and walk you through generating the curves to plot
## (perrin)






