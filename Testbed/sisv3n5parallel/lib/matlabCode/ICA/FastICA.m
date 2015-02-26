function W=FastICA(ini,Z,epsilon,g)
[dim N]=size(Z);
W=ini;
W=W*real(inv(W'*W)^(1/2));
Wold=zeros(dim,dim);
crit=zeros(1,dim);
while(1-min(crit)>epsilon)
    Wold=W;
    switch g
        case 1
            W=(Z*((Z'*W).^ 3))/N-3*W;
        case 2
            U=Z'*W;
            Usquared=U.^2;
            ex=exp(-Usquared/2);
            gauss=U.*ex;
            dGauss=(1-Usquared).*ex;
            W=Z*gauss/N-ones(dim,1)*sum(dGauss).*W/N;
    end            
    W=W*real(inv(W'*W)^(1/2));
    crit=abs(sum(W.*Wold));            
end
