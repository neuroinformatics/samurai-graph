# Data creating script using SATELLITE 4.
# Example1.txt
dataset = 100;
x1 = (0~dataset)/dataset;
y1 = sin(x1*2*PI);
buffer2text("Example1.txt","%4.2f   %6.4f\r\n",x1, y1);

# Example2.txt
dataset = 25;
x2 = (0~dataset)/dataset;
y2 = cos(x2*2*PI);
el2 = 0-abs(nrand(dataset+1,1,0,0.05));
eh2 = abs(nrand(dataset+1,2,0,0.05));
buffer2text("Example2.txt","%4.2f   %6.4f   %6.4f   %6.4f\r\n",x2, y2, el2, eh2);

# Example3.txt
dataset = 20
string x3label_data[dataset+1];
x3label_data[0]  = "A"; x3label_data[1]  = "B"; x3label_data[2]  = "C";
x3label_data[3]  = "D"; x3label_data[4]  = "E"; x3label_data[5]  = "F";
x3label_data[6]  = "G"; x3label_data[7]  = "H"; x3label_data[8]  = "I";
x3label_data[9]  = "J"; x3label_data[10] = "K"; x3label_data[11] = "L";
x3label_data[12] = "M"; x3label_data[13] = "N"; x3label_data[14] = "O";
x3label_data[15] = "P"; x3label_data[16] = "Q"; x3label_data[17] = "R";
x3label_data[18] = "S"; x3label_data[19] = "T"; x3label_data[20] = "U";
x3 = (0~dataset)/dataset;
y3 = sin(x3*PI) * 1.5;
string x3label[dataset+1];
for(i=0; i<dataset+1; i++){
  if(i<10){ a=String(i); }else{ a="{" + i + "}"; }
  x3label[i] = x3label_data[i] + "^" + a + "_i";
}
buffer2text("Example3.txt","%4.2f   %6.4f   \"%s\"\r\n",x3, y3, x3label);

# Example4.txt
dataset = 20
string x4label_data[dataset+1];
x4label_data[0]  = "A"; x4label_data[1]  = "B"; x4label_data[2]  = "C";
x4label_data[3]  = "D"; x4label_data[4]  = "E"; x4label_data[5]  = "F";
x4label_data[6]  = "G"; x4label_data[7]  = "H"; x4label_data[8]  = "I";
x4label_data[9]  = "J"; x4label_data[10] = "K"; x4label_data[11] = "L";
x4label_data[12] = "M"; x4label_data[13] = "N"; x4label_data[14] = "O";
x4label_data[15] = "P"; x4label_data[16] = "Q"; x4label_data[17] = "R";
x4label_data[18] = "S"; x4label_data[19] = "T"; x4label_data[20] = "U";
x4 = (0~dataset)/dataset;
y4 = 1-(1/(1+exp(-((10*x4)-5))))
string x4label[dataset+1];
for(i=0; i<dataset+1; i++){
  if(i<10){ a=String(i); }else{ a="{" + i + "}"; }
  x4label[i] = x4label_data[i] + "^" + a + "_i";
}
el4 = 0-abs(nrand(dataset+1,3,0,0.005));
eh4 = abs(nrand(dataset+1,4,0,0.005));
buffer2text("Example4.txt","%4.2f   %6.4f   %6.4f   %6.4f   \"%s\"\r\n",x4, y4, el4, eh4, x4label);

# Example5.txt
dataset = 20
x5 = (0~dataset)/dataset;
y5 = exp(-x5);
buffer2text("Example5.txt","%4.2f   %6.4f\r\n",x5, y5);

# Example6.txt
dataset = 25
x5 = (0~dataset)/dataset;
y5a = sin(x5*2*PI) * 0.6;
y5b = sin(x5*2*PI) * 0.8;
y5c = sin(x5*2*PI) * 1.0;
y5d = sin(x5*2*PI) * 1.2;
buffer2text("Example6.txt","%4.2f   %6.4f   %6.4f   %6.4f   %6.4f\r\n",x5, y5a,y5b,y5c,y5d);

# Example7.txt
dataset = 25
x6 = (0~dataset)/dataset;
y6a = sin((x6 - 0.0)*2*PI);
y6b = sin((x6 - 0.05)*2*PI);
y6c = sin((x6 - 0.10)*2*PI);
y6d = sin((x6 - 0.15)*2*PI);
buffer2text("Example7.txt","%6.4f   %6.4f   %6.4f   %6.4f\r\n", y6a,y6b,y6c,y6d);
