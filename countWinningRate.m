%function printData(x,y)
figure
test = xlsread('survival1.xlsx', 'E1:E3000');

y=test(:,1);

B = reshape(y,[30,100]);

for i=[1:30]
C(i)=sum(B(i,:)==7)/100;
%countWinningRate=sum(B(:1)==7)/100
end

x=[1:30];
plot(x,C)

title('Winning Rate in every 100 battles ');
%scatter3(x,y,z,'filled')
xlabel('Number of 100 battles'); % x-axis label
ylabel('Winning Rate');% y-axis label
