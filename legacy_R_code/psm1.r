
## begin msli tex

## learing R $R = S^2$
## test of tex text

# test of r comment 
require(graphics)

with(cars, {
    plot(speed, dist)
    lines(supsmu(speed, dist))
    lines(supsmu(speed, dist, bass = 7), lty = 2)
    })

## end msli tex
