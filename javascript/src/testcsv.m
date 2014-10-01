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

loglet = @(t,a,k,b)( (k ./ (1 + exp( -1.0 .*(log(81)./a) .* (t - b)))));

randnum = @(low,high) ( (high - low).* rand() + low);

%% REMEMBER to set number loglets 


t = 0:0.1:6;
t = t';

a = 2;
k = 1.0;
tm = 3;

noise = randn(size(t)) * 0.02;;

y = loglet(t,a,k,tm) + noise;

plot(t,y)

data = [t,y];

xlswrite('testdata.csv',data)






%% end msli tex

