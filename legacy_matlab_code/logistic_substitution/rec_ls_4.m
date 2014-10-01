%psm 2-4-98

clear all
close all

load rff.mat


%% new data 1996   2900     778900  225300

new = [1996   2900     778900  225300];


temp = new(2) + new(3) + new(4);
tf(1,1) = 1996;
tf(1,2) = new(:,2) / temp;
tf(1,3) = new(:,3) / temp;
tf(1,4) = new(:,4) / temp;

tff(1,1) = tf(1,1);
tff(1,2) = tf(1,2) / (1 - tf(1,2));
tff(1,4) = tf(1,3) / (1 - tf(1,3));
tff(1,3) = tf(1,4) / (1 - tf(1,4));

rff = [rff;tff];

rec_media = [

1975   257000   0   16200
1976   273000   0   21800
1977   344000   0   36900
1978   341300   0   61300
1979   318300   0   82800
1980   322800   0   110200
1981   295200   0   137000
1982   243900   0   182300
1983   209600   800     236800

1984   204600   5800    332000
1985   167000   22600   339100
1986   125200   53000   344500
1987   107000   102100  410000
1988   72400    149700  450100
1989   34600    207200  446200
1990   11700    286500  442200
1991   4800     333300  360100
1992   2300     407500  366400
1993   1200     495400	339500
1994   1900     662100	345400
1995   2200     727600  272600
1996   2900     778900  225300
];



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%% LS - MODEL %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



t_start = 1976;
t_end = 2011;
h = 0.01;

t=t_start:h:t_end;
t = t';

st = size(t);
st = st(1);

%% Number of technologies
N_Techs = 4;

LS = zeros(st + 2,N_Techs + 1);
LS(3:st+2,1) = t;

%%%%%%%%%%%%%%%%%% PARAMETER ESTIMATES, from least-squares, or guesses %%%%%

%first declining logistic


param = polyfit(rff(1:10,1),log(rff(1:10,2)),1);
dtguess1 = log(81) / param(1);
tmguess1 =  -1 * param(2) / param(1);

LS(1,2) = dtguess1;
LS(2,2) = tmguess1;

LS(1,3) = -dtguess1;
LS(2,3) = tmguess1;

param2 = polyfit(rff(10:19,1),log(rff(10:19,4)),1);
dtguess2 = log(81) / param2(1);
tmguess2 =  -1 * param2(2) / param2(1);

LS(1,4) = dtguess2;
LS(2,4) = tmguess2;

LS(1,5) = 13;
LS(2,5) = 2012;

LSFF = LS;


%%% technology number 2 is saturation first
sat = 2;


%%%%%%%%%%%%%%%%% MAIN LS ALGORITM  %%%%%%%%%%%%%%%%%%%%%%%%%%5

for i=1:st,

   for j=1:N_Techs,
    
      if (j ~= sat),
	 dt = LS(1,j+1);
	 a = log(81) / dt;
         tm = LS(2,j+1);
         LS(i+2,j+1) = 1 / ( 1 + exp( -a * (LS(i+2,1) - tm))) ;
      end
   end

   temp = 0.0;
   for j=1:N_Techs,
      if (j ~= sat),
	 temp = temp + LS(i+2,j+1); 
      end
   end
   LS(i+2,sat+1) = 1 - temp;

   for j=1:N_Techs,
     LSFF(i+2,j+1) = LS(i+2,j+1) ./ ( 1 - LS(i+2,j+1));
   end

%%%%%%%%%%%%%%%%%% SATURATION DETERMINATION %%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%% from conversation with Naki  

   if (i > 10 & sat <= N_Techs),
     yh = log(LSFF(i+2,sat+1));
     y0 = log(LSFF(i+1,sat+1))	;
     y_prime = (yh - y0) / h;

    
     test = diff(log(LSFF(i-9:i+2,sat+1))) / h;
     test2 = diff(test) / h;
     test3 = diff(test2) / h;
   end
%%%%%%%% Switching between the non-logistic saturation function and the downward logistic
%%%%%%%% 
%%%%%%% test = y_prime
%%%%%%% test2 = y_double_prime
%%%%%%% test3 = y_triple_prime

%%% since we want to minimize ( y_double_prime / y_prime), we take
%%% derivitive and set it  equal to zero 
%%% (y_prime * y_triple_prime - (y_double_prime * y_double_prime) = zero
%%% 
%%% but we want the crossing, so we see when it's greater than
%%% zero, and  use that to set the downward curvature


   
     if (i > 10 & sat <= N_Techs & (test(end) * test3(end) - test2(end) * test2(end)) > 0),
     %%%% if (i == 10000),
       format short e
       disp('-----------------------------------------')
       y_prime
       test(end)
       disp(sat);
       disp(test3(end))
       disp(LS(i+2,1))
       disp('-----------------------------------------')
       disp(LS(1,:))
       disp(LS(2,:))
       disp('-----------------------------------------')
       %%% but this code is fine %%%%%%%%
       LS(1,sat+1) = log(81) / y_prime;
       LS(2,sat+1) = LS(i+2,1) + (1 / (log(81) / LS(1,sat+1))) *log( (1/LS(i+2,sat+1)) -1);
       sat = sat + 1;
       disp('-----------------------------------------')
       disp(LS(1,:))
       disp(LS(2,:))
       disp('-----------------------------------------')
     
     
     end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% END OF SATURATION FUNCTION 

end  % end of main LS loop

hold off

figure(1)

semilogy(LSFF(3:st+2,1),LSFF(3:st+2,2),'-k');
hold
for i=3:N_Techs,

  semilogy(LSFF(3:st+2,1),LSFF(3:st+2,i),'-k');
end
i= i +1;
  semilogy(LSFF(3:st+2,1),LSFF(3:st+2,i),'--k');



semilogy(rff(:,1),rff(:,2),'ok',rff(:,1),rff(:,3),'+k',rff(:,1),rff(:,4),'xk')

axis([t_start t_end 0.01 100])



xlabel('\fontsize{14}Year')
ylabel('\fontsize{14}F / ( 1 - F) ')
set(gca,'FontSize',14);
 
newaxis=axes('Position',get(gca,'Pos'),'Color','none');
set(newaxis,'YScale','log');
set(newaxis,'YLim',[0.01 100]);
set(newaxis,'TickLength',[0 0]);
set(newaxis,'Xlim',[t_start t_end]);
set(newaxis,'XTickLabel','');
set(newaxis,'YTickLabel',[' 1%';'10%';'50%';'90%';'99%']);
view(-359.9999999999,90);
set(gca,'FontSize',14);

text(1978,10,'\fontsize{14}Records')
text(1985,3,'\fontsize{14}Cassettes')
text(1997,14,'\fontsize{14}CD''s');
text(2006,0.1,'\fontsize{14}DVD''s ?')
%% print -deps2 rec_ls_4.eps


