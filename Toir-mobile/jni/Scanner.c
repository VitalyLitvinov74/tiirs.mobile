#include <jni.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <thread_db.h>
#include "debug.h"
#include <termios.h>
int g_nDevice=0;
unsigned char const    checktype[1] = {0x63};
unsigned char const   findcard1[10]={0xAA,0xAA,0xAA,0x96,0x69,0x00,0x03,0x20,0x01,0x22};
unsigned char const selectcard1[10]={0xAA,0xAA,0xAA,0x96,0x69,0x00,0x03,0x20,0x02,0x21};
unsigned char const   readcard1[10]={0xAA,0xAA,0xAA,0x96,0x69,0x00,0x03,0x30,0x01,0x32};
unsigned char const   findcard_cmp1[15]=  {0xAA,0xAA,0xAA,0x96,0x69,0x00,0x08,0x00,0x00,0x9F,0x00,0x00,0x00,0x00,0x97};
unsigned char const   selectcard_cmp1[19]={0xAA,0xAA,0xAA,0x96,0x69,0x00,0x0C,0x00,0x00,0x90,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x9C};
#define TRANSFER_COUNT 4096
#define max_buffer_size   1295   /*定义缓冲区最大宽度*/

#define SCAN_TYPE_PATH "/data/bar_scan_type"
#define SCANNER_TYPE_RED 1
#define SCANNER_TYPE_LASER 2
#define SERIAL_STREAM_PATH		"/dev/ttyMT1"
#define CMD_DEVICE_PATH				"/dev/msm_io_cm7"

int g_type=-1;
char IsGB2312(char buf[],int nlen)
{
	char flag=0,nret=1,ntemp=0;
	int i=0;
	for(i=0;i<nlen;i++)
	{
		if((flag==1)&&((buf[i]>0xA0)&&(buf[i]<0xff)))
		{
			nret&= 1;
			flag=0;
			ntemp=1;
		}
		else if((flag==1)&&(buf[i]<0xa1))
		{
			nret&= 0;
			flag=0;
		}
		else if((flag==0)&&((buf[i]>0xA0)&&(buf[i]<0xf8)))
		{
			flag=1;
		}
		else if((flag==0)&&((buf[i]<0xA0)&&(buf[i]>127)))
		{
			flag=0;
			nret&= 0;
		}
		else
			flag=0;
	}
	if((nret==1)&&(ntemp==0))
	nret=0;
	return nret;
}

/*
 * Class:     Scanner
 * Method:    WindowsTojstring
 * Signature: (I)V
 */
JNIEXPORT jstring JNICALL WindowsTojstring( JNIEnv* env, char* buf,char isGB2312)
{
        jclass Class_string;  
        jmethodID mid_String,mid_getBytes;  
        jbyteArray bytes;  
        jbyte* log_utf8;  
        jstring codetype,jstr;  
        Class_string = (*env)->FindClass(env,"java/lang/String");//获取class
         
        //先将gbk字符串转为java里的string格式
        mid_String = (*env)->GetMethodID(env,Class_string, "<init>", "([BLjava/lang/String;)V");  
        bytes = (*env)->NewByteArray(env,strlen(buf));  
        (*env)->SetByteArrayRegion(env,bytes, 0, strlen(buf), (jbyte*)buf);  
        //codetype = (*env)->NewStringUTF(env,"utf-8"); 
        if(isGB2312==1)
        codetype = (*env)->NewStringUTF(env,"gbk");  
        else
        codetype = (*env)->NewStringUTF(env,"utf-8");
        jstr= (jstring)(*env)->NewObject(env,Class_string, mid_String, bytes, codetype);   
        (*env)->DeleteLocalRef(env,bytes);  
        return  jstr;
       
};

void Scan()
{
	if(g_nDevice>0)
	{
		struct termios term;
		tcgetattr(g_nDevice,&term);
		cfsetospeed(&term,B9600);
		term.c_oflag|=(1<<15);
		tcsetattr(g_nDevice,TCSANOW,&term);
		/*
		int setio;
setio=open("/dev/msm_io_cm7",0);
   if (setio < 0) 
   { 
		printf("open device leds"); 
		//exit(1); 
                 return (-3);
  }
  		printf("open msm_io_cm7 OK"); 

    ioctl(setio, 1, 3);   
    usleep(5000);
    ioctl(setio, 1, 2);
    //usleep(10000);  
        //ioctl(setio, 1,0);
        //ioctl(setio, 1,2);
    close(setio);*/

	}
}
char scan_read_type()
{
	char data=-1;
	int setio=open("/dev/msm_io_cm7",0);
	if (setio < 0) 
	{ 
		if(g_nDevice>0)
		close(g_nDevice);
		return -1;
	}
	ioctl(setio, 0, 0);
	usleep(10000);
	ioctl(setio, 1, 1);  //给pn512加电,平时 ioctl(setio, 1, 0);
	close(setio);
	int fd,s,retv,i;
	struct termios term;
	char  hd[128];
	fd = open(SERIAL_STREAM_PATH,O_RDWR|O_NOCTTY);  /*读写方式打开串口*/
	tcgetattr(g_nDevice,&term);
	//		tcflush(g_nDevice,TCIOFLUSH);
	if(fd<0)
	{
		LOGI("openerror");
	//		ioctl(setio, 1, 0);  //给pn512加电,平时 ioctl(setio, 1, 0);
	//close(setio);

		return -1;
	}
	//tcflush(g_nDevice,TCIOFLUSH);
	cfsetispeed(&term,B9600);
	cfsetospeed(&term,B9600);
	term.c_cc[VTIME]    = 1;   /* inter-character timer unused */
	term.c_cc[VMIN]     = 0;   /* blocking read until 1 chars received */

	if(tcsetattr(fd,TCSANOW,&term)==0)
	{
		tcflush(fd,TCIOFLUSH);
		memset(hd,0,128);
		retv = write(fd, checktype, 1);

		usleep( 1000);
		retv = read(fd, hd, 4);
		retv = read(fd, hd+retv, 4);
		retv = read(fd, hd+retv, 4);
		retv = read(fd, hd+retv, 4);
		if(retv == 0)
		{
			retv = write(fd, checktype, 1);
			usleep(100 * 1000);
			retv = read(fd, hd, 4);
			retv = read(fd, hd+retv, 4);
			retv = read(fd, hd+retv, 4);
			retv = read(fd, hd+retv, 4);

		}

		if (retv > 0) {
				for(i = 0; i < retv; i ++) {
				LOGE("data[%d] == %d", i, hd[i]);
			}
		}
		unsigned char *pS=(unsigned char *)memchr(hd,'s',retv);
		if((pS!=NULL)&&((pS-(unsigned char *)hd)<=(retv-4)))
		{
			unsigned char psoft[]={'s','o','f','t'};
			if(memcmp(pS,psoft,4)==0)
			data = SCANNER_TYPE_RED;
		}
		else
		{
			unsigned char command = 0x55;
			if (255 == hd[0] && 1 == retv) {
				retv = write(fd, &command, 1);
				LOGE("write length is %d\n", retv);

				retv = read(fd, hd, 100);
				LOGE("read length is %d, again\n", retv);
				if (retv > 0) {
					for(i = 0; i < retv; i ++) {
						LOGE("data[%d] == %d", i, hd[i]);
					}
				}
				data = SCANNER_TYPE_RED; 
			} else if (!strcmp(hd, "soft"))	{
				data = SCANNER_TYPE_RED; 
				LOGE("determine check type is red\n");
			} else {
				data = SCANNER_TYPE_LASER; 
				LOGE("determine check type is laser\n");
			}
		}
	}
	close(fd);
	//			ioctl(setio, 1, 0);  //给pn512加电,平时 ioctl(setio, 1, 0);
	//close(setio);

	LOGI("2222222222222222222222222222222222222222222222222222222222222222222");
	return data;
}
char GetData(char data)
{
	if(((data>='0')&&(data<='9'))||((data>='a')&&(data<='z'))||((data>='A')&&(data<='Z')))
	{
		return data;
	}
	else
	{
		switch(data)
		{
			case '!':
			case '@':
			case '#':		
			case '$':		
			case '%':		
			case '^':			
			case '&':		
			case '*':		
			case '(':
			case ')':
			case '-':
			case '+':
			case '_':
			case '=':
			case '{':
			case '[':
			case '}':
			case ']':
			case ':':
			case ';':
			case '"':
			case '\'':
			case '<':
			case ',':
			case '>':
			case '.':
			case '?':
			case '/':
			case '~':
			case '`':
			case '|':
			case '\\':
			case ' ':
			return data;
		}
	}
	return 0xff;
}

/*
 * Class:     Scanner
 * Method:    OpenSCA
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ru_toir_mobile_rfid_Scanner_InitSCA
  (JNIEnv *env, jclass jc){
		
char  hd[max_buffer_size],*rbuf; /*定义接收缓冲区*/

int flag_close,retv,i,ncount=0,time_r;

struct termios opt;
int err;
int realdata=0;
int fd,s;
memset(hd,0,max_buffer_size);
/*******************************************************************/
 fd = open(SERIAL_STREAM_PATH,O_RDWR|O_NOCTTY);  /*读写方式打开串口*/
 printf("open /dev/ttyMSM2\n");
 if(fd == -1)  /*打开失败*/
 return -1;
 
 
/*******************************************************************/

tcgetattr(fd,&opt);
cfmakeraw(&opt);
opt.c_cc[VTIME]    = 1;   /* inter-character timer unused */
opt.c_cc[VMIN]     = 0;   /* blocking read until 1 chars received */


/*****************************************************************/

cfsetispeed(&opt,B38400); /*波特率设置为115200bps*/


/*******************************************************************/

tcsetattr(fd,TCSANOW,&opt);
//retv=write(fd, findcard1, 10);
//rbuf=hd; /*数据保存*/
//if(retv!=10)
//{   
 //printf("write data err...\n");
 //flag_close=close(fd);
 //return 0;
//}	 

//retv=read(fd,rbuf,15);   /*接收数据*/
//if(retv<15)
//{
	//retv+=read(fd,rbuf,15-retv);   /*接收数据*/
	
//}

//if(retv!=15)
//{
	 	
  //printf("findcard read err... 0x=%d\n",retv);	
////  printf("%d",rbuf[0]);
  //flag_close=close(fd);
  //return 0;
//}
////if(retv!=19)
//{    
	 	
  //printf("selectcard read err...  0x=%d\n",retv);	
  //for(i=0;i<retv;i++)
  //printf("0x%02X,",rbuf[i]);
  ////flag_close=close(fd);
  ////return 0;
//}
  //printf("\r\n");
//retv=write(fd, selectcard1, 10);
//rbuf=hd; /*数据保存*/
//if(retv!=10)
//{   
 //printf("selectcard write  err... 0x=%d\n",retv);
 //flag_close=close(fd);
 //return 0;
//}	 

//retv=read(fd,rbuf,19);   /*接收数据*/
//if(retv<19)
//{
	//retv+=read(fd,&rbuf[retv],19-retv);   /*接收数据*/
	
//}

////if(retv!=19)
//{    
	 	
  //printf("selectcard read err...  0x=%d\n",retv);	
  //for(i=0;i<retv;i++)
  //printf("0x%02X,",rbuf[i]);
  ////flag_close=close(fd);
  ////return 0;
//}
  //printf("\r\n");
//tcflush(fd,TCIOFLUSH);
//retv=write(fd, readcard1, 10);
//rbuf=hd; /*数据保存*/
//if(retv!=10)
//{   
 //printf("readcard write  err...\n");
 //flag_close=close(fd);
 //return 0;
//}	 
//printf("ready data...\n");
    
     //retv=0;
//rbuf=hd; /*数据保存*/
//sleep(2);
//time_r=0;     
   //while(ncount<1295)       

  //{
      

      //retv=read(fd,rbuf,512);
    
      //if(retv==-1)
     //{
       ////printf("err_read");
     //}
     //else if(retv==0)
     //{
		//time_r++; 
		//sleep(1);
		//if(time_r>30)
		//break;
	 //}
	 //else
	 //{  time_r=0;
		//ncount+=retv;
        //rbuf+=retv;
        //if(ncount>1294)
        //break; 
		 
	 //}
     
 //}

///*******************************************************************/

//printf("The data received is:%d\n",ncount);  /*输出接收到的数据*/

//for(i=0;i<ncount;i++)

//{

     //printf("%02x",hd[i]);
     //if((ncount%100)==0)
     //break;
//}

//printf("\n");
//flag_close=close(fd);
	//g_type=scan_read_type();
return 0;
};
/*
 * Class:     Scanner
 * Method:    CloseSCA
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_ru_toir_mobile_rfid_Scanner_CloseSCA
  (JNIEnv *env, jclass jc){
			if(g_nDevice>0)
			{
					close(g_nDevice);  
					g_nDevice=0;
			}
};

/*
 * Class:     Scanner
 * Method:    ScanSCA
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_ru_toir_mobile_rfid_Scanner_ScanSCA
  (JNIEnv *env, jclass jc,jint nComand){
	 if(g_nDevice>0)
     {
		tcflush(g_nDevice,TCIOFLUSH);
		LOGI("hello %d--%d",g_nDevice,nComand);
		//g_nLen=0;
		
			if(nComand==0x55)
			{
				//red
					if(g_nDevice>0)
					{
							//jbyte comman=0x55;
							unsigned char comman[2]={0x55,0};
							write(g_nDevice,comman,1);    
					}
			}
			else
			{
					//laser
					Scan();
			}
	}
};

/*
 * Class:     Scanner
 * Method:    ReadDataSCA
 * Signature: ()[B
 */
JNIEXPORT jint JNICALL Java_ru_toir_mobile_rfid_Scanner_ReadDataSCA
  (JNIEnv *env, jclass jc,jint nComand,jbyteArray jpout){
	jsize nout = (*env)->GetArrayLength(env, jpout); 	
	jbyte *pout = (*env)->GetByteArrayElements(env, jpout, 0); 
	jint nresult=0;
	  int nLen=0,ncount=0,nret=0,i;
	  int fd,s;

	  	char string[10],buf[1024];
		memset(string,0,10);
		memset(buf,0,1024);
		g_nDevice=open(SERIAL_STREAM_PATH,O_RDWR|O_NOCTTY);
		struct termios term;
		int setio,ret=0;
		setio=open("/dev/msm_io_cm7",0);
		if (setio < 0) 
		{ 
			  if(g_nDevice>0)
			  close(g_nDevice);
			 nresult=-3;
			 goto End;
		}
		//ioctl(setio, 1, 1);  //给pn512加电,平时 ioctl(setio, 1, 0);
		ioctl(setio, 7, 0);
		usleep(10000);
		tcgetattr(g_nDevice,&term);
		cfsetispeed(&term,B9600);
		cfsetospeed(&term,B9600);
		term.c_cc[VTIME]    = 1;   /* inter-character timer unused */
		term.c_cc[VMIN]     = 0;   /* blocking read until 1 chars received */

		if(tcsetattr(g_nDevice,TCSANOW,&term)!=0)
		{
			  if(g_nDevice>0)
			  close(g_nDevice);
			  if(setio>0)
			  close(setio);
			 nresult=-1;
			 goto End;
		}
		tcflush(g_nDevice,TCIOFLUSH);
		if(nComand==0x55)
		{
			unsigned char comman[2]={0x55,0};
			write(g_nDevice,comman,1);    
		}
		else
		{
			ret=ioctl(setio, 6, 0);
		}
	  if(g_nDevice>0)
      {
		  int nMax=nComand==0x55?90:30;
		  while((ncount<nMax)&&(nret<2))
		  {
			  nLen=read(g_nDevice,buf+nret,1024-nret);
			  ncount++;
			  nret+=nLen;
			  usleep(10000);
		  }
		  if(nret>0)
		  {
			  
			  while(nLen>0)
			  {
				  nLen=read(g_nDevice,buf+nret,1024-nret);
				  nret+=nLen;
			  }
			  if(nComand!=0x55)
			  {
				  memcpy(pout,buf,nret<nout?nret:nout);
				  nresult=nret;
			  }
			  else
			  {
				int nlen=0,i=0;
				for(i=0;i<(nret<nout?nret:nout);i++)
				{
					pout[nlen]=GetData(buf[i]);
					if(pout[nlen]!=0xff)
					nlen++;
				}
				nresult=nlen;
			  }
		  }
		  else
		  nresult=-4;
		  close(g_nDevice);  
      }
      

End:
	if (setio >0) 
	{
		ioctl(setio, 7, 0);
		close(setio);
	}
	(*env)->ReleaseByteArrayElements(env,jpout, pout, 0);
	return nresult;
};
/*
 * Class:     Scanner
 * Method:    ReadDataSCA
 * Signature: (I)V
 */
JNIEXPORT jstring JNICALL Java_ru_toir_mobile_rfid_Scanner_ReadSCA
  (JNIEnv *env, jclass jc,jint nComand){	  
	  int nLen=0,ncount=0,nret=0,i;
	  int fd,s;

	  	char string[10],buf[1024];
		memset(string,0,10);
		memset(buf,0,1024);
		g_nDevice=open(SERIAL_STREAM_PATH,O_RDWR|O_NOCTTY);
		struct termios term;
		//tcgetattr(fd,&opt);
		int setio,ret=0;
		setio=open("/dev/msm_io_cm7",0);
		if (setio < 0) 
		{ 
			  if(g_nDevice>0)
			  close(g_nDevice);
			//printf("open device leds"); 
			return (*env)->NewStringUTF(env, string);
		}
    //ioctl(setio, 1, 1);  //给pn512加电,平时 ioctl(setio, 1, 0);
		ioctl(setio, 7, 0);
		usleep(10000);
		tcgetattr(g_nDevice,&term);
		//		tcflush(g_nDevice,TCIOFLUSH);

		//tcflush(g_nDevice,TCIOFLUSH);
		cfsetispeed(&term,B9600);
		cfsetospeed(&term,B9600);
		term.c_cc[VTIME]    = 1;   /* inter-character timer unused */
		term.c_cc[VMIN]     = 0;   /* blocking read until 1 chars received */

		if(tcsetattr(g_nDevice,TCSANOW,&term)!=0)
		{
			  if(g_nDevice>0)
			  close(g_nDevice);
			  if(setio>0)
			  close(setio);
			return (*env)->NewStringUTF(env, string);
		}
		tcflush(g_nDevice,TCIOFLUSH);
		if(nComand==0x55)
		{
			unsigned char comman[2]={0x55,0};
			write(g_nDevice,comman,1);    
		}
		else
		{
			ret=ioctl(setio, 6, 0);
		}
	  if(g_nDevice>0)
      {
		  int nMax=nComand==0x55?90:30;
		  while((ncount<nMax)&&(nret<2))
		  {
			  nLen=read(g_nDevice,buf+nret,1024-nret);
			  ncount++;
			  nret+=nLen;
			  usleep(10000);
		  }
		  if(nret>2)
		  {
			  while(nLen>0)
			  {
				  nLen=read(g_nDevice,buf+nret,1024-nret);
				  nret+=nLen;
			  }
		  }
		  close(g_nDevice);  
		  //{
			  //char temp[9];
			  //int i=0;
			  //memset(temp,0,9);
			  //for(i=0;i<nret;i++)
			  //{
				  //sprintf(temp,"0x%x,",buf[i]);
				  //memcpy(string+i*5,temp,5);
			  //}
			  //LOGI(string);
		  //}
      }
	  //if (setio >0) 
	  {
		 ioctl(setio, 7, 0);
		  close(setio);
		}
		if(nComand!=0x55)
		{
			return WindowsTojstring(env,buf,IsGB2312(buf,nret));
		}
		else
		{
			int nlen=0,i=0;
			for(i=0;i<nret;i++)
			{
				buf[nlen]=GetData(buf[i]);
				if(buf[nlen]!=0xff)
				nlen++;
			}
			memcpy(string,buf,nlen);
			return (*env)->NewStringUTF(env, string);
		}
		
};
/*
 * Class:     Scanner
 * Method:    ReadDataSCA
 * Signature: (I)V
 */
JNIEXPORT jstring JNICALL Java_ru_toir_mobile_rfid_Scanner_ReadSCAAuto
  (JNIEnv *env, jclass jc){	  
	  int nLen=0,ncount=0,nret=0,i;
	  int fd,s;

	  	char string[10],buf[1024];
		memset(string,0,10);
		memset(buf,0,1024);
		g_nDevice=open(SERIAL_STREAM_PATH,O_RDWR|O_NOCTTY);
		if(g_nDevice<0)
		{
			return (*env)->NewStringUTF(env, string);
		}
		struct termios term;
		//tcgetattr(fd,&opt);
		int setio,ret=0;
		if(g_type==-1)
		{
			g_type=scan_read_type();
		}
		setio=open("/dev/msm_io_cm7",0);
		if (setio < 0) 
		{ 
			  if(g_nDevice>0)
			  close(g_nDevice);
			//printf("open device leds"); 
			 return (*env)->NewStringUTF(env, string);
		}
   // ioctl(setio, 1, 1);  //给pn512加电,平时 ioctl(setio, 1, 0);
		ioctl(setio, 7, 0);
		usleep(20000);
		tcgetattr(g_nDevice,&term);
		//		tcflush(g_nDevice,TCIOFLUSH);

		//tcflush(g_nDevice,TCIOFLUSH);
		cfsetispeed(&term,B9600);
		cfsetospeed(&term,B9600);
		term.c_cc[VTIME]    = 1;   /* inter-character timer unused */
		term.c_cc[VMIN]     = 0;   /* blocking read until 1 chars received */


		if(tcsetattr(g_nDevice,TCSANOW,&term)!=0)
		{
			  if(g_nDevice>0)
			  close(g_nDevice);
			  if(setio>0)
			  {
			 // ioctl(setio, 1, 0);
			  close(setio);
			  }
			 return (*env)->NewStringUTF(env, string);
		}
		tcflush(g_nDevice,TCIOFLUSH);
		if(g_type==SCANNER_TYPE_RED)
		{
			//usleep(65000);
			unsigned char comman[2]={0x55,0};
			int nwrite=write(g_nDevice,comman,1); 
			//usleep(15000); 
			//write(g_nDevice,comman,1);  
			//LOGE("write444444444444444444444444444===%d",nwrite);  
		}
		else
		{
			ret=ioctl(setio, 6, 0);
		}
	  //read serialport
	  if(g_nDevice>0)
      {
		  int nMax=g_type==SCANNER_TYPE_RED?90:30;
		  //LOGI("g_nDevice11111111111111111111111111\r\n");
		  while((ncount<nMax)&&(nLen<2))
		  //while((ncount<30))
		  {
			  //nLen=read(g_nDevice,string,12);
			  nLen=read(g_nDevice,buf+nret,1024-nret);
			  //LOGI("hello %d--%d",nLen,nret);
			  ncount++;
			  nret+=nLen;
			  usleep(10000);
		  }
		  if(nret>2)
		  {
			  while(nLen>0)
			  {
				  nLen=read(g_nDevice,buf+nret,1024-nret);
				  nret+=nLen;
			  }
		  }
		  close(g_nDevice);  
      }
	  if (setio >0) 
	  {
		 ioctl(setio, 7, 0);
		  close(setio);
		}
		if(g_type!=SCANNER_TYPE_RED)
		{
			return WindowsTojstring(env,buf,IsGB2312(buf,nret));
		}
		else
		{
			int nlen=0,i=0;
			for(i=0;i<nret;i++)
			{
				buf[nlen]=GetData(buf[i]);
				if(buf[nlen]!=0xff)
				nlen++;
			}
			memcpy(string,buf,nlen);
			return (*env)->NewStringUTF(env, string);
		}
};

/*
 * Class:     Scanner
 * Method:    ReadDataSCA
 * Signature: (I)V
 */
JNIEXPORT jstring JNICALL Java_ru_toir_mobile_rfid_Scanner_ReadSCAEx
  (JNIEnv *env, jclass jc,jint nComand,jint nCode){	  
	  int nLen=0,ncount=0,nret=0,i;
	  int fd,s;

	  	char string[10],buf[1024];
		memset(string,0,10);
		memset(buf,0,1024);
		g_nDevice=open(SERIAL_STREAM_PATH,O_RDWR|O_NOCTTY);
		struct termios term;
		//tcgetattr(fd,&opt);
		int setio,ret=0;
		setio=open("/dev/msm_io_cm7",0);
		if (setio < 0) 
		{ 
			  if(g_nDevice>0)
			  close(g_nDevice);
			//printf("open device leds"); 
			return (*env)->NewStringUTF(env, string);
		}
		ioctl(setio, 1, 1);  //给pn512加电,平时 ioctl(setio, 1, 0);
		ioctl(setio, 1, 7);
		usleep(1000);
		tcgetattr(g_nDevice,&term);
		//		tcflush(g_nDevice,TCIOFLUSH);

		//tcflush(g_nDevice,TCIOFLUSH);
		cfsetispeed(&term,B9600);
		cfsetospeed(&term,B9600);
		term.c_cc[VTIME]    = 1;   /* inter-character timer unused */
		term.c_cc[VMIN]     = 0;   /* blocking read until 1 chars received */

		if(tcsetattr(g_nDevice,TCSANOW,&term)!=0)
		{
			  if(g_nDevice>0)
			  close(g_nDevice);
			  if(setio>0)
			  close(setio);
			return (*env)->NewStringUTF(env, string);
		}
		tcflush(g_nDevice,TCIOFLUSH);
		if(nComand==0x55)
		{
			unsigned char comman[2]={0x55,0};
			write(g_nDevice,comman,1);    
		}
		else
		{
			ret=ioctl(setio, 1, 6);
		}
	  //read serialport
	  if(g_nDevice>0)
      {
		  int nMax=nComand==0x55?90:30;
		  while((ncount<nMax)&&(nret<2))
		  {
			  nLen=read(g_nDevice,buf+nret,1024-nret);
			  ncount++;
			  nret+=nLen;
			  usleep(10000);
		  }
		  if(nret>2)
		  {
			  while(nLen>0)
			  {
				  nLen=read(g_nDevice,buf+nret,1024-nret);
				  nret+=nLen;
			  }
		  }
		  close(g_nDevice);  
      }
	  if (setio >0) 
	  {
		 // ret=ioctl(setio, 1, 0);
		 ioctl(setio, 1, 7);
		  close(setio);
		}
		if(nComand!=0x55)
		{
			return WindowsTojstring(env,buf,nCode);
		}
		else
		{
			int nlen=0,i=0;
			for(i=0;i<nret;i++)
			{
				buf[nlen]=GetData(buf[i]);
				if(buf[nlen]!=0xff)
				nlen++;
			}
			memcpy(string,buf,nlen);
			return (*env)->NewStringUTF(env, string);
		}
		
};
JNIEXPORT jbyteArray JNICALL Java_ru_toir_mobile_rfid_Scanner_ReadData
  (JNIEnv *env, jclass jc,jint nComand){
	  int nLen=0,ncount=0,nret=0,i;
	  int fd,s;

	  	char buf[1024];
		memset(buf,0,1024);
		g_nDevice=open(SERIAL_STREAM_PATH,O_RDWR|O_NOCTTY);
		struct termios term;
		int setio,ret=0;
		setio=open("/dev/msm_io_cm7",0);
		if (setio < 0) 
		{ 
			  if(g_nDevice>0)
			  close(g_nDevice);
			 goto End;
		}
		ioctl(setio, 1, 1);  //给pn512加电,平时 ioctl(setio, 1, 0);
		ioctl(setio, 1, 7);
		usleep(1000);
		tcgetattr(g_nDevice,&term);
		cfsetispeed(&term,B9600);
		cfsetospeed(&term,B9600);
		term.c_cc[VTIME]    = 1;   /* inter-character timer unused */
		term.c_cc[VMIN]     = 0;   /* blocking read until 1 chars received */

		if(tcsetattr(g_nDevice,TCSANOW,&term)!=0)
		{
			  if(g_nDevice>0)
			  close(g_nDevice);
			  if(setio>0)
			  close(setio);
			 goto End;
		}
		tcflush(g_nDevice,TCIOFLUSH);
		if(nComand==0x55)
		{
			unsigned char comman[2]={0x55,0};
			write(g_nDevice,comman,1);    
		}
		else
		{
			ret=ioctl(setio, 1, 6);
		}
	  if(g_nDevice>0)
      {
		  int nMax=nComand==0x55?90:30;
		  while((ncount<nMax)&&(nret<2))
		  {
			  nLen=read(g_nDevice,buf+nret,1024-nret);
			  ncount++;
			  nret+=nLen;
			  usleep(10000);
		  }
		  if(nret>0)
		  {
			  
			  while(nLen>0)
			  {
				  nLen=read(g_nDevice,buf+nret,1024-nret);
				  nret+=nLen;
			  }
			 // memcpy(pout,buf,nret<nout?nret:nout);
			 // nresult=nret;
		  }
		 
		  close(g_nDevice);  
      }
      

End:
	if (setio >0) 
	{
		ioctl(setio, 1, 7);
		close(setio);
	}
  jbyteArray jarray = (*env)->NewByteArray(env,nret);
  jbyte *pout = (*env)->GetByteArrayElements(env, jarray, 0); 
  if(nComand!=0x55)
  {
	  memcpy(pout,buf,nret); 
  }
  else
  {
	int nlen=0,i=0;
	for(i=0;i<nret;i++)
	{
		pout[nlen]=GetData(buf[i]);
		if(pout[nlen]!=0xff)
		nlen++;
	}
  }
	(*env)->ReleaseByteArrayElements(env,jarray, pout, 0);
	return jarray;
  }
