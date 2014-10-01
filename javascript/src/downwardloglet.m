clear all
close all
more off
format short e 
rand('twister',sum(100*clock))

%% begin msli tex 
%% loglets for web 2.0
%% \[
%% p(t) = \frac{\kappa}{1 + \exp\left(\frac{\log_e(81)}{-\alpha} (t - \beta)\right)}
%% \]

%% \[
%% p(t) = \frac{k}{1 + \exp\left(\frac{\log_e(81)}{-a} (t - b)\right)}
%% \]

%% note that alpha = 0 could be numerical issue 

loglet = @(t,a,k,b)( (k ./ (1 + exp( -1.0 .*(log(81)./a) .* (t - b)))))

randnum = @(low,high) ( (high - low).* rand() + low)

t = 1900:5:2000;
k = 100;
a = -50;
b = 1950;

y = loglet(t,a,k,b);

for i=1:(max(size(y))),

  y(i) = y(i) + randnum(-2,2);
  
end





plot(t,y,'-og')




%% end msli tex

