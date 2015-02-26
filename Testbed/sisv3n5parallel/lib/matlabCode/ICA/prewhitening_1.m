function [P R]=prewhitening_1(x,k)
Rxx=x*x';
[u,d,v]=svd(Rxx); 
P=u(:,1:k);
R=d(1:k,1:k);