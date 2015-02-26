function accuracy = ICA_I(par_name,par_imgPath,par_rstPath,par_K)
%ICA_I('yale','E:\SIS\workspace\FaceRecognition\Data\Face\yale\','E:\SIS\workspace\FaceRecognition\Data\Result\ICA\',37)
%par_name='yale';
%par_imgPath='E:\SIS\workspace\FaceRecognition\Data\Face\yale\';
%par_rstPath='E:\SIS\workspace\FaceRecognition\Data\Result\ICA\';
%par_K=37;%ѡ��ͶӰ����

%Initial variable
filelist=dir([par_imgPath,'*-01.bmp']);
c=size(filelist,1);

filelist=dir(strcat(par_imgPath,'001-*.bmp'));
p=size(filelist,1);

if(exist(par_rstPath,'dir')==0)
    mkdir(par_rstPath);
end

%��ȡͼ���С
a=imread(strcat(par_imgPath,'001-01.bmp'));
[M,N] = size(a);
NN=M*N; 
IMG=zeros(NN,c,p); 

for i=1:c 
    if i<10
        s1=['00',int2str(i)];
    elseif i<100
        s1=['0', int2str(i)];
    else s1=[int2str(i)];
    end
    for j=1:p 
        if j<10
            s2=['0', int2str(j)];
        else s2=[int2str(j)];
        end
        B=imread(strcat(par_imgPath,s1,'-',s2,'.bmp'));
        B=double(B); 
        B=reshape(B,[NN,1]); 
        IMG(:,i,j)=B; 
    end 
end 

ln=floor(p*0.5);    %ѵ��������
train=zeros(1,ln);            %�洢ѵ������λ��
test=zeros(1,(p-ln));         %�洢��������λ��
%ѡ��ѵ������
train=1:ln;           %����ǰ��ln��ͼ��ѵ������
test=ln+1:p;           %ʣ��p-ln��ͼ������������
              

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%ICA%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%fprintf('************ICAʵ����***********************\n');
c1=clock;
X1_=zeros(NN,ln*c);
for i=1:c
    for j=1:ln
        X1_(:,j+(i-1)*ln)=IMG(:,i,train(j));
    end
end
%X2_�ǲ���������
X2_=zeros(NN,(p-ln)*c);
for i=1:c
    for j=1:p-ln
        X2_(:,j+(i-1)*(p-ln))=IMG(:,i,test(j));
    end
end
%��ѵ���������Ļ�
mx=zeros(NN,1);
for i=1:ln*c
    mx=mx+X1_(:,i);
end
mx=(1/(ln*c)).*mx;
X1=zeros(NN,ln*c);
for i=1:ln*c
    X1(:,i)=X1_(:,i)-mx;
end
%�׻���������
x=X1';
k=par_K;%ѡ��ͶӰ����
%[P1 R]=prewhitening_1(x,k);

Rxx=x*x';
[u,d,v]=svd(Rxx); 
P1=u(:,1:k);
R=d(1:k,1:k);

P2=(inv(R)*P1'*x)';
Z=P2'*X1;
ini=randn(k,k);
epsilon=0.01;
g=1;
Wd=FastICA(ini,Z,epsilon,g);
W=Wd*P2';

%%%%%%%����ʶ��%%
recogintion=zeros(c*(p-ln), 3);
feature1=W*X1_;                          %��ȡѵ����������
feature2=W*X2_;                          %��ȡ������������
c2=clock;
%fprintf('������ȡʱ��:');etime(c2,c1)     %������ȡʱ��
wrong=0;
for i=1:c
    for j=1:(p-ln)
        d=1e6;
        a2=feature2(:,j+(i-1)*(p-ln));
        for a=1:c
            for b=1:ln
                a1=feature1(:,b+(a-1)*ln);
                d0=a2-a1;
                d0=norm(d0);
                if d>d0
                   d=d0;
                    index=a;
                end
            end
        end
        if index~=i;
            wrong=wrong+1;
        end
        recogintion((i-1)*(p-ln)+j,1)=i;
        recogintion((i-1)*(p-ln)+j,2)=j+ln;
        recogintion((i-1)*(p-ln)+j,3)=(index==i);

    end
end
% write the test result to txt
dlmwrite([par_rstPath,'ICA_', par_name,  '_K',num2str(par_K), '.txt'], recogintion);    

c3=clock;
%fprintf('����ʱ��:');etime(c3,c2)                      %����ʱ��
%fprintf('��ʶ��ʱ��:');etime(c3,c1)                      %��ʶ��ʱ��
accuracy=(c*(p-ln)-wrong)/(c*(p-ln)); %���ʶ����

