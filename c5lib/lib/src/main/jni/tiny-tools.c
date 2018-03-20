#include <jni.h>
#include <fcntl.h>
#include <android/log.h>
#include <termio.h>
//#include <stdio.h>
//#include <stdbool.h>
//#include "reader2d.h"

/* только чтоб собиралось
int  IsGB2312(int a1, int a2);
int  WindowsTojstring(int a1, const char *a2, int a3);
int Scan();
signed int scan_read_type();
unsigned int  GetData(unsigned int result);
*/

jint Java_android_hardware_barcode_Scanner_InitSCA(JNIEnv *env, jclass jc);

jint Java_android_hardware_barcode_Scanner_CloseSCA(JNIEnv *env, jclass jc);

/* только чтоб собиралось
jint Java_android_hardware_barcode_Scanner_ReadSCAAuto(int a1);
jint Java_android_hardware_barcode_Scanner_ReadSCA(int a1, int a2, int a3);
jint Java_android_hardware_barcode_Scanner_ReadSCAEx(int a1, int a2, int a3, uint8_t a4);
jint Java_android_hardware_barcode_Scanner_ReadDataSCA(int32_t *a1, int a2, int a3, int a4);
jint Java_android_hardware_barcode_Scanner_ReadData(int a1, int a2, int a3);
ssize_t  Java_android_hardware_barcode_Scanner_ScanSCA(int a1, int a2, int a3);
*/

/* только чтоб собиралось
_UNKNOWN unk_3920; // weak
_UNKNOWN checktype; // weak
_UNKNOWN g_type; // weak
*/

int g_nDevice; // глобальная переменная содержащая в себе дескриптор порта сканера
// метка для сообщений
const char *TAG2D = "ScannerJNI2D";


jint Java_android_hardware_barcode_Scanner_InitSCA(JNIEnv *env, jclass jc) {
    int descriptor;
    int returnCode;
    int result;
    struct termios cfg;

    // возможно обнуление глобального буфера для чтения/разбора данных
//    char s; // [sp+24h] [bp-524h]@1
//    memset(&s, 0, 0x50Fu);

    __android_log_print(ANDROID_LOG_INFO, TAG2D, "InitSCA: %p, %p", env, jc);
    descriptor = open("/dev/ttyMT1", O_RDWR | O_NOCTTY);
    returnCode = descriptor;
//    puts("open /dev/ttyMSM2");
    if (descriptor != -1) {
        ioctl(descriptor, TCGETS, &cfg);
        // здесь не ясно каким образом инициализируется порт
//        cfg &= 0xFFFFFA14;
        returnCode = 0;
        ioctl(descriptor, TCSETS, &cfg);
    }

    result = returnCode;

    return result;
}

jint Java_android_hardware_barcode_Scanner_CloseSCA(JNIEnv *env, jclass jc) {
    int result;

    __android_log_print(ANDROID_LOG_INFO, TAG2D, "CloseSCA: %p, %p", env, jc);

    result = g_nDevice;
    if (g_nDevice > 0) {
        result = close(g_nDevice);
        g_nDevice = 0;
    }

    return result;
}

/* только чтоб собиралось
int  IsGB2312(int a1, int a2)
{
  int v2; // r4@1
  int v3; // r3@1
  signed int v4; // r6@1
  signed int v5; // r0@1
  signed int v6; // r2@1
  unsigned int v7; // r7@2
  unsigned int v8; // r5@2

  v2 = 0;
  v3 = a1;
  v4 = 0;
  v5 = 1;
  v6 = 0;
  while ( v2 < a2 )
  {
    v7 = *(int8_t *)(v3 + v2);
    v8 = (v7 + 95) & 0xFF;
    if ( v6 == 1 )
    {
      if ( v8 <= 0x5D )
      {
        v4 = 1;
LABEL_10:
        v6 = 0;
        goto LABEL_12;
      }
      if ( v7 > 0xA0 )
        goto LABEL_10;
    }
    else
    {
      v6 = 1;
      if ( v8 <= 0x56 )
        goto LABEL_12;
      if ( ((v7 - 128) & 0xFF) > 0x1F )
        goto LABEL_10;
    }
    v5 = 0;
    v6 = 0;
LABEL_12:
    ++v2;
  }
  return v5 & -((__PAIR__(v4, v4) - __PAIR__((unsigned int)(v4 - 1), 1)) >> 32);
}
*/

/* только чтоб собиралось
int  WindowsTojstring(int a1, const char *a2, int a3)
{
  const char *v3; // r6@1
  int v4; // ST14_4@1
  int v5; // r5@1
  size_t v6; // r0@1
  int v7; // r7@1
  size_t v8; // r0@1
  const char *v9; // r1@2
  int v10; // r4@4
  int v12; // [sp+Ch] [bp-24h]@1
  int v13; // [sp+10h] [bp-20h]@1

  v3 = a2;
  v4 = a3;
  v5 = a1;
  v12 = (*(int (**)(void))(*(int32_t *)a1 + 24))();
  v13 = (*(int ( **)(int, int, const char *, const char *))(*(int32_t *)v5 + 132))(
          v5,
          v12,
          "<init>",
          "([BLjava/lang/String;)V");
  v6 = strlen(v3);
  v7 = (*(int ( **)(int, size_t))(*(int32_t *)v5 + 704))(v5, v6);
  v8 = strlen(v3);
  (*(void ( **)(int, int, int32_t, size_t))(*(int32_t *)v5 + 832))(v5, v7, 0, v8);
  if ( v4 == 1 )
    v9 = "gbk";
  else
    v9 = "utf-8";
  (*(void ( **)(int, const char *))(*(int32_t *)v5 + 668))(v5, v9);
  v10 = (*(int ( **)(int, int, int, int))(*(int32_t *)v5 + 112))(v5, v12, v13, v7);
  (*(void ( **)(int, int))(*(int32_t *)v5 + 92))(v5, v7);
  return v10;
}
*/

/* только чтоб собиралось
int Scan()
{
  int result; // r0@1
  int v1; // [sp+0h] [bp-38h]@2
  int v2; // [sp+4h] [bp-34h]@2
  unsigned int v3; // [sp+8h] [bp-30h]@2

  result = g_nDevice;
  if ( g_nDevice > 0 )
  {
    ioctl(g_nDevice, 0x5401u, &v1);
    v3 = v3 & 0xFFFFEFF0 | 0xD;
    v2 |= 0x8000u;
    result = ioctl(g_nDevice, 0x5402u, &v1);
  }

  return result;
}
*/

/* только чтоб собиралось
signed int scan_read_type()
{
  int v0; // r0@1
  int v1; // r5@1
  signed int v2; // r6@2
  int v3; // r5@4
  ssize_t v4; // r0@7
  ssize_t v5; // r0@7
  ssize_t v6; // r0@7
  signed int v7; // r6@7
  ssize_t v8; // r0@8
  ssize_t v9; // r0@8
  ssize_t v10; // r0@8
  int v11; // r4@10
  int v12; // r3@11
  void *v13; // r0@12
  const void *v14; // r7@12
  ssize_t v15; // r0@18
  ssize_t v16; // r7@18
  int v17; // r6@19
  int v18; // ST00_4@20
  int v19; // r3@20
  int v20; // r3@22
  signed int result; // r0@27
  int v22; // [sp+0h] [bp-D8h]@0
  char v23; // [sp+13h] [bp-C5h]@16
  char dest; // [sp+14h] [bp-C4h]@14
  char v25; // [sp+18h] [bp-C0h]@4
  unsigned int v26; // [sp+20h] [bp-B8h]@6
  char v27; // [sp+2Eh] [bp-AAh]@6
  char v28; // [sp+2Fh] [bp-A9h]@6
  char s[128]; // [sp+3Ch] [bp-9Ch]@7

  v0 = open("/dev/msm_io_cm7", 0);
  v1 = v0;
  if ( v0 >= 0 )
  {
    ioctl(v0, 0, 0);
    usleep(0x2710u);
    ioctl(v1, 1u, 1);
    close(v1);
    v3 = open("/dev/ttyMT1", 258);
    ioctl(g_nDevice, 0x5401u, &v25);
    if ( v3 < 0 )
    {
      __android_log_print(4, "ScannerJNI", "openerror");
      v2 = 255;
      goto LABEL_27;
    }
    v27 = 1;
    v26 = v26 & 0xFFFFEFF0 | 0xD;
    v28 = 0;
    v2 = 255;
    if ( ioctl(v3, 0x5402u, &v25) )
      goto LABEL_26;
    ioctl(v3, 0x540Bu, 2);
    memset(s, 0, 0x80u);
    write(v3, &checktype, 1u);
    usleep(0x3E8u);
    v4 = read(v3, s, 4u);
    v5 = read(v3, &s[v4], 4u);
    v6 = read(v3, &s[v5], 4u);
    v7 = read(v3, &s[v6], 4u);
    if ( !v7 )
    {
      write(v3, &checktype, 1u);
      usleep(0x186A0u);
      v8 = read(v3, s, 4u);
      v9 = read(v3, &s[v8], 4u);
      v10 = read(v3, &s[v9], 4u);
      v7 = read(v3, &s[v10], 4u);
    }
    if ( v7 > 0 )
    {
      v11 = 0;
      do
      {
        v22 = (uint8_t)s[v11];
        v12 = v11++;
        ((void ( *)(signed int, const char *, const char *, int))__android_log_print)(
          6,
          "ScannerJNI",
          "data[%d] == %d",
          v12);
      }
      while ( v11 != v7 );
    }
    v13 = memchr(s, 115, v7);
    v14 = v13;
    if ( v13 && v7 - 3 > (int8_t *)v13 - s )
    {
      memcpy(&dest, &unk_3920, 4u);
      v2 = 1;
      if ( memcmp(v14, &dest, 4u) )
        v2 = 255;
      goto LABEL_26;
    }
    v23 = 85;
    if ( v7 != 1 || (uint8_t)s[0] != 255 )
    {
      if ( strcmp(s, "soft") )
      {
        ((void ( *)(signed int, const char *, const char *, int))__android_log_print)(
          6,
          "ScannerJNI",
          "determine check type is laser\n",
          v20);
        v2 = 2;
        goto LABEL_26;
      }
      ((void ( *)(signed int, const char *, const char *, int))__android_log_print)(
        6,
        "ScannerJNI",
        "determine check type is red\n",
        v20);
    }
    else
    {
      v15 = write(v3, &v23, 1u);
      __android_log_print(6, "ScannerJNI", "write length is %d\n", v15);
      v16 = read(v3, s, 0x64u);
      __android_log_print(6, "ScannerJNI", "read length is %d, again\n", v16, v22);
      v2 = 1;
      if ( v16 <= 0 )
      {
LABEL_26:
        close(v3);
        __android_log_print(4, "ScannerJNI", "2222222222222222222222222222222222222222222222222222222222222222222");
        goto LABEL_27;
      }
      v17 = 0;
      do
      {
        v18 = (uint8_t)s[v17];
        v19 = v17++;
        __android_log_print(6, "ScannerJNI", "data[%d] == %d", v19, v18);
      }
      while ( v17 != v16 );
    }
    v2 = 1;
    goto LABEL_26;
  }
  v2 = 255;
  if ( g_nDevice > 0 )
    close(g_nDevice);
LABEL_27:
  result = v2;

  return result;
}
*/

/* только чтоб собиралось
unsigned int  GetData(unsigned int result)
{
  if ( ((result - 97) & 0xFF) > 0x19 && result - 48 > 9 && result - 65 > 0x19 )
  {
    if ( result <= 0x40 )
    {
      if ( result >= 0x3A || result - 32 <= 0xF )
        return result;
      return 255;
    }
    if ( result < 0x5B || result > 0x60 && result - 123 > 3 )
      return 255;
  }
  return result;
}
*/

/* только чтоб собиралось
ssize_t  Java_android_hardware_barcode_Scanner_ScanSCA(int a1, int a2, int a3)
{
  int v3; // r5@1
  ssize_t result; // r0@1
  char buf; // [sp+Ch] [bp-14h]@4
  char v6; // [sp+Dh] [bp-13h]@4

  v3 = a3;
  result = g_nDevice;
  if ( g_nDevice > 0 )
  {
    ioctl(g_nDevice, 0x540Bu, 2);
    __android_log_print(4, "ScannerJNI", "hello %d--%d", g_nDevice, v3);
    if ( v3 == 85 )
    {
      result = g_nDevice;
      if ( g_nDevice > 0 )
      {
        buf = 85;
        v6 = 0;
        result = write(g_nDevice, &buf, 1u);
      }
    }
    else
    {
      result = Scan();
    }
  }
  return result;
}
*/

/* только чтоб собиралось
int  Java_android_hardware_barcode_Scanner_ReadDataSCA(int32_t *a1, int a2, int a3, int a4)
{
  int32_t *v4; // r7@1
  int v5; // r2@1
  int i; // r5@9
  signed int v7; // r4@16
  ssize_t v8; // r5@16
  ssize_t v9; // r0@20
  size_t v10; // r2@25
  int v11; // r3@30
  signed int v12; // r5@8
  int result; // r0@37
  int fd; // [sp+0h] [bp-470h]@1
  int v15; // [sp+4h] [bp-46Ch]@1
  int v16; // [sp+8h] [bp-468h]@1
  signed int v17; // [sp+Ch] [bp-464h]@16
  int8_t *dest; // [sp+10h] [bp-460h]@1
  int v19; // [sp+14h] [bp-45Ch]@1
  signed int v20; // [sp+18h] [bp-458h]@14
  char buf; // [sp+20h] [bp-450h]@11
  char v22; // [sp+21h] [bp-44Fh]@11
  char s; // [sp+24h] [bp-44Ch]@1
  char v24; // [sp+30h] [bp-440h]@4
  unsigned int v25; // [sp+38h] [bp-438h]@4
  char v26; // [sp+46h] [bp-42Ah]@4
  char v27; // [sp+47h] [bp-429h]@4
  char src[936]; // [sp+54h] [bp-41Ch]@1
  int v29; // [sp+454h] [bp-1Ch]@1

  v4 = a1;
  v19 = a4;
  v16 = a3;
  v5 = *a1;
  v15 = (*(int (**)(void))(v5 + 684))();
  dest = (int8_t *)(*(int ( **)(int32_t *, int, int32_t))(*v4 + 736))(v4, v19, 0);
  memset(&s, 0, 0xAu);
  memset(src, 0, 0x400u);
  g_nDevice = open("/dev/ttyMT1", 258);
  fd = open("/dev/msm_io_cm7", 0);
  if ( fd < 0 )
  {
    if ( g_nDevice > 0 )
      close(g_nDevice);
    v12 = 3;
    goto LABEL_36;
  }
  ioctl(fd, 7u, 0);
  usleep(0x2710u);
  ioctl(g_nDevice, 0x5401u, &v24);
  v25 = v25 & 0xFFFFEFF0 | 0xD;
  v26 = 1;
  v27 = 0;
  if ( ioctl(g_nDevice, 0x5402u, &v24) )
  {
    if ( g_nDevice > 0 )
      close(g_nDevice);
    if ( !fd )
    {
      v12 = 1;
LABEL_36:
      i = -v12;
      goto LABEL_37;
    }
    close(fd);
    i = -1;
LABEL_35:
    ioctl(fd, 7u, 0);
    close(fd);
    goto LABEL_37;
  }
  ioctl(g_nDevice, 0x540Bu, 2);
  if ( v16 == 85 )
  {
    buf = v16;
    v22 = 0;
    write(g_nDevice, &buf, 1u);
  }
  else
  {
    ioctl(fd, 6u, 0);
  }
  i = 0;
  if ( g_nDevice > 0 )
  {
    v20 = 30;
    if ( v16 == 85 )
      v20 = 90;
    v7 = 0;
    v17 = 0;
    v8 = 0;
    while ( (((unsigned int)v7 >> 31) + ((unsigned int)v7 <= 1)) & 0xFF && v17 < v20 )
    {
      v8 = read(g_nDevice, &src[v7], 1024 - v7);
      v7 += v8;
      ++v17;
      usleep(0x2710u);
    }
    v9 = v8;
    if ( v7 > 0 )
    {
      while ( v9 > 0 )
      {
        v9 = read(g_nDevice, &src[v7], 1024 - v7);
        v7 += v9;
      }
      if ( v16 == 85 )
      {
        for ( i = 0; ; ++i )
        {
          v11 = v7;
          if ( v7 > v15 )
            v11 = v15;
          if ( i >= v11 )
            break;
          dest[i] = GetData((uint8_t)src[i]);
        }
      }
      else
      {
        v10 = v7;
        if ( v7 > v15 )
          v10 = v15;
        memcpy(dest, src, v10);
        i = v7;
      }
    }
    else
    {
      i = -4;
    }
    close(g_nDevice);
  }
  if ( fd )
    goto LABEL_35;
LABEL_37:
  (*(void ( **)(int32_t *, int, int8_t *, int32_t))(*v4 + 768))(v4, v19, dest, 0);
  result = i;

  return result;
}
*/

/* только чтоб собиралось
int  Java_android_hardware_barcode_Scanner_ReadSCA(int a1, int a2, int a3)
{
  int v3; // r7@1
  int v4; // r0@2
  int v5; // r4@13
  ssize_t v6; // r5@16
  ssize_t i; // r0@21
  int v8; // r0@26
  int result; // r0@26
  unsigned int v10; // r0@27
  size_t v11; // r6@27
  int v12; // r5@27
  int fd; // [sp+4h] [bp-464h]@1
  int v14; // [sp+8h] [bp-460h]@1
  signed int v15; // [sp+Ch] [bp-45Ch]@16
  signed int v16; // [sp+10h] [bp-458h]@14
  char buf; // [sp+18h] [bp-450h]@11
  char v18; // [sp+19h] [bp-44Fh]@11
  char s; // [sp+1Ch] [bp-44Ch]@1
  char v20; // [sp+28h] [bp-440h]@4
  unsigned int v21; // [sp+30h] [bp-438h]@4
  char v22; // [sp+3Eh] [bp-42Ah]@4
  char v23; // [sp+3Fh] [bp-429h]@4
  char src[944]; // [sp+4Ch] [bp-41Ch]@1

  v3 = a1;
  v14 = a3;
  memset(&s, 0, 0xAu);
  memset(src, 0, 0x400u);
  g_nDevice = open("/dev/ttyMT1", 258);
  fd = open("/dev/msm_io_cm7", 0);
  if ( fd < 0 )
  {
    v4 = g_nDevice;
    if ( g_nDevice <= 0 )
      goto LABEL_31;
    goto LABEL_9;
  }
  ioctl(fd, 7u, 0);
  usleep(0x2710u);
  ioctl(g_nDevice, 0x5401u, &v20);
  v21 = v21 & 0xFFFFEFF0 | 0xD;
  v22 = 1;
  v23 = 0;
  if ( ioctl(g_nDevice, 0x5402u, &v20) )
  {
    if ( g_nDevice > 0 )
      close(g_nDevice);
    if ( !fd )
      goto LABEL_31;
    v4 = fd;
LABEL_9:
    close(v4);
    goto LABEL_31;
  }
  ioctl(g_nDevice, 0x540Bu, 2);
  if ( v14 == 85 )
  {
    buf = v14;
    v18 = 0;
    write(g_nDevice, &buf, 1u);
  }
  else
  {
    ioctl(fd, 6u, 0);
  }
  v5 = 0;
  if ( g_nDevice > 0 )
  {
    v16 = 30;
    if ( v14 == 85 )
      v16 = 90;
    v5 = 0;
    v15 = 0;
    v6 = 0;
    while ( (((unsigned int)v5 >> 31) + ((unsigned int)v5 <= 1)) & 0xFF && v15 < v16 )
    {
      v6 = read(g_nDevice, &src[v5], 1024 - v5);
      v5 += v6;
      ++v15;
      usleep(0x2710u);
    }
    if ( v5 > 2 )
    {
      for ( i = v6; i > 0; v5 += i )
        i = read(g_nDevice, &src[v5], 1024 - v5);
    }
    close(g_nDevice);
  }
  ioctl(fd, 7u, 0);
  close(fd);
  if ( v14 != 85 )
  {
    v8 = IsGB2312((int)src, v5);
    result = WindowsTojstring(v3, src, v8);
    goto LABEL_32;
  }
  v12 = 0;
  v11 = 0;
  while ( v12 < v5 )
  {
    v10 = GetData((uint8_t)src[v12]);
    src[v11] = v10;
    v11 += (__PAIR__(v10 - 255, v10 - 255) - __PAIR__(v10 - 256, 1)) >> 32;
    ++v12;
  }
  memcpy(&s, src, v11);
LABEL_31:
  result = (*(int ( **)(int, char *))(*(int32_t *)v3 + 668))(v3, &s);
LABEL_32:
  return result;
}
*/

/* только чтоб собиралось
int  Java_android_hardware_barcode_Scanner_ReadSCAAuto(int a1)
{
  int v1; // r5@1
  int v2; // r0@5
  int v3; // r6@7
  int v4; // r6@16
  ssize_t i; // r0@24
  int v6; // r0@31
  int result; // r0@31
  unsigned int v8; // r0@32
  size_t v9; // r7@32
  int v10; // r4@32
  int fd; // [sp+4h] [bp-464h]@4
  unsigned int v12; // [sp+8h] [bp-460h]@19
  signed int v13; // [sp+Ch] [bp-45Ch]@19
  signed int v14; // [sp+10h] [bp-458h]@17
  char buf; // [sp+18h] [bp-450h]@14
  char v16; // [sp+19h] [bp-44Fh]@14
  char s; // [sp+1Ch] [bp-44Ch]@1
  char v18; // [sp+28h] [bp-440h]@7
  unsigned int v19; // [sp+30h] [bp-438h]@7
  char v20; // [sp+3Eh] [bp-42Ah]@7
  char v21; // [sp+3Fh] [bp-429h]@7
  char src[944]; // [sp+4Ch] [bp-41Ch]@1

  v1 = a1;
  memset(&s, 0, 0xAu);
  memset(src, 0, 0x400u);
  g_nDevice = open("/dev/ttyMT1", 258);
  if ( g_nDevice < 0 )
    goto LABEL_36;
  if ( g_type == -1 )
    g_type = scan_read_type();
  fd = open("/dev/msm_io_cm7", 0);
  if ( fd < 0 )
  {
    v2 = g_nDevice;
    if ( g_nDevice <= 0 )
      goto LABEL_36;
    goto LABEL_12;
  }
  ioctl(fd, 7u, 0);
  usleep(0x4E20u);
  ioctl(g_nDevice, 0x5401u, &v18);
  v19 = v19 & 0xFFFFEFF0 | 0xD;
  v20 = 1;
  v21 = 0;
  v3 = ioctl(g_nDevice, 0x5402u, &v18);
  if ( v3 )
  {
    if ( g_nDevice > 0 )
      close(g_nDevice);
    if ( !fd )
      goto LABEL_36;
    v2 = fd;
LABEL_12:
    close(v2);
    goto LABEL_36;
  }
  ioctl(g_nDevice, 0x540Bu, 2);
  if ( g_type == 1 )
  {
    buf = 85;
    v16 = v3;
    write(g_nDevice, &buf, 1u);
  }
  else
  {
    ioctl(fd, 6u, v3);
  }
  v4 = 0;
  if ( g_nDevice > 0 )
  {
    v14 = 30;
    if ( g_type == 1 )
      v14 = 90;
    v4 = 0;
    v13 = 0;
    v12 = 0;
    while ( ((v12 >> 31) + (v12 <= 1)) & 0xFF && v13 < v14 )
    {
      v12 = read(g_nDevice, &src[v4], 1024 - v4);
      ++v13;
      v4 += v12;
      usleep(0x2710u);
    }
    if ( v4 > 2 )
    {
      for ( i = v12; i > 0; v4 += i )
        i = read(g_nDevice, &src[v4], 1024 - v4);
    }
    close(g_nDevice);
  }
  if ( fd )
  {
    ioctl(fd, 7u, 0);
    close(fd);
  }
  if ( g_type != 1 )
  {
    v6 = IsGB2312((int)src, v4);
    result = WindowsTojstring(v1, src, v6);
    goto LABEL_37;
  }
  v10 = 0;
  v9 = 0;
  while ( v10 < v4 )
  {
    v8 = GetData((uint8_t)src[v10]);
    src[v9] = v8;
    v9 += (__PAIR__(v8 - 255, v8 - 255) - __PAIR__(v8 - 256, 1)) >> 32;
    ++v10;
  }
  memcpy(&s, src, v9);
LABEL_36:
  result = (*(int ( **)(int, char *))(*(int32_t *)v1 + 668))(v1, &s);
LABEL_37:
  return result;
}
*/

/* только чтоб собиралось
int  Java_android_hardware_barcode_Scanner_ReadSCAEx(int a1, int a2, int a3, uint8_t a4)
{
  int v4; // r7@1
  int v5; // r0@2
  int v6; // r0@4
  char v7; // r4@4
  unsigned int v8; // r4@13
  ssize_t v9; // r5@16
  ssize_t i; // r0@21
  int result; // r0@28
  unsigned int v12; // r0@29
  size_t v13; // r6@29
  signed int v14; // r5@29
  int fd; // [sp+0h] [bp-468h]@1
  int v16; // [sp+4h] [bp-464h]@1
  signed int v17; // [sp+8h] [bp-460h]@16
  signed int v18; // [sp+Ch] [bp-45Ch]@14
  uint8_t v19; // [sp+10h] [bp-458h]@1
  char buf; // [sp+18h] [bp-450h]@11
  char v21; // [sp+19h] [bp-44Fh]@11
  char s; // [sp+1Ch] [bp-44Ch]@1
  char v23; // [sp+28h] [bp-440h]@4
  unsigned int v24; // [sp+30h] [bp-438h]@4
  char v25; // [sp+3Eh] [bp-42Ah]@4
  char v26; // [sp+3Fh] [bp-429h]@4
  char src[944]; // [sp+4Ch] [bp-41Ch]@1

  v4 = a1;
  v19 = a4;
  v16 = a3;
  memset(&s, 0, 0xAu);
  memset(src, 0, 0x400u);
  g_nDevice = open("/dev/ttyMT1", 258);
  fd = open("/dev/msm_io_cm7", 0);
  if ( fd < 0 )
  {
    v5 = g_nDevice;
    if ( g_nDevice <= 0 )
      goto LABEL_33;
    goto LABEL_9;
  }
  ioctl(fd, 1u, 1);
  ioctl(fd, 1u, 7);
  usleep(0x3E8u);
  ioctl(g_nDevice, 0x5401u, &v23);
  v24 = v24 & 0xFFFFEFF0 | 0xD;
  v25 = 1;
  v26 = 0;
  v6 = ioctl(g_nDevice, 0x5402u, &v23);
  v7 = v6;
  if ( v6 )
  {
    if ( g_nDevice > 0 )
      close(g_nDevice);
    if ( !fd )
      goto LABEL_33;
    v5 = fd;
LABEL_9:
    close(v5);
    goto LABEL_33;
  }
  ioctl(g_nDevice, 0x540Bu, 2);
  if ( v16 == 85 )
  {
    buf = 85;
    v21 = v7;
    write(g_nDevice, &buf, 1u);
  }
  else
  {
    ioctl(fd, 1u, 6);
  }
  v8 = 0;
  if ( g_nDevice > 0 )
  {
    v18 = 30;
    if ( v16 == 85 )
      v18 = 90;
    v8 = 0;
    v17 = 0;
    v9 = 0;
    while ( ((v8 >> 31) + (v8 <= 1)) & 0xFF && v17 < v18 )
    {
      v9 = read(g_nDevice, &src[v8], 1024 - v8);
      v8 += v9;
      ++v17;
      usleep(0x2710u);
    }
    if ( (signed int)v8 > 2 )
    {
      for ( i = v9; i > 0; v8 += i )
        i = read(g_nDevice, &src[v8], 1024 - v8);
    }
    close(g_nDevice);
  }
  if ( fd )
  {
    ioctl(fd, 1u, 7);
    close(fd);
  }
  if ( v16 != 85 )
  {
    result = WindowsTojstring(v4, src, v19);
    goto LABEL_34;
  }
  v14 = 0;
  v13 = 0;
  while ( v14 < (signed int)v8 )
  {
    v12 = GetData((uint8_t)src[v14]);
    src[v13] = v12;
    v13 += (__PAIR__(v12 - 255, v12 - 255) - __PAIR__(v12 - 256, 1)) >> 32;
    ++v14;
  }
  memcpy(&s, src, v13);
LABEL_33:
  result = (*(int ( **)(int, char *))(*(int32_t *)v4 + 668))(v4, &s);
LABEL_34:
  return result;
}
*/

/* только чтоб собиралось
int  Java_android_hardware_barcode_Scanner_ReadData(int a1, int a2, int a3)
{
  int v3; // r7@1
  int v4; // r0@4
  char v5; // r4@4
  unsigned int v6; // r4@7
  ssize_t v7; // r5@15
  ssize_t v8; // r0@19
  int v9; // r0@26
  int8_t *v10; // r0@26
  int8_t *v11; // r6@26
  signed int v12; // r5@26
  int result; // r0@30
  int fd; // [sp+4h] [bp-45Ch]@1
  int fda; // [sp+4h] [bp-45Ch]@26
  int v16; // [sp+8h] [bp-458h]@1
  signed int v17; // [sp+Ch] [bp-454h]@15
  signed int v18; // [sp+10h] [bp-450h]@13
  char buf; // [sp+1Ch] [bp-444h]@10
  char v20; // [sp+1Dh] [bp-443h]@10
  char v21; // [sp+20h] [bp-440h]@4
  unsigned int v22; // [sp+28h] [bp-438h]@4
  char v23; // [sp+36h] [bp-42Ah]@4
  char v24; // [sp+37h] [bp-429h]@4
  char s[952]; // [sp+44h] [bp-41Ch]@1
  int v26; // [sp+444h] [bp-1Ch]@1

  v3 = a1;
  v16 = a3;
  memset(s, 0, 0x400u);
  g_nDevice = open("/dev/ttyMT1", 258);
  fd = open("/dev/msm_io_cm7", 0);
  if ( fd < 0 )
  {
    if ( g_nDevice > 0 )
      close(g_nDevice);
    v6 = 0;
    goto LABEL_26;
  }
  ioctl(fd, 1u, 1);
  ioctl(fd, 1u, 7);
  usleep(0x3E8u);
  ioctl(g_nDevice, 0x5401u, &v21);
  v22 = v22 & 0xFFFFEFF0 | 0xD;
  v23 = 1;
  v24 = 0;
  v4 = ioctl(g_nDevice, 0x5402u, &v21);
  v5 = v4;
  if ( v4 )
  {
    if ( g_nDevice > 0 )
      close(g_nDevice);
    v6 = fd;
    if ( fd )
    {
      close(fd);
      v6 = 0;
LABEL_25:
      ioctl(fd, 1u, 7);
      close(fd);
      goto LABEL_26;
    }
  }
  else
  {
    ioctl(g_nDevice, 0x540Bu, 2);
    if ( v16 == 85 )
    {
      buf = 85;
      v20 = v5;
      write(g_nDevice, &buf, 1u);
    }
    else
    {
      ioctl(fd, 1u, 6);
    }
    v6 = 0;
    if ( g_nDevice > 0 )
    {
      v18 = 30;
      if ( v16 == 85 )
        v18 = 90;
      v6 = 0;
      v17 = 0;
      v7 = 0;
      while ( ((v6 >> 31) + (v6 <= 1)) & 0xFF && v17 < v18 )
      {
        v7 = read(g_nDevice, &s[v6], 1024 - v6);
        v6 += v7;
        ++v17;
        usleep(0x2710u);
      }
      v8 = v7;
      if ( (signed int)v6 > 0 )
      {
        while ( v8 > 0 )
        {
          v8 = read(g_nDevice, &s[v6], 1024 - v6);
          v6 += v8;
        }
      }
      close(g_nDevice);
    }
    if ( fd )
      goto LABEL_25;
  }
LABEL_26:
  v9 = (*(int ( **)(int, unsigned int))(*(int32_t *)v3 + 704))(v3, v6);
  fda = v9;
  v10 = (int8_t *)(*(int ( **)(int, int, int32_t))(*(int32_t *)v3 + 736))(v3, v9, 0);
  v11 = v10;
  v12 = 0;
  if ( v16 == 85 )
  {
    while ( v12 < (signed int)v6 )
    {
      v11[v12] = GetData((uint8_t)s[v12]);
      ++v12;
    }
  }
  else
  {
    memcpy(v10, s, v6);
  }
  (*(void ( **)(int, int, int8_t *, int32_t))(*(int32_t *)v3 + 768))(v3, fda, v11, 0);
  result = fda;
  return result;
}
*/
