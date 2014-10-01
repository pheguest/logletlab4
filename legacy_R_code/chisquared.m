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

%% REMEMBER to set number loglets 


load sunx.txt
load suny.txt


% Parameters:
%   Estimate Std. Error t value Pr(>|t|)    
% k 261.0397     2.6360   99.03 5.54e-15 ***
% a  50.1036     1.7181   29.16 3.20e-10 ***
% b  34.2734     0.4594   74.61 7.06e-14 ***
% ---
% Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1 

% Residual standard error: 3.757 on 9 degrees of freedom

% Number of iterations to convergence: 5 
% Achieved convergence tolerance: 3.055e-06 

sunfit = loglet(sunx,50.1036,261.0397,34.2734);
x = linspace(min(sunx),max(sunx),256);
y = loglet(x,50.1036,261.0397,34.2734);

res = suny - sunfit;
res2 = res.^2
sumres2 = sum(res2)

chi2 = sum(res2 ./ sunfit)






hold off
plot(sunx,suny,'ok')
hold
plot(sunx,sunfit,'xk')
plot(x,y,'-g')

%% end msli tex


figure(2)
plot(sunx,res,'-ok')




